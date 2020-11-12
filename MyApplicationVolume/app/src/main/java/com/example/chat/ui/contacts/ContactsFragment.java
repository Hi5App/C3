package com.example.chat.ui.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.ChatManager;
import com.example.chat.chatlist.ContactAdapter;
import com.example.chat.chatlist.DividerItemDecoration;
import com.example.chat.chatlist.LetterView;
import com.example.myapplication__volume.Myapplication;
import com.example.myapplication__volume.R;


public class ContactsFragment extends Fragment {

    private ContactsViewModel contactsViewModel;

    private static RecyclerView contactList;
    private static String[] contactNames = {"default"};
    private LinearLayoutManager layoutManager;
    private static LetterView letterView;
    private static ContactAdapter adapter;
    private static ChatManager mChatManager;
    private static TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root;
        root = inflater.inflate(R.layout.chat_view, container, false);
        textView = root.findViewById(R.id.text_home);
        contactList = (RecyclerView) root.findViewById(R.id.contact_list);
        letterView = (LetterView) root.findViewById(R.id.letter_view);

        layoutManager = new LinearLayoutManager(getContext());
        adapter = new ContactAdapter(getContext(), contactNames);

        contactList.setLayoutManager(layoutManager);
        contactList.addItemDecoration(new DividerItemDecoration(getContext(), com.example.chat.chatlist.DividerItemDecoration.VERTICAL_LIST));
        contactList.setAdapter(adapter);

        letterView.setCharacterListener(new LetterView.CharacterClickListener() {
            @Override
            public void clickCharacter(String character) {
                layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(character),0);
            }

            @Override
            public void clickArrow() {
                layoutManager.scrollToPositionWithOffset(0,0);
            }
        });

        mChatManager = Myapplication.the().getChatManager();
        String friendslist = mChatManager.queryFriends();

        if (friendslist.equals("")){
            textView.setText("No friends yet !");

            textView.setVisibility(View.VISIBLE);
            contactList.setVisibility(View.GONE);
            letterView.setVisibility(View.GONE);
//            root = inflater.inflate(R.layout.fragment_home, container, false);
        }else {
            contactNames = friendslist.split(";");
            Log.e("HomeFragment",friendslist);
            adapter.refreshContact(contactNames);

            textView.setVisibility(View.GONE);
            contactList.setVisibility(View.VISIBLE);
            letterView.setVisibility(View.VISIBLE);

//        contactNames = new String[] {"朱一行", "邢飞", "赵轩", "黄磊", "郭妍妍", "薛杰", "刘健", "叶想桥", "熊烽", "成诗琪", "员之曦", "钟烨", "刘迪", "袁京洲", "李莹鑫", "$01", "*100", "zyh"};
        }

        refresh();

        return root;
    }

    /**
     * refresh the layout view
     */
    public static void refresh(){

        String friendslist = mChatManager.queryFriends();
        if (friendslist.equals("")){
            textView.setText("No friends yet !");

            textView.setVisibility(View.VISIBLE);
            contactList.setVisibility(View.GONE);
            letterView.setVisibility(View.GONE);

        }else {
            Log.e("HomeFragment",friendslist);
            contactNames = friendslist.split(";");
            adapter.refreshContact(contactNames);
            adapter.notifyDataSetChanged();

            textView.setVisibility(View.GONE);
            contactList.setVisibility(View.VISIBLE);
            letterView.setVisibility(View.VISIBLE);


        }

    }



}