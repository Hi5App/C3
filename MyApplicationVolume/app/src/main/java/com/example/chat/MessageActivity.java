package com.example.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.basic.ChatHelpUtils;
import com.example.chat.adapter.MessageAdapter;
import com.example.chat.model.MessageBean;
import com.example.chat.model.MessageListBean;
import com.example.myapplication__volume.BaseActivity;
import com.example.myapplication__volume.Myapplication;
import com.example.myapplication__volume.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmMessageType;
import io.agora.rtm.RtmStatusCode;

public class MessageActivity extends BaseActivity {

    private final String TAG = MessageActivity.class.getSimpleName();

    private TextView mTitleTextView;
    private EditText mMsgEditText;
    private ImageView mBigImage;
    private RecyclerView mRecyclerView;
    private List<MessageBean> mMessageBeanList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;

    private boolean mIsPeerToPeerMode = true;
    private String mUserId = "";
    private String mPeerId = "";
    private String mChannelName = "";
    private int mChannelMemberCount = 1;

    private ChatManager mChatManager;
    private RtmClient mRtmClient;
    private RtmClientListener mClientListener;
    private RtmChannel mRtmChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_chat);
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.height = (int)(getWindowManager().getDefaultDisplay().getHeight() * 0.8);
//        params.width = (int)(getWindowManager().getDefaultDisplay().getWidth() * 0.8);
//        getWindow().setAttributes(params);
        init();
    }

    private void init() {
        mChatManager = Myapplication.the().getChatManager();
        mRtmClient = mChatManager.getRtmClient();
        mClientListener = new MyRtmClientListener();
        mChatManager.registerListener(mClientListener);


        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver,true);

        /* get the target name*/
        Intent intent = getIntent();
        mIsPeerToPeerMode = intent.getBooleanExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, true);
        mUserId = intent.getStringExtra(MessageUtil.INTENT_EXTRA_USER_ID);
        String targetName = intent.getStringExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME);

//        mMessageBeanList = MainActivity.messageMap.get(targetName);
//        for (int i = 0; i < mMessageBeanList.size(); i++){
//            mMessageBeanList.get(i).setBackground(getMessageColor(targetName));
//        }

        Toolbar bar = findViewById(R.id.activity_wechat_chat_toolbar);
        setSupportActionBar(bar);
        getSupportActionBar().setTitle("");
        EditText et_msg = findViewById(R.id.message_edittiext);
        ImageView iv_add = findViewById(R.id.activity_wechat_chat_iv_add);
        Button btn_send = findViewById(R.id.activity_wechat_chat_btn_send);

        btn_send.startAnimation(getVisibleAnim(false, btn_send));
        btn_send.setVisibility(View.GONE);

        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("tag", "onTextChanged --- start -> " + start + " , count ->" + count + "，before ->" + before);
                if (start == 0 && count > 0) {
                    btn_send.startAnimation(getVisibleAnim(true, btn_send));
                    btn_send.setVisibility(View.VISIBLE);
                    iv_add.setVisibility(View.GONE);
                }

                if (start == 0 && count == 0) {
                    //btn_send.startAnimation(getVisibleAnim(false, btn_send));
                    btn_send.setVisibility(View.GONE);
                    iv_add.startAnimation(getVisibleAnim(true, iv_add));
                    iv_add.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        mTitleTextView = findViewById(R.id.message_title);
        mTitleTextView = findViewById(R.id.activity_wechat_chat_tv_name);
        if (mIsPeerToPeerMode) {
            mPeerId = targetName;
            mTitleTextView.setText(mPeerId);

            // load history chat records
            MessageListBean messageListBean = MessageUtil.getExistMessageListBean(mPeerId);
            if (messageListBean != null) {

                mMessageBeanList.addAll(messageListBean.getMessageBeanList());
            }

            // load offline messages since last chat with this peer.
            // Then clear cached offline messages from message pool
            // since they are already consumed.
            MessageListBean offlineMessageBean = new MessageListBean(mPeerId, mChatManager);
            mMessageBeanList.addAll(offlineMessageBean.getMessageBeanList());
            mChatManager.removeAllOfflineMessages(mPeerId);
        } else {
            mChannelName = targetName;
            mChannelMemberCount = 1;
            mTitleTextView.setText(MessageFormat.format("{0}({1})", mChannelName, mChannelMemberCount));
            createAndJoinChannel();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mMessageAdapter = new MessageAdapter(this, mMessageBeanList, message -> {
            if (message.getMessage().getMessageType() == RtmMessageType.IMAGE) {
                if (!TextUtils.isEmpty(message.getCacheFile())) {
                    Glide.with(this).load(message.getCacheFile()).into(mBigImage);
                    mBigImage.setVisibility(View.VISIBLE);
                } else {
                    ChatImageUtil.cacheImage(this, mRtmClient, (RtmImageMessage) message.getMessage(), new ResultCallback<String>() {
                        @Override
                        public void onSuccess(String file) {
                            message.setCacheFile(file);
                            runOnUiThread(() -> {
                                Glide.with(MessageActivity.this).load(file).into(mBigImage);
                                mBigImage.setVisibility(View.VISIBLE);
                            });
                        }

                        @Override
                        public void onFailure(ErrorInfo errorInfo) {

                        }
                    });
                }
            }
        });
        mRecyclerView = findViewById(R.id.message_list);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mMessageAdapter);

        mMsgEditText = findViewById(R.id.message_edittiext);
        mBigImage = findViewById(R.id.big_image);
    }


