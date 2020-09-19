package gg.base.library.widget;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.dongjin.mylibrary.R;


public class MaxHeightNestScrollView extends NestedScrollView {

    private final Context mContext;
    private int mMaxHeight;

    public MaxHeightNestScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightNestScrollView);
        mMaxHeight = arr.getLayoutDimension(R.styleable.MaxHeightNestScrollView_mhnsv_maxHeight, mMaxHeight);
        arr.recycle();
    }

    DisplayMetrics d = new DisplayMetrics();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

            display.getMetrics(d);
            if (mMaxHeight > 0) {
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(mMaxHeight, View.MeasureSpec.AT_MOST);
            } else {
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(d.heightPixels / 2, View.MeasureSpec.AT_MOST);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
