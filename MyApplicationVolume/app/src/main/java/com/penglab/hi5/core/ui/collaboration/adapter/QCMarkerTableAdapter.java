package com.penglab.hi5.core.ui.collaboration.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.penglab.hi5.R;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.core.ui.ImageClassify.ClassifySolutionInfo;
import com.penglab.hi5.core.ui.ImageClassify.adapter.ClassifySolutionTableAdapter;
import com.penglab.hi5.core.ui.collaboration.QCMarkerInfo;

import java.util.List;

public class QCMarkerTableAdapter extends RecyclerView.Adapter<QCMarkerTableAdapter.TableViewHolder> {
    private final List<ImageMarker> unCheckedMarkerList;
    private final List<ImageMarker> checkedMarkerList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private int selectedPosition = -1;  // 记录当前选中的位置
    private final OnMarkerClickListener onMarkerClickListener;

    public QCMarkerTableAdapter(List<ImageMarker> unCheckedMarkerList, List<ImageMarker> checkedMarkerList, OnMarkerClickListener onMarkerClickListener) {
        this.unCheckedMarkerList = unCheckedMarkerList;
        this.checkedMarkerList = checkedMarkerList;
        this.onMarkerClickListener = onMarkerClickListener;
    }

    public List<ImageMarker> getUnCheckedMarkerList() {
        return unCheckedMarkerList;
    }

    public List<ImageMarker> getCheckedMarkerList() { return checkedMarkerList; }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    public interface OnMarkerClickListener {
        void onMarkerLongClick(int position);
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == TYPE_ITEM) {
            view = inflater.inflate(R.layout.item_qc_marker_info_table_row, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_qc_marker_info_table_header_row, parent, false);
        }
        return new TableViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            if(position - 1 < unCheckedMarkerList.size()){
                holder.bindItem(false, unCheckedMarkerList.get(position - 1));
            }
            else {
                holder.bindItem(true, checkedMarkerList.get(position - 1 - unCheckedMarkerList.size()));
            }
        }
        else{
            holder.bindHeader();
        }

        // 不是header
        if (position >= 1 && position <= unCheckedMarkerList.size()){
            // 设置背景颜色
            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(Color.BLUE);  // 设置选中的项背景为蓝色
                holder.colorTextView.setTextColor(Color.WHITE);
                holder.typeTextView.setTextColor(Color.WHITE);
                holder.stateTextView.setTextColor(Color.WHITE);
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);  // 设置未选中的项背景为透明
                holder.colorTextView.setTextColor(Color.BLACK);
                holder.typeTextView.setTextColor(Color.BLACK);
                holder.stateTextView.setTextColor(Color.BLACK);
            }

            // 设置点击事件
            holder.colorTextView.setOnClickListener(v -> {
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

            // 设置点击事件
            holder.typeTextView.setOnClickListener(v -> {
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

            // 设置点击事件
            holder.stateTextView.setOnClickListener(v -> {
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
            holder.colorTextView.setOnLongClickListener(v -> {
                int clickedPosition = holder.getBindingAdapterPosition();
                if (clickedPosition - 1 < unCheckedMarkerList.size() && clickedPosition - 1 >= 0){
                    // 长按事件触发时，调用接口方法
                    onMarkerClickListener.onMarkerLongClick(clickedPosition - 1);
                }
                return true; // 返回 true 表示事件已被处理
            });

            // 设置长按事件监听器
            holder.typeTextView.setOnLongClickListener(v -> {
                int clickedPosition = holder.getBindingAdapterPosition();
                if (clickedPosition - 1 < unCheckedMarkerList.size() && clickedPosition - 1 >= 0){
                    // 长按事件触发时，调用接口方法
                    onMarkerClickListener.onMarkerLongClick(clickedPosition - 1);
                }
                return true; // 返回 true 表示事件已被处理
            });

            // 设置长按事件监听器
            holder.stateTextView.setOnLongClickListener(v -> {
                int clickedPosition = holder.getBindingAdapterPosition();
                if (clickedPosition - 1 < unCheckedMarkerList.size() && clickedPosition - 1 >= 0){
                    // 长按事件触发时，调用接口方法
                    onMarkerClickListener.onMarkerLongClick(clickedPosition - 1);
                }
                return true; // 返回 true 表示事件已被处理
            });
        }
        else if (position > unCheckedMarkerList.size()) {
            // 设置背景颜色
            holder.itemView.setBackgroundColor(Color.GRAY);
            holder.colorTextView.setTextColor(Color.BLACK);
            holder.typeTextView.setTextColor(Color.BLACK);
            holder.stateTextView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return unCheckedMarkerList.size() + checkedMarkerList.size() + 1;
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView colorColumnView;
        TextView typeColumnView;
        TextView stateColumnView;

        TextView colorTextView;
        TextView typeTextView;
        TextView stateTextView;
        TextView colorView;

        public TableViewHolder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER){
                colorColumnView = itemView.findViewById(R.id.qc_marker_color_column_text_view);
                typeColumnView = itemView.findViewById(R.id.qc_marker_type_column_text_view);
                stateColumnView = itemView.findViewById(R.id.qc_marker_state_column_text_view);
            } else{
                colorTextView = itemView.findViewById(R.id.qc_marker_color_text_view);
                typeTextView = itemView.findViewById(R.id.qc_marker_type_text_view);
                stateTextView = itemView.findViewById(R.id.qc_marker_state_text_view);
                colorView = itemView.findViewById(R.id.qc_marker_color_shape_view);
            }
        }

        @SuppressLint("SetTextI18n")
        public void bindItem(boolean isChecked, ImageMarker marker) {
            // 设置数据
            colorView.setBackgroundColor(Color.parseColor(marker.getColorStr()));
            colorTextView.setText(marker.getColorStr());
            typeTextView.setText(marker.comment);
            if (isChecked) {
                stateTextView.setText("checked");
            }
            else {
                stateTextView.setText("unchecked");
            }
        }

        @SuppressLint("SetTextI18n")
        public void bindHeader() {
            if (colorColumnView != null) {
                colorColumnView.setText("color");
            }
            if (typeTextView != null) {
                typeTextView.setText("type");
            }
            if (stateTextView != null) {
                stateTextView.setText("state");
            }
        }
    }
}
