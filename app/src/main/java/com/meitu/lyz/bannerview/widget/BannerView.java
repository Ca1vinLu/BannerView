package com.meitu.lyz.bannerview.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.meitu.lyz.bannerview.util.ConvertUtils;

import java.util.List;

/**
 * @author LYZ 2018.04.17
 */
public class BannerView extends LinearLayout {
    private static final String TAG = "BannerView";

    private static final int DEFAULT_WIDTH = 276;
    private static final int DEFAULT_HEIGHT = 348;

    private float mScale = DEFAULT_HEIGHT * 1f / DEFAULT_WIDTH;

    private Context mContext;
    private ViewPager mViewPager;
    private ViewPagerIndicator mIndicator;

    private int mPagerItemWidth = 0;
    private int mPagerMargin = 10;

    private int mTextSize = 16;
    private int mIndicatorRadius = 3;
    private int mIndicatorTextMargin = 30;

    private int mIndicatorColorSelected;
    private int mIndicatorColorUnselected;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttr();
        initViewPager();
        initIndicator();
//        initListener();
    }

    private void initAttr() {
        setClipChildren(false);
        setClipToPadding(false);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        mTextSize = ConvertUtils.sp2px(mTextSize, mContext);
        mIndicatorRadius = ConvertUtils.dp2px(mIndicatorRadius, mContext);
        mIndicatorTextMargin = ConvertUtils.dp2px(mIndicatorTextMargin, mContext);
        mPagerMargin = ConvertUtils.dp2px(mPagerMargin, mContext);
    }

    private void initViewPager() {
        mViewPager = new ViewPager(mContext) {

            //notifyDataSetChanged()时会导致currentItem所在位置变为坐标原点
            private int mLastCurrentItem = 0;

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
                int mMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec)
                        - mTextSize * 2 - mIndicatorTextMargin - mIndicatorRadius * 2;
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

        };


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: " + position + "  " + positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(TAG, "mViewPager onLayoutChange: ");
            }
        });


//        mViewPager.setPageMargin(mPagerMargin);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setClipChildren(false);
        mViewPager.setClipToPadding(false);
        mViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        addView(mViewPager);
    }

    private void initIndicator() {
        mIndicator = new ViewPagerIndicator(mContext);
        mIndicator.attachViewPager(mViewPager);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                mTextSize * 2 + mIndicatorRadius * 2 + mIndicatorTextMargin, Gravity.CENTER);
        mIndicator.setLayoutParams(layoutParams);
        addView(mIndicator);
    }

    private void initListener() {
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: ");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    public void setAdapter(PagerAdapter adapter, List<String> titleStr) {
        mViewPager.setAdapter(adapter);
        setTitleStr(titleStr);
    }

    public void setAdapter(PagerAdapter adapter) {
        mViewPager.setAdapter(adapter);
    }

    public void setTitleStr(List<String> titleStr) {
        mIndicator.setTitleStr(titleStr);
    }

    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer pageTransformer) {
        mViewPager.setPageTransformer(reverseDrawingOrder, pageTransformer);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }
}
