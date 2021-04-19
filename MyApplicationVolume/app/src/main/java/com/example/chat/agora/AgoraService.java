package com.example.chat.agora;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication__volume.Nim.InfoCache;
import com.example.chat.agora.activity.PeerToPeerVideoActivity;
import com.example.chat.agora.activity.PeerToPeerVoiceActivity;
import com.example.chat.agora.message.AgoraMsgManager;

import java.util.Map;
import java.util.regex.Pattern;

import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;

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

            if(AVConfig.status == AVConfig.Status.FREE){
                if (Pattern.matches(videoMsgPattern, message.getText())) {

                    Log.e(TAG,"Pattern.matches videoMsgPattern");

                    String targetName = msg.substring(11, msg.indexOf("##In##"));
                    PeerToPeerVideoActivity.actionStart(mContext, message.getText(), targetName, PeerToPeerVideoActivity.CALLED_SIDE);

                } else if (Pattern.matches(callMsgPattern, message.getText())){
                    Log.e(TAG,"Pattern.matches videoMsgPattern");

                    String targetName = msg.substring(10, msg.indexOf("##In##"));
                    PeerToPeerVoiceActivity.actionStart(mContext, message.getText(), targetName, PeerToPeerVoiceActivity.CALLED_SIDE);
                }
            }else if(AVConfig.status == AVConfig.Status.FREE){

            }


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


}
