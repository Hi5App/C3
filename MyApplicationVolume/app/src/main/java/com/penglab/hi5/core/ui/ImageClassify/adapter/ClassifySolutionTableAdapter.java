package com.penglab.hi5.core.ui.ImageClassify.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.ImageClassify.ClassifySolutionInfo;
import com.penglab.hi5.core.ui.ImageClassify.UserRatingResultInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassifySolutionTableAdapter extends RecyclerView.Adapter<ClassifySolutionTableAdapter.ViewHolder> {
    private final List<ClassifySolutionInfo> data;
    private int selectedPosition = -1;  // 记录当前选中的位置
    private final Context context;
    private final OnSolutionClickListener onSolutionClickListener;

    public ClassifySolutionTableAdapter(Context context, List<ClassifySolutionInfo> solutionData, OnSolutionClickListener onSolutionClickListener) {
        // 将 HashMap 转换为列表
        this.data = solutionData;
        this.context = context;
        this.onSolutionClickListener = onSolutionClickListener;
    }

    public List<ClassifySolutionInfo> getSolutions(){
        return data;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public interface OnSolutionClickListener {
        void onSolutionLongClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null){
            throw new IllegalStateException("Context must not be null");
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        view = inflater.inflate(R.layout.item_solution_table_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassifySolutionInfo info = data.get(position); // 注意索引变化
        holder.bind(info);

        // 设置背景颜色
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.BLUE);  // 设置选中的项背景为蓝色
            holder.solutionNameTextView.setTextColor(Color.WHITE);
            holder.solutionDetailTextView.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);  // 设置未选中的项背景为透明
            holder.solutionNameTextView.setTextColor(Color.BLACK);
            holder.solutionDetailTextView.setTextColor(Color.BLACK);
        }

        // 设置点击事件
        holder.solutionNameTextView.setOnClickListener(v -> {
            // Use getAdapterPosition to get the correct position
            int clickedPosition = holder.getBindingAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                // Update selected position and notify item changes
                int previousSelected = selectedPosition;
                selectedPosition = clickedPosition;
                notifyItemChanged(previousSelected);
                notifyItemChanged(clickedPosition);
            }
        });

        // 设置长按事件监听器
        holder.solutionNameTextView.setOnLongClickListener(v -> {
            int clickedPosition = holder.getBindingAdapterPosition();
            // 长按事件触发时，调用接口方法
            onSolutionClickListener.onSolutionLongClick(clickedPosition);
            return true; // 返回 true 表示事件已被处理
        });

        // 设置点击事件
        holder.solutionDetailTextView.setOnClickListener(v -> {
            // Use getAdapterPosition to get the correct position
            int clickedPosition = holder.getBindingAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                // Update selected position and notify item changes
                int previousSelected = selectedPosition;
                selectedPosition = clickedPosition;
                notifyItemChanged(previousSelected);
                notifyItemChanged(clickedPosition);
            }
        });

        // 设置长按事件监听器
        holder.solutionDetailTextView.setOnLongClickListener(v -> {
            int clickedPosition = holder.getBindingAdapterPosition();
            // 长按事件触发时，调用接口方法
            onSolutionClickListener.onSolutionLongClick(clickedPosition);
            return true; // 返回 true 表示事件已被处理
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView solutionNameTextView;
        public final TextView solutionDetailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            solutionNameTextView = itemView.findViewById(R.id.solution_name_text_view);
            solutionDetailTextView = itemView.findViewById(R.id.solution_detail_text_view);

            // 设置边框
//            itemView.setBackgroundResource(R.drawable.table_border);
        }
        public void bind(ClassifySolutionInfo info) {
            // 设置数据
            solutionNameTextView.setText(info.solutionName);
            solutionDetailTextView.setText(info.solutionDetail);
        }
    }
}
