package com.meitu.lyz.bannerview.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * @author LYZ 2018.04.19
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    //最小缩放比
    private static final float MIN_SCALE = 0.8f;
    private static final String TAG = "ZoomOutPageTransformer";

    private ViewPager mViewPager;
    private float mPositionFixer;
    private boolean isSetFixer = false;

    public ZoomOutPageTransformer(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public ZoomOutPageTransformer() {
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void transformPage(@NonNull View view, float position) {

        if(!isSetFixer) {
            final int mClientWidth = mViewPager.getMeasuredWidth() -
                    mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
            mPositionFixer = ((float)mViewPager.getPaddingStart()) / mClientWidth;
            isSetFixer = true;
        }

        position -= mPositionFixer;

        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        Log.d(TAG, "transformPage: position" + position);

        float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));

        // Modify the default slide transition to shrink the page as well
        float vertMargin = pageHeight * (1 - scaleFactor) / 2;
        float horzMargin = pageWidth * (1 - scaleFactor) / 2;
        if (position < 0) {
            view.setTranslationX(horzMargin - vertMargin / 2);
        } else {
            view.setTranslationX(-horzMargin + vertMargin / 2);
        }

        // Scale the page down (between MIN_SCALE and 1)
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);


    }
}