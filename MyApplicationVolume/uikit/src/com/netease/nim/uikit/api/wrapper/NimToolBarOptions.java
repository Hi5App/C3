package com.netease.nim.uikit.api.wrapper;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.activity.ToolBarOptions;

/**
 * Created by hzxuwen on 2016/6/16.
 */
public class NimToolBarOptions extends ToolBarOptions {

    public NimToolBarOptions() {
//        logoId = R.drawable.nim_actionbar_nest_dark_logo;
//        navigateId = R.drawable.nim_actionbar_dark_back_icon;
        navigateId = R.drawable.ic_baseline_keyboard_arrow_left_24;
        isNeedNavigate = true;
    }
}
