package com.example.chat.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.basic.ChatHelpUtils;
import com.example.chat.model.MessageBean;
import com.example.myapplication__volume.R;

import java.util.List;

import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmMessageType;



public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<MessageBean> messageBeanList;
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    public MessageAdapter(Context context, List<MessageBean> messageBeanList, @NonNull OnItemClickListener listener) {
        this.inflater = ((Activity) context).getLayoutInflater();
        this.messageBeanList = messageBeanList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.msg_item_layout, parent, false);
        View view = inflater.inflate(R.layout.item_msg_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        setupView(holder, position);
    }

    @Override
    public int getItemCount() {
        return messageBeanList.size();
    }

    private void setupView(MyViewHolder holder, int position) {
        MessageBean currentMsgData = messageBeanList.get(position);
        MessageBean preMsgData = null;

        if (position >= 1)
            preMsgData = messageBeanList.get(position - 1);

        if (currentMsgData.isBeSelf()) {
            holder.textViewSelfName.setText(currentMsgData.getAccount());
        } else {
            holder.textViewOtherName.setText(currentMsgData.getAccount());
            if (currentMsgData.getBackground() != 0) {
                holder.textViewOtherName.setBackgroundResource(currentMsgData.getBackground());
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(currentMsgData);
        });

        RtmMessage rtmMessage = currentMsgData.getMessage();
        switch (rtmMessage.getMessageType()) {
            case RtmMessageType.TEXT:
                if (currentMsgData.isBeSelf()) {
                    initTimeStamp(holder, currentMsgData, preMsgData);
                    holder.sendMsg.setVisibility(View.VISIBLE);
                    holder.sendMsg.setText(rtmMessage.getText());
                } else {
                    initTimeStamp(holder, currentMsgData, preMsgData);
                    holder.receiveMsg.setVisibility(View.VISIBLE);
                    holder.receiveMsg.setText(rtmMessage.getText());
                }

                holder.imageViewSelfImg.setVisibility(View.GONE);
                holder.imageViewOtherImg.setVisibility(View.GONE);
                break;
            case RtmMessageType.IMAGE:
                RtmImageMessage rtmImageMessage = (RtmImageMessage) rtmMessage;
                RequestBuilder<Drawable> builder = Glide.with(holder.itemView)
                        .load(rtmImageMessage.getThumbnail())
                        .override(rtmImageMessage.getThumbnailWidth(), rtmImageMessage.getThumbnailHeight());
                if (currentMsgData.isBeSelf()) {
                    holder.imageViewSelfImg.setVisibility(View.VISIBLE);
                    builder.into(holder.imageViewSelfImg);
                } else {
                    holder.imageViewOtherImg.setVisibility(View.VISIBLE);
                    builder.into(holder.imageViewOtherImg);
                }

                holder.sendMsg.setVisibility(View.GONE);
                holder.receiveMsg.setVisibility(View.GONE);
                break;
        }

        holder.senderLayout.setVisibility(currentMsgData.isBeSelf() ? View.VISIBLE : View.GONE);
        holder.receiverLayout.setVisibility(currentMsgData.isBeSelf() ? View.GONE : View.VISIBLE);
    }

    private void initTimeStamp(MyViewHolder holder, MessageBean currentMsgData, MessageBean preMsgData) {
        String showTime;
        if (preMsgData == null) {
            showTime = ChatHelpUtils.calculateShowTime(ChatHelpUtils.getCurrentMillisTime(), currentMsgData.getTimeStamp());
        } else {
            showTime = ChatHelpUtils.calculateShowTime(currentMsgData.getTimeStamp(), preMsgData.getTimeStamp());
        }
        if (showTime != null) {
            holder.timeStamp.setVisibility(View.VISIBLE);
            holder.timeStamp.setText(showTime);
        } else {
            holder.timeStamp.setVisibility(View.GONE);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(MessageBean message);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
//        private TextView textViewOtherName;
//        private TextView textViewOtherMsg;
//        private ImageView imageViewOtherImg;
//        private TextView textViewSelfName;
//        private TextView textViewSelfMsg;
//        private ImageView imageViewSelfImg;
//        private RelativeLayout layoutLeft;
//        private RelativeLayout layoutRight;

        ImageView imageViewOtherImg, imageViewSelfImg;
        TextView timeStamp, receiveMsg, sendMsg, textViewOtherName, textViewSelfName;
        RelativeLayout senderLayout;
        LinearLayout receiverLayout;

        @SuppressLint("CutPasteId")
        MyViewHolder(View itemView) {
            super(itemView);
            textViewOtherName =  itemView.findViewById(R.id.item_name_receiver);
            textViewSelfName =  itemView.findViewById(R.id.item_name_sender);
            timeStamp =  itemView.findViewById(R.id.item_wechat_msg_iv_time_stamp);
            receiveMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_receiver_msg);
            sendMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_sender_msg);
            imageViewOtherImg =  itemView.findViewById(R.id.item_img_receiver);
            imageViewSelfImg =  itemView.findViewById(R.id.item_img_sender);
            senderLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_sender);
            receiverLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_receiver);
        }

//        MyViewHolder(View itemView) {
//            super(itemView);
//
//            textViewOtherName = itemView.findViewById(R.id.item_name_l);
//            textViewOtherMsg = itemView.findViewById(R.id.item_msg_l);
//            imageViewOtherImg = itemView.findViewById(R.id.item_img_l);
//            textViewSelfName = itemView.findViewById(R.id.item_name_r);
//            textViewSelfMsg = itemView.findViewById(R.id.item_msg_r);
//            imageViewSelfImg = itemView.findViewById(R.id.item_img_r);
//            layoutLeft = itemView.findViewById(R.id.item_layout_l);
//            layoutRight = itemView.findViewById(R.id.item_layout_r);
//        }
    }
}
