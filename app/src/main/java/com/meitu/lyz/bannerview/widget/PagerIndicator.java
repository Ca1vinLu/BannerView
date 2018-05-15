package com.meitu.lyz.bannerview.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 一个简易的ViewPager指示器，可实现形如1/6形式的指示器
 *
 * @author LYZ 2018.05.08
 */
public class PagerIndicator extends AppCompatTextView {

    private static final String DEFAULT_FORMAT_STRING = "%d/%d";
    private int mSize;
    private String mFormatStr = DEFAULT_FORMAT_STRING;
    private ViewPager mViewPager;

    public PagerIndicator(Context context) {
        this(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 绑定ViewPager，并配置OnPageChangeListener
     */
    public void attachViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setText(String.format(mFormatStr, position + 1, mSize));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 刷新时需调用此函数来重置{@link #mSize}
     *
     * @param size 新的size
     */
    public void setTotalSize(int size) {
        mSize = size;
        setText(String.format(mFormatStr, mViewPager.getCurrentItem() + 1, mSize));
    }

    public void setFormatStr(String formatStr) {
        mFormatStr = formatStr;
    }
}
