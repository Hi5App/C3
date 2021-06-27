//package com.penglab.hi5.chat.nim.session.action;
//
//import android.util.Log;
//
//import com.penglab.hi5.chat.nim.InfoCache;
//import com.penglab.hi5.R;
//import com.penglab.hi5.chat.agora.activity.PeerToPeerVoiceActivity;
//import com.penglab.hi5.chat.agora.message.AgoraMsgManager;
//import com.netease.nim.uikit.business.session.actions.BaseAction;
//import com.netease.nim.uikit.common.ToastHelper;
//import com.netease.nim.uikit.common.util.sys.NetworkUtil;
//
//import io.agora.rtm.ErrorInfo;
//import io.agora.rtm.ResultCallback;
//import io.agora.rtm.RtmClient;
//import io.agora.rtm.RtmMessage;
//import io.agora.rtm.RtmStatusCode;
//
//import static com.penglab.hi5.core.MainActivity.Toast_in_Thread_static;
//
//public class AudioAction extends BaseAction {
//
//    public AudioAction(){
//        super(R.drawable.message_plus_audio_chat_selector, R.string.input_panel_aduiochat);
//    }
//
//    @Override
//    public void onClick() {
//        if (NetworkUtil.isNetAvailable(getActivity())) {
//
//            startAudioCall();
//        } else {
//            ToastHelper.showToast(getActivity(), R.string.network_is_not_available);
//        }
//    }
//
//    private void startAudioCall(){
//
//        AgoraMsgManager agoraMsgManager = AgoraMsgManager.getInstance();
//        RtmClient mRtmClient = agoraMsgManager.getRtmClient();
//
//        String channelName = getAccount() + "And" + InfoCache.getAccount();
//        String callMessage = "##CallFrom" + InfoCache.getAccount() + "##In##" + channelName + "##";
//        RtmMessage message = mRtmClient.createMessage();
//        message.setText(callMessage);
//
//        Log.e("Audio", "start AudioCall");
//        mRtmClient.sendMessageToPeer(getAccount(), message, agoraMsgManager.getSendMessageOptions(), new ResultCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//
//                /*
//                start PeerToPeerVideo
//                 */
//                Log.e("Video", "send VideoCall successfully !");
//
//                PeerToPeerVoiceActivity.actionStart(getActivity(), callMessage, getAccount(), PeerToPeerVoiceActivity.CALL_SIDE);
//
//            }
//
//            @Override
//            public void onFailure(ErrorInfo errorInfo) {
//                final int errorCode = errorInfo.getErrorCode();
//
//                Log.e("Video", "Fail to send VideoCall !");
//
//
//                getActivity().runOnUiThread(() -> {
//                    switch (errorCode){
//                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_TIMEOUT:
//                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_FAILURE:
//                            Toast_in_Thread_static(getActivity().getString(R.string.call_failed));
//                            break;
//                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_PEER_UNREACHABLE:
//                            Toast_in_Thread_static(getActivity().getString(R.string.peer_offline));
//                            break;
//                        case RtmStatusCode.PeerMessageError.PEER_MESSAGE_ERR_CACHED_BY_SERVER:
//                            Toast_in_Thread_static(getActivity().getString(R.string.call_cached));
//                            break;
//                    }
//                });
//            }
//        });
//    }
//
//
//
//    // another implementation
//
//
////    // 获取 RtmCallManager 实例
////    RtmCallManager rtmCallManager = mRtmClient.getRtmCallManager();
////
////    String channelName = getAccount() + "And" + InfoCache.getAccount();
////    String callMessage = "##CallFrom" + InfoCache.getAccount() + "##In##" + channelName + "##";
////
////    //创建 LocalInvitation
////    LocalInvitation invitation = rtmCallManager.createLocalInvitation(getAccount());
////        invitation.setContent(callMessage);
////    //发送呼叫邀请
////        rtmCallManager.sendLocalInvitation(invitation, new ResultCallback<Void>(){
////        @Override
////        public void onSuccess(Void aVoid) {
////
////        }
////
////        @Override
////        public void onFailure(ErrorInfo errorInfo) {
////
////        }
////    });
//
//
//
//}
