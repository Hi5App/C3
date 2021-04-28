package com.example.chat.agora.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chat.agora.basic.RingPlayer;
import com.example.chat.agora.message.AgoraMsgManager;
import com.example.myapplication__volume.BaseActivity;
import com.example.myapplication__volume.R;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.common.annotation.NonNull;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;

import static com.example.myapplication__volume.MainActivity.Toast_in_Thread_static;


public class PeerToPeerVideoActivity extends BaseActivity {



    /**
     *  Call-side: onCreate:  isCalling = false, vibrator = true,  ring = true
     *             refused:   isCalling = false, vibrator = false, ring = false          ()
     *             received:  isCalling = true,  vibrator = false, ring = false
     *
     */

    private static final String TAG = PeerToPeerVideoActivity.class.getSimpleName();


    public static final String CALL_SIDE = "CALL-SIDE";
    public static final String CALLED_SIDE = "CALLED-SIDE";


    private static String CHANNELNAME = "";

    private static String PEERID = "";

    private static String USERTYPE = "";                // "CALL-SIDE": call-side;  "CALLED-SIDE": called-side

    private static boolean isCalling = false;

    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;

    private RtmClientListener rtmClientListener;



    /**
     * called-side ----------------------------------------------------------------------------------------------
     */

    private HeadImageView headImageView;
    private ImageView mStartCallBtn;
    private ImageView mEndCallBtn;



    /**
     * for calling alert -----------------------------------------------------------------------------------------
     */
    private Vibrator mVibrator;
    private boolean needCancel = true;
    private Timer timer = new Timer();




    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        /**
         * Occurs when the first remote video frame is received and decoded.
         * This callback is triggered in either of the following scenarios:
         *
         *     The remote user joins the channel and sends the video stream.
         *     The remote user stops sending the video stream and re-sends it after 15 seconds. Possible reasons include:
         *         The remote user leaves channel.
         *         The remote user drops offline.
         *         The remote user calls the muteLocalVideoStream method.
         *         The remote user calls the disableVideo method.
         *
         * @param uid User ID of the remote user sending the video streams.
         * @param width Width (pixels) of the video stream.
         * @param height Height (pixels) of the video stream.
         * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until this callback is triggered.
         */
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);

                    /*
                    when peer receive the invitation
                     */
                    if (USERTYPE.equals(CALL_SIDE)){
                        switchView(mLocalVideo);
                        switchView(mRemoteVideo);
                        mLocalContainer.setVisibility(View.VISIBLE);

                        finishAlert();
                        needCancel = false;
                        isCalling = true;
//                        AVConfig.status = AVConfig.Status.PEERTOPEERVIEDO;
                    }
                }
            });
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mLogView.logI("User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft(uid);
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        ViewGroup parent = mRemoteContainer;
        if (parent.indexOfChild(mLocalVideo.view) > -1) {
            parent = mLocalContainer;
        }

        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        if (mRemoteVideo != null) {
            return;
        }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(mRemoteVideo);
    }

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
        }
        finish();
    }










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rtmClientListener = new VideoRtmClientListener();
        AgoraMsgManager agoraMsgManager = AgoraMsgManager.getInstance();
        agoraMsgManager.registerListener(rtmClientListener);


        Log.e(TAG, USERTYPE);
        if(USERTYPE.equals(CALLED_SIDE)){
            Log.e(TAG,"USERTYPE.equals(CALLED_SIDE)");
            setContentView(R.layout.activity_video_chat_called_view);
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



    /**
     * when successfully init video call
     */
    private void initCall(){

        setContentView(R.layout.activity_video_chat_view);
        initUI();

        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();
        }
    }


    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);

    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initEngineAndJoinChannel() {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();

        if (USERTYPE.equals(CALL_SIDE)){
            switchView(mLocalVideo);
            switchView(mRemoteVideo);
            mLocalContainer.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        mRtcEngine.joinChannel(token, CHANNELNAME, "Extra Optional Data", 0);
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
            endCall();
            RtcEngine.destroy();
        }
    }


    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }



    public void onEndCall(View view) {

        if (isCalling){
            sendMsg("##EndCalling##");
            endCall();
        }else {
            sendMsg("##CancelCalling##");
            finishAlert();
        }
        finish();
    }


    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            mLocalContainer.addView(canvas.view);
        }
    }

    public void onLocalContainerClick(View view) {
        switchView(mLocalVideo);
        switchView(mRemoteVideo);
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
        hint.setText(UserInfoHelper.getUserName(PEERID) + " is requesting a video call");

    }

    public void refuseCall(View view) {

        sendMsg("##RefuseToAnswer##");
        needCancel = false;
        finishAlert();
        finish();
    }


    public void answerCall(View view) {

//        sendMsg("##SuccessToAnswer##");
        initCall();
        finishAlert();
        isCalling = true;
        needCancel = false;
//        AVConfig.status = AVConfig.Status.PEERTOPEERVIEDO;
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

        isCalling = false;
        CHANNELNAME = channel;
        PEERID      = peerId;
        USERTYPE    = userType;

        Intent intent = new Intent(context, PeerToPeerVideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }




    /**
     * send msg between users
     * @param msg message sent
     */
    private void sendMsg(String msg){
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




    class VideoRtmClientListener implements RtmClientListener {

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
                        Toast_in_Thread_static("Video call is REFUSED");
                        finishAlert();
                        finish();
                    }
                } else if (msg.equals("##EndCalling##")){
                    /*
                    peer refuse the call
                     */
                    if (peerId.equals(PEERID)){
                        Toast_in_Thread_static("Video call is ENDED");
                        finish();
                    }
                } else if (msg.equals("##CancelCalling##")) {
                    if (peerId.equals(PEERID)) {
                        Toast_in_Thread_static("Video call is CANCELED");
                        finishAlert();
                        finish();
                    }
                }

//                } else if (msg.equals("##UserBusy##")){
//                    if (peerId.equals(PEERID)){
//                        Toast_in_Thread_static("Video call is CANCELED");
//                        finishAlert();
//                        finish();
//                    }
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
        return cmpNameTemp.equals("ComponentInfo{com.example.myapplication__volume/com.example.chat.agora.activity.PeerToPeerVideoActivity}");
    }

}
