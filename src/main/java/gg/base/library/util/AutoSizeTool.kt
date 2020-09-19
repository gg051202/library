package gg.base.library.util

import android.util.SparseArray
import android.util.TypedValue
import androidx.core.util.set
import com.blankj.utilcode.util.Utils

/**
 * Created by guilin on 2019-04-22 17:56.
 * email 973635949@qq.com
 */
object AutoSizeTool {

    private val sparseArray = SparseArray<Int>()

    fun dp2px(dp: Int): Int {
        if (sparseArray[dp] == null) {
            sparseArray[dp] = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                         dp.toFloat(),
                                                         Utils.getApp().resources.displayMetrics) + 0.5f).toInt()
        }
        return sparseArray[dp]
    }

    fun sp2px(sp: Float): Int {
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Utils.getApp().resources.displayMetrics) + 0.5f).toInt()
    }

    fun pt2px(pt: Float): Int {
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, pt, Utils.getApp().resources.displayMetrics) + 0.5f).toInt()
    }

    fun in2px(`in`: Float): Int {
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, `in`, Utils.getApp().resources.displayMetrics) + 0.5f).toInt()
    }

    fun mm2px(mm: Float): Int {
        return (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, mm, Utils.getApp().resources.displayMetrics) + 0.5f).toInt()
    }
}