package com.zerodsoft.calendarplatform.event.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class BottomNavigationBehavior<V extends View> extends CoordinatorLayout.Behavior<V>
{
    @ViewCompat.NestedScrollType
    private int lastStartedType;
    private ValueAnimator offsetAnimator;
    private boolean isSnappingEnabled = false;

    public BottomNavigationBehavior()
    {
    }

    public BottomNavigationBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull View dependency)
    {
        if (dependency instanceof FloatingActionButton)
        {
            updateFab(child, dependency);
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull View dependency)
    {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull View dependency)
    {
        super.onDependentViewRemoved(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type)
    {
        if (axes != ViewCompat.SCROLL_AXIS_VERTICAL)
        {
            return false;
        }
        lastStartedType = type;
        offsetAnimator.cancel();

        return true;
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type)
    {
        if (!isSnappingEnabled)
        {
            return;
        }

        // add snap behaviour
        // Logic here borrowed from AppBarLayout onStopNestedScroll code
        if (lastStartedType == ViewCompat.TYPE_TOUCH || type == ViewCompat.TYPE_NON_TOUCH)
        {
            // find nearest seam
            float currTranslation = child.getTranslationY();
            float childHalfHeight = child.getHeight() * 0.5f;

            // translate down
            if (currTranslation >= childHalfHeight)
            {
                animateBarVisibility(child, false);
            }
            // translate up
            else
            {
                animateBarVisibility(child, true);
            }
        }
    }

    private void animateBarVisibility(View child, boolean isVisible)
    {
        if (offsetAnimator == null)
        {
            offsetAnimator = new ValueAnimator();
            offsetAnimator.setInterpolator(new DecelerateInterpolator());
            offsetAnimator.setDuration(150L);
            offsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator)
                {
                    child.setTranslationY((Float) valueAnimator.getAnimatedValue());
                }
            });
        } else
        {
            offsetAnimator.cancel();
        }

        float targetTranslation = isVisible ? 0f : (float) child.getHeight();
        offsetAnimator.setFloatValues(child.getTranslationY(), targetTranslation);
        offsetAnimator.start();
    }

    private void updateSnackbar(View child, Snackbar.SnackbarLayout snackbarLayout)
    {
        if (snackbarLayout.getLayoutParams() instanceof CoordinatorLayout.LayoutParams)
        {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) snackbarLayout.getLayoutParams();

            layoutParams.setAnchorId(child.getId());
            layoutParams.anchorGravity = Gravity.TOP;
            layoutParams.gravity = Gravity.TOP;

            snackbarLayout.setLayoutParams(layoutParams);
        }
    }

    private boolean updateFab(View child, View dependency)
    {
        if (dependency instanceof FloatingActionButton)
        {
            float oldTranslation = child.getTranslationY();
            float height = (float) dependency.getHeight();
            float newTranslation = dependency.getTranslationY() - height;
            child.setTranslationY(newTranslation);

            return oldTranslation != newTranslation;
        }
        return false;
    }
}
