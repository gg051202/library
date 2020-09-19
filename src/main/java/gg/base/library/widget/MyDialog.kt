package gg.base.library.widget

import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.dongjin.mylibrary.R
import com.dongjin.mylibrary.databinding.FrameCustomLayoutMyDialogBinding
import gg.base.library.base.BaseActivity

/**
 * Created by sss on 2020/8/23 16:55.
 * email jkjkjk.com
 */
class MyDialog(val baseActivity: BaseActivity,
               val showCancleButton: Boolean = true,
               val title: String? = null,
               val desc: CharSequence? = null,
               val submitFun: (() -> Unit)? = null,
               val cancleFun: (() -> Unit)? = null,
               val submitText: String? = "确定",
               val cancleText: String? = "取消") {


    private val mBinding: FrameCustomLayoutMyDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(
        baseActivity), R.layout.frame_custom_layout_my_dialog, null, false)
    private val alertDialog: AlertDialog = AlertDialog.Builder(baseActivity, R.style.DefaultDialogStyle)
            .setView(mBinding.root)
            .setCancelable(false)
            .create()

    init {
        alertDialog.window?.setGravity(Gravity.CENTER)
        mBinding.title = title
        mBinding.desc = desc
        mBinding.submitText = submitText
        mBinding.cancleText = cancleText
        mBinding.showCancleButton = showCancleButton

        mBinding.submitTextView.setOnClickListener {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
            submitFun?.invoke()
        }
        mBinding.cancelTextView.setOnClickListener {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
            cancleFun?.invoke()
        }
    }


    fun show(): MyDialog {
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
        return this
    }

}