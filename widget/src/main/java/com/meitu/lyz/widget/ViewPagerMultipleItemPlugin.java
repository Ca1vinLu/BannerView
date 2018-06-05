package com.meitu.lyz.widget;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;

/**
 * 帮助计算有多个item时ViewPager的padding，及处理ViewPager onSizeChange时位置修正的辅助插件类
 *
 * @author LYZ 2018.05.17
 */
public class ViewPagerMultipleItemPlugin {
    private ViewPager mViewPager;

    //刷新前Item的位置，因为notifyDataSetChanged()时会导致currentItem所在位置变为坐标原点
    private int mLastSelectedItem = 0;

    //ViewPager子页的宽度
    private int mPagerItemWidth;


    //item高宽比
    private float mRatio = -1;

    private static final float DEFAULT_WIDTH_PROPORTION = 0.75F;
    private static final float DEFAULT_HEIGHT_PROPORTION = 1F;

    //item最大宽占比
    private float mWidthProportion = DEFAULT_WIDTH_PROPORTION;
    //item最大高占比
    private float mHeightProportion = DEFAULT_HEIGHT_PROPORTION;

    //是否开启Padding的计算修正
    private boolean mOpenSetPadding = true;


    //DataSetObserver监听ViewPager刷新并记录刷新前Item的位置
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            mLastSelectedItem = mViewPager.getCurrentItem();
        }
    };

    public ViewPagerMultipleItemPlugin() {
    }

    public ViewPagerMultipleItemPlugin(ViewPager viewPager) {
        bindViewPager(viewPager);
    }

    public void bindViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        if (mViewPager.getAdapter() != null) {
            mViewPager.getAdapter().registerDataSetObserver(mDataSetObserver);
        }
        mViewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
            @Override
            public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
                if (newAdapter != null) {
                    newAdapter.registerDataSetObserver(mDataSetObserver);
                }
            }
        });

        mViewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, final int left, final int top, final int right, final int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left == oldLeft && top == oldTop && right == oldRight && bottom == oldBottom) {
                    return;
                }

                final ViewPager vp = ((ViewPager) v);

                if (isOpenSetPadding() && !(vp instanceof MultipleItemViewPager)) {
                    final int width = right - left;
                    final int height = bottom - top;
                    vp.post(new Runnable() {
                        @Override
                        public void run() {
                            //设置ViewPager的padding来显示多个Item
                            calculatePadding(width, height);
                            onSizeChange();
                            int mVisibleOffsetNum = (int) (Math.ceil((vp.getWidth() - mPagerItemWidth) / 2f / mPagerItemWidth));
                            if (mVisibleOffsetNum > mViewPager.getOffscreenPageLimit()) {
                                mViewPager.setOffscreenPageLimit(mVisibleOffsetNum);
                            }

                        }
                    });
                }


            }
        });
    }

    /**
     * 计算ViewPager的Padding使其能显示多个Item
     *
     * @param vpWidth  宽度
     * @param vpHeight 高度
     * @return Item宽度
     */
    public int calculatePadding(int vpWidth, int vpHeight) {
        if (vpWidth == 0 || vpHeight == 0)
            return 0;

        int mMeasuredWidth = vpWidth;
        int mMeasuredHeight = vpHeight;
        int mParentWidth = mMeasuredWidth;
        int mParentHeight = mMeasuredHeight;

        //计算ViewPager宽高
        float scale = (mMeasuredHeight * 1f / (mMeasuredWidth));


        if (getRatio() > 0) {//若设置了固定比例
            if (scale < getRatio()) {//对宽进行调整
                mMeasuredWidth = (int) (mMeasuredHeight / getRatio());

            } else {//对高进行调整
                mMeasuredWidth *= getWidthProportion();
                mMeasuredHeight = (int) (mMeasuredWidth * getRatio());

            }

            //若调整后的高度过大，则进行二次修正
            if (mParentHeight * getHeightProportion() < mMeasuredHeight) {
                mMeasuredHeight = (int) (mMeasuredHeight * getHeightProportion());
                mMeasuredWidth = (int) (mMeasuredHeight / getRatio());
            }

            //若调整后的宽度过大，则进行二次修正
            if (mParentWidth * getWidthProportion() < mMeasuredWidth) {
                mMeasuredWidth = (int) (mParentWidth * getWidthProportion());
                mMeasuredHeight = (int) (mMeasuredWidth * getRatio());
            }
        } else {//未设置固定比例则按默认宽高占比裁剪
            mMeasuredWidth = (int) (mParentWidth * getWidthProportion());
            mMeasuredHeight = (int) (mMeasuredHeight * getHeightProportion());
        }

        //计算padding
        int paddingStart = (mParentWidth - mMeasuredWidth) / 2;
        int paddingTop = (mParentHeight - mMeasuredHeight) / 2;
        mViewPager.setPadding(paddingStart, paddingTop, paddingStart, paddingTop);

        mPagerItemWidth = mMeasuredWidth;
        return mPagerItemWidth;
    }


    /**
     * 当ViewPager大小变化时调用此函数进行修正
     */
    public void onSizeChange() {
        //模拟点触，触发 mScroller.abortAnimation()，避免滑动未完成导致的偏差
        MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
        mViewPager.onTouchEvent(event);
        event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
        mViewPager.onTouchEvent(event);

        //减去mLastCurrentItem后修正ScrollX的值
        mViewPager.scrollTo(mPagerItemWidth * (mViewPager.getCurrentItem() - mLastSelectedItem), mViewPager.getScrollY());
    }


    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    public float getWidthProportion() {
        return mWidthProportion;
    }

    public void setWidthProportion(float widthProportion) {
        mWidthProportion = widthProportion;
    }

    public float getHeightProportion() {
        return mHeightProportion;
    }

    public void setHeightProportion(float heightProportion) {
        mHeightProportion = heightProportion;
    }

    public boolean isOpenSetPadding() {
        return mOpenSetPadding;
    }

    public void setOpenSetPadding(boolean openSetPadding) {
        mOpenSetPadding = openSetPadding;
    }
}
