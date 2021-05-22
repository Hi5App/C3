package com.penglab.hi5.core.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.penglab.hi5.R;

import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.ViewHolder> {

    private Context mContext;

    private List<Quest> mQuestList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView questContent;
        Button receiveButton;

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            questContent = (TextView) view.findViewById(R.id.quest_content_text);
            receiveButton = (Button) view.findViewById(R.id.quest_receive_button);
        }
    }

    public QuestAdapter(List<Quest> questList){
        mQuestList = questList;
    }

    @NonNull
    @Override
    public QuestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.quest_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestAdapter.ViewHolder holder, int position) {
        Quest quest = mQuestList.get(position);
        holder.questContent.setText(quest.getContent() + " " + quest.getAlreadyDone() + "/" + quest.getToBeDone());
        if (quest.getStatus() == Quest.Status.UnFinished){
            holder.receiveButton.setEnabled(false);
            holder.receiveButton.setText(Integer.toString(quest.getReward()));
        }
        if (quest.getStatus() == Quest.Status.Pending) {
            holder.receiveButton.setEnabled(true);
            holder.receiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Score score = Score.getInstance();
                    score.addScore(quest.getReward());
                    holder.receiveButton.setEnabled(false);
                    holder.receiveButton.setText("√");
                    quest.setStatus(Quest.Status.Finished);

                    DailyQuestsContainer dailyQuestsContainer = DailyQuestsContainer.getInstance();
                    dailyQuestsContainer.updateNDailyQuest(position, Quest.Status.Finished);

                }
            });
            holder.receiveButton.setText(Integer.toString(quest.getReward()));
        }
        else if (quest.getStatus() == Quest.Status.Finished) {
            holder.receiveButton.setEnabled(false);
            holder.receiveButton.setText("√");
        }
    }

    @Override
    public int getItemCount() {
        return mQuestList.size();
    }
}