//    private void observerSyncDataComplete() {
//        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance()
//                .observeSyncDataCompletedEvent(
//                        (Observer<Void>) v -> DialogMaker
//                                .dismissProgressDialog());
//        //如果数据没有同步完成，弹个进度Dialog
//        if (!syncCompleted) {
//            DialogMaker.showProgressDialog(MessageActivity.this, getString(R.string.prepare_data))
//                    .setCanceledOnTouchOutside(false);
//        }
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsPeerToPeerMode) {
            MessageUtil.addMessageListBeanList(new MessageListBean(mPeerId, mMessageBeanList));
        } else {
            leaveAndReleaseChannel();
        }
        mChatManager.unregisterListener(mClientListener);
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver,false);
    }


    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    Log.e(TAG,"New Msg");
                    for (int i = 0; i < messages.size(); i++){
                        Log.e(TAG,"Msg from " + messages.get(i).getFromNick() + ": " + messages.get(i).getContent());
                    }
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                }
            };




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                final String file = resultUri.getPath();
                ChatImageUtil.uploadImage(this, mRtmClient, file, new ResultCallback<RtmImageMessage>() {
                    @Override
                    public void onSuccess(final RtmImageMessage rtmImageMessage) {
                        runOnUiThread(() -> {
                            MessageBean messageBean = new MessageBean(mUserId, rtmImageMessage, true, ChatHelpUtils.getCurrentMillisTime());
                            messageBean.setCacheFile(file);
                            mMessageBeanList.add(messageBean);
                            mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                            mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);

                            if (mIsPeerToPeerMode) {
                                sendPeerMessage(rtmImageMessage);
                            } else {
                                sendChannelMessage(rtmImageMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.getError().printStackTrace();
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_wechat_chat_btn_send:
                Log.e("onClick","R.id.activity_wechat_chat_btn_send");
                String msg = mMsgEditText.getText().toString();
                if (!msg.equals("")) {
                    RtmMessage message = mRtmClient.createMessage();
                    message.setText(msg);

                    MessageBean messageBean = new MessageBean(mUserId, message, true, ChatHelpUtils.getCurrentMillisTime());
                    mMessageBeanList.add(messageBean);
                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);

                    if (mIsPeerToPeerMode) {
                        sendPeerMessage(message);
                    } else {
                        sendChannelMessage(message);
                    }
                }
                mMsgEditText.setText("");
                break;
            case R.id.selection_img_btn:
                CropImage.activity().start(this);
                break;
            case R.id.big_image:
                mBigImage.setVisibility(View.GONE);
                break;
        }
    }

    public void onClickFinish(View v) {
        finish();
    }

    /**
     * API CALL: send message to peer
     */
    private void sendPeerMessage(final RtmMessage message) {
        mRtmClient.sendMessageToPeer(mPeerId, message, mChatManager.getSendMessageOptions(), new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // do nothing
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                // refer to RtmStatusCode.PeerMessageState for the message state
                final int errorCode = errorInfo.getErrorCode();
                runOnUiThread(() -> {
                    switch (errorCode) {
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_TIMEOUT:
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_FAILURE:
                            showToast(getString(R.string.send_msg_failed));
                            break;
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_PEER_UNREACHABLE:
                            showToast(getString(R.string.peer_offline));
                            break;
                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_CACHED_BY_SERVER:
                            showToast(getString(R.string.message_cached));
                            break;
                    }
                });
            }
        });
    }

    /**
     * API CALL: create and join channel
     */
    private void createAndJoinChannel() {
        // step 1: create a channel instance
        mRtmChannel = mRtmClient.createChannel(mChannelName, new MyChannelListener());
        if (mRtmChannel == null) {
            showToast(getString(R.string.join_channel_failed));
            finish();
            return;
        }

        Log.e("channel", mRtmChannel + "");

        // step 2: join the channel
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                Log.i(TAG, "join channel success");
                getChannelMemberList();
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "join channel failed");
                runOnUiThread(() -> {
                    showToast(getString(R.string.join_channel_failed));
                    finish();
                });
            }
        });
    }

    /**
     * API CALL: get channel member list
     */
    private void getChannelMemberList() {
        mRtmChannel.getMembers(new ResultCallback<List<RtmChannelMember>>() {
            @Override
            public void onSuccess(final List<RtmChannelMember> responseInfo) {
                runOnUiThread(() -> {
                    mChannelMemberCount = responseInfo.size();
                    refreshChannelTitle();
                });
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.e(TAG, "failed to get channel members, err: " + errorInfo.getErrorCode());
            }
        });
    }

    /**
     * API CALL: send message to a channel
     */
    private void sendChannelMessage(RtmMessage message) {
        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                // refer to RtmStatusCode.ChannelMessageState for the message state
                final int errorCode = errorInfo.getErrorCode();
                runOnUiThread(() -> {
                    switch (errorCode) {
                        case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_TIMEOUT:
                        case RtmStatusCode.ChannelMessageError.CHANNEL_MESSAGE_ERR_FAILURE:
                            showToast(getString(R.string.send_msg_failed));
                            break;
                    }
                });
            }
        });
    }

    /**
     * API CALL: leave and release channel
     */
    private void leaveAndReleaseChannel() {
        if (mRtmChannel != null) {
            mRtmChannel.leave(null);
            mRtmChannel.release();
            mRtmChannel = null;
        }
    }

    /**
     * API CALLBACK: rtm event listener
     */
    class MyRtmClientListener implements RtmClientListener {

        @Override
        public void onConnectionStateChanged(final int state, int reason) {
            runOnUiThread(() -> {
                switch (state) {
                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING:
                        showToast(getString(R.string.reconnecting));
                        break;
                    case RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED:
                        showToast(getString(R.string.account_offline));
                        setResult(MessageUtil.ACTIVITY_RESULT_CONN_ABORTED);
                        finish();
                        break;
                }
            });
        }

        @Override
        public void onMessageReceived(final RtmMessage message, final String peerId) {
            runOnUiThread(() -> {
                if (peerId.equals(mPeerId)) {
                    MessageBean messageBean = new MessageBean(peerId, message, false, ChatHelpUtils.getCurrentMillisTime());
                    messageBean.setBackground(getMessageColor(peerId));
                    mMessageBeanList.add(messageBean);
                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
                } else {
                    MessageUtil.addMessageBean(peerId, message);
                }
            });
        }

        @Override
        public void onImageMessageReceivedFromPeer(final RtmImageMessage rtmImageMessage, final String peerId) {
            runOnUiThread(() -> {
                if (peerId.equals(mPeerId)) {
                    MessageBean messageBean = new MessageBean(peerId, rtmImageMessage, false, ChatHelpUtils.getCurrentMillisTime());
                    messageBean.setBackground(getMessageColor(peerId));
                    mMessageBeanList.add(messageBean);
                    mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                    mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
                } else {
                    MessageUtil.addMessageBean(peerId, rtmImageMessage);
                }
            });
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

    /**
     * API CALLBACK: rtm channel event listener
     */
    class MyChannelListener implements RtmChannelListener {
        @Override
        public void onMemberCountUpdated(int i) {

        }

        @Override
        public void onAttributesUpdated(List<RtmChannelAttribute> list) {

        }

        @Override
        public void onMessageReceived(final RtmMessage message, final RtmChannelMember fromMember) {
            runOnUiThread(() -> {
                String account = fromMember.getUserId();
                Log.i(TAG, "onMessageReceived account = " + account + " msg = " + message);
                MessageBean messageBean = new MessageBean(account, message, false, ChatHelpUtils.getCurrentMillisTime());
                messageBean.setBackground(getMessageColor(account));
                mMessageBeanList.add(messageBean);
                mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
            });
        }

        @Override
        public void onImageMessageReceived(final RtmImageMessage rtmImageMessage, final RtmChannelMember rtmChannelMember) {
            runOnUiThread(() -> {
                String account = rtmChannelMember.getUserId();
                Log.i(TAG, "onMessageReceived account = " + account + " msg = " + rtmImageMessage);
                MessageBean messageBean = new MessageBean(account, rtmImageMessage, false, ChatHelpUtils.getCurrentMillisTime());
                messageBean.setBackground(getMessageColor(account));
                mMessageBeanList.add(messageBean);
                mMessageAdapter.notifyItemRangeChanged(mMessageBeanList.size(), 1);
                mRecyclerView.scrollToPosition(mMessageBeanList.size() - 1);
            });
        }

        @Override
        public void onFileMessageReceived(RtmFileMessage rtmFileMessage, RtmChannelMember rtmChannelMember) {

        }

        @Override
        public void onMemberJoined(RtmChannelMember member) {
            runOnUiThread(() -> {
                mChannelMemberCount++;
                refreshChannelTitle();
            });
        }

        @Override
        public void onMemberLeft(RtmChannelMember member) {
            runOnUiThread(() -> {
                mChannelMemberCount--;
                refreshChannelTitle();
            });
        }
    }

    private int getMessageColor(String account) {
        for (int i = 0; i < mMessageBeanList.size(); i++) {
            if (account.equals(mMessageBeanList.get(i).getAccount())) {
                return mMessageBeanList.get(i).getBackground();
            }
        }
        return MessageUtil.COLOR_ARRAY[MessageUtil.RANDOM.nextInt(MessageUtil.COLOR_ARRAY.length)];
    }

    private void refreshChannelTitle() {
        String titleFormat = getString(R.string.channel_title);
        String title = String.format(titleFormat, mChannelName, mChannelMemberCount);
        mTitleTextView.setText(title);
    }

    private void showToast(final String text) {
        runOnUiThread(() -> Toast.makeText(MessageActivity.this, text, Toast.LENGTH_SHORT).show());
    }


    private Animation getVisibleAnim(boolean show, View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int y = view.getMeasuredHeight() / 4;
        int x = view.getMeasuredWidth() / 4;
        if (show) {
            ScaleAnimation showAnim = new ScaleAnimation(0.01f, 1f, 0.01f, 1f, x, y);
            showAnim.setDuration(200);
            return showAnim;

        } else {

            ScaleAnimation hiddenAnim = new ScaleAnimation(1f, 0.01f, 1f, 0.01f, x, y);
            hiddenAnim.setDuration(200);
            return hiddenAnim;
        }
    }
}