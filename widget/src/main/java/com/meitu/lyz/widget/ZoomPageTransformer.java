package com.meitu.lyz.widget;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * 缩放效果的PageTransformer
 *
 * @author LYZ 2018.04.27
 */
public class ZoomPageTransformer extends FixPagerTransformer {

    //最小缩放比
    private float mMinScale = 0.8f;

    @Override
    void fixTransformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();

        float scaleFactor = mMinScale + (1 - mMinScale) * (1 - Math.abs(position));
        scaleFactor = Math.max(mMinScale, scaleFactor);


        if (position < -1) {
            float nextScaleFactor = mMinScale + (1 - mMinScale) * (1 - Math.abs(position + 1));
            page.setTranslationX(pageWidth * (1 - nextScaleFactor) / 2);

        } else if (position > 1) {
            float preScaleFactor = mMinScale + (1 - mMinScale) * (1 - Math.abs(position - 1));
            page.setTranslationX(-pageWidth * (1 - preScaleFactor) / 2);
        }


        // Scale the page down (between mMinScale and 1)
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);
    }


    public void setMinScale(float minScale) {
        this.mMinScale = minScale;
    }
}
