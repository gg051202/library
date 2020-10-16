package gg.base.library.base

import android.view.View
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * Created by sss on 2020/10/15 17:20.
 * email jkjkjk.com
 */
class MyBaseViewHolder(view: View) : BaseViewHolder(view) {

    override fun setGone(viewId: Int, isGone: Boolean): BaseViewHolder {
        return super.setGone(viewId, !isGone)
    }
}