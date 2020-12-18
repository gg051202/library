package gg.base.library.util.bindingadapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.chad.library.adapter.base.BaseQuickAdapter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import gg.base.library.Constants
import gg.base.library.R
import gg.base.library.util.*
import gg.base.library.util.AutoSizeTool.dp2px
import gg.base.library.widget.CommonMenu
import gg.base.library.widget.FakeBoldTextView
import gg.base.library.widget.GGFlowLayout
import gg.base.library.widget.download.RedPointTextView
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation.CornerType
import me.jessyan.autosize.utils.ScreenUtils

/**
 * Created by sss on 2020/8/20 00:13.
 * email jkjkjk.com
 */
@BindingAdapter(value = ["marginTopPx", "marginRightPx", "marginBottomPx", "marginLeftPx"],
        requireAll = false)
fun setMarginPx(view: View, marginTopPx: Int?, marginRightPx: Int?, marginBottomPx: Int?, marginLeftPx: Int?) {
    val layoutParams = view.layoutParams
    if (layoutParams is MarginLayoutParams) {
        marginTopPx?.let { layoutParams.topMargin = it }
        marginRightPx?.let { layoutParams.rightMargin = it }
        marginBottomPx?.let { layoutParams.bottomMargin = it }
        marginLeftPx?.let { layoutParams.leftMargin = it }
        view.layoutParams = layoutParams
    }
}

@BindingAdapter(value = ["marginTopDp", "marginRightDp", "marginBottomDp", "marginLeftDp"],
        requireAll = false)
fun setMarginDp(view: View, marginTopDp: Int?, marginRightDp: Int?, marginBottomDp: Int?, marginLeftDp: Int?) {
    val layoutParams = view.layoutParams
    if (layoutParams is MarginLayoutParams) {
        marginTopDp?.let { layoutParams.topMargin = dp2px(it) }
        marginRightDp?.let { layoutParams.rightMargin = dp2px(it) }
        marginBottomDp?.let { layoutParams.bottomMargin = dp2px(it) }
        marginLeftDp?.let { layoutParams.leftMargin = dp2px(it) }
        view.layoutParams = layoutParams
    }
}

@BindingAdapter(value = ["layout_height_px", "layout_width_px"], requireAll = false)
fun setHeightPx(view: View, layout_height_px: Int?, layout_width_px: Int?) {
    val layoutParams = view.layoutParams
    layout_height_px?.let { layoutParams.height = it }
    layout_width_px?.let { layoutParams.width = it }
    view.layoutParams = layoutParams
}

@BindingAdapter(value = ["layout_height_dp", "layout_width_dp"], requireAll = false)
fun setHeightDp(view: View, layout_height_dp: Int?, layout_width_dp: Int?) {
    val layoutParams = view.layoutParams
    layout_height_dp?.let {
        when (it) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            else -> {
                layoutParams.height = dp2px(it)
            }
        }
    }
    layout_width_dp?.let {
        when (it) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            else -> {
                layoutParams.width = dp2px(it)
            }
        }
    }
    view.layoutParams = layoutParams
}


/**
 * @param url        首选的加载资源
 * @param url2       如果首选的加载资源为空，加载这个
 * @param radiusDp   圆角
 * @param cornerType 圆角的类型
 * @param urlSkipMemoryCache 有这么一种情况，如果本来图片很小，通过用户交互缩放后，避免直接从内存中直接获取小的图片，因此可以设置urlSkipMemoryCache跳过内存缓存。
 */
@BindingAdapter(value = [
    "url",
    "url2",
    "urlRadiusDp",
    "urlCornerType",
    "urlNotNeedCenterCrop",
    "urlSkipMemoryCache",
    "urlPlaceHolder",
], requireAll = false)
fun loadImage(imageView: ImageView,
              url: String?,
              url2: Any?,
              radiusDp: Int?,
              cornerType: CornerType?,
              urlNotNeedCenterCrop: Boolean?,
              urlSkipMemoryCache: Boolean?,
              urlPlaceHolder: Int?) {
    val options: RequestOptions = Constants.getRequestOptions(dp2px(radiusDp
            ?: 0), cornerType ?: CornerType.ALL)
    urlPlaceHolder?.let {
        options.error(it)
        options.placeholder(it)
    }
    urlNotNeedCenterCrop?.let {
        if (it) {
            options.transform(RoundedCornersTransformation(dp2px(radiusDp ?: 0), 0, cornerType ?: CornerType.ALL))
        }
    }

    val target = if (!TextUtils.isEmpty(url)) url else url2

    val build = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

    imageView.setImageResource(0)
    Glide.with(imageView.context)
            .load(target)
            .skipMemoryCache(urlSkipMemoryCache ?: false)
            .transition(DrawableTransitionOptions.with(build))
            .apply(options)
            .into(imageView)
}

