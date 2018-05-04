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

    private static final float DEFAULT_WIDTH = 276;
    private static final float DEFAULT_HEIGHT = 348;
    //Item默认宽高比
    private float mRatio = DEFAULT_HEIGHT / DEFAULT_WIDTH;

    //page的实际宽度
    private int mPagerItemWidth;
    //ViewPager刷新前的CurrentItem
    private int mLastCurrentItem;
    //ViewPager单侧可见Item数
    private int mVisibleOffsetNum;

    @Override
    public void transformPage(@NonNull View page, float position) {
        ViewPager mViewPager;
        if (page.getParent() instanceof ViewPager)
            mViewPager = ((ViewPager) page.getParent());
        else return;


        //notifyDataSetChanged()后，此时layout()还未执行，position均为0，进行修正
        if (position == 0 && mViewPager.isLayoutRequested()) {
            //修正offscreenPageLimit
            int offscreenPageLimit = mViewPager.getOffscreenPageLimit() + mVisibleOffsetNum;
            //子View个数
            int childCount = mViewPager.getChildCount();
            int curItem = mViewPager.getCurrentItem();

            /*
             * ViewPager刷新后添加Item的规则为先添加最中间的---》向左依次添加Item---》向右依次添加Item
             * 先计算ViewPager左侧的Item数，即可推出右侧的Item数
             * 然后根据下标值推出实际的Position
             * */
            if (page != mViewPager.getChildAt(0)) {
                int leftNum = Math.min(curItem, offscreenPageLimit);

                for (int i = 1; i < childCount; i++) {
                    if (mViewPager.getChildAt(i) == page) {
                        if (i <= leftNum)
                            position = -i;
                        else
                            position = i - leftNum;
                    }
                }
            }
        } else if (isMultipleItem()) {//修正Padding造成的Position误差
            final float mClientWidth = mViewPager.getMeasuredWidth() -
                    mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
            float positionFixer = mViewPager.getPaddingStart() / mClientWidth;
            position -= positionFixer;
        }


        fixTransformPage(page, position);
    }

    abstract void fixTransformPage(@NonNull View page, float position);

    public boolean isMultipleItem() {
        return true;
    }


    public double getFixedRatio() {
        return mRatio;
    }


    /**
     * 对ViewPager进行非侵入式配置，必须在ViewPager setAdapter后调用
     * 设置Adapter的DataSetObserver来获取{@link #mLastCurrentItem}
     * ViewPager设置OnLayoutChangeListener来设置ViewPager的padding及处理size变化的情况
     */
    public void bindViewPager(final ViewPager viewPager) {
        if (viewPager.getAdapter() == null) {
            Log.e(TAG, "ViewPager haven't set adapter yet!");
            return;
        }

        final PagerAdapter adapter = viewPager.getAdapter();
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mLastCurrentItem = viewPager.getCurrentItem();
            }
        });


        if (isMultipleItem()) {
            viewPager.setClipToPadding(false);
            viewPager.setClipChildren(false);
            setPagerPadding(viewPager, viewPager.getWidth(), viewPager.getHeight());


            viewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, final int left, final int top, final int right, final int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left == oldLeft && top == oldTop && right == oldRight && bottom == oldBottom)
                        return;

                    final ViewPager vp = ((ViewPager) v);

                    if (isMultipleItem()) {
                        final int width = right - left;
                        final int height = bottom - top;
                        vp.post(new Runnable() {
                            @Override
                            public void run() {

                                setPagerPadding(vp, width, height);
                                //模拟点触，触发 mScroller.abortAnimation()，避免滑动未完成导致的偏差
                                MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
                                vp.onTouchEvent(event);
                                event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
                                vp.onTouchEvent(event);

                                //减去mLastCurrentItem后修正ScrollX的值
                                vp.scrollTo(mPagerItemWidth * (vp.getCurrentItem() - mLastCurrentItem), vp.getScrollY());
                            }
                        });

                    }


                }
            });

        }


    }

    /**
     * 计算ViewPager的Padding使其能显示多个Item
     *
     * @param w 宽度
     * @param h 高度
     */
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

        //计算单侧可显示的Item数
        mVisibleOffsetNum = (int) (Math.ceil((mParentWidth - mPagerItemWidth) / 2f / mPagerItemWidth));
    }

}