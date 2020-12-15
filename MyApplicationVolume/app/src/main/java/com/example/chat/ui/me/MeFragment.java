package com.example.chat.ui.me;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication__volume.Nim.DemoCache;
import com.example.myapplication__volume.Nim.contact.activity.UserProfileSettingActivity;
import com.example.myapplication__volume.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.business.uinfo.UserInfoHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;


public class MeFragment extends Fragment {

    private String account;

    // 基本信息
    private HeadImageView headImageView;

    private TextView nameText;

    private TextView nickText;

    private TextView accountText;

    private ImageView rightArrow;

    private RelativeLayout settingsLayout;

    private ImageView rightArrow_settings;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.me_fragment, container, false);
        account = DemoCache.getAccount();
        findViews(rootView);

        return rootView;
    }


    private void findViews(View view) {
        headImageView = view.findViewById(R.id.user_head_image);
        nickText = view.findViewById(R.id.user_nick);
        accountText = view.findViewById(R.id.user_account);
        rightArrow = view.findViewById(R.id.right_arrow);

        settingsLayout = view.findViewById(R.id.settings);
        rightArrow_settings = settingsLayout.findViewById(R.id.right_arrow);

        rightArrow.setOnClickListener(onClickListener);
        settingsLayout.setOnClickListener(onClickListener);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == rightArrow) {
                UserProfileSettingActivity.start(getContext(),account);
            } else if (v == settingsLayout) {
                Toast.makeText(getContext(),"Settings is in development !", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * ***************************** life cycle *******************************
     */

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * ***************************** life cycle end *******************************
     */


    private void updateUserInfo() {
        if (NimUIKit.getUserInfoProvider().getUserInfo(account) != null) {
            updateUserInfoView();
            return;
        }
        NimUIKit.getUserInfoProvider().getUserInfoAsync(account, (SimpleCallback<NimUserInfo>) (success, result, code) -> updateUserInfoView());
    }

    private void updateUserInfoView() {
        accountText.setText("帐号：" + account);
        headImageView.loadBuddyAvatar(account);
        if (TextUtils.equals(account, DemoCache.getAccount())) {
            nickText.setText(UserInfoHelper.getUserName(account));
        }
        final NimUserInfo userInfo = (NimUserInfo) NimUIKit.getUserInfoProvider().getUserInfo(account);
        if (userInfo == null) {
            Log.e("MeFragment", "userInfo is null when updateUserInfoView");
            return;
        }

    }

}