/**
 * @param firstDontNeedAnim 第一次显示不需要加载动画,为了解决，某些页面的view，本来就是gone，不需要展示动画
 */
@BindingAdapter(value = ["gone", "gone_anim_type", "gone_anim_time", "gone_first_dont_need_anim"],
        requireAll = false)
fun setGone(view: View, isGone: Boolean, goneAnimType: Int, goneAnimTime: Int, firstDontNeedAnim: Boolean) {
    var goneAnimTime = goneAnimTime
    if (goneAnimTime == 0) {
        goneAnimTime = 300
    }
    if (view.getTag(R.id.firstDontNeedAnim) == null) {
        view.setTag(R.id.firstDontNeedAnim, firstDontNeedAnim)
    }
    if (goneAnimType == 0 || view.getTag(R.id.firstDontNeedAnim) as Boolean) {
        view.visibility = if (isGone) View.VISIBLE else View.GONE
        view.setTag(R.id.firstDontNeedAnim, false)
    } else if (goneAnimType == 1) { //顶部滑入，顶部滑出
        if (isGone) {
            if (view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(view, "translationY", -180f, 0f)
                        .setDuration(goneAnimTime.toLong())
                        .start()
            }
        } else {
            if (view.visibility != View.GONE) {
                view.animate()
                        .translationY(-view.height.toFloat())
                        .setDuration(goneAnimTime.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                view.visibility = View.GONE
                            }
                        })
            }
        }
    } else if (goneAnimType == 2) { //底部滑入，底部滑出
        if (isGone) {
            if (view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(view, "translationY", 180f, 0f)
                        .setDuration(goneAnimTime.toLong())
                        .start()
            }
        } else {
            if (view.visibility != View.GONE) {
                view.animate()
                        .translationY(view.height.toFloat())
                        .setDuration(goneAnimTime.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                view.visibility = View.GONE
                            }
                        })
            }
        }
    } else if (goneAnimType == 3) { //alpha 0->1 透明进入，透明退出
        if (isGone) {
            if (view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(goneAnimTime.toLong()).start()
            }
        } else {
            if (view.visibility != View.GONE) {
                val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
                alpha.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.GONE
                    }
                })
                alpha.setDuration(goneAnimTime.toLong()).start()
            }
        }
    } else if (goneAnimType == 4) { //立即visable，延时gone
        if (isGone) {
            if (view.visibility != View.VISIBLE) {
                view.visibility = View.VISIBLE
            }
        } else {
            if (view.visibility != View.GONE) {
                val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 1f)
                alpha.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.GONE
                    }
                })
                alpha.setDuration(goneAnimTime.toLong()).start()
            }
        }
    }
}

/**
 * @param color  @{0xffF2F3F8}
 * @param radius @{3}
 */
@BindingAdapter(value = ["background_solidColor0x", "background_radius_dp", "background_pressedColor", "background_cornerType"],
        requireAll = false)
fun setViewBackground(v: View, color: Int, radiusDp: Float, backgroundPressedColor: Boolean,
                      cornerType: CornerType? = CornerType.ALL) {
    //初始化一个空对象
    val stalistDrawable = StateListDrawable()
    val drawablePressedFlase = GradientDrawable()
    drawablePressedFlase.setColor(color)
    drawablePressedFlase.setCornerRadius(cornerType, radiusDp)

    stalistDrawable.addState(intArrayOf(-android.R.attr.state_pressed, android.R.attr.state_enabled),
            drawablePressedFlase)
    if (backgroundPressedColor) {
        val drawablePressedTrue = GradientDrawable()
        //            color& 0xa0ffffff 可以把颜色变透明
        drawablePressedTrue.setColor(ColorUtils.blendARGB(color, Color.BLACK, 0.1f))
        drawablePressedTrue.setCornerRadius(cornerType, radiusDp)

        stalistDrawable.addState(intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled),
                drawablePressedTrue)
    } else {
        stalistDrawable.addState(intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled),
                drawablePressedFlase)
    }

    //            //没有任何状态时显示的图片
    //            stalistDrawable.addState(new int[]{}, );
    v.background = stalistDrawable
}

