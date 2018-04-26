package com.meitu.lyz.bannerview.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author LYZ 2018.04.17
 */
public class MultipleItemViewPager extends FrameLayout {
    private static final String TAG = "MultipleItemViewPager";

    private Context mContext;
    private ViewPager mViewPager;


    public MultipleItemViewPager(@NonNull Context context) {
        this(context, null);
    }

    public MultipleItemViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleItemViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttr();
        initViewPager();
    }

    private void initAttr() {
        setClipChildren(false);
        setClipToPadding(false);
    }

    private void initViewPager() {
        mViewPager = new CustomViewPager(mContext);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setClipChildren(false);
        mViewPager.setClipToPadding(false);
        mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        mViewPager.setPageTransformer(false, new ZoomOutPageTransformer(mViewPager));
        addView(mViewPager);
    }


    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }


    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer pageTransformer) {
        mViewPager.setPageTransformer(reverseDrawingOrder, pageTransformer);
        if (pageTransformer instanceof ZoomOutPageTransformer)
            ((ZoomOutPageTransformer) pageTransformer).setViewPager(mViewPager);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public class CustomViewPager extends ViewPager {

        private static final float DEFAULT_WIDTH = 276;
        private static final float DEFAULT_HEIGHT = 348;

        private float mScale = DEFAULT_HEIGHT / DEFAULT_WIDTH;


        //notifyDataSetChanged()时会导致currentItem所在位置变为坐标原点
        private int mLastCurrentItem = 0;
        private int mPagerItemWidth = 0;

        public CustomViewPager(@NonNull Context context) {
            this(context, null);
        }

        public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
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

            //减去mLastCurrentItem后修正ScrollX的值
            scrollTo(mPagerItemWidth * (getCurrentItem() - mLastCurrentItem), getScrollY());

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
            mPagerItemWidth = mMeasuredWidth;
            int paddingStart = (mParentWidth - mMeasuredWidth) / 2;
            setPadding(paddingStart, 0, paddingStart, 0);

            //生成新的MeasureSpec
//                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMeasuredWidth, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMeasuredHeight, MeasureSpec.EXACTLY);

            //计算OffscreenPageLimit
            int limit = (int) Math.ceil(mParentWidth * 1f / mMeasuredWidth / 2 / mMeasuredWidth);
            setOffscreenPageLimit(limit);

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        }

    }


    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        //最小缩放比
        private static final float MIN_SCALE = 0.8f;
        private static final String TAG = "ZoomOutPageTransformer";

        private ViewPager mViewPager;

        public ZoomOutPageTransformer(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        public ZoomOutPageTransformer() {
        }

        public void setViewPager(ViewPager viewPager) {
            mViewPager = viewPager;
        }

        public void transformPage(@NonNull View view, float position) {

            final float mClientWidth = mViewPager.getMeasuredWidth() -
                    mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
            float positionFixer = mViewPager.getPaddingStart() / mClientWidth;

            //notifyDataSetChanged()后，此时requestLayout()还未执行，position均为0，进行修正
            if (position == 0 && view.getTag() instanceof Integer) {
                int curItem = mViewPager.getCurrentItem();
                int viewPos = (Integer) view.getTag();
                position += (viewPos - curItem);
            } else
                position -= positionFixer;

            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();


            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            scaleFactor = Math.max(MIN_SCALE, scaleFactor);

            // Modify the default slide transition to shrink the page as well
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;


            if (position < -1) {
                float nextScaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position + 1));
                view.setTranslationX(pageWidth * (1 - nextScaleFactor) / 2);

            } else if (position > 1) {
                float preScaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position - 1));
                view.setTranslationX(-pageWidth * (1 - preScaleFactor) / 2);
            }

            Log.d(TAG, "transformPage: position" + position + " scaleFactor:" + scaleFactor + " horzMargin:" + horzMargin);

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);


        }
    }
}
