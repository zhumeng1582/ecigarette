package com.industio.ecigarette.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class ViewAnimate {
    /**
     * View展开动画
     *
     * @param v
     * @param mHiddenViewMeasuredHeight
     */
    public static void animateOpen(View v, int mHiddenViewMeasuredHeight) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(v, 0, mHiddenViewMeasuredHeight);
        animator.start();
    }

    public static void topOpen(View v, int mHiddenViewMeasuredHeight) {
        v.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = mHiddenViewMeasuredHeight;
        v.setLayoutParams(layoutParams);
    }

    /**
     * View折叠动画
     *
     * @param view
     */
    public static void animateClose(final View view, final AnimaionLoadEndListener listener) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                listener.onLoadEnd();
            }
        });
        animator.start();
    }

    private static ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);

            }
        });
        return animator;
    }

    public interface AnimaionLoadEndListener {
        void onLoadEnd();
    }
}
