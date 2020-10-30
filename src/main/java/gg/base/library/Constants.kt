package gg.base.library

import android.content.Context
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by sss on 2020/8/19 17:30.
 * email jkjkjk.com
 */
class Constants {

    companion object {

        const val DEFAULT_PRIMARY = (0xffac5bb5).toInt()
        const val DEFAULT_BLACK = (0xff333333).toInt()
        const val DEFAULT_GREY = (0xff79797a).toInt()
        const val DEFAULT_ERR = (0xff3aeeb).toInt()
        const val DEFAULT_BLOCK = (0xfff5f5f5).toInt()

        var DEBUG: Boolean = BuildConfig.DEBUG
        var FLAVOR: String = BuildConfig.FLAVOR

        //todo 如果集成库，需要初始化这些变量，还需要 Utils.init(this);
        var SHOW_LOG = true
        var APPLICATION_ID: String = ""

        fun isDevelop(): Boolean {
            return FLAVOR.contains("_develop")
        }

        fun isProduct(): Boolean {
            return !DEBUG && FLAVOR.contains("_product")
        }

        /**
         * 会根据当前屏幕的比例，设置合适的基准宽度，到达适配适配不同比例的屏幕的效果。
         */
        var BASE_WIDTH = (360 * (19 / 9f / ScreenUtils.getScreenHeight() * ScreenUtils.getScreenWidth())).toInt()


        /**
         * 默认的占位图
         */
        private val IMAGE_LOAD_HOLDER: Int = R.drawable.frame_bg_place_holder

        /**
         * 默认的加载错误的图
         */
        private val IMAGE_LOAD_ERR: Int = R.drawable.frame_bg_place_holder


        fun getRequestOptions(radiusPx: Int = 0, cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL): RequestOptions {
            return RequestOptions()
                    .transform(CenterCrop(), RoundedCornersTransformation(radiusPx, 0, cornerType))
                    .error(IMAGE_LOAD_ERR)
                    .placeholder(IMAGE_LOAD_HOLDER)
                    .timeout(60_1000)
        }


        fun getRefreshHeader(context: Context): ClassicsHeader {
            return getRefreshHeader(context, 0xffffffff.toInt())
        }

        fun getRefreshHeader(context: Context, color: Int): ClassicsHeader {
            return getRefreshHeader(context, color, DEFAULT_GREY)
        }

        fun getRefreshHeader(context: Context, color: Int, textColor: Int): ClassicsHeader {
            ClassicsHeader.REFRESH_HEADER_PULLING = "下拉以刷新"
            ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在刷新"
            ClassicsHeader.REFRESH_HEADER_LOADING = "正在加载"
            ClassicsHeader.REFRESH_HEADER_RELEASE = "释放以刷新"
            ClassicsHeader.REFRESH_HEADER_FINISH = ""
            ClassicsHeader.REFRESH_HEADER_FAILED = ""
            val classicsHeader = ClassicsHeader(context)
            classicsHeader.setBackgroundColor(color)
            classicsHeader.setEnableLastTime(false)
            classicsHeader.setFinishDuration(1)
            classicsHeader.setTextSizeTitle(13f)
            classicsHeader.setAccentColor(textColor)
            classicsHeader.setDrawableSize(13f)
            classicsHeader.setDrawableMarginRight(10f)
            return classicsHeader
        }

        fun getRefreshFooter(context: Context?, color: Int): ClassicsFooter {
            return getRefreshFooter(context, color, DEFAULT_GREY)
        }

        fun getRefreshFooter(context: Context?, color: Int, textColor: Int): ClassicsFooter {
            val classicsFooter = ClassicsFooter(context)
            classicsFooter.setBackgroundColor(color)
            classicsFooter.setFinishDuration(1)
            classicsFooter.setTextSizeTitle(13f)
            classicsFooter.setAccentColor(textColor)
            classicsFooter.setDrawableSize(13f)
            classicsFooter.setDrawableMarginRight(10f)
            return classicsFooter
        }

    }

}