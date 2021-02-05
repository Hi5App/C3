//package com.example.myapplication__volume.collaboration;
//
//import android.app.Activity;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.util.Log;
//import com.example.rkgg.UI.DBManager;
//import com.example.rkgg.UI.TypeConversion;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.ref.WeakReference;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
///**
// * Created by RKGG on 2017/9/21. */
//
//public class SocketService extends Service {
//    private static final String TAG = "SocketService";
//    /**
//     * 心跳检测时间 */
//    private static final long HEART_BEAT_RATE = 25 * 1000;
//    /**
//     * 常规数据心跳时间 */
//    private static final long REGULAR_BEAT_RATE = 30 * 1000;
//    /**
//     * 主机IP */
//    private static String HOST = "";
//    /**
//     * 端口号 */
//    public static final int PORT = 8865;
//    /**
//     * 登录广播 */
//    public static final String LOGIN_ACTION = "com.example.login_ACTION";
//    /**
//     * 消息广播 */
//    public static final String MESSAGE_ACTION = "com.example.message_ACTION";
//    /**
//     * 心跳广播 */
//    public static final String HEART_BEAT_ACTION = "com.example.heart_beat_ACTION";
//    private boolean HEARTBEAT_NORMAL = false;
//    /**
//     * 登出广播 */
//    public static final String LOGOUT_ACTION = "com.example.logout_ACTION";
//    /**
//     * 释放资源广播 */
//    public static final String RELEASESOCKET_ACTION = "com.example.releaseSocket_ACTION";
//
//    private long sendTime = 0L;
//    private String startHours;
//    private String endHours;
//    private int workCounts;
//    private int workTime;
//    public DBManager dbManager;
//    /**
//     * 弱引用 在引用对象的同时允许对垃圾对象进行回收 */
//    public WeakReference<Socket> mSocket;
//    private Object lock = new Object();
//    private ReadThread mReadThread;
//    private TypeConversion tc;
//    private TcpCommond tcd;
//    public static boolean flag = false;
//    public static int socket_flag = 0;
//    public static int logout_flag = 0;
//    //返回的云空间结构
//    public static byte[] cloud_results;
//    //下载文件的内容
//    public static byte[] file_down;
//    //分块数据长度
//    public static byte[] block_down;
//    private IBackService.Stub iBackService = new IBackService.Stub() {
//        @Override
//        public boolean sendMessage(String message) throws RemoteException {
//            String s = "";
//            byte[] bs = tc.hexStringToByteArray(message);
//            return sendMsg(bs);
//        }
//
//        @Override
//        public boolean sendCommond(byte[] bytes) throws RemoteException {
//            return sendMsg(bytes);
//        }
//    };
//
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return iBackService;
//    }
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        dbManager = new DBManager(this);
//        tc = new TypeConversion();
//        tcd = new TcpCommond(this);
//        new InitSocketThread().start();
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        //在销毁service的时候，结束线程，这里不需要再释放socket
//        if (heartHandler.getLooper().getThread().getState().toString().equals("RUNNABLE")) {
//            heartHandler.removeCallbacks(heartBeatRunnable);
//        }
//        if (regularHandler.getLooper().getThread().getState().toString().equals("RUNNABLE")) {
//            regularHandler.removeCallbacks(regularRunnable);
//        }
//        sendMsg(tcd.deviceLogout());
//        flag = false;
//        socket_flag = 0;
//    }
//
//
//    // 发送心跳包
//    public Handler heartHandler = new Handler();
//    public Runnable heartBeatRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (flag == true) {//判断socket是否连连接
//                            boolean isSuccess = sendMsg(tcd.heartBeatbag());//如果发送失败，就重新初始化一个socket
//                            if (!isSuccess) {
//                                heartHandler.removeCallbacks(heartBeatRunnable);
//                                mReadThread.release();
//                                releaseLastSocket(mSocket);
//                                new InitSocketThread().start();
//                            }
//                        }
//                    }
//                }).start();
//            }
//            heartHandler.postDelayed(this, HEART_BEAT_RATE);
//        }
//    };
//
//    //常规数据
//    private Handler regularHandler = new Handler();
//    private Runnable regularRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (System.currentTimeMillis() - sendTime >= REGULAR_BEAT_RATE) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (flag == true) {
//                            boolean isSuccess = sendMsg(tcd.regularData());
//                            if (!isSuccess) {
//                                regularHandler.removeCallbacks(regularRunnable);
//                                mReadThread.release();
//                                releaseLastSocket(mSocket);
//                                new InitSocketThread().start();
//                            }
//                        }
//                    }
//                }).start();
//            }
//            regularHandler.postDelayed(this, REGULAR_BEAT_RATE);
//        }
//    };
//
//    //发送定位数据
//    private Handler gpsHandler = new Handler();
//    private Runnable gpsRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (HEARTBEAT_NORMAL) {
//                boolean isSuccess = sendMsg(tcd.positioningData());
//                if (!isSuccess) {
//                    gpsHandler.removeCallbacks(gpsRunnable);
//                    mReadThread.release();
//                    sendMsg(tcd.positioningData());
//                    releaseLastSocket(mSocket);
//                    new InitSocketThread().start();
//                }
//            }
//            gpsHandler.post(this);
//        }
//    };
//
//    public boolean sendMsg(byte[] bytes) {
//        if (null == mSocket || null == mSocket.get()) {
//            return false;
//        }
//        Socket soc = mSocket.get();
//        try {
//            if (!soc.isClosed() && !soc.isOutputShutdown()) {
//                OutputStream os = soc.getOutputStream();
//                try {
//                    os.write(bytes);
//                    os.flush();
//                }catch (Exception e){
//                    Log.e("MainActivity","Socket is disconnected!");
//                }
//                // 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
//                sendTime = System.currentTimeMillis();
//            } else {
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    // 初始化socket
//    private void initSocket() {
//        Socket socket = null;
//        try {
//            // 域名解析
//            HOST = InetAddress.getByName("xxxx.xx.xx").getHostAddress();
//            socket = new Socket(HOST, PORT);
//            mSocket = new WeakReference<Socket>(socket);
//        }catch (Exception e){}
//        sendMsg(tcd.deviceLogin());//登陆设备
//        mReadThread = new ReadThread(socket);
//        mReadThread.start();
//    }
//
//    // 释放socket
//    public void releaseLastSocket(WeakReference<Socket> mSocket) {
//        if (null != mSocket) {
//            sendMsg(tcd.deviceLogout());
//            Socket sk = mSocket.get();
//            try {
//                if (!sk.isClosed()) {
//                    sk.close();
//                }
//                sk = null;
//                mSocket = null;
//            }catch (Exception e){
//                System.out.println("NULL!!!");
//            }
//        }
//    }
//
//    class InitSocketThread extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            try {
//                initSocket();
//            }catch (Exception e){
//                System.out.println("端口占用！！！");
//            }
//        }
//    }
//
//    class ReadThread extends Thread {
//        private WeakReference<Socket> mWeakSocket;
//        private boolean isStart = true;
//
//        public ReadThread(Socket socket) {
//            mWeakSocket = new WeakReference<Socket>(socket);
//        }
//
//        public void release() {
//            isStart = false;
//            sendMsg(tcd.deviceLogout());
//            releaseLastSocket(mWeakSocket);
//        }
//
//        //同步方法读取返回得数据
//        @Override
//        public void run() {
//            super.run();
//            Socket socket = mWeakSocket.get();
//            if (null != socket) {
//                try {
//                    InputStream is = socket.getInputStream();
//                    byte[] buffer = new byte[65535];//测试过程发现接收的文件会达到5000多字节
//                    int length;
//                    try {
//                        while (!socket.isClosed() && !socket.isInputShutdown()
//                                && isStart && ((length = is.read(buffer)) != -1)) {
//                            synchronized (this) {
//                                if (length > 0) {
//                                    byte[] temp = new byte[length];
//                                    System.arraycopy(buffer, 0, temp, 0, length);
//                                    //获取返回值的第3位，表明命令类型
//                                    String msg_three = tc.bytesToHexString(temp).substring(4, 6);
//                                    //获取返回值的第4位，表明数据返回的情况
//                                    String msg_four = tc.bytesToHexString(temp).substring(6, 8);
//                                    // 收到服务器过来的消息，就通过Broadcast发送出去
//                                    if (msg_three.equals("01")) {//登陆
//                                        switch (msg_four) {
//                                            case "01":
//                                                Log.i(TAG, "登录成功");
//                                                //先发送一个常规包，确定在线情况
//                                                flag = true;
//                                                socket_flag = 1;
//                                                sendMsg(tcd.regularData());
//                                                //初始化成功返回标志后，开始发送心跳包
//                                                heartHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
//                                                //发送广播，防止不同的activity重新new一个Serivce
//                                                Intent intent = new Intent(LOGIN_ACTION);
//                                                intent.putExtra("condition", "Socket_Connected");
//                                                sendBroadcast(intent);
//                                                break;
//                                            case "02":
//                                                Log.i(TAG, "登录超时");
//                                                mReadThread.release();
//                                                releaseLastSocket(mSocket);
////                                                new InitSocketThread().start();
//                                                break;
//                                            case "03":
//                                                Log.i(TAG, "账号密码错误");
//                                                mReadThread.release();
//                                                releaseLastSocket(mSocket);
////                                                new InitSocketThread().start();
//                                                break;
//                                            case "04":
//                                                Log.i(TAG, "其他位置登录");
//                                                //需要在销毁的时候登出设备，否则会出现这个错误
//                                                mReadThread.release();
//                                                releaseLastSocket(mSocket);
////                                                new InitSocketThread().start();
//                                                break;
//                                            default:
//                                                break;
//                                        }
//                                    } else if (msg_three.equals("02")) {//实时上报
//                                        switch (msg_four) {
//                                            case "01":
//                                                Log.i(TAG, "实时信息上报成功");
//                                                break;
//                                            case "02":
//                                                Log.i(TAG, "实时信息上报超时");
//                                                break;
//                                            default:
//                                                break;
//                                        }
//                                    } else if (msg_three.equals("03")) {// 心跳
//                                        switch (msg_four) {
//                                            case "01":
//                                                Log.i(TAG, "心跳正常");
//                                                //心跳正常后，开始发送常规数据，经测试发现，常规数据也需要定时发送，
//                                                //否则发送一次后，大概1min左右会出现掉线状态
//                                                regularHandler.postDelayed(regularRunnable, REGULAR_BEAT_RATE);
//                                                break;
//                                            case "":
//                                                break;
//                                        }
//                                    } else if (msg_three.equals("05")) {//登出
//                                        switch (msg_four) {
//                                            case "01":
//                                                Log.i(TAG, "登出成功");
//                                                logout_flag = 1;
//                                                mReadThread.release();
//                                                releaseLastSocket(mSocket);
//                                                break;
//                                            case "02":
//                                                Log.i(TAG, "登出超时");
//                                                break;
//                                            default:
//                                                break;
//                                        }
//                                    }  else {
//                                        // 其他消息回复
//                                        Intent intent = new Intent(MESSAGE_ACTION);
//                                        intent.putExtra("message", tc.bytesToHexString(temp));
//                                        sendBroadcast(intent);
//                                    }
//                                }
//                            }
//                        }
//                    }catch (Exception e){
//                        flag = false;
//                        socket_flag = 0;
//                        //释放socket
//                        mReadThread.release();
//                        releaseLastSocket(mSocket);
////                        Intent intent = new Intent(MESSAGE_ACTION);
////                        intent.putExtra("message", "服务器已断开！");
////                        sendBroadcast(intent);
//                        System.out.println("网络异常！");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