fun GradientDrawable.setCornerRadius(cornerType: CornerType?, radiusDp: Float) {
    val r = dp2px(radiusDp.toInt()).toFloat()
    var array: FloatArray? = null
    when (cornerType) {
        CornerType.ALL, null -> array = floatArrayOf(r, r, r, r, r, r, r, r)
        CornerType.TOP_LEFT -> array = floatArrayOf(r, r, 0f, 0f, 0f, 0f, 0f, 0f)
        CornerType.TOP_RIGHT -> array = floatArrayOf(0f, 0f, r, r, 0f, 0f, 0f, 0f, 0f, 0f)
        CornerType.BOTTOM_RIGHT -> array = floatArrayOf(0f, 0f, 0f, 0f, r, r, 0f, 0f)
        CornerType.BOTTOM_LEFT -> array = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, r, r)
        CornerType.TOP -> array = floatArrayOf(r, r, r, r, 0f, 0f, 0f, 0f)
        CornerType.BOTTOM -> array = floatArrayOf(0f, 0f, 0f, 0f, r, r, r, r)
        CornerType.LEFT -> array = floatArrayOf(r, r, 0f, 0f, 0f, 0f, r, r)
        CornerType.RIGHT -> array = floatArrayOf(0f, 0f, r, r, r, r, 0f, 0f)
        CornerType.OTHER_TOP_LEFT -> array = floatArrayOf(0f, 0f, r, r, r, r, r, r)
        CornerType.OTHER_TOP_RIGHT -> array = floatArrayOf(r, r, 0f, 0f, r, r, r, r)
        CornerType.OTHER_BOTTOM_RIGHT -> array = floatArrayOf(r, r, r, r, 0f, 0f, r, r)
        CornerType.OTHER_BOTTOM_LEFT -> array = floatArrayOf(r, r, r, r, r, r, 0f, 0f)
        CornerType.DIAGONAL_FROM_TOP_LEFT -> array = floatArrayOf(r, r, 0f, 0f, r, r, 0f, 0f)
        CornerType.DIAGONAL_FROM_TOP_RIGHT -> array = floatArrayOf(0f, 0f, r, r, 0f, 0f, r, r)
    }
    cornerRadii = array
}


@BindingAdapter(value = ["rpt_number"])
fun setRedPointNumber(v: RedPointTextView, pointViewNumber: Int) {
    v.setNumber(pointViewNumber)
}

@BindingAdapter(value = ["adapter", "adapterLayoutManager", "adapterGridCount", "adapterFirstItemVerticalSpacingDp", "adapterFirstItemHorizontalSpacingDp"],
        requireAll = false)
fun setRecyclerViewAdapter(recyclerView: RecyclerView,
                           adapter: BaseQuickAdapter<*, *>?,
                           layoutManager: Int? = null,
                           gridCount: Int = 3,
                           adapterFirstItemVerticalSpacingDp: Int? = null,
                           adapterFirstItemHorizontalSpacingDp: Int? = null) {


    adapter?.let { recyclerView.adapter = adapter }
    adapterFirstItemVerticalSpacingDp?.let {
        recyclerView.addItemDecoration(FirstItemVerticalMarginDecoration(it))
    }
    adapterFirstItemHorizontalSpacingDp?.let {
        recyclerView.addItemDecoration(FirstItemHorzontalMarginDecoration(it))
    }
    when (layoutManager) {
        1, null -> recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        2 -> recyclerView.layoutManager = LinearLayoutManager(recyclerView.context, HORIZONTAL, false)
        3 -> recyclerView.layoutManager = GridLayoutManager(recyclerView.context, gridCount)
    }


}

@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("belowStatusBarMargin")
fun belowStatusBarMargin(view: View, needBelowStatusBar: Boolean?) {
    needBelowStatusBar?.let {
        if (it) {
            view.updateLayoutParams<MarginLayoutParams> {
                topMargin = ScreenUtils.getStatusBarHeight()
            }
        }
    }
}

@BindingAdapter("belowStatusBarPadding")
fun belowStatusBarPadding(view: View, needBelowStatusBar: Boolean?) {
    needBelowStatusBar?.let {
        if (it) {
            view.setPadding(view.paddingLeft,
                    view.paddingTop + ScreenUtils.getStatusBarHeight(),
                    view.paddingRight,
                    view.paddingBottom)
        }
    }
}

@BindingAdapter("ggfl_list_String")
fun setGGFlowLayoutRes(view: GGFlowLayout<String>, list: ArrayList<String>) {
    view.setViewList(list)
}

