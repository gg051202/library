package gg.base.library.base

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.dongjin.mylibrary.BR
import com.dongjin.mylibrary.R
import com.gyf.immersionbar.ImmersionBar
import gg.base.library.Constants
import gg.base.library.base.others.*
import gg.base.library.util.*
import gg.base.library.widget.MyDialog
import kotlinx.android.synthetic.main.frame_activity_default_err_view.*
import kotlinx.android.synthetic.main.frame_custom_layout_base_loading_view.*
import kotlinx.android.synthetic.main.frame_custom_layout_base_loading_view.loadingTextView
import kotlinx.android.synthetic.main.frame_custom_layout_my_action_bar.*
import me.jessyan.autosize.internal.CustomAdapt
import java.util.*


/**
 * Created by sss on 2020/8/19 09:40.
 * email jkjkjk.com
 */
abstract class BaseActivity : AppCompatActivity(),
        IRunOperation by RunOperationImpl(),
        IDiffentOperation,
        CustomAdapt {

    private lateinit var mBinding: ViewDataBinding

    private var lineLoadingView: LottieAnimationView? = null
    private var dialogLoadingView: View? = null
    private var defaultErrView: View? = null
    private var mLoadingViewAnimator: ObjectAnimator? = null
    var mActivityProvider: ViewModelProvider? = null
    protected lateinit var mActivity: BaseActivity
    var mActionBar: View? = null
    lateinit var mActivityInitConfig: InitConfigData
    var mOnBackClickFun: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        //        lifecycle.addObserver(BaseLifecycleObserver())
        setFullScreen()

        mActivityInitConfig = getActivityInitConfig()
        mBinding = DataBindingUtil.setContentView(this, mActivityInitConfig.layoutId)
        mBinding.lifecycleOwner = this
        mBinding.setVariable(BR.vm, mActivityInitConfig.viewModel)
        mBinding.setVariable(BR.onClickProxy, mActivityInitConfig.onClickProxy)

        AppManager.getAppManager().addActivity(this)

        if (mActivityInitConfig.showActionBar) {
            initActionBar(this)
            initMenu(this)
        }

        init(savedInstanceState)
        setListener()

        //以下是开发者代码
        NormalUtil.autoGo(this)
        addLogButton(mBinding, this)
        addActivityInfo(mBinding, this)
    }


    abstract fun getActivityInitConfig(): InitConfigData

    abstract fun init(savedInstanceState: Bundle?)

    open fun setListener() {

    }

    //*********************ActionBar设置相关*********************
    fun setTitle(title: String) {
        baseTitleTextView.text = title
    }

    open fun addMenu(imgRes: Int, listener: View.OnClickListener) {
        mActivityInitConfig.viewModel.menuResList.value?.let { it ->
            it.forEach { data ->
                if (data.imgRes == imgRes) {
                    return
                }
            }
            val menuResList = mActivityInitConfig.viewModel.menuResList
            menuResList.value?.add(MenuData(imgRes = imgRes, listener = listener))
            menuResList.postValue(menuResList.value)
        }
    }

    protected open fun addMenu(menuText: String, listener: View.OnClickListener) {
        mActivityInitConfig.viewModel.menuResList.value?.let { it ->
            it.forEach { data ->
                if (data.name == menuText) {
                    return
                }
            }
            val menuResList = mActivityInitConfig.viewModel.menuResList
            menuResList.value?.add(MenuData(name = menuText, listener = listener))
            menuResList.postValue(menuResList.value)
        }
    }
    //*********************ActionBar设置相关*********************


    /**
     * 拿到当前Activity的ViewModel
     */
    inline fun <reified T : BaseViewModel> getActivityViewModel(): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this)
        }
        val viewModel = mActivityProvider!!.get(T::class.java)
        setBaseViewModelOber(viewModel, this, this)
        return viewModel
    }

    /**
     * 显示一个默认的dialog
     */
    override fun showLoadingView(loadingViewStatus: LoadingViewStatus) {
        when (loadingViewStatus.type) {
            LoadingViewStatus.Type.ROUND_CIRCLE -> {
                if (dialogLoadingView == null) {
                    dialogLoadingView = LayoutInflater.from(mActivity)
                            .inflate(R.layout.frame_custom_layout_base_loading_view, null, false)
                    val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.gravity = Gravity.CENTER
                    addContentView(dialogLoadingView, layoutParams)
                }
                dialogLoadingView?.let {
                    dialogLoadingView?.visibility = View.VISIBLE
                    loadingTextView.text = loadingViewStatus.msg
                    if (mLoadingViewAnimator == null) {
                        mLoadingViewAnimator = ObjectAnimator.ofFloat(loading, "rotation", 0f, 360f)
                    }
                    mLoadingViewAnimator?.let {
                        it.duration = 1500
                        it.repeatCount = 9999
                        it.interpolator = LinearInterpolator()
                        it.repeatMode = ValueAnimator.RESTART

                        if (!it.isStarted) {
                            it.start()
                        }
                    }
                }
            }
            LoadingViewStatus.Type.LINE -> {
                if (lineLoadingView == null) {
                    lineLoadingView = LottieAnimationView(mActivity)
                    lineLoadingView
                    lineLoadingView?.let {
                        it.setAnimation("frame_anim_loading_line.json")
                        it.repeatCount = LottieDrawable.INFINITE
                        val layoutParams = FrameLayout.LayoutParams(AutoSizeTool.dp2px(100),
                                AutoSizeTool.dp2px(200))
                        layoutParams.gravity = Gravity.CENTER
                        addContentView(it, layoutParams)
                    }
                }
                lineLoadingView?.let {
                    it.setGone(true)
                    it.playAnimation()
                }

            }
        }

    }

    override fun hideLoadingView(loadingViewStatus: LoadingViewStatus) {
        mLoadingViewAnimator?.let {
            if (it.isRunning) {
                it.cancel()
            }
            dialogLoadingView?.setVisibility(View.GONE)
        }
        lineLoadingView?.let {
            it.cancelAnimation()
            it.setGone(false)
        }
    }

    override fun showErrView(msg: String, retryCallback: (() -> Unit)?) {
        if (defaultErrView == null) {
            defaultErrView = View.inflate(mActivity, R.layout.frame_activity_default_err_view, null)
            val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)
            addContentView(defaultErrView, layoutParams)
        }
        defaultErrView?.let {
            defaultErrView?.visibility = View.VISIBLE
            loadingErrMsgTextView.text = msg
            loadingErrRetryTextView.setOnClickListener {
                hideErrView()
                retryCallback?.invoke()
            }
        }
    }


    override fun hideErrView() {
        defaultErrView?.visibility = View.GONE
    }

    fun setBackClickListener(func: () -> Unit) {
        mOnBackClickFun = func
    }

    override fun onResume() {
        super.onResume()
        NormalUtil.autoRecordActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancle()
        lineLoadingView?.let {
            it.cancelAnimation()
            lineLoadingView = null
        }
        mLoadingViewAnimator?.let {
            it.cancel()
            mLoadingViewAnimator = null
        }
        mBinding.unbind()
        AppManager.getAppManager().removeActivity(this)

    }

    /**
     * 设置全屏
     */
    fun setFullScreen() {
        ImmersionBar.with(this).statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .navigationBarColor(R.color.white) //导航栏颜色，不写默认黑色
                .init()
    }

    override fun finish() {
        super.finish()
        cancle()
    }

    fun finish(delay: Long) {
        post(delayTime = delay) {
            finish()
        }
    }

    override fun isBaseOnWidth(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    override fun getSizeInDp(): Float {
        return 360f
    }

    //*********************申请权限代码 start*********************

    private val requestPermission = 1221
    private var list = mutableListOf<PermissionData>()
    private lateinit var succ: () -> Unit
    private var fail: (() -> Boolean)? = null

    open fun checkPermission(succ: () -> Unit, fail: (() -> Boolean)? = null, vararg permissions: String) {
        if (!NormalUtil.isOver6_0()) { //如果是6.0以下系统，不需要验证权限
            succ()
            return
        }
        this.succ = succ
        this.fail = fail
        list = ArrayList<PermissionData>()
        for (permission in permissions) {
            val data = PermissionData()
            data.permissionName = permission
            data.isGranted = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            if (!data.isGranted) { //对要申请的权限进行一次筛选，list里的数据为需要筛选的权限
                list.add(data)
            }
        }
        if (list.isNotEmpty()) {
            val strs = arrayOfNulls<String>(list.size)
            var i = 0
            for (data in list) {
                strs[i++] = data.permissionName
            }
            ActivityCompat.requestPermissions(this, strs, requestPermission)
        } else {
            this.succ()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != requestPermission) {
            return
        }
        var failPermissionName = ""
        var isAllGranted = 0
        for (i in list.indices) {
            val item: PermissionData = list[i]

            if (i < grantResults.size) {
                item.isResult = grantResults[i] == PackageManager.PERMISSION_GRANTED
            } else {
                item.isResult = false
            }
            if (!item.isResult) {
                isAllGranted++ //如果有一个申请结果失败，就自增1，表示申请失败了
                when (item.permissionName) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE -> failPermissionName = "读写文件"
                    Manifest.permission.CALL_PHONE -> failPermissionName = "拨打电话"
                    Manifest.permission.CAMERA -> failPermissionName = "拍摄照片"
                    Manifest.permission.RECORD_AUDIO -> failPermissionName = "录音"
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION -> failPermissionName = "定位"
                    else -> {
                    }
                }
            }
        }
        if (isAllGranted == 0) {
            this.succ()
        } else {
            fail?.let { it() }
            if (fail == null || !fail!!.invoke()) {
                val ssb = SpannableStringBuilder()
                val s = SpannableString("权限管理")
                s.setSpan(ForegroundColorSpan(Constants.DEFAULT_PRIMARY),
                        0,
                        4,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.append("暂未允许$failPermissionName，您可以在${s}中开启")

                MyDialog(baseActivity = mActivity,
                        title = "提示",
                        desc = ssb,
                        cancleText = "算了吧",
                        submitText = "去系统设置",
                        submitFun = {
                            GotoPermissionPageUtils(mActivity).jump()
                        },
                        showCancleButton = true).show()
            }
        }
    }
    //*********************申请权限代码 end*********************


    override fun goActivity(clazz: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, clazz)
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun goActivityForResult(clazz: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(this, clazz)
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    override fun hideAllView() {
        mBinding.root.setGone(false)
    }

    override fun showAllView() {
        mBinding.root.setGone(true)
    }


    fun ll(tag: String? = "", msg: Any?) {
        LL.i(tag, msg.toString())
    }

    //    private var lastClickTime: Long = 0
    //    private var lastY = 0f


    //    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    //        val clickSpaceTime = 20
    //        if (ev.action == MotionEvent.ACTION_DOWN) {
    //            lastY = ev.y
    //            var flag = true
    //            val currentClickTime = System.currentTimeMillis()
    //            if (currentClickTime - lastClickTime >= clickSpaceTime) {
    //                flag = false
    //            }
    //            lastClickTime = currentClickTime
    //            if (flag) {
    //                return true
    //            }
    //        } else if (ev.action == MotionEvent.ACTION_MOVE && abs(lastY - ev.y) > 50) {
    //            lastClickTime = System.currentTimeMillis() - clickSpaceTime - 1
    //        }
    //        return super.dispatchTouchEvent(ev)
    //    }
}
