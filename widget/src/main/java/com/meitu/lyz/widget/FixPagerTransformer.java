package com.meitu.lyz.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * FixPagerTransformer实现了当ViewPager设置Padding后PageTransformer的position的修正
 * 以及刷新数据集后position的修正，继承后应实现{@link #fixTransformPage(View, float)}  而不是  {@link #transformPage(View, float)}
 * <p>
 * 可以选择是否开启对ViewPager的Padding非侵入式设置
 *
 * @author LYZ 2018.04.27
 */
public abstract class FixPagerTransformer implements ViewPager.PageTransformer {

    private static final String TAG = "FixPagerTransformer";

    @Override
    public void transformPage(@NonNull View page, float position) {
        ViewPager mViewPager;
        if (page.getParent() instanceof ViewPager) {
            mViewPager = ((ViewPager) page.getParent());
        } else {
            return;
        }

        //notifyDataSetChanged()后，此时layout()还未执行，position均为0，进行修正
        if (position == 0 && mViewPager.isLayoutRequested()) {
            int offscreenPageLimit = mViewPager.getOffscreenPageLimit();
            //子View个数
            int childCount = mViewPager.getChildCount();
            int curItem = mViewPager.getCurrentItem();

            /*
             * ViewPager刷新后添加Item的规则为先添加最中间的---》向左依次添加Item---》向右依次添加Item
             * 先计算ViewPager左侧的Item数，即可推出右侧的Item数
             * 然后根据下标值推出实际的Position
             * */
            if (page != mViewPager.getChildAt(0)) {
                int leftNum = Math.min(curItem, offscreenPageLimit);

                for (int i = 1; i < childCount; i++) {
                    if (mViewPager.getChildAt(i) == page) {
                        if (i <= leftNum)
                            position = -i;
                        else
                            position = i - leftNum;
                    }
                }
            }
        } else if (mViewPager.getPaddingStart() != 0) {//修正Padding造成的Position误差
            final float mClientWidth = mViewPager.getMeasuredWidth() -
                    mViewPager.getPaddingLeft() - mViewPager.getPaddingRight();
            float positionFixer = mViewPager.getPaddingStart() / mClientWidth;
            position -= positionFixer;
        }

        //将修正后的posting传入fixTransformPage(@NonNull View page, float position)
        fixTransformPage(page, position);
    }


    /**
     * @param position 修正后的position
     */
    protected abstract void fixTransformPage(@NonNull View page, float position);


}
