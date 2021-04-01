package com.example.myapplication__volume.agora;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication__volume.Nim.InfoCache;
import com.example.myapplication__volume.agora.activity.PeerToPeerVideoActivity;
import com.example.myapplication__volume.agora.message.AgoraMsgManager;

import java.util.Map;
import java.util.regex.Pattern;

import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;

public class AgoraService extends Service {

    private static String TAG = "AgoraService";

    private AgoraMsgManager agoraMsgManager;

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

            Log.e(TAG, message.getText() + " from " + peerId);
            String msg = message.getText();
            if (Pattern.matches(videoMsgPattern, message.getText())) {

                Log.e(TAG,"Pattern.matches videoMsgPattern");

                String targetName = msg.substring(11, msg.indexOf("##In##"));
                String channelName = msg.substring(msg.indexOf("##In##") + 6, msg.lastIndexOf("##"));

                PeerToPeerVideoActivity.actionStart(mContext, message.getText(), targetName, PeerToPeerVideoActivity.CALLED_SIDE);

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
