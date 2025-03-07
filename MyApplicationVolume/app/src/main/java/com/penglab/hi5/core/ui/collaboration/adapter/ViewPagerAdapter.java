package com.penglab.hi5.core.ui.collaboration.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.penglab.hi5.basic.image.ImageMarker;
import com.penglab.hi5.core.ui.collaboration.QCMarkerInfo;
import com.penglab.hi5.core.ui.collaboration.fragment.QCInfoFragment;
import com.penglab.hi5.core.ui.collaboration.fragment.QCStatisFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final QCInfoFragment qcInfoFragment;
    private final QCStatisFragment qcStatisFragment;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ImageMarker> unCheckedMarkerList,
                            List<ImageMarker> checkedMarkerList, QCMarkerTableAdapter.OnMarkerClickListener onMarkerClickListener,
                            int removedOverlapSegNum, int removedErrSegNum) {
        super(fragmentActivity);
        qcInfoFragment = new QCInfoFragment(unCheckedMarkerList, checkedMarkerList, onMarkerClickListener);
        qcStatisFragment = new QCStatisFragment(unCheckedMarkerList, removedOverlapSegNum, removedErrSegNum);
    }

    @Override
    public int getItemCount() {
        return 2; // 两页
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return qcInfoFragment;
        } else {
            return qcStatisFragment;
        }
    }

    // 获取指定位置的 Fragment
    public Fragment getFragment(int position) {
        if(position == 0){
            return qcInfoFragment;
        }
        else{
            return qcStatisFragment;
        }
    }

}

