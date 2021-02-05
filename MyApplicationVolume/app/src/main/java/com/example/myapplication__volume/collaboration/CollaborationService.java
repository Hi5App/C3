package com.example.myapplication__volume.collaboration;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.myapplication__volume.collaboration.basic.ReceiveMsgInterface;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class CollaborationService extends Service {

    private static final String TAG = "CollaborationService";

    private ReceiveMsgInterface receiveMsgInterface;

    // Binder given to clients
    private final IBinder binder = new LocalBinder();

    private ReadThread mReadThread;

    boolean mAllowRebind; // indicates whether onRebind should be used

    private Socket msgSocket;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {


        public CollaborationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CollaborationService.this;
        }

        public void addReceiveMsgInterface(ReceiveMsgInterface mreceiveMsgInterface){
            receiveMsgInterface = mreceiveMsgInterface;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();

        ServerConnector serverConnector = ServerConnector.getInstance();
        msgSocket = serverConnector.getManageSocket();
        mReadThread = new ReadThread(msgSocket);
        mReadThread.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }

    /**
     * thread for read and process msg
     */
    class ReadThread extends Thread {
        private WeakReference<Socket> mWeakSocket;
        private boolean isStart = true;

        public ReadThread(Socket socket) {
            mWeakSocket = new WeakReference<Socket>(socket);
        }

        //同步方法读取返回得数据
        @Override
        public void run() {
            super.run();
            Socket socket = mWeakSocket.get();
            if (null != socket) {
                try {
                    InputStream is = socket.getInputStream();
                    byte[] buffer = new byte[65535];          //测试过程发现接收的文件会达到5000多字节
                    int length;
                    try {
                        while (!socket.isClosed() && !socket.isInputShutdown()
                                && isStart && ((length = is.read(buffer)) != -1)) {
                            synchronized (this) {
                                if (length > 0) {
                                    byte[] msg = new byte[length];
                                    System.arraycopy(buffer, 0, msg, 0, length);
                                    receiveMsgInterface.onRecMessage(msg);

                                }
                            }
                        }
                    }catch (Exception e){

//                        flag = false;
//                        socket_flag = 0;
//                        //释放socket
//                        mReadThread.release();
//                        releaseLastSocket(mSocket);
////                        Intent intent = new Intent(MESSAGE_ACTION);
////                        intent.putExtra("message", "服务器已断开！");
////                        sendBroadcast(intent);

                        System.out.println("网络异常！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
