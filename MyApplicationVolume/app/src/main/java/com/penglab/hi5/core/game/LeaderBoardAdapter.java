package com.penglab.hi5.core.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.penglab.hi5.R;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.List;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {
    private Context mContext;

    private List<LeaderBoardItem> leaderBoardItemList;

    public LeaderBoardAdapter(List<LeaderBoardItem> leaderBoardItems){
        leaderBoardItemList = leaderBoardItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderBoardItem leaderBoardItem = leaderBoardItemList.get(position);
        holder.ranking.setText(Integer.toString(position + 1));
        holder.headImageView.loadBuddyAvatar(leaderBoardItem.getAccount());
        holder.nickname.setText(leaderBoardItem.getNickname());
        holder.score.setText(Integer.toString(leaderBoardItem.getScore()));
    }

    @Override
    public int getItemCount() {
        return leaderBoardItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView ranking;
        HeadImageView headImageView;
        TextView nickname;
        TextView score;

        public ViewHolder(@NonNull View view) {
            super(view);
            cardView = (CardView) view;
            ranking = (TextView) view.findViewById(R.id.ranking_leaderboard);
            headImageView = (HeadImageView)view.findViewById(R.id.head_image_leaderboard);
            nickname = (TextView)view.findViewById(R.id.nickname_leaderboard);
            score = (TextView)view.findViewById(R.id.score_leaderboard);
        }
    }
}
