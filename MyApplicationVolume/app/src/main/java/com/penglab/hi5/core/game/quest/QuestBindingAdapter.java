package com.penglab.hi5.core.game.quest;

import android.content.Context;

import androidx.databinding.ViewDataBinding;

import com.penglab.hi5.R;
import com.penglab.hi5.core.game.BaseBindingAdapter;
import com.penglab.hi5.databinding.QuestItemBinding;

import java.util.ArrayList;

/**
 * Created by Yihang zhu 12/31/21
 */
public class QuestBindingAdapter extends BaseBindingAdapter<Quest, QuestItemBinding> {

    public QuestBindingAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.quest_item;
    }

    @Override
    protected void onBindItem(QuestItemBinding binding, Quest item) {
        binding.setQuest(item);
    }


}
