package com.penglab.hi5.core.ui.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;

import com.gigamole.infinitecycleviewpager.VerticalInfiniteCycleViewPager;
import com.penglab.hi5.R;
import com.penglab.hi5.core.ui.home.utils.Utils;

import static com.penglab.hi5.core.ui.home.utils.Utils.setupItem;

/**
 * Modified by Jackiexing on 12/09/21.
 */
public class HorizontalPagerAdapter extends PagerAdapter {

    private final Utils.LibraryObject[] LIBRARIES = new Utils.LibraryObject[]{
            new Utils.LibraryObject(
                    R.drawable.ic_design,
                    "Big Image"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_strategy,
                    "Smart Imaging"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_chat_icon,
                    "Chat"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_development,
                    "Help"
            ),
            new Utils.LibraryObject(
                    R.drawable.ic_development,
                    "Check"
            )
    };

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public HorizontalPagerAdapter(final Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return LIBRARIES.length;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;

        view = mLayoutInflater.inflate(R.layout.item, container, false);
        setupItem(view, LIBRARIES[position]);

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
