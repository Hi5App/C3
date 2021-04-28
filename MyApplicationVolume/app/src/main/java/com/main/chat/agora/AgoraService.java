package com.main.chat.agora;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.main.chat.agora.activity.PeerToPeerVideoActivity;
import com.main.chat.agora.activity.PeerToPeerVoiceActivity;
import com.main.chat.agora.message.AgoraMsgManager;
import com.main.chat.nim.InfoCache;
import com.main.core.R;

import java.util.Map;
import java.util.regex.Pattern;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;

import static com.main.core.MainActivity.Toast_in_Thread_static;

public class AgoraService extends Service {

    private static String TAG = "AgoraService";

    private static AgoraMsgManager agoraMsgManager;

    private String username = null;

    private Context mContext;

    public AgoraService(){
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        username = InfoCache.getAccount();

        agoraMsgManager = AgoraMsgManager.getInstance();
        agoraMsgManager.registerListener(new MyRtmClientListener());

        Log.e(TAG,"finish init AgoraService");

    }


    /*
    process call from others
     */
    class MyRtmClientListener implements RtmClientListener {

        private final String callMsgPattern = "##CallFrom.*##In##.*##";
        private final String videoMsgPattern = "##VideoFrom.*##In##.*##";

        @Override
        public void onConnectionStateChanged(final int state, int reason) {
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onMessageReceived(final RtmMessage message, final String peerId) {

            //  ##VideoFromzx##In##xfffffAndzx##
            //  ##CallFromzx##In##xfffffAndzx##

            Log.e(TAG, message.getText() + " from " + peerId);
            String msg = message.getText();

//            if(AVConfig.status == AVConfig.Status.FREE){


                if (Pattern.matches(videoMsgPattern, message.getText())) {

                    Log.e(TAG,"Pattern.matches videoMsgPattern");

                    String targetName = msg.substring(11, msg.indexOf("##In##"));
                    PeerToPeerVideoActivity.actionStart(mContext, message.getText(), targetName, PeerToPeerVideoActivity.CALLED_SIDE);

                } else if (Pattern.matches(callMsgPattern, message.getText())){
                    Log.e(TAG,"Pattern.matches videoMsgPattern");

                    String targetName = msg.substring(10, msg.indexOf("##In##"));
                    PeerToPeerVoiceActivity.actionStart(mContext, message.getText(), targetName, PeerToPeerVoiceActivity.CALLED_SIDE);
                }


//            }else {
//                if (Pattern.matches(videoMsgPattern, message.getText())) {
//
//                    Log.e(TAG,"Pattern.matches videoMsgPattern");
//
//                    String targetName = msg.substring(11, msg.indexOf("##In##"));
//                    sendMsg(targetName,"##UserBusy##");
//
//                } else if (Pattern.matches(callMsgPattern, message.getText())){
//                    Log.e(TAG,"Pattern.matches videoMsgPattern");
//
//                    String targetName = msg.substring(10, msg.indexOf("##In##"));
//                    sendMsg(targetName,"##UserBusy##");
//                }

//            }


        }

        @SuppressLint("LongLogTag")
        @Override
        public void onImageMessageReceivedFromPeer(final RtmImageMessage rtmImageMessage, final String peerId) {
        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {
        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {
        }

        @Override
        public void onTokenExpired() {
        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {
        }
    }


    private void sendMsg(String msg, String target){

        AgoraMsgManager agoraMsgManager = AgoraMsgManager.getInstance();
        RtmClient mRtmClient = agoraMsgManager.getRtmClient();

        RtmMessage message = mRtmClient.createMessage();
        message.setText(msg);

        Log.e("Audio", "start AudioCall");
        mRtmClient.sendMessageToPeer(target, message, agoraMsgManager.getSendMessageOptions(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Video", "send VideoCall successfully !");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                final int errorCode = errorInfo.getErrorCode();

                Log.e("Video", "Fail to send VideoCall !");
                switch (errorCode){
                    case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_TIMEOUT:
                    case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_FAILURE:
                        Toast_in_Thread_static(getString(R.string.call_failed));
                        break;
                    case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_PEER_UNREACHABLE:
                        Toast_in_Thread_static(getString(R.string.peer_offline));
                        break;
                    case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_CACHED_BY_SERVER:
                        Toast_in_Thread_static(getString(R.string.call_cached));
                        break;
                }
            }
        });
    }

}
