package gg.base.library.base.others

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ContentFrameLayout
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import gg.base.library.R
import gg.base.library.Constants
import gg.base.library.base.BaseActivity
import gg.base.library.base.BaseViewModel
import gg.base.library.util.AutoSizeTool
import gg.base.library.util.LL
import gg.base.library.util.bindingadapter.setCornerRadius
import gg.base.library.widget.ShowLogDialog
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.frame_custom_layout_my_action_bar.*
import me.jessyan.autosize.utils.ScreenUtils

/**
 * Created by sss on 2020/8/20 14:32.
 * email jkjkjk.com
 */
fun setBaseViewModelOber(viewModel: BaseViewModel, owner: LifecycleOwner, diffentOperation: IDiffentOperation) {
    viewModel.loadingView.observe(owner, {
        if (it.isShowing) {
            diffentOperation.showLoadingView(it)
        } else {
            diffentOperation.hideLoadingView(it)
        }
    })

    viewModel.toastErrMessage.observe(owner, { value ->
        ToastUtils.showShort(value)
    })

}

fun initActionBar(baseActivity: BaseActivity) {
    baseActivity.findViewById<FitWindowsLinearLayout>(R.id.action_bar_root)?.let {
        baseActivity.mActionBar = baseActivity.layoutInflater.inflate(R.layout.frame_custom_layout_my_action_bar,
                                                                      it,
                                                                      false)
        it.addView(baseActivity.mActionBar,
                   0,
                   ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                          ViewGroup.LayoutParams.WRAP_CONTENT))
        baseActivity.baseTitleTextView.text = baseActivity.mActivityInitConfig.title
        baseActivity.baseBackBackImageView.visibility = if (baseActivity.mActivityInitConfig.needShowBack) View.VISIBLE else View.GONE
        baseActivity.baseBackBackImageView.setOnClickListener {
            if (baseActivity.mOnBackClickFun != null) {
                baseActivity.mOnBackClickFun?.invoke()
            } else {
                baseActivity.finish()
            }
        }
        baseActivity.mActionBar?.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0)
    }

}

fun initMenu(baseActivity: BaseActivity) {
    baseActivity.mActivityInitConfig.viewModel.menuResList.observe(baseActivity, Observer { it ->
        it.forEach { data ->
            if (data.cacheView == null) {
                if (data.imgRes != 0) {
                    data.cacheView = ImageView(baseActivity).apply {
                        val left: Int = AutoSizeTool.dp2px(12)
                        setPadding(left, left + 1 - 1, left, left + 1 - 1)
                        addCircleRipple()
                        layoutParams = ViewGroup.LayoutParams(AutoSizeTool.dp2px(43), AutoSizeTool.dp2px(43))
                        setImageResource(data.imgRes)
                        setOnClickListener(data.listener)
                    }
                } else {
                    data.cacheView = TextView(baseActivity).apply {
                        setTextColor(0xff555555.toInt())
                        text = data.name
                        addCircleRipple()
                        setOnClickListener(data.listener)
                        gravity = Gravity.CENTER
                        setPadding(AutoSizeTool.dp2px(9),
                                   AutoSizeTool.dp2px(4),
                                   AutoSizeTool.dp2px(9),
                                   AutoSizeTool.dp2px(4))
                        textSize = 15f
                    }
                }
            }
            data.cacheView?.apply {
                if (parent != null) {
                    (parent as ViewGroup).removeView(this)
                }
            }
            baseActivity.baseMenuImageViewLayout.addView(data.cacheView)
        }
    })
}

public fun View.addCircleRipple() = with(TypedValue()) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
        setBackgroundResource(resourceId)
    }
}

public fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}

fun addLogButton(binding: ViewBinding, activity: BaseActivity) {
    if (gg.base.library.activity.DevelopActivity.isShowLogButton() && binding.root.parent is ContentFrameLayout) {
        val logButton = TextView(activity)

        val drawablePressedFlase = GradientDrawable()
        drawablePressedFlase.setColor(0x88000000.toInt())
        drawablePressedFlase.setCornerRadius(RoundedCornersTransformation.CornerType.ALL, 3f)
        logButton.text = "日志"
        logButton.setTextColor(0xffffffff.toInt())
        logButton.gravity = Gravity.CENTER
        logButton.textSize = 11f
        logButton.background = drawablePressedFlase
        logButton.setOnClickListener {
            ShowLogDialog(activity).show()
        }
        val layoutParams = FrameLayout.LayoutParams(AutoSizeTool.dp2px(40), AutoSizeTool.dp2px(21))
        layoutParams.topMargin = AutoSizeTool.dp2px(400)
        layoutParams.leftMargin = AutoSizeTool.dp2px(20)
        (binding.root.parent as ContentFrameLayout).addView(logButton, layoutParams)
    }
}

fun addActivityInfo(binding: ViewBinding, activity: BaseActivity) {
    if (Constants.isDevelop() && binding.root.parent is ContentFrameLayout) {
        val descTextView = TextView(activity)
        descTextView.text = activity.javaClass.simpleName
        descTextView.maxLines = 6
        descTextView.textSize = 10f
        val color = 0x90eb8f8f.toInt()
        descTextView.setTextColor(color)
        descTextView.setOnLongClickListener {
            descTextView.setTextColor(if (descTextView.currentTextColor == color) 0x00000000 else color)
            false
        }
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                    AutoSizeTool.dp2px(30))
        layoutParams.topMargin = AutoSizeTool.dp2px(3)
        layoutParams.leftMargin = AutoSizeTool.dp2px(10)
        (binding.root.parent as ContentFrameLayout).addView(descTextView, layoutParams)
    }
}