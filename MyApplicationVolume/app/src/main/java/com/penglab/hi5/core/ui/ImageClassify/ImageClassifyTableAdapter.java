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

public class ImageClassifyTableAdapter extends RecyclerView.Adapter<ImageClassifyTableAdapter.ViewHolder> {
    private List<UserRatingResultInfo> data;
    private Context context;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private boolean showDetails;

    public interface DataCallback {
        void onTotalCountAvailable(int totalCount);
        void onDetailAvailable(List<UserRatingResultInfo> details);
    }
    private DataCallback dataCallback;

    public ImageClassifyTableAdapter(Context context,List<UserRatingResultInfo> data, DataCallback dataCallback) {
        this.context = context;
        this.data = data;
        this.dataCallback = dataCallback;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
        notifyDataSetChanged();
    }
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_FOOTER; // 总数显示在第一行
        } else {
            return TYPE_ITEM;
        }
    }
    public List<UserRatingResultInfo> getUserRatingResultInfos (){
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
        if (viewType == TYPE_ITEM) {
            view =inflater.inflate(R.layout.item_table_row, parent, false);
        } else {
            view =inflater.inflate(R.layout.item_footer_row, parent, false);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(showDetails) {
            if (getItemViewType(position) == TYPE_FOOTER) {
                holder.bindTotal(data.size()); // 总计行
            } else {
                // 注意这里由于总计行是第一行，数据行的索引需要减1
                UserRatingResultInfo info = data.get(position - 1); // 注意索引变化
                holder.bind(info);
            }
        } else {
            if(position ==0){
                holder.bindTotal(data.size());
            }
        }
    }

    @Override
    public int getItemCount() {
        if(showDetails) {
            return  data.size() +1;
        }else {
            return 1;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView totalTextView;

        private TextView imageNameTextView, ratingTextView, descriptionTextView, uploadTimeTextView;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_FOOTER){
                totalTextView = itemView.findViewById(R.id.total_text_view);
            } else {

            imageNameTextView = itemView.findViewById(R.id.image_name_text_view);
            ratingTextView = itemView.findViewById(R.id.rating_text_view);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
            uploadTimeTextView = itemView.findViewById(R.id.upload_time_text_view);

            // 设置边框
            itemView.setBackgroundResource(R.drawable.table_border);
            }
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
        public void bindTotal(int total) {
            if (totalTextView != null) {
                totalTextView.setText("Total: " + total);
            }
        }
    }
}
