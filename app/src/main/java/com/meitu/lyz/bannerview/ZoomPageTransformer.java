package com.meitu.lyz.bannerview;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author LYZ 2018.04.27
 */
public class ZoomPageTransformer extends FixPagerTransformer {
    private static final float MIN_SCALE = 0.8f;

    @Override
    void fixTransformPage(@NonNull View page, float position) {
        int pageWidth = page.getWidth();

        float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
        scaleFactor = Math.max(MIN_SCALE, scaleFactor);


        if (position < -1) {
            float nextScaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position + 1));
            page.setTranslationX(pageWidth * (1 - nextScaleFactor) / 2);

        } else if (position > 1) {
            float preScaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position - 1));
            page.setTranslationX(-pageWidth * (1 - preScaleFactor) / 2);
        }


        // Scale the page down (between MIN_SCALE and 1)
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);
    }


}
