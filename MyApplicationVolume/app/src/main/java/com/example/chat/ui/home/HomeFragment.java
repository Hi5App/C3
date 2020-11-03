package com.example.chat.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.ChatManager;
import com.example.chat.chatlist.ContactAdapter;
import com.example.chat.chatlist.DividerItemDecoration;
import com.example.chat.chatlist.LetterView;
import com.example.myapplication__volume.Myapplication;
import com.example.myapplication__volume.R;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private RecyclerView contactList;
    private String[] contactNames;
    private LinearLayoutManager layoutManager;
    private LetterView letterView;
    private ContactAdapter adapter;
    private ChatManager mChatManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.chat_view, container, false);
        mChatManager = Myapplication.the().getChatManager();
        String friendslist = mChatManager.queryFriends();
        contactNames = friendslist.split(";");

//        contactNames = new String[] {"朱一行", "邢飞", "赵轩", "黄磊", "郭妍妍", "薛杰", "刘健", "叶想桥", "熊烽", "成诗琪", "员之曦", "钟烨", "刘迪", "袁京洲", "李莹鑫", "$01", "*100", "zyh"};
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

        return root;
    }
}