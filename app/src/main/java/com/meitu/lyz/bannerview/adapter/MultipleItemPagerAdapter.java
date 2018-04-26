package com.meitu.lyz.bannerview.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * @author LYZ 2018.04.19
 */
public class MultipleItemPagerAdapter extends PagerAdapter {

    private static final String TAG = "PagerAdapter";
    private List<ImageView> mViews;
    private ViewPager mViewPager;

    public MultipleItemPagerAdapter(List<ImageView> mViews) {
        this.mViews = mViews;

    }

    public MultipleItemPagerAdapter() {
    }

    public MultipleItemPagerAdapter(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void setNewData(List<ImageView> mViews) {
        this.mViews = mViews;
        notifyDataSetChanged();
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    @Override
    public int getCount() {
        return mViews == null ? 0 : mViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public float getPageWidth(int position) {
        return 1f;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: position" + position);
        if (mViews != null && mViews.size() > 0) {
            View view = mViews.get(position);
            view.setTag(position);
            container.addView(view);
            return view;
        } else return null;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.d(TAG, "destroyItem: position " + position);
        container.removeView(((View) object));
    }

    @Override
    public void notifyDataSetChanged() {
        int curItem = mViewPager.getCurrentItem();
        super.notifyDataSetChanged();
        if (mViews != null && curItem < mViews.size())
            mViewPager.setCurrentItem(curItem);
        else mViewPager.setCurrentItem(0);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "text" + position;
    }
}