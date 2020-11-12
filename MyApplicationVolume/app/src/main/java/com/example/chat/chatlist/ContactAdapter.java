package com.example.chat.chatlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.ChatManager;
import com.example.chat.MessageActivity;
import com.example.chat.MessageUtil;
import com.example.myapplication__volume.Myapplication;
import com.example.myapplication__volume.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String[] mContactNames; // 联系人名称字符串数组
    private List<String> mContactList; // 联系人名称List（转换成拼音）
    private List<Contact> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List
    private ChatManager mChatManager;

    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public ContactAdapter(Context context, String[] contactNames) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mContactNames = contactNames;
        mChatManager = Myapplication.the().getChatManager();

        handleContact();
    }

    private void handleContact() {
        mContactList = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < mContactNames.length; i++) {
            String pinyin = Utils.getPingYin(mContactNames[i]);
            map.put(pinyin, mContactNames[i]);
            mContactList.add(pinyin);
        }
        Collections.sort(mContactList, new ContactComparator());

        resultList = new ArrayList<>();
        characterList = new ArrayList<>();

        for (int i = 0; i < mContactList.size(); i++) {
            String name = mContactList.get(i);
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character, ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#", ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }

            resultList.add(new Contact(map.get(name), ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character, parent, false));
        } else {
            final RecyclerView.ViewHolder holder = new ContactHolder(mLayoutInflater.inflate(R.layout.item_contact, parent, false));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    Contact contact = resultList.get(position);
                    startMsgPeerToPeer(contact.getmName());
                }
            });
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position).getmName());
        } else if (holder instanceof ContactHolder) {
            ((ContactHolder) holder).mTextView.setText(resultList.get(position).getmName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return resultList.get(position).getmType();
    }

    @Override
    public int getItemCount() {
        return resultList == null ? 0 : resultList.size();
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        CharacterHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.character);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        ContactHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.contact_name);
        }
    }

    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getmName().equals(character)) {
                    return i;
                }
            }
        }

        return -1; // -1不会滑动
    }


    private void startMsgPeerToPeer(String peer){
        Log.d("PeerToPeer", "Start To Chat");
        if (peer.equals("")) {
            Toast_in_Thread(mContext.getString(R.string.account_empty));
        } else if (peer.length() >= MessageUtil.MAX_INPUT_NAME_LENGTH) {
            Toast_in_Thread(mContext.getString(R.string.account_too_long));
        } else if (peer.startsWith(" ")) {
            Toast_in_Thread(mContext.getString(R.string.account_starts_with_space));
        } else if (peer.equals("null")) {
            Toast_in_Thread(mContext.getString(R.string.account_literal_null));
        } else if (peer.equals(mChatManager.getUsername())) {
            Toast_in_Thread(mContext.getString(R.string.account_cannot_be_yourself));
        } else {
            openMessageActivity(true, peer);
        }
    }


    private void openMessageActivity(boolean isPeerToPeerMode, String targetName){
        Intent intent = new Intent(mContext, MessageActivity.class);
        intent.putExtra(MessageUtil.INTENT_EXTRA_IS_PEER_MODE, isPeerToPeerMode);
        intent.putExtra(MessageUtil.INTENT_EXTRA_TARGET_NAME, targetName);
        intent.putExtra(MessageUtil.INTENT_EXTRA_USER_ID, mChatManager.getUsername());
//        intent.putExtra(MessageUtil.INTENT_EXTRA_MESSAGE_LIST, messageMap.get(targetName));
        mContext.startActivity(intent);
    }


    public void refreshContact(String[] contactNames){
        mContactNames = contactNames;
        Log.e("ContactsAdapter", Arrays.toString(contactNames));
        Log.e("ContactsAdapter", Arrays.toString(mContactNames));
        handleContact();
    }

    public void Toast_in_Thread(String message){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}