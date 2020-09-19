package gg.base.library.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.google.android.material.internal.FlowLayout
import gg.base.library.util.AutoSizeTool
import gg.base.library.util.LL
import gg.base.library.util.toast

/**
 * Created by sss on 2020/8/28 10:29.
 * email jkjkjk.com
 */
@SuppressLint("RestrictedApi")
class GGFlowLayout<T> constructor(context: Context, at: AttributeSet) : FlowLayout(context, at) {
    lateinit var mViewStore: ((T) -> View)
    private var mList: ArrayList<T> = ArrayList()


    init {
        itemSpacing = AutoSizeTool.dp2px(10)
        lineSpacing = AutoSizeTool.dp2px(10)
    }


    fun add(t: T, index: Int? = null) {
        if (!this::mViewStore.isInitialized) {
            toast("GGFlowLayout没有初始化viewStore")
            return
        }
        val child = mViewStore(t)
        child.tag = "isAdded" //如果有这个tag，setList的时候会被删除
        if (index == null) {
            addView(child)
            mList.add(t)
        } else {
            addView(child, index)
            mList.add(index, t)

        }
    }

    fun setViewList(list: ArrayList<T>) {
        val notNeedDeleteViews = ArrayList<View>()
        for (i in 0 until childCount) {
            val childAt = getChildAt(i)
            if (childAt != null && childAt.tag == null) {
                notNeedDeleteViews.add(childAt)
            }
        }
        removeAllViews()

        list.forEach {
            if (!TextUtils.isEmpty(it.toString())) {
                add(it)
            }
        }
        notNeedDeleteViews.forEach {
            addView(it)
        }
    }

    fun size(): Int {
        return mList.size
    }

    fun getList(): ArrayList<T> {
        return mList
    }

    fun delete(view: View) {
        mList.removeAt(indexOfChild(view))
        removeView(view)
    }

    fun setViewCreater(viewCreate: ((T) -> View)) {
        mViewStore = viewCreate
    }

}