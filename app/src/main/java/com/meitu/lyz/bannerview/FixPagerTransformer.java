package com.meitu.lyz.bannerview;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author LYZ 2018.04.27
 */
public abstract class FixPagerTransformer implements ViewPager.PageTransformer {

    private static final String TAG = "FixPagerTransformer";

    private int mPagerItemWidth;
    private int mLastCurrentItem;

    @Override
    public void transformPage(@NonNull View page, float position) {
        ViewPager mViewPager;
        if (page.getParent() instanceof ViewPager)
            mViewPager = ((ViewPager) page.getParent());
        else return;

        mViewPager.getCurrentItem();
        //notifyDataSetChanged()后，此时requestLayout()还未执行，position均为0，进行修正
        if (position == 0 && page.getTag() instanceof Integer) {
            int curItem = mViewPager.getCurrentItem();
            int viewPos = (Integer) page.getTag();
            position += (viewPos - curItem);
        } else if (isFixedRatio()) {
            final float mClientWidth = mViewPager.getMeasuredWidth() -
                    mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
            float positionFixer = mViewPager.getPaddingStart() / mClientWidth;
            position -= positionFixer;
        }
        fixTransformPage(page, position);
    }

    abstract void fixTransformPage(@NonNull View page, float position);

    public boolean isFixedRatio() {
        return false;
    }


    public double getFixedRatio() {
        return 0;
    }


    public boolean bindViewPager(final ViewPager viewPager) {
        if (viewPager.getAdapter() == null) {
            Log.e(TAG, "ViewPager haven't set adapter yet!");
            return false;
        }

        PagerAdapter adapter = viewPager.getAdapter();
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mLastCurrentItem = viewPager.getCurrentItem();
            }
        });

        if (isFixedRatio()) {
            viewPager.setClipToPadding(false);
            viewPager.setClipChildren(false);
            setPagerPadding(viewPager, viewPager.getWidth(), viewPager.getHeight());
        }


        viewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left == oldLeft && top == oldTop && right == oldRight && bottom == oldBottom)
                    return;

                ViewPager vp = ((ViewPager) v);

                if (isFixedRatio()) {
                    setPagerPadding(vp, right - left, bottom - top);
                }

                //模拟点触，触发 mScroller.abortAnimation()，避免滑动未完成导致的偏差
                MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
                vp.onTouchEvent(event);
                event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
                vp.onTouchEvent(event);

                //减去mLastCurrentItem后修正ScrollX的值
                vp.scrollTo(mPagerItemWidth * (vp.getCurrentItem() - mLastCurrentItem), vp.getScrollY());

            }
        });


        return true;
    }

    private void setPagerPadding(ViewPager viewPager, int w, int h) {
        int mMeasuredWidth = w;
        int mMeasuredHeight = (int) ((h) * 0.95);
        int mParentWidth = mMeasuredWidth;
        int mParentHeight = mMeasuredHeight;

        //计算ViewPager宽高
        float scale = (mMeasuredHeight * 1f / (mMeasuredWidth));
        if (scale < getFixedRatio()) {
            mMeasuredWidth = (int) (mMeasuredHeight / getFixedRatio());
//                    mMeasuredWidth = (int) Math.min(mParentWidth * 0.75, mMeasuredWidth);
            if (mParentWidth * 0.75 < mMeasuredWidth) {
                mMeasuredWidth = (int) (mParentWidth * 0.75);
                mMeasuredHeight = (int) (mMeasuredWidth * getFixedRatio());
            }
        } else {
            mMeasuredWidth *= 0.75;
            mMeasuredHeight = (int) (mMeasuredWidth * getFixedRatio());
        }
        mPagerItemWidth = mMeasuredWidth;
        int paddingStart = (mParentWidth - mMeasuredWidth) / 2;
        int paddingTop = (h - mMeasuredHeight) / 2;
        viewPager.setPadding(paddingStart, paddingTop, paddingStart, paddingTop);


//        viewPager.requestLayout();
//        viewPager.measure(View.MeasureSpec.makeMeasureSpec(mMeasuredWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(mMeasuredHeight, View.MeasureSpec.EXACTLY));
    }
}
