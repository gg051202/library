package gg.base.library.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import gg.base.library.Constants
import gg.base.library.base.others.IRunOperation
import gg.base.library.util.LL

class TimeButton(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    val MAX_LENGTH = if (Constants.isDevelop()) 10 else 60

    private var mIRunOperation: IRunOperation? = null
    private var complete: (() -> Unit)? = null


    init {
        if (context is IRunOperation) {
            mIRunOperation = context
        }
    }

    val format = "%ss"
    val mRegetTextString = "重新获取"

    var mLength = 0


    fun start(startFun: (() -> Unit)? = null) {
        mLength = MAX_LENGTH
        startFun?.invoke()
        post { isEnabled = false }
        help()
    }

    private fun help() {
        mIRunOperation?.post(1000, "countTime") {
            LL.i("正在计时 $mLength")
            if (mLength > 0) {
                text = String.format(format, mLength--)
                help()
            } else {
                text = mRegetTextString
                mLength = MAX_LENGTH
                isEnabled = true
                complete?.invoke()
            }
        }
    }

}