package gg.base.library.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

public class AndroidBug5497Workaround {

    // For more information, see https://issuetracker.google.com/issues/36911528
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity(Activity activity, OnKeyboardStateChangeListener listener) {
        new AndroidBug5497Workaround(activity, listener);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity, OnKeyboardStateChangeListener listener) {
        onKeyboardStateChangeListener = listener;
        FrameLayout content = activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(this::possiblyResizeChildOfContent);
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
//                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                if (onKeyboardStateChangeListener != null) {
                    onKeyboardStateChangeListener.change(true, heightDifference);
                }
            } else {
                // keyboard probably just became hidden
//                frameLayoutParams.height = usableHeightSansKeyboard;
                if (onKeyboardStateChangeListener != null) {
                    onKeyboardStateChangeListener.change(false, 0);
                }
            }
//            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    public interface OnKeyboardStateChangeListener {
        void change(boolean isShow, int keyboardHeight);
    }

    private OnKeyboardStateChangeListener onKeyboardStateChangeListener;

    public void setOnKeyboardStateChangeListener(OnKeyboardStateChangeListener l) {
        onKeyboardStateChangeListener = l;
    }
}