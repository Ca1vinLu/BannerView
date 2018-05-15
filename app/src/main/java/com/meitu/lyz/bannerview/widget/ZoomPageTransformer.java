package com.meitu.lyz.bannerview.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.meitu.lyz.widget.FixPagerTransformer;

/**
 * 缩放效果的PageTransformer
 *
 * @author LYZ 2018.04.27
 */
public class ZoomPageTransformer extends FixPagerTransformer {

    private static final String TAG = "ZoomPageTransformer";

    //最小缩放比
    private float mMinScale = 0.83f;
    private int mRingWidth;


    @Override
    protected void fixTransformPage(@NonNull View page, float position) {
        int pageWidth = page.getHeight();
        if (pageWidth == 0) {
            ViewPager vp = (ViewPager) page.getParent();
            pageWidth = vp.getHeight();
        }

        float scaleFactor = (mMinScale + (1 - mMinScale) * (1 - Math.abs(position)));
        scaleFactor = Math.max(mMinScale, scaleFactor);


        //偏移值  内圆到外圆
        int offset = (int) ((mRingWidth - pageWidth) / 2 + (1 - mMinScale) * pageWidth / 2);
        if (position <= -1) {
            page.setTranslationX(-offset);
        } else if (position <= 0) {
            offset *= (-position / 1f);
            page.setTranslationX(-offset);
        } else if (position < 1) {
            offset *= (position / 1f);
            page.setTranslationX(offset);
        } else {
            page.setTranslationX(offset);
        }

        // Scale the page down (between mMinScale and 1)
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);

        Log.d(TAG, "fixTransformPage: " +
                " position: " + position +
                " scaleFactor: " + scaleFactor +
                " TranslationX: " + page.getTranslationX());
    }


    public void setMinScale(float minScale) {
        this.mMinScale = minScale;
    }

    public void bindCircleView(View circle) {
        circle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int w = right - left;
                mRingWidth = w;
            }
        });
    }

    @Override
    public double getRatio() {
        return 1;
    }
}
