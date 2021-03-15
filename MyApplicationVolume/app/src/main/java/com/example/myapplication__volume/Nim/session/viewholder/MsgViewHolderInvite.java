package com.example.myapplication__volume.Nim.session.viewholder;

import android.widget.ImageView;

import com.example.myapplication__volume.Nim.session.extension.GuessAttachment;
import com.example.myapplication__volume.Nim.session.extension.InviteAttachment;
import com.example.myapplication__volume.R;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

public class MsgViewHolderInvite extends MsgViewHolderBase {

    private InviteAttachment inviteAttachment;
    private ImageView imageView;

    public MsgViewHolderInvite(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.invite_msg;
    }

    @Override
    public void inflateContentView() {
        imageView = (ImageView) view.findViewById(R.id.invite_msg_text);
    }

    @Override
    public void bindContentView() {
        if (message.getAttachment() == null) {
            return;
        }

        imageView.setImageResource(R.drawable.ic_baseline_emoji_people_24);

    }
}
