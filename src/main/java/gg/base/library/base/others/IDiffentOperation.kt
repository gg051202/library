package gg.base.library.base.others

import android.os.Bundle

/**
 * Created by sss on 2020/8/20 11:42.
 * email jkjkjk.com
 */
interface IDiffentOperation {

    fun showLoadingView(type: LoadingViewStatus = LoadingViewStatus())

    fun hideLoadingView(type: LoadingViewStatus = LoadingViewStatus())

    fun goActivity(clazz: Class<*>, bundle: Bundle? = null)

    fun goActivityForResult(clazz: Class<*>, bundle: Bundle? = null, requestCode: Int)

    fun hideAllView()

    fun showAllView()

    fun showErrView(msg: String = "哎呀呀，加载失败了~", retryCallback: (() -> Unit)?)

    fun hideErrView()

}