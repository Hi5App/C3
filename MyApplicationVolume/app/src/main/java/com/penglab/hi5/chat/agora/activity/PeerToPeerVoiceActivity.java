package com.penglab.hi5.chat.agora.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.penglab.hi5.chat.agora.basic.RingPlayer;
import com.penglab.hi5.chat.agora.message.AgoraMsgManager;
import com.penglab.hi5.core.BaseActivity;
import com.penglab.hi5.R;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.common.annotation.NonNull;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;

import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;

public class PeerToPeerVoiceActivity extends BaseActivity {

    private static final String TAG = PeerToPeerVoiceActivity.class.getSimpleName();

    public static final String CALL_SIDE = "CALL-SIDE";
    public static final String CALLED_SIDE = "CALLED-SIDE";


    private static String CHANNELNAME = "";

    private static String PEERID = "";

    private static String USERTYPE = "";                // "CALL-SIDE": call-side;  "CALLED-SIDE": called-side

    private static boolean isCalling = false;

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;

    private RtcEngine mRtcEngine;
    private RtmClientListener rtmClientListener;


    /**
     * called-side ----------------------------------------------------------------------------------------------
     */

    private HeadImageView headImageView;
    private ImageView mStartCallBtn;
    private ImageView mEndCallBtn;
    private TextView callingHint;



    /**
     * for calling alert -----------------------------------------------------------------------------------------
     */
    private Vibrator mVibrator;
    private boolean needCancel = true;
    private Timer timer = new Timer();


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@androidx.annotation.NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    callingHint = findViewById(R.id.calling_hint_text);
                    callingHint.setText(UserInfoHelper.getUserName(PEERID));
                    break;
            }
        }
    };




    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who
         * leaves
         * the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, final int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft(uid, reason);
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            if (USERTYPE.equals(CALL_SIDE)){
                finishAlert();
                needCancel = false;
                isCalling = true;

//                AVConfig.status = AVConfig.Status.PEERTOPEERVOICE;

            }
        }

        /**
         * Occurs when a remote user stops/resumes sending the audio stream.
         * The SDK triggers this callback when the remote user stops or resumes sending the audio stream by calling the muteLocalAudioStream method.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's audio stream is muted/unmuted:
         *
         *     true: Muted.
         *     false: Unmuted.
         */
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVoiceMuted(uid, muted);
                }
            });
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rtmClientListener = new PeerToPeerVoiceActivity.VideoRtmClientListener();
        AgoraMsgManager agoraMsgManager = AgoraMsgManager.getInstance();
        agoraMsgManager.registerListener(rtmClientListener);


        Log.e(TAG, USERTYPE);
        if(USERTYPE.equals(CALLED_SIDE)){
            Log.e(TAG,"USERTYPE.equals(CALLED_SIDE)");
            setContentView(R.layout.activity_voice_chat_called_view);
            initUICALLED();
        }else {
            Log.e(TAG,"! USERTYPE.equals(CALLED_SIDE)");
            initCall();
        }


        // 获得系统的Vibrator实例
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        mVibrator.vibrate(new long[]{500, 100, 500, 100, 500, 100}, 0);
        RingPlayer.playRing(this);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (needCancel){
                    finishAlert();
                    finish();
                }
            }
        },20000);


    }



    private void initCall(){
        setContentView(R.layout.activity_voice_chat_view);
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        joinChannel();

        headImageView = findViewById(R.id.user_head_image_voice);
        headImageView.loadBuddyAvatar(PEERID);

        if (USERTYPE.equals(CALL_SIDE)){
            callingHint = findViewById(R.id.calling_hint_text);
            callingHint.setText("You are calling " + UserInfoHelper.getUserName(PEERID));
        }else {
            callingHint = findViewById(R.id.calling_hint_text);
            callingHint.setText(UserInfoHelper.getUserName(PEERID));
        }

    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        Log.i(TAG, "checkSelfPermission " + permission + " " + requestCode);
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode);

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }
        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*
        unregister the rtmClientListener
         */
        AgoraMsgManager.getInstance().unregisterListener(rtmClientListener);
        timer.cancel();

