package com.meitu.lyz.bannerview.widget;

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
 * Created by LYZ on 2018/4/25 0025.
 */
public class MultipleItemViewPager extends ViewPager {

    private static final String TAG = "MultipleItemViewPager";
    private Context mContext;

    private static final int DEFAULT_WIDTH = 276;
    private static final int DEFAULT_HEIGHT = 348;
    private float mScale = DEFAULT_HEIGHT * 1f / DEFAULT_WIDTH;

    //notifyDataSetChanged()时会导致currentItem所在位置变为坐标原点
    private int mLastCurrentItem = 0;

    public MultipleItemViewPager(@NonNull Context context) {
        this(context, null);
    }

    public MultipleItemViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setClipChildren(false);
        setClipToPadding(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //模拟点触，触发 mScroller.abortAnimation()，避免滑动未完成导致的偏差
        MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
        onTouchEvent(event);
        event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
        onTouchEvent(event);

        //计算page实际的宽并scroll至正确的位置
        int realPageWidth = w - getPaddingStart() - getPaddingEnd();
        //减去mLastCurrentItem后修正ScrollX的值
        scrollTo(realPageWidth * (getCurrentItem() - mLastCurrentItem), getScrollY());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "mViewPager onMeasure: ");
        int mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        int mParentWidth = mMeasuredWidth;

        //计算ViewPager宽高
        float scale = (mMeasuredHeight * 1f / (mMeasuredWidth));
        if (scale < mScale) {
            mMeasuredWidth = (int) (mMeasuredHeight / mScale);
//                    mMeasuredWidth = (int) Math.min(mParentWidth * 0.75, mMeasuredWidth);
            if (mParentWidth * 0.75 < mMeasuredWidth) {
                mMeasuredWidth = (int) (mParentWidth * 0.75);
                mMeasuredHeight = (int) (mMeasuredWidth * mScale);
            }
        } else {
            mMeasuredWidth *= 0.75;
            mMeasuredHeight = (int) (mMeasuredWidth * mScale);
        }
        int paddingStart = (mParentWidth - mMeasuredWidth) / 2;
        setPadding(paddingStart, 0, paddingStart, 0);

        //生成新的MeasureSpec
//                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMeasuredWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMeasuredHeight, MeasureSpec.EXACTLY);

        //计算OffscreenPageLimit
        int limit = (int) Math.ceil(mParentWidth * 1f / mMeasuredWidth / 2);
        setOffscreenPageLimit(limit + 1);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }
}
