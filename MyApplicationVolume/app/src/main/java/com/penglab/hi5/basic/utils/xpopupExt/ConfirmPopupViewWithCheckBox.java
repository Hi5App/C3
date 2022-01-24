package com.penglab.hi5.basic.utils.xpopupExt;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.penglab.hi5.R;

public class ConfirmPopupViewWithCheckBox extends CenterPopupView implements View.OnClickListener {
    OnCancelListener cancelListener;
    OnConfirmListener confirmListener;
    OnCheckListener checkListener;
    TextView tv_title, tv_content, tv_cancel, tv_confirm;
    CharSequence title, content, hint, cancelText, confirmText, optionText;
    EditText et_input;
    CheckBox cb_option;
    View divider1, divider2;
    public boolean isHideCancel = false;

    public static ConfirmPopupViewWithCheckBox init(Context context, String title, String content, OnConfirmListener onConfirmListener, OnCancelListener onCancelListener, OnCheckListener onCheckListener){
        return new ConfirmPopupViewWithCheckBox(context)
                .setListener(onConfirmListener, onCancelListener, onCheckListener)
                .setTitleContent(title, content, null);
    }

    private ConfirmPopupViewWithCheckBox(@NonNull Context context) {
        super(context);
    }

    /**
     * 绑定已有布局
     * @param layoutId 要求布局中必须包含的TextView以及id有：tv_title，tv_content，tv_cancel，tv_confirm
     * @return
     */
    public ConfirmPopupViewWithCheckBox bindLayout(int layoutId){
        bindLayoutId = layoutId;
        return this;
    }

    // 返回自定义弹窗的布局
    @Override
    protected int getImplLayoutId() {
        return bindLayoutId != 0 ? bindLayoutId : R.layout.xpopup_center_impl_confirm_with_checkbox;
    }

    // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
    @Override
    protected void onCreate() {
        super.onCreate();
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
        cb_option = findViewById(R.id.cb_option);
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
        et_input = findViewById(R.id.et_input);
        divider1 = findViewById(R.id.xpopup_divider1);
        divider2 = findViewById(R.id.xpopup_divider2);

        if(bindLayoutId==0) applyPrimaryColor();

        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        cb_option.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            tv_content.setText(content);
        }else {
            tv_content.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            tv_cancel.setText(cancelText);
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
        }
        if (!TextUtils.isEmpty(optionText)) {
            cb_option.setText(optionText);
        }
        if (isHideCancel) {
            tv_cancel.setVisibility(GONE);
            if(divider2!=null) divider2.setVisibility(GONE);
        }
        if(bindItemLayoutId==0 && popupInfo.isDarkTheme){
            applyDarkTheme();
        }
    }

    protected void applyPrimaryColor() {
        if(bindItemLayoutId==0){
            tv_confirm.setTextColor(XPopup.getPrimaryColor());
        }
    }

    @Override
    protected void applyDarkTheme() {
        super.applyDarkTheme();
        tv_title.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        tv_content.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        tv_cancel.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        tv_confirm.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        cb_option.setTextColor(getResources().getColor(R.color._xpopup_white_color));
        findViewById(R.id.xpopup_divider).setBackgroundColor(getResources().getColor(R.color._xpopup_dark_color));
        findViewById(R.id.xpopup_divider_h).setBackgroundColor(getResources().getColor(R.color._xpopup_dark_color));
        ((ViewGroup)tv_title.getParent()).setBackgroundResource(R.drawable._xpopup_round3_dark_bg);
    }

    public TextView getTitleTextView(){
        return findViewById(R.id.tv_title);
    }

    public TextView getContentTextView(){
        return findViewById(R.id.tv_content);
    }

    public TextView getCancelTextView(){
        return findViewById(R.id.tv_cancel);
    }

    public TextView getConfirmTextView(){
        return findViewById(R.id.tv_confirm);
    }

    public CheckBox getOptionCheckBox(){
        return findViewById(R.id.cb_option);
    }

    public ConfirmPopupViewWithCheckBox setListener(OnConfirmListener confirmListener, OnCancelListener cancelListener, OnCheckListener checkListener) {
        this.cancelListener = cancelListener;
        this.confirmListener = confirmListener;
        this.checkListener = checkListener;
        return this;
    }

    public ConfirmPopupViewWithCheckBox setTitleContent(CharSequence title, CharSequence content, CharSequence hint) {
        this.title = title;
        this.content = content;
        this.hint = hint;
        return this;
    }

    public ConfirmPopupViewWithCheckBox setCancelText(CharSequence cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public ConfirmPopupViewWithCheckBox setOptionText(CharSequence optionText) {
        this.optionText = optionText;
        return this;
    }

    public ConfirmPopupViewWithCheckBox setConfirmText(CharSequence confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    @Override
    public void onClick(View v) {
        Log.e("onClick","viewId: " + v.getId());
        if (v == tv_cancel) {
            if (cancelListener != null) cancelListener.onCancel();
            dismiss();
        } else if (v == tv_confirm) {
            if (confirmListener != null) confirmListener.onConfirm();
            if (popupInfo.autoDismiss) dismiss();
        } else if (v == cb_option) {
            if (checkListener != null) checkListener.onChecked(v);
        }
    }

    protected int getMaxWidth() {
        return popupInfo.maxWidth==0 ? super.getMaxWidth() : popupInfo.maxWidth;
    }
}
