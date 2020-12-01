package gg.base.library.widget

import android.app.Activity
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import gg.base.library.R
import gg.base.library.databinding.FrameCustomLayoutMyDialogBinding
import gg.base.library.util.AutoSizeTool

/**
 * Created by sss on 2020/8/23 16:55.
 * email jkjkjk.com
 */
class MyDialog(val activity: Activity,
               val showCancleButton: Boolean = true,
               private val descGravity: Int = Gravity.CENTER,
               val title: CharSequence? = null,
               val desc: CharSequence? = null,
               val submitFun: (() -> Unit)? = null,
               private val cancleFun: (() -> Unit)? = null,
               val submitText: CharSequence? = "确定",
               val cancleText: CharSequence? = "取消") {


    private val mBinding: FrameCustomLayoutMyDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(
            activity), R.layout.frame_custom_layout_my_dialog, null, false)
    val alertDialog: AlertDialog = AlertDialog.Builder(activity, R.style.FrameDialogStyle)
            .setView(mBinding.root)
            .create()

    init {
        alertDialog.window?.setGravity(Gravity.CENTER)
        mBinding.title = title
        mBinding.desc = desc
        mBinding.submitText = submitText
        mBinding.cancleText = cancleText
        mBinding.showCancleButton = showCancleButton
        mBinding.descTextView.gravity = descGravity
        mBinding.descTextView.movementMethod = LinkMovementMethod.getInstance();
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
        alertDialog.window?.let {
            val attributes = it.attributes
            attributes.width = AutoSizeTool.getWidth()
            it.attributes = attributes
        }
        return this
    }

}