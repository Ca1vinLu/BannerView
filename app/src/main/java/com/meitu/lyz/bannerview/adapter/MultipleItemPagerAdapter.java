package com.meitu.lyz.bannerview.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author LYZ 2018.04.19
 */
public class MultipleItemPagerAdapter<T extends View> extends PagerAdapter {

    private static final String TAG = "PagerAdapter";
    private List<T> mViews;
    private List<String> mTitles;
    private ViewPager mViewPager;

    public MultipleItemPagerAdapter() {
    }

    public MultipleItemPagerAdapter(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public MultipleItemPagerAdapter(List<T> views, ViewPager viewPager) {
        mViews = views;
        mViewPager = viewPager;
    }

    public MultipleItemPagerAdapter(List<T> views, List<String> titles, ViewPager viewPager) {
        mViews = views;
        mTitles = titles;
        mViewPager = viewPager;
    }


    public void setNewViews(List<T> views) {
        mViews = views;
        notifyDataSetChanged();
    }

    public void setNewViews(List<T> views, List<String> titles) {
        mViews = views;
        mTitles = titles;
        notifyDataSetChanged();
    }


    public void addViews(List<T> addData) {
        mViews.addAll(addData);
        notifyDataSetChanged();
    }

    public void addViews(List<T> addData, List<String> titles) {
        mViews.addAll(addData);
        mTitles.addAll(titles);
        notifyDataSetChanged();
    }

    public void addView(T addData) {
        mViews.add(addData);
        notifyDataSetChanged();
    }

    public void addView(T addData, String title) {
        mViews.add(addData);
        mTitles.add(title);
        notifyDataSetChanged();
    }

    public void removeView(int position) {
        mViews.remove(position);
        if (mTitles != null)
            mTitles.remove(position);
    }


    public List<T> getViews() {
        return mViews;
    }

    public List<String> getTitles() {
        return mTitles;
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
        return mTitles != null ? mTitles.get(position) : null;
    }
}