package com.penglab.hi5.core.ui.ImageClassify.adapter;

import android.content.Context;
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
    private final List<Map.Entry<String, String>> data;
    private final Context context;

    public ClassifySolutionTableAdapter(Context context, Map<String, String> solutionData) {
        // 将 HashMap 转换为列表
        data = new ArrayList<>(solutionData.entrySet());
        this.context = context;
    }

    public List<Map.Entry<String, String>> getSolutions(){
        return data;
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
        Map.Entry<String, String> entry = data.get(position); // 注意索引变化
        ClassifySolutionInfo info = new ClassifySolutionInfo();
        info.solutionName = entry.getKey();
        info.solutionDetail = entry.getValue();
        holder.bind(info);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView solutionNameTextView;
        private final TextView solutionDetailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            solutionNameTextView = itemView.findViewById(R.id.solution_name_text_view);
            solutionDetailTextView = itemView.findViewById(R.id.solution_detail_text_view);

            // 设置边框
            itemView.setBackgroundResource(R.drawable.table_border);
        }
        public void bind(ClassifySolutionInfo info) {
            // 设置数据
            solutionNameTextView.setText(info.solutionName);
            solutionDetailTextView.setText(info.solutionDetail);
        }
    }
}