//        AVConfig.status = AVConfig.Status.FREE;

        if (isCalling || USERTYPE.equals(CALL_SIDE)){
            /*
              Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

              This method is useful for apps that occasionally make voice or video calls,
              to free up resources for other operations when not making calls.
             */
            leaveChannel();
            RtcEngine.destroy();
            mRtcEngine = null;
        }
    }



    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(iv.isSelected());
    }


    public void onSwitchSpeakerphoneClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        } else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        // Enables/Disables the audio playback route to the speakerphone.
        //
        // This method sets whether the audio is routed to the speakerphone or earpiece. After calling this method, the SDK returns the onAudioRouteChanged callback to indicate the changes.
        mRtcEngine.setEnableSpeakerphone(view.isSelected());
    }


    public void onEncCallClicked(View view) {

        if (isCalling){
            sendMsg("##EndCalling##");
        }else {
            sendMsg("##CancelCalling##");
            finishAlert();
        }
        finish();
    }


    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }


    private void joinChannel() {
        String accessToken = getString(R.string.agora_access_token);
        if (TextUtils.equals(accessToken, "") || TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null; // default, no token
        }

        // Sets the channel profile of the Agora RtcEngine.
        // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
        // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);

        // Allows a user to join a channel.
        mRtcEngine.joinChannel(accessToken, CHANNELNAME, "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }


    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }


    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
        finish();
    }


    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }





    /**
     * called-side ----------------------------------------------------------------------------------------------
     */


    private void initUICALLED(){

        mStartCallBtn = findViewById(R.id.btn_startCall);
        mEndCallBtn = findViewById(R.id.btn_endCall);
        headImageView = findViewById(R.id.user_head_image_called);
        headImageView.loadBuddyAvatar(PEERID);

        TextView hint = findViewById(R.id.called_hint_text);
        hint.setText(UserInfoHelper.getUserName(PEERID) + " is calling you now");

    }

    public void refuseCall(View view) {

        sendMsg("##RefuseToAnswer##");
        needCancel = false;
        finishAlert();
        finish();
    }


    public void answerCall(View view) {

        sendMsg("##SuccessToAnswer##");
        initCall();
        finishAlert();
        isCalling = true;
        needCancel = false;
//        AVConfig.status = AVConfig.Status.PEERTOPEERVOICE;
    }







    /**
     * start activity
     * @param context the context
     * @param channel video channel
     */
    public static void actionStart(Context context, String channel, String peerId, String userType){

        Log.e(TAG,"actionStart()");
        Log.e(TAG,"channel: " + channel);
        Log.e(TAG,"peerId: " + peerId);
        Log.e(TAG,"userType: " + userType);

        isCalling   = false;
        CHANNELNAME = channel;
        PEERID      = peerId;
        USERTYPE    = userType;

        Intent intent = new Intent(context, PeerToPeerVoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }




    /**
     * send msg between users
     * @param msg message sent
     */
    private void sendMsg(String PEERID, String msg){
        AgoraMsgManager agoraMsgManager = AgoraMsgManager.getInstance();
        RtmClient mRtmClient = agoraMsgManager.getRtmClient();

        RtmMessage message = mRtmClient.createMessage();
        message.setText(msg);
        mRtmClient.sendMessageToPeer(PEERID, message, agoraMsgManager.getSendMessageOptions(), new ResultCallback() {
            @Override
            public void onSuccess(Object o) {
                Log.e(TAG,"send msg successfully!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                final int errorCode = errorInfo.getErrorCode();
                Log.e(TAG, "Fail to send msg !");
                runOnUiThread(() -> {
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
                });
            }
        });
    }

    private void sendMsg(String msg){
        sendMsg(PEERID, msg);
    }




    class VideoRtmClientListener implements RtmClientListener {
        private final String callMsgPattern = "##CallFrom.*##In##.*##";

        @Override
        public void onConnectionStateChanged(final int state, int reason) {

        }

        @SuppressLint("LongLogTag")
        @Override
        public void onMessageReceived(final RtmMessage message, final String peerId) {
            Log.d("onMessageReceived", message.getText() + " from " + peerId);

            if (isTopActivity()) {
                Log.d("onMessageReceived","isTopActivity: " + message.getText() + " from " + peerId);
                String msg = message.getText();

                if (msg.equals("##RefuseToAnswer##")){
                    /*
                    peer refuse the call
                     */
                    if (peerId.equals(PEERID)){
                        Toast_in_Thread_static("Voice call is REFUSED");
                        finishAlert();
                        finish();
                    }
                } else if (msg.equals("##EndCalling##")){
                    /*
                    peer refuse the call
                     */
                    if (peerId.equals(PEERID)){
                        Toast_in_Thread_static("Voice call is ENDED");
                        finish();
                    }
                } else if (msg.equals("##CancelCalling##")){
                    if (peerId.equals(PEERID)){
                        Toast_in_Thread_static("Voice call is CANCELED");
                        finishAlert();
                        finish();
                    }
                } else if (msg.equals("##SuccessToAnswer##")) {
                    Log.e(TAG, "try to reset the name");

                    if (peerId.equals(PEERID)) {
                        Log.e(TAG, "try to reset the name");
                        handler.sendEmptyMessage(1);
                    }
                }

//                } else if (msg.equals("##UserBusy##")){
//                if (peerId.equals(PEERID)){
//                    Toast_in_Thread_static("User is Busy");
//                    finishAlert();
//                    finish();
//                }
//                } else if (Pattern.matches(callMsgPattern, message.getText())){
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            String targetName = msg.substring(10, msg.indexOf("##In##"));
//                            sendMsg(targetName,"##UserBusy##");
//                            Log.e(TAG,"Pattern.matches videoMsgPattern, targetName: " + targetName);
//                        }
//                    }, 2 * 1000);
//                }
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



    /*
    finish alert
     */
    private void finishAlert(){
        mVibrator.cancel();
        RingPlayer.stopRing();
    }



    /*
    Judge whether the activity is on the Top
     */
    private boolean isTopActivity(){
        ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;
        if(runningTaskInfos != null){
            cmpNameTemp = runningTaskInfos.get(0).topActivity.toString();
        }
        if(cmpNameTemp == null){
            return false;
        }
        Log.d(TAG, "isTopActivity" + cmpNameTemp);
        return cmpNameTemp.equals("ComponentInfo{com.example.core/com.example.chat.agora.activity.PeerToPeerVoiceActivity}");
    }
}
