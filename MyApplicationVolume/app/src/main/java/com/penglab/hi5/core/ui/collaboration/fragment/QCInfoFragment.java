package com.penglab.hi5.core.ui.collaboration.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.penglab.hi5.R;
import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.core.ui.collaboration.QCMarkerInfo;
import com.penglab.hi5.core.ui.collaboration.adapter.QCMarkerTableAdapter;

import java.util.List;

public class QCInfoFragment extends Fragment {
    private final List<ImageMarker> unCheckedMarkerList;
    private final List<ImageMarker> checkedMarkerList;
    private final QCMarkerTableAdapter.OnMarkerClickListener onMarkerClickListener;

    private RecyclerView recyclerView;
    private QCMarkerTableAdapter adapter;

    public QCInfoFragment(List<ImageMarker> unCheckedMarkerList, List<ImageMarker> checkedMarkerList, QCMarkerTableAdapter.OnMarkerClickListener onMarkerClickListener) {
        this.unCheckedMarkerList = unCheckedMarkerList;
        this.checkedMarkerList = checkedMarkerList;
        this.onMarkerClickListener = onMarkerClickListener;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qc_info, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new QCMarkerTableAdapter(unCheckedMarkerList, checkedMarkerList, onMarkerClickListener);
        recyclerView.setAdapter(adapter);

        TextView title = view.findViewById(R.id.tableTitle);
        title.setText("QC Marker Infos");

        return view;
    }

    // 更新数据的方法
    public void updateData(List<ImageMarker> unCheckedMarkerList, List<ImageMarker> checkedMarkerList) {
        adapter = new QCMarkerTableAdapter(unCheckedMarkerList, checkedMarkerList, onMarkerClickListener);
        recyclerView.setAdapter(adapter);
    }
}

