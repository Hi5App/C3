package com.penglab.hi5.core.ui.collaboration;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lxj.xpopup.core.CenterPopupView;
import com.penglab.hi5.core.ui.collaboration.adapter.CustomListAdapter;

import java.util.List;
import com.penglab.hi5.R;

public class CustomCenterListPopupView extends CenterPopupView {
    private String title;
    private List<String> data;
    private CustomListAdapter.OnItemClickListener onItemClickListener;

    public CustomCenterListPopupView(Context context, String title, List<String> data, CustomListAdapter.OnItemClickListener listener) {
        super(context);
        this.title = title;
        this.data = data;
        this.onItemClickListener = listener;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_custom_list; // 自定义的弹窗布局文件
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        // 设置标题
        TextView tvTitle = findViewById(R.id.tv_popup_title);
        tvTitle.setText(title);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CustomListAdapter(data, (position, text) -> {
            onItemClickListener.onItemClick(position, text);
            // 关闭弹窗
            dismiss();
        }));

        // 添加分割线
        int dividerColor = ContextCompat.getColor(getContext(), R.color.divider_color); // 使用 ContextCompat 获取颜色
        recyclerView.addItemDecoration(new DividerItemDecoration(dividerColor, 2));

//        // 设置弹窗背景颜色为白色
//        View contentView = (View) findViewById(R.id.recyclerView).getParent();
//        contentView.setBackgroundColor(Color.WHITE);  // 将父布局背景设置为白色
    }
}
