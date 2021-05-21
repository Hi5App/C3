package com.main.chat.nim.session.viewholder;

import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.chat.ChatActivity;
import com.main.chat.nim.session.extension.InviteAttachment;
import com.main.core.MainActivity;
import com.main.core.R;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

public class MsgViewHolderInvite extends MsgViewHolderBase {

    private InviteAttachment inviteAttachment;
    private TextView textView;
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
        textView = (TextView) view.findViewById(R.id.invite_msg_text);
        imageView = (ImageView) view.findViewById(R.id.invite_msg_image);
    }

    @Override
    public void bindContentView() {
        if (message.getAttachment() == null) {
            return;
        }

        inviteAttachment = (InviteAttachment) message.getAttachment();
        String invitor = inviteAttachment.getInvitor();
        String path = inviteAttachment.getPath();
        String []list = path.split("/");
        String roomName = list[list.length - 1];

        textView.setText(invitor + " is inviting you to join the game in " + roomName);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                textView.setText("000");
                Log.d("MsgViewHolderInvite", "onclick");
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = inviteAttachment.getInvitor() + " " + inviteAttachment.getPath() + " " + inviteAttachment.getSoma();
                Log.d("MsgViewHolderInvite", "Send Empty Message");
//                ChatActivity.chatHandler.sendEmptyMessage(0);
                ChatActivity.chatHandler.sendMessage(msg);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                textView.setText("000");
                Log.d("MsgViewHolderInvite", "onclick");
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = inviteAttachment.getInvitor() + " " + inviteAttachment.getPath() + " " + inviteAttachment.getSoma();
                Log.d("MsgViewHolderInvite", "Send Empty Message");
//                ChatActivity.chatHandler.sendEmptyMessage(0);
                ChatActivity.chatHandler.sendMessage(msg);
            }
        });

    }
}
