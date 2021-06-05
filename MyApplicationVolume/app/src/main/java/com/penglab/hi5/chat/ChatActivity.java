package com.penglab.hi5.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.penglab.hi5.core.BaseActivity;
import com.penglab.hi5.chat.nim.InfoCache;
import com.penglab.hi5.core.MainActivity;
import com.penglab.hi5.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;

import cn.carbs.android.library.MDDialog;


/**
 * the main activity for chat MESSAGE & VIDEO
 */

public class ChatActivity extends BaseActivity {

    public static final int MAX_INPUT_NAME_LENGTH = 64;

    private static final String TAG = ChatActivity.class.getSimpleName();

    private static Context chatContext;

    private int FragmentId;

    @SuppressLint("HandlerLeak")
    public static Handler chatHandler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    Log.d(TAG, "handleMessage: 0" + (String)msg.obj);
                    String [] inviteMessage = ((String) msg.obj).split(" ");
                    MainActivity.actionStart(chatContext, inviteMessage[0], inviteMessage[1], inviteMessage[2]);

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_main);

        chatContext = this;

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_recentContacts, R.id.navigation_contactList, R.id.navigation_Me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar_chat);

        FragmentId = navController.getCurrentDestination().getId();

        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        ImageView add = toolbar.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriends();
            }
        });
    }


    /**
     * add friends
     */
    public void addFriends(){

        Log.e(TAG, "addFriends");
        MDDialog mdDialog = new MDDialog.Builder(this)
                .setContentView(R.layout.peer_chat)
                .setNegativeButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButton(R.string.btn_add, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .setPositiveButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {
                        Log.e("PeerToPeer", " PeerToPeer Add Friends");
                        EditText targetEdit = (EditText)contentView.findViewById(R.id.target_name_edit);
                        String mTargetName = targetEdit.getText().toString();
                        EditText verificationEdit = (EditText)contentView.findViewById(R.id.verification_msg);
                        String verificationMsg = verificationEdit.getText().toString();

                        if (mTargetName.equals("")) {
                            Toast_in_Thread(getString(R.string.account_empty));
                        } else if (mTargetName.length() >= MAX_INPUT_NAME_LENGTH) {
                            Toast_in_Thread(getString(R.string.account_too_long));
                        } else if (mTargetName.startsWith(" ")) {
                            Toast_in_Thread(getString(R.string.account_starts_with_space));
                        } else if (mTargetName.equals("null")) {
                            Toast_in_Thread(getString(R.string.account_literal_null));
                        } else if (mTargetName.equals(InfoCache.getAccount())) {
                            Toast_in_Thread(getString(R.string.account_cannot_be_yourself));
                        } else {

                                final VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
                                NIMClient.getService(FriendService.class).addFriend(new AddFriendData(mTargetName, verifyType, verificationMsg))
                                        .setCallback(new RequestCallback<Void>() {
                                            @Override
                                            public void onSuccess(Void param) {
                                                Toast_in_Thread("You Request is sent successfully !");
                                            }

                                            @Override
                                            public void onFailed(int code) {
                                                Toast_in_Thread("Fail to send your Request !");
                                            }

                                            @Override
                                            public void onException(Throwable exception) {
                                                Toast_in_Thread("Some Exception occur !");
                                            }
                                        });
                        }
                    }
                })
                .setNegativeButtonMultiListener(new MDDialog.OnMultiClickListener() {
                    @Override
                    public void onClick(View clickedView, View contentView) {

                    }
                })
                .setTitle(R.string.title_add_friends)
                .create();

        mdDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        chatContext = null;
    }
}
