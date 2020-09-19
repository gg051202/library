package gg.base.library.util;


import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirstItemHorzontalMarginDecoration extends RecyclerView.ItemDecoration {
    private int spaceStart;
    private int spaceEnd;

    public FirstItemHorzontalMarginDecoration(int spaceStartDp) {
        this.spaceStart = AutoSizeTool.INSTANCE.dp2px(spaceStartDp);
    }

    public FirstItemHorzontalMarginDecoration(int spaceStartDp, int spaceEndDp) {
        this.spaceStart = AutoSizeTool.INSTANCE.dp2px(spaceStartDp);
        this.spaceEnd = AutoSizeTool.INSTANCE.dp2px(spaceEndDp);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = spaceStart;
        }
        if (parent.getAdapter() != null) {
            if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                outRect.right = spaceEnd;
            }
        }
    }
}