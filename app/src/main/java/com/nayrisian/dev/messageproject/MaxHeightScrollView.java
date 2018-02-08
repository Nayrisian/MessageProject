package com.nayrisian.dev.messageproject;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.ScrollView;

/**
 *
 * Created by Nayrisian on 26/12/2016.
 */
public class MaxHeightScrollView extends ScrollView {
    public static final int maxHeight = 100; // 100dp

    // default constructors
    public MaxHeightScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(dpToPx(getResources(),maxHeight), MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }
}