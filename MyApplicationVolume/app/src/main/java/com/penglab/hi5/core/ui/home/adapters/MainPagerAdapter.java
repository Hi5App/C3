package com.penglab.hi5.core.ui.home.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.penglab.hi5.core.ui.home.screens.HorizontalPagerFragment;

/**
 * Modified by Jackiexing on 12/09/21.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private final static int COUNT = 3;

    public MainPagerAdapter(final FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(final int position) {
        return new HorizontalPagerFragment();
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
