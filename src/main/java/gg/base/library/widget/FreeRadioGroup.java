package gg.base.library.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.dongjin.mylibrary.R;


/**
 * Author:      Sbingo
 * Date:        2016/11/20 0020
 * Time:        13:54
 * Description: a moveable radiogroup could fade out, and auto back to its parent's left or right,
 */

public class FreeRadioGroup extends FrameLayout {


    private float currentX;
    private float currentY;
    private int currentLeft;
    private int currentTop;
    private int parentWidth;
    private int parentHeight;
    private int viewWidth;
    private int viewHight;
    private int minLeftMargin;
    private int maxLeftMargin;
    private int rightDistance;
    private int minTopMargin;
    private int maxTopMargin;
    private int bottomDistance;
    private int leftPadding;
    private int topPadding;
    private boolean moveable;
    private boolean autoBack;
    private ObjectAnimator mObjectAnimator;

    public FreeRadioGroup(Context context) {
        this(context, null);
    }

    public FreeRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FreeRadioGroup);
        moveable = ta.getBoolean(R.styleable.FreeRadioGroup_frg_moveable, true);
        autoBack = ta.getBoolean(R.styleable.FreeRadioGroup_frg_autoBack, true);
        ta.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (moveable) {
            ViewGroup parentView = ((ViewGroup) getParent());
            MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            viewWidth = getRight() - getLeft();
            viewHight = getBottom() - getTop();
            parentWidth = parentView.getMeasuredWidth();
            parentHeight = parentView.getMeasuredHeight();
            minLeftMargin = 0;
            leftPadding = parentView.getPaddingLeft();
            rightDistance = lp.rightMargin + parentView.getPaddingRight();
            maxLeftMargin = parentWidth - rightDistance - viewWidth - leftPadding;
            minTopMargin = 0;
            topPadding = parentView.getPaddingTop();
            bottomDistance = lp.bottomMargin + parentView.getPaddingBottom();
            maxTopMargin = parentHeight - bottomDistance - viewHight - topPadding;
        }
    }


    private boolean interceptClick;
    private long startTime;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setAlpha(1f);
                if (moveable) {
                    MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
                    currentX = ev.getRawX();
                    currentY = ev.getRawY();
                    currentLeft = lp.leftMargin;
                    currentTop = lp.topMargin;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveable) {
                    currentLeft += ev.getRawX() - currentX;
                    currentTop += ev.getRawY() - currentY;
                    //判断左边界
                    currentLeft = Math.max(currentLeft, minLeftMargin);
                    //判断右边界
                    currentLeft = (leftPadding + currentLeft + viewWidth + rightDistance) > parentWidth ? maxLeftMargin : currentLeft;
                    //判断上边界
                    currentTop = Math.max(currentTop, minTopMargin);
                    //判断下边界
                    currentTop = (topPadding + currentTop + viewHight + bottomDistance) > parentHeight ? maxTopMargin : currentTop;
                    MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
                    lp.leftMargin = currentLeft;
                    lp.topMargin = currentTop;
                    setLayoutParams(lp);
                    currentX = ev.getRawX();
                    currentY = ev.getRawY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (moveable && autoBack) {
                    MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
                    int fromLeftMargin = lp.leftMargin;
                    if (getLeft() < (parentWidth - getLeft() - viewWidth)) {
                        lp.leftMargin = minLeftMargin;
                    } else {
                        lp.leftMargin = maxLeftMargin;
                    }
                    mObjectAnimator = ObjectAnimator.ofInt(new Wrapper(this), "leftMargin", fromLeftMargin, lp.leftMargin);
                    mObjectAnimator.setInterpolator(new OvershootInterpolator());
                    mObjectAnimator.setDuration(500);
                    mObjectAnimator.start();
                }
                break;
            default:
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                interceptClick = false;//当按下的时候设置isclick为false，具体原因看后边的讲解
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                interceptClick = false;//当按钮被移动的时候设置isclick为true
                break;
            case MotionEvent.ACTION_UP:
                //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                long l = System.currentTimeMillis() - startTime;
                interceptClick = l > 0.2 * 1000L;
                break;
            default:
        }
        return interceptClick;

    }

    /**
     * 包装类
     */
    static class Wrapper {
        private ViewGroup mTarget;

        public Wrapper(ViewGroup mTarget) {
            this.mTarget = mTarget;
        }

        public int getLeftMargin() {
            MarginLayoutParams lp = (MarginLayoutParams) mTarget.getLayoutParams();
            return lp.leftMargin;
        }

        public void setLeftMargin(int leftMargin) {
            MarginLayoutParams lp = (MarginLayoutParams) mTarget.getLayoutParams();
            lp.leftMargin = leftMargin;
            mTarget.requestLayout();
        }
    }


    public void destory() {
        if (mObjectAnimator != null) {
            mObjectAnimator.cancel();
            mObjectAnimator = null;
        }
    }
}