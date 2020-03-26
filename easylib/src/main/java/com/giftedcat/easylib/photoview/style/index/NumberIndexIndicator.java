package com.giftedcat.easylib.photoview.style.index;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.viewpager.widget.ViewPager;

import com.giftedcat.easylib.photoview.style.IIndexIndicator;
import com.giftedcat.easylib.photoview.view.indicator.NumberIndicator;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 图片翻页时使用 {@link NumberIndicator} 去指示当前图片的位置
 * <p>
 * Created by Hitomis on 2017/4/23 0023.
 * <p>
 * email: 196425254@qq.com
 */
public class NumberIndexIndicator implements IIndexIndicator {

    private NumberIndicator numberIndicator;

    @Override
    public void attach(FrameLayout parent) {
        FrameLayout.LayoutParams indexLp = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        indexLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        indexLp.topMargin = 30;

        numberIndicator = new NumberIndicator(parent.getContext());
        numberIndicator.setLayoutParams(indexLp);

        parent.addView(numberIndicator);
    }

    @Override
    public void onShow(ViewPager viewPager) {
        numberIndicator.setVisibility(View.VISIBLE);
        numberIndicator.setViewPager(viewPager);
    }

    @Override
    public void onHide() {
        if (numberIndicator == null) return;
        numberIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onRemove() {
        if (numberIndicator == null) return;
        ViewGroup vg = (ViewGroup) numberIndicator.getParent();
        if (vg != null) {
            vg.removeView(numberIndicator);
        }
    }
}
