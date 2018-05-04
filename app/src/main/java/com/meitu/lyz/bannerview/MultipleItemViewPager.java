package com.meitu.lyz.bannerview;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @author LYZ 2018.05.04
 */
public class MultipleItemViewPager extends ViewPager {

    private static final String TAG = "MultipleItemViewPager";

    private static final float DEFAULT_WIDTH = 276;
    private static final float DEFAULT_HEIGHT = 348;

    private float mRatio = DEFAULT_HEIGHT / DEFAULT_WIDTH;


    //notifyDataSetChanged()时会导致currentItem所在位置变为坐标原点
    private int mLastCurrentItem = 0;
    private int mPagerItemWidth = 0;


    public MultipleItemViewPager(@NonNull Context context) {
        this(context, null);
    }

    public MultipleItemViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            mLastCurrentItem = 0;
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    mLastCurrentItem = getCurrentItem();
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        int mParentWidth = mMeasuredWidth;
        int mParentHeight = mMeasuredHeight;

        //计算ViewPager宽高
        float scale = (mMeasuredHeight * 1f / (mMeasuredWidth));
        if (scale < mRatio) {
            mMeasuredWidth = (int) (mMeasuredHeight / mRatio);
//                    mMeasuredWidth = (int) Math.min(mParentWidth * 0.75, mMeasuredWidth);
            if (mParentWidth * 0.75 < mMeasuredWidth) {
                mMeasuredWidth = (int) (mParentWidth * 0.75);
                mMeasuredHeight = (int) (mMeasuredWidth * mRatio);
            }
        } else {
            mMeasuredWidth *= 0.75;
            mMeasuredHeight = (int) (mMeasuredWidth * mRatio);
        }
        mPagerItemWidth = mMeasuredWidth;
        int paddingStart = (mParentWidth - mMeasuredWidth) / 2;
        int paddingTop = (mParentHeight - mMeasuredHeight) / 2;
        setPadding(paddingStart, paddingTop, paddingStart, paddingTop);

        //生成新的MeasureSpec
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMeasuredHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //模拟点触，触发 mScroller.abortAnimation()，避免滑动未完成导致的偏差
        MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
        onTouchEvent(event);
        event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
        onTouchEvent(event);

        //减去mLastCurrentItem后修正ScrollX的值
        scrollTo(mPagerItemWidth * (getCurrentItem() - mLastCurrentItem), getScrollY());

    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, @Nullable PageTransformer transformer) {
        if (transformer instanceof FixPagerTransformer) {
            ((FixPagerTransformer) transformer).setOpenFixPadding(false);
            super.setPageTransformer(reverseDrawingOrder, transformer);
        } else {
            Log.e(TAG, "PageTransformer must extend FixPageTransformer!");
        }
    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, @Nullable PageTransformer transformer, int pageLayerType) {
        if (transformer instanceof FixPagerTransformer) {
            ((FixPagerTransformer) transformer).setOpenFixPadding(false);
            super.setPageTransformer(reverseDrawingOrder, transformer, pageLayerType);
        } else {
            Log.e(TAG, "PageTransformer must extend FixPageTransformer!");
        }
    }

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
    }
}
