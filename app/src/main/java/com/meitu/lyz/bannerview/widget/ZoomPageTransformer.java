package com.meitu.lyz.bannerview.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.meitu.lyz.widget.FixPagerTransformer;

/**
 * 缩放PageTransformer，可实现item的间距修正即设置
 *
 * @author LYZ 2018.04.27
 */
public class ZoomPageTransformer extends FixPagerTransformer {

    private static final String TAG = "ZoomPageTransformer";

    //最小缩放比
    private float mMinScale = 0.83f;

    //外部View的宽度
    private int mRingWidth;
    //item间距
    private int mItemMargin = -1;


    @Override
    protected void fixTransformPage(@NonNull View page, float position) {

        //计算缩放比
        float scaleFactor = (mMinScale + (1 - mMinScale) * (1 - Math.abs(position)));
        scaleFactor = Math.max(mMinScale, scaleFactor);


        //设置XY缩放
        page.setScaleX(scaleFactor);
        page.setScaleY(scaleFactor);



        /*
         * item间的间距实际上就等于 (1 - mMinScale) * itemWidth
         * 计算偏移，中间三个位置即position在[-1,1]这个区间中时，偏移值为内圆到外圆的距离加上(1 - mMinScale) * itemWidth / 2
         * 因为左右两边已经存在(1 - mMinScale) * itemWidth / 2 的间距,所以偏移值还需补上(1 - mMinScale) * itemWidth / 2
         * 当设置了特定的间距时，则再次进行修正
         * */

        //ViewPager item的直径，若此时View还未进行测量，则直接取ViewPager的高度
        int itemWidth = page.getHeight();
        if (itemWidth == 0) {
            ViewPager vp = (ViewPager) page.getParent();
            itemWidth = vp.getHeight() - vp.getPaddingTop() - vp.getPaddingBottom();
        }

        //item缩放后的剩余宽度
        float leftSpace = (1 - mMinScale) * itemWidth;
        //偏移值
        float offset = (mRingWidth - itemWidth) / 2f + leftSpace / 2f;

        //若设置了特定的间距，则进行修正
        if (mItemMargin >= 0) {
            offset += (mItemMargin - leftSpace) * Math.abs(position);
        }

        if (position < -1) {//position  (-∞ , -1)
            page.setTranslationX(-offset);
        } else if (position <= 0) {//position  [-1 , 0]
            offset *= (-position / 1f);
            page.setTranslationX(-offset);
        } else if (position <= 1) {//position  [0 , 1]
            offset *= (position / 1f);
            page.setTranslationX(offset);
        } else {//position  (1 , +∞)
            page.setTranslationX(offset);
        }


        Log.d(TAG, "fixTransformPage: " +
                " position: " + position +
                " scaleFactor: " + scaleFactor +
                " TranslationX: " + page.getTranslationX());
    }


    public void setMinScale(float minScale) {
        this.mMinScale = minScale;
    }


    public int getItemMargin() {
        return mItemMargin;
    }

    public void setItemMargin(int itemMargin) {
        mItemMargin = itemMargin;
    }

    /**
     * 绑定外部View，获取外部View的宽度并赋值给{@link #mRingWidth}
     */
    public void bindCircleView(View circle) {
        circle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int w = right - left;
                mRingWidth = w;
            }
        });
    }


    /**
     * 固定宽高比为1
     */
    @Override
    public double getRatio() {
        return 1;
    }
}
