package com.penglab.hi5.basic.utils.xpopupExt;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.CenterPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.penglab.hi5.R;

/**
 * Created by Jackiexing on 01/18/21
 */
public class ConfirmPopupViewExt extends CenterPopupView implements View.OnClickListener{
    OnCancelListener cancelListener;
    OnIgnoreListener ignoreListener;
    OnConfirmListener confirmListener;
    TextView tv_title, tv_content, tv_cancel, tv_ignore, tv_confirm;
    CharSequence title, content, hint, cancelText, ignoreText, confirmText;
    EditText et_input;
    View divider1, divider2;
    public boolean isHideCancel = false;

    public static ConfirmPopupViewExt init(Context context, String title, String content, OnConfirmListener onConfirmListener, OnIgnoreListener onIgnoreListener, OnCancelListener onCancelListener){
        return new ConfirmPopupViewExt(context)
                .setListener(onConfirmListener, onIgnoreListener, onCancelListener)
                .setTitleContent(title, content, null);
    }

    private ConfirmPopupViewExt(@NonNull Context context) {
        super(context);
    }

    /**
     * 绑定已有布局
     * @param layoutId 要求布局中必须包含的TextView以及id有：tv_title，tv_content，tv_cancel，tv_confirm
     * @return
     */
    public ConfirmPopupViewExt bindLayout(int layoutId){
        bindLayoutId = layoutId;
        return this;
    }

    // 返回自定义弹窗的布局
    @Override
    protected int getImplLayoutId() {
        return bindLayoutId != 0 ? bindLayoutId : R.layout.xpopup_center_impl_confirm_three_buttons;
    }

    // 执行初始化操作，比如：findView，设置点击，或者任何你弹窗内的业务逻辑
    @Override
    protected void onCreate() {
        super.onCreate();
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_ignore = findViewById(R.id.tv_ignore);
        tv_confirm = findViewById(R.id.tv_confirm);
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
        et_input = findViewById(R.id.et_input);
        divider1 = findViewById(R.id.xpopup_divider1);
        divider2 = findViewById(R.id.xpopup_divider2);

        if(bindLayoutId==0) applyPrimaryColor();

        tv_cancel.setOnClickListener(this);
        tv_ignore.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);

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
        if (!TextUtils.isEmpty(ignoreText)) {
            tv_ignore.setText(ignoreText);
        }
        if (!TextUtils.isEmpty(confirmText)) {
            tv_confirm.setText(confirmText);
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

    public TextView getIgnoreTextView(){
        return findViewById(R.id.tv_ignore);
    }

    public TextView getConfirmTextView(){
        return findViewById(R.id.tv_confirm);
    }
    public ConfirmPopupViewExt setListener(OnConfirmListener confirmListener, OnIgnoreListener ignoreListener, OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
        this.ignoreListener = ignoreListener;
        this.confirmListener = confirmListener;
        return this;
    }

    public ConfirmPopupViewExt setTitleContent(CharSequence title, CharSequence content, CharSequence hint) {
        this.title = title;
        this.content = content;
        this.hint = hint;
        return this;
    }

    public ConfirmPopupViewExt setCancelText(CharSequence cancelText) {
        this.cancelText = cancelText;
        return this;
    }

    public ConfirmPopupViewExt setIgnoreText(CharSequence ignoreText) {
        this.ignoreText = ignoreText;
        return this;
    }

    public ConfirmPopupViewExt setConfirmText(CharSequence confirmText) {
        this.confirmText = confirmText;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_cancel) {
            if (cancelListener != null) cancelListener.onCancel();
            dismiss();
        } else if (v == tv_ignore) {
            if (ignoreListener != null) ignoreListener.onIgnore();
            dismiss();
        } else if (v == tv_confirm) {
            if (confirmListener != null) confirmListener.onConfirm();
            if (popupInfo.autoDismiss) dismiss();
        }
    }

    protected int getMaxWidth() {
        return popupInfo.maxWidth==0 ? super.getMaxWidth() : popupInfo.maxWidth;
    }
}
