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
    private int mLastScrollX;

    public ZoomOutPageTransformer(ViewPager viewPager) {
        mViewPager = viewPager;
        mLastScrollX = viewPager.getScrollX();
    }

    public ZoomOutPageTransformer() {
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mLastScrollX = viewPager.getScrollX();
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