package com.stx.xbanner2;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * @author: xiaohaibin.
 * @time: 2020/1/9
 * @mail:xhb_199409@163.com
 * @github:https://github.com/xiaohaibin
 * @describe:
 */
public class ScaleInTransformer implements ViewPager.PageTransformer {

    private static final float DEFAULT_CENTER = 0.5f;
    public static final float DEFAULT_MIN_SCALE = 0.85f;
    public static final float MAX_SCALE = 0.999f;
    private float mMinScale;

    public ScaleInTransformer(float minScale) {
        mMinScale = minScale;
    }

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        view.setPivotY(pageHeight / 2);
        view.setPivotX(pageWidth / 2);
        if (position < -1) {
            // This page is way off-screen to the left.
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setPivotX(pageWidth);
        } else if (position <= 1) {
            // Modify the default slide transition to shrink the page as well
            if (position < 0) {
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(pageWidth * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
            } else {
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
            }
        } else {
            view.setPivotX(0);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
        }
    }
}
