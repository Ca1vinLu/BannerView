package com.meitu.lyz.bannerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.meitu.lyz.bannerview.util.ConvertUtils;

import java.util.List;

/**
 * @author LYZ 2018.04.19
 */
public class ViewPagerIndicator extends View {

    private Context mContext;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ViewPager mViewPager;

    private List<String> mTitleStr;
    private int mTextSize = 16;
    private int mIndicatorRadius = 3;
    private int mIndicatorMargin = 10;
    private int mIndicatorTextMargin = 30;
    private int mSelectedPos = 0;
    private float mPosOffset = 0;
    private int mOffsetPixels = 12;

    private int mIndicatorColorSelected;
    private int mIndicatorColorUnselected;
    private int mTextColor;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttr();
    }


    private void initAttr() {

        mTextColor = Color.BLACK;
        mIndicatorColorSelected = Color.BLACK;
        mIndicatorColorUnselected = Color.GRAY;

        mTextSize = ConvertUtils.sp2px(mTextSize, mContext);
        mIndicatorTextMargin = ConvertUtils.dp2px(mIndicatorTextMargin, mContext);
        mIndicatorRadius = ConvertUtils.dp2px(mIndicatorRadius, mContext);
        mIndicatorMargin = ConvertUtils.dp2px(mIndicatorMargin, mContext);
        mOffsetPixels = ConvertUtils.dp2px(mOffsetPixels, mContext);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        int width = getMeasuredWidth();
        int center = width / 2;

        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setAlpha((int) (255 * Math.abs(mPosOffset - 0.5) * 2));
        //绘制文字并计算偏移值
        float offset;
        if (mPosOffset >= 0.5) {
            offset = (float) (mTextSize * (1 - mPosOffset));
        } else {
            offset = (float) (-mTextSize * mPosOffset);
        }
        canvas.drawText(mTitleStr.get(mSelectedPos), center + offset, mTextSize * 2, mPaint);


        //绘制指示器
        int indicatorStart = center - ((mIndicatorRadius * 2 + mIndicatorMargin) * (mTitleStr.size() - 1)) / 2;
        for (int i = 0, size = mTitleStr.size(); i < size; i++, indicatorStart += (mIndicatorRadius * 2 + mIndicatorMargin)) {
            if (i == mSelectedPos)
                mPaint.setColor(mIndicatorColorSelected);
            else mPaint.setColor(mIndicatorColorUnselected);
            canvas.drawCircle(indicatorStart, mTextSize * 2 + mIndicatorTextMargin + mIndicatorRadius, mIndicatorRadius, mPaint);
        }

//        canvas.drawCircle(0, 0, 100, mPaint);

    }

    public void attachViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mSelectedPos = position + Math.round(positionOffset);
                mPosOffset = positionOffset;
                ViewPagerIndicator.this.invalidate();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setTitleStr(List<String> titleStr) {
        mTitleStr = titleStr;
    }
}
