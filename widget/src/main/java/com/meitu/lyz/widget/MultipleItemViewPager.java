package com.meitu.lyz.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 同时显示多个item的ViewPager
 * 配合Position修正插件{@link  FixPagerTransformer} 及padding计算插件{@link ViewPagerMultipleItemPlugin}来实现
 *
 * @author LYZ 2018.05.04
 */
public class MultipleItemViewPager extends ViewPager {

    private static final String TAG = "MultipleItemViewPager";

    private Context mContext;
    private static final float DEFAULT_WIDTH = 276;
    private static final float DEFAULT_HEIGHT = 348;


    private ViewPagerMultipleItemPlugin mPagerPlugin;


    public MultipleItemViewPager(@NonNull Context context) {
        this(context, null);
    }

    public MultipleItemViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPagerPlugin = new ViewPagerMultipleItemPlugin(this);
        setClipChildren(false);
        setClipToPadding(false);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attributeSet) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.MultipleItemViewPager);

        float radio = typedArray.getFloat(R.styleable.MultipleItemViewPager_radio, DEFAULT_HEIGHT / DEFAULT_WIDTH);
        mPagerPlugin.setRatio(radio);

        float widthProportion = typedArray.getFloat(R.styleable.MultipleItemViewPager_widthProportion, 0.75f);
        float heightProportion = typedArray.getFloat(R.styleable.MultipleItemViewPager_heightProportion, 1f);
        mPagerPlugin.setWidthProportion(widthProportion);
        mPagerPlugin.setHeightProportion(heightProportion);

        typedArray.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //设置ViewPager的Padding
        mPagerPlugin.calculatePadding(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));

        //调用onMeasure来对Item进行测量
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mPagerPlugin.onSizeChange();
    }


    public float getRatio() {
        return mPagerPlugin.getRatio();
    }

    public void setRatio(float ratio) {
        mPagerPlugin.setRatio(ratio);
    }


    public float getWidthProportion() {
        return mPagerPlugin.getWidthProportion();
    }

    public void setWidthProportion(float widthProportion) {
        mPagerPlugin.setWidthProportion(widthProportion);
    }

    public float getHeightProportion() {
        return mPagerPlugin.getHeightProportion();
    }

    public void setHeightProportion(float heightProportion) {
        mPagerPlugin.setHeightProportion(heightProportion);
    }

    public ViewPagerMultipleItemPlugin getPagerPlugin() {
        return mPagerPlugin;
    }
}
