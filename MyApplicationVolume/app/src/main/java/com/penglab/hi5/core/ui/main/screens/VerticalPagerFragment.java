package com.penglab.hi5.core.ui.main.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gigamole.infinitecycleviewpager.VerticalInfiniteCycleViewPager;
import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.main.adapters.VerticalPagerAdapter;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class VerticalPagerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vertical, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final VerticalInfiniteCycleViewPager verticalInfiniteCycleViewPager =
                (VerticalInfiniteCycleViewPager) view.findViewById(R.id.vicvp);
        verticalInfiniteCycleViewPager.setAdapter(new VerticalPagerAdapter(getContext()));

        verticalInfiniteCycleViewPager.setScrollDuration(1000);
        verticalInfiniteCycleViewPager.startAutoScroll(true);
    }
}
