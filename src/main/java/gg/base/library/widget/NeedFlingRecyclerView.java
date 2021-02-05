package gg.base.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by sss on 1/30/21 14:20.
 * email jkjkjk.com
 * 配合 HomeConstraintLayout 使用
 */
public class NeedFlingRecyclerView extends RecyclerView {


    private boolean needFling = false;

    public NeedFlingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public NeedFlingRecyclerView(@NonNull Context context) {
        super(context);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE && computeVerticalScrollOffset() == 0) {
            if (mOnScrollToTopListener != null) {
                mOnScrollToTopListener.onScrollTop(this);
            }
        }
        super.onScrollStateChanged(state);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (needFling) {
            return super.fling(velocityX, velocityY);
        } else {
            return false;
        }
    }


    public void setNeedFling(boolean needFling) {
        this.needFling = needFling;
    }

    public interface OnScrollToTopListener {
        void onScrollTop(View view);
    }

    private OnScrollToTopListener mOnScrollToTopListener;

    public void setOnScrollToTopListener(OnScrollToTopListener l) {
        mOnScrollToTopListener = l;
    }
}