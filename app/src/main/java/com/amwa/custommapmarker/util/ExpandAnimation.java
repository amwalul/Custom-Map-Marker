package com.amwa.custommapmarker.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ExpandAnimation extends Animation {

    private View view;
    private final float mStartWeight;
    private final float mDeltaWeight;

    public ExpandAnimation(View view, float startWeight, float endWeight) {
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            this.view = view;
        } else {
            throw new IllegalArgumentException("The view should have LinearLayout as parent");
        }

        mStartWeight = startWeight;
        mDeltaWeight = endWeight - startWeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
        view.setLayoutParams(layoutParams);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
