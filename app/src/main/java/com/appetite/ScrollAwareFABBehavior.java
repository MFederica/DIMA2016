package com.appetite;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {


    private static final String TAG = ScrollAwareFABBehavior.class.getSimpleName();

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
        Log.d(TAG, "ScrollAwareFABBehavior");
    }


    public boolean onStartNestedScroll(CoordinatorLayout parent, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {

        return true;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency instanceof RecyclerView || dependency instanceof Snackbar.SnackbarLayout)
            return true;

        return false;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                               FloatingActionButton child, View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // TODO Auto-generated method stub
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);
        //Log.d(TAG, "onNestedScroll called, dyConsumed = " + String.valueOf(dyConsumed) + ", dyUnconsumed = "+ dyUnconsumed);
            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                Log.d(TAG, "child.hide()");
                child.hide();
            } else if ((dyConsumed < 0 || dyUnconsumed < 0 ) && child.getVisibility() != View.VISIBLE) {
                Log.d(TAG, "child.show()");
                child.show();
            }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        //handles snackbar movements
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }
}