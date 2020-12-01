package gg.base.library.widget

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.yuyh.library.imgsel.ISNav
import com.yuyh.library.imgsel.config.ISCameraConfig
import com.yuyh.library.imgsel.config.ISListConfig
import gg.base.library.R
import gg.base.library.base.BaseActivity
import gg.base.library.databinding.DialogLayoutSelectImageBinding

/**
 * current :可以传Activity或者Fragment
 */
class SelectImageDialog(val baseActivity: BaseActivity,
                        private val current: Any,
                        needCrop: Boolean = false,
                        cropSizeX: Int = 1,
                        cropSizeY: Int = 1,
                        maxNumber: Int = 1,
                        private val submitFun: (() -> Unit)? = null,
                        private val cancleFun: (() -> Unit)? = null) {


    var requestCode = -1

    private val mBinding: DialogLayoutSelectImageBinding = DataBindingUtil.inflate(LayoutInflater.from(
        baseActivity), R.layout.dialog_layout_select_image, null, false)
    private val alertDialog: AlertDialog = AlertDialog.Builder(baseActivity, R.style.FrameDialogStyle)
            .setView(mBinding.root)
            .create()

    init {
        //        if (current !is BaseActivity || current !is BaseFragment) {
        //            throw RuntimeException("请检查SelectImageDialog current参数是否正确")
        //        }

        alertDialog.window?.setGravity(Gravity.CENTER)

        mBinding.photoLayout.setOnClickListener {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
            submitFun?.invoke()

            baseActivity.checkPermission(succ = {
                val config = ISCameraConfig.Builder()
                        .needCrop(needCrop)
                        .cropSize(cropSizeX, cropSizeY, 300, (300 / (cropSizeX / 1f / cropSizeY)).toInt())
                        .build()
                ISNav.getInstance().toCameraActivity(current, config, requestCode)
            }, permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))

        }
        mBinding.albumLayout.setOnClickListener {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
            cancleFun?.invoke()

            baseActivity.checkPermission(succ = {
                val config = ISListConfig.Builder()
                        .multiSelect(true)
                        .rememberSelected(false)
                        .btnBgColor(Color.parseColor("#F54F4C"))
                        .btnTextColor(Color.parseColor("#ffffff"))
                        .statusBarColor(Color.parseColor("#00000000"))
                        .backResId(R.mipmap.frame_base_back_hei)
                        .title("图片")
                        .titleColor(Color.parseColor("#333333"))
                        .titleBgColor(Color.parseColor("#ffffff"))
                        .needCrop(needCrop)
                        .cropSize(cropSizeX, cropSizeY, 300, (300 / (cropSizeX / 1f / cropSizeY)).toInt())
                        .needCamera(false)
                        .maxNum(maxNumber)
                        .build()
                ISNav.getInstance().toListActivity(current, config, requestCode)
            }, permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))

        }
    }


    fun show(requestCode: Int): SelectImageDialog {
        this.requestCode = requestCode
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
        return this
    }

    companion object {
        fun onActivityResult(requestCode: Int,
                             resultCode: Int,
                             data: Intent?,
                             yourRequestCode: Int,
                             succ: (ArrayList<String>) -> Unit) {
            if (requestCode == yourRequestCode && resultCode == Activity.RESULT_OK && data != null) {
                val stringExtra = data.getStringExtra("result")
                if (!TextUtils.isEmpty(stringExtra)) {
                    succ(arrayListOf(stringExtra as String))
                } else {
                    val pathList = data.getStringArrayListExtra("result")
                    pathList?.let {
                        if (it.isNotEmpty()) {
                            succ(it)
                        }
                    }
                }

            }
        }
    }

}