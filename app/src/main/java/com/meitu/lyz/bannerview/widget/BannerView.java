package com.meitu.lyz.bannerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author LYZ 2018.04.17
 */
public class BannerView extends ViewPager {

    private static final int DEFAULT_WIDTH = 276;
    private static final int DEFAULT_HEIGHT = 348;

    private Context mContext;

    private int mPaddingTop, mPaddingBottom, mPaddingStart, mPaddingEnd;
    private int mChildWidth, mChildHeight;
    private float mScale = DEFAULT_WIDTH / DEFAULT_HEIGHT;

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setClipChildren(false);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        params.width = mChildWidth;
        params.height = mChildHeight;
        if (child instanceof ImageView)
            ((ImageView) child).setScaleType(ImageView.ScaleType.FIT_CENTER);
        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int mHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mMeasuredHeight = MeasureSpec.getSize(heightMeasureSpec);




    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
