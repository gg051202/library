package gg.base.library

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.dongjin.mylibrary.BuildConfig
import com.dongjin.mylibrary.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Created by sss on 2020/8/19 17:30.
 * email jkjkjk.com
 */
class Constants {

    companion object {
        var SHOW_LOG = true

        var TEST_URL = false

        var autoRank = false

        const val DEFAULT_PRIMARY = (0xffF54F4C).toInt()
        const val DEFAULT_BLACK = (0xff333333).toInt()
        const val DEFAULT_GREY = (0xff79797a).toInt()
        const val DEFAULT_ERR = (0xff3aeeb).toInt()
        const val DEFAULT_BLOCK = (0xffa0a0b).toInt()

        var FLAVOR: String = ""
        var DEBUG: Boolean = false
        var APPLICATION_ID: String = ""

        fun isDevelop(): Boolean {
            return FLAVOR == "_develop"
        }

        fun isDebug(): Boolean {
            return BuildConfig.DEBUG
        }

        fun isProduct(): Boolean {
            return !DEBUG && FLAVOR == "_product"
        }


        /**
         * 默认的占位图
         */
        private val IMAGE_LOAD_HOLDER: Int = R.drawable.frame_bg_place_holder

        /**
         * 默认的加载错误的图
         */
        private val IMAGE_LOAD_ERR: Int = R.drawable.frame_bg_place_holder


        fun getRequestOptions(radiusPx: Int = 0, cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL): RequestOptions {
            return RequestOptions().transform(CenterCrop(),
                    RoundedCornersTransformation(radiusPx,
                            0,
                            cornerType
                                    ?: RoundedCornersTransformation.CornerType.ALL))
                    .error(IMAGE_LOAD_ERR)
                    .placeholder(IMAGE_LOAD_HOLDER)
                    .timeout(60_1000)
        }

    }

}