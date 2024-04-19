package com.penglab.hi5.core.ui.ImageClassify;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penglab.hi5.R;

import java.util.List;

public class ImageClassifyTableAdapter extends RecyclerView.Adapter {
    private List<UserRatingResultInfo> data;
    private Context context;

    public ImageClassifyTableAdapter(List<UserRatingResultInfo> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_table_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView imageNameTextView, ratingTextView, descriptionTextView, uploadTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageNameTextView = itemView.findViewById(R.id.image_name_text_view);
            ratingTextView = itemView.findViewById(R.id.rating_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            uploadTimeTextView = itemView.findViewById(R.id.upload_time_text_view);

            // 设置边框
            itemView.setBackgroundResource(R.drawable.table_border);
        }

        public void bind(UserRatingResultInfo resultInfo) {
            // 设置数据
            imageNameTextView.setText(resultInfo.imageName);
            ratingTextView.setText(resultInfo.ratingEnum);
            descriptionTextView.setText(resultInfo.additionalRatingDescription);
            uploadTimeTextView.setText(resultInfo.uploadTime);

            // 设置标题加粗
            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
            imageNameTextView.setTypeface(boldTypeface);
            ratingTextView.setTypeface(boldTypeface);
            descriptionTextView.setTypeface(boldTypeface);
            uploadTimeTextView.setTypeface(boldTypeface);
        }
    }


}