@BindingAdapter(value = ["srl_headerColorblock", "srl_enableLoadMore"], requireAll = false)
fun setBelowStatusBar(refreshLayout: SmartRefreshLayout, headerColorblock: Boolean?, enableLoadMore: Boolean?) {
    headerColorblock?.let {
        refreshLayout.setRefreshHeader(Constants.getRefreshHeader(refreshLayout.context, Constants.DEFAULT_BLOCK))
    }
    enableLoadMore?.let {
        refreshLayout.setEnableLoadMore(it)
    }
}

@BindingAdapter(value = ["cm_RightText", "cm_RightTextColor"], requireAll = false)
fun setRightText(commonMenu: CommonMenu, rightText: CharSequence?, color: Int) {
    commonMenu.setRightText(rightText)
    if (color != 0) {
        commonMenu.setRightTextColor(color)
    }
}

//TextView someThing
@SuppressLint("UseCompatLoadingForDrawables")
@BindingAdapter(value = [
    "text",
    "textHolder",
    "textSizeSp",
    "textColor0xff",
    "textLeftDrawable",
    "textLeftDrawableHeightDp"
], requireAll = false)
fun setText(textView: TextView,
            text: String?,
            textHolder: String?,
            textSizeSp: Int?,
            textColor: Int?,
            textLeftDrawable: Int?,
            textLeftDrawableHeight: Int?) {

    textColor?.let {
        textView.setTextColor(it)
    }
    textSizeSp?.let {
        textView.textSize = textSizeSp.toFloat()
    }
    val realText = text.or(textHolder).or(textView.text.toString())
    val oldText = textView.text
    if (!SomeUtil.haveContentsChanged(realText, oldText)) {
        return  // 数据没有变化不进行刷新视图
    }
    textView.text = realText

    textLeftDrawable?.let {
        val drawable: Drawable = textView.context.resources.getDrawable(textLeftDrawable)
        val h = textLeftDrawableHeight ?: 10
        drawable.setBounds(0, 0, dp2px(h), dp2px(h))
        textView.setCompoundDrawables(drawable, null, null, null)
    }

}

/**
 *  textColor 必须传 0xff000000 格式
 */
@BindingAdapter(value = ["android:text", "android:textColor", "fbt_bold_size"], requireAll = false)
fun setboldText(textView: FakeBoldTextView, text: String?, color: Int?, fbt_bold_size: Float?) {
    color?.let { textView.color = color }
    fbt_bold_size?.let { textView.boldSize = fbt_bold_size }
    text?.let { textView.setBoldText(text) }
}


@BindingAdapter(value = ["htmlText", "htmlTextRes", "htmlTextHolder", "htmlTextNeedClick"], requireAll = false)
fun setHtmlText(textView: TextView, value: String?, valueRes: Int?, htmlTextHolder: String?, needClick: Boolean? = false) {
    value?.let { it ->
        if (TextUtils.isEmpty(it)) {
            return
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
            valueRes?.let {
                textView.text = Html.fromHtml(textView.context.getString(it), Html.FROM_HTML_MODE_COMPACT)
            }
        } else {
            @Suppress("DEPRECATION")
            textView.text = Html.fromHtml(it)
            valueRes?.let {
                @Suppress("DEPRECATION")
                textView.text = Html.fromHtml(textView.context.getString(it))
            }
        }
        htmlTextHolder?.let {
            if (TextUtils.isEmpty(textView.text.toString().replace("\n", "").replace("\t", ""))) {
                textView.text = htmlTextHolder
            }
        }
        needClick?.let { it1 ->
            if (it1) {
                textView.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }
}


@BindingAdapter(value = ["text_underLine"], requireAll = false)
fun setTextUnderLine(textView: TextView, text: CharSequence?) {
    text?.let {
        val oldText = textView.text
        if (!SomeUtil.haveContentsChanged(text, oldText) || TextUtils.isEmpty(text)) {
            return  // 数据没有变化不进行刷新视图
        }
        val ss = SpannableString(text)
        ss.setSpan(UnderlineSpan(), 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = ss
    }
}

//View someThing
@BindingAdapter("android:background")
fun setBackground(view: View, resource: Int) {
    view.setBackgroundResource(resource)
}

@BindingAdapter("android:enabled")
fun setEnabled(view: View, enable: Boolean) {
    view.isEnabled = enable
}


@BindingAdapter("selected")
fun setViewSelected(view: View, selected: Boolean) {
    view.isSelected = selected
}

@BindingAdapter(value = ["visable"])
fun setVisable(v: View, visable: Boolean) {
    v.setVisable(visable)
}

@BindingAdapter(value = ["alpha"])
fun setViewAlpha(v: View, alpha: Float) {
    v.alpha = alpha
}
