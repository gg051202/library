package gg.base.library.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.dongjin.mylibrary.BR
import androidx.lifecycle.ViewModelProvider
import gg.base.library.base.others.*
import gg.base.library.base.others.RunOperationImpl
import gg.base.library.base.others.setBaseViewModelOber
import gg.base.library.base.others.IDiffentOperation
import gg.base.library.base.others.IRunOperation
import gg.base.library.util.LL
import gg.base.library.util.setGone

/**
 * Created by sss on 2020/8/19 14:02.
 * email jkjkjk.com
 */
abstract class BaseFragment : Fragment(),
                              IRunOperation by RunOperationImpl(),
                              IDiffentOperation {

    lateinit var mActivity: BaseActivity
    var mActivityProvider: ViewModelProvider? = null
    var mFragmentProvider: ViewModelProvider? = null
    protected lateinit var mViewDataBinding: ViewDataBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity as BaseActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val activityInitConfig = getFragmentInitConfig()
        mViewDataBinding = DataBindingUtil.inflate(inflater, activityInitConfig.layoutId, container, false)
        mViewDataBinding.lifecycleOwner = this
        mViewDataBinding.setVariable(BR.vm, activityInitConfig.viewModel)
        mViewDataBinding.setVariable(BR.onClickProxy, activityInitConfig.onClickProxy)
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    abstract fun getFragmentInitConfig(): InitConfigData

    /**
     * 默认的初始化方法
     */
    protected abstract fun init()

    override fun showLoadingView(loadingViewStatus: LoadingViewStatus) {
        mActivity.showLoadingView(loadingViewStatus)
    }

    override fun hideLoadingView(loadingViewStatus: LoadingViewStatus) {
        mActivity.hideLoadingView(loadingViewStatus)
    }

    inline fun <reified T : BaseViewModel> getActivityViewModel(): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(mActivity)
        }
        val viewModel = mActivityProvider!!.get(T::class.java)
        setBaseViewModelOber(viewModel, mActivity, this)
        return viewModel
    }

    inline fun <reified T : BaseViewModel> getFragmentViewModel(): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        val viewModel = mFragmentProvider!!.get(T::class.java)
        setBaseViewModelOber(viewModel, this, this)
        return viewModel
    }

    override fun goActivity(clazz: Class<*>, bundle: Bundle?) {
        val intent = Intent(context, clazz)
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun goActivityForResult(clazz: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(context, clazz)
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    override fun hideAllView() {
        mViewDataBinding.root.setGone(false)
    }

    override fun showAllView() {
        mViewDataBinding.root.setGone(true)
    }

    override fun showErrView(msg: String, retryCallback: (() -> Unit)?) {
        mActivity.showErrView(msg, retryCallback)
    }


    override fun hideErrView() {
        mActivity.hideErrView()
    }


    override fun onDestroy() {
        super.onDestroy()
        cancle()
    }

    fun ll(tag: String? = "", msg: Any?) {
        LL.i(tag, msg.toString())
    }
}