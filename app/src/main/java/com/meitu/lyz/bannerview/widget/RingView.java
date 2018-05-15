package com.meitu.lyz.bannerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author LYZ 2018.05.15
 */
public class RingView extends View {


    private Context mContext;
    private int mHeight, mWidth;
    private int mRadius;
    private Point mCenterPoint = new Point();
    private Path mCirclePath=new Path();
    private Paint mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

    public RingView(Context context) {
        this(context, null);
    }

    public RingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int diameter;
//        if (widthMode != MeasureSpec.UNSPECIFIED && heightMode != MeasureSpec.UNSPECIFIED) {
//            diameter = Math.min(widthSize, heightSize);
//        } else if (widthMode != MeasureSpec.UNSPECIFIED) {
//            diameter = widthSize;
//        } else if (heightMode != MeasureSpec.UNSPECIFIED) {
//            diameter = heightSize;
//        } else {
//            diameter = ConvertUtils.dp2px(DEFAULT_DIAMETER, mContext);
//        }
//
//        setMeasuredDimension(diameter, diameter);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int w = getMeasuredWidth();
//        int h = getMeasuredHeight();
//        w = Math.min(w, h);
//        setMeasuredDimension(w, w);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = Math.min(w, h) / 2;
        mCenterPoint.set(w / 2, h / 2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
