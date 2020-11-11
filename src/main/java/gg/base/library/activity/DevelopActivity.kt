package gg.base.library.activity;

import gg.base.library.R
import gg.base.library.base.BaseActivity
import gg.base.library.base.others.InitConfigData
import gg.base.library.util.SPUtils2
import gg.base.library.util.toast
import gg.base.library.vm.DevelopActivityViewModel
import kotlin.system.exitProcess

class DevelopActivity : BaseActivity() {
    lateinit var mViewModel: DevelopActivityViewModel

    override fun getActivityInitConfig(): InitConfigData {
        mViewModel = getActivityViewModel()
        return InitConfigData(layoutId = R.layout.frame_activity_layout_develop,
                              viewModel = mViewModel,
                              showActionBar = false,
                              onClickProxy = OnClickProxy())
    }

    override fun init() {
        setHasSetDevelopMode(true)

        mViewModel.testUrl.value = isTestUrl()
        mViewModel.showLogButton.value = isShowLogButton()

        mViewModel.testUrl.observe(this, {
            setTestUrl(it)
        })
        mViewModel.showLogButton.observe(this, {
            setShowLogButton(it)
        })

    }

    inner class OnClickProxy {
        fun closeLog() {

        }

        fun showLog() {

        }

        fun restart() {
            toast("退出登录重新进入才能生效")
            exitProcess(0)
        }

        fun copy() {

        }
    }


    companion object {
        private const val DEVELOP_MODE = "DEVELOP_MODE_randomstrasd1"

        fun setHasSetDevelopMode(value: Boolean) {
            SPUtils2.put(DEVELOP_MODE, value)
        }

        fun isHasSetDevelopMode(): Boolean {
            return SPUtils2.get2(DEVELOP_MODE, false)
        }

        private const val TEST_URL = "TEST_URL_randomstr"

        fun setTestUrl(value: Boolean) {
            SPUtils2.put(TEST_URL, value)
        }

        fun isTestUrl(): Boolean {
            return SPUtils2.get2(TEST_URL, false)
        }

        private const val SHOW_LOG_BUTTON = "SHOW_HTTP_DIALOG_randomstr"

        fun setShowLogButton(value: Boolean) {
            SPUtils2.put(SHOW_LOG_BUTTON, value)
        }

        fun isShowLogButton(): Boolean {
            return SPUtils2.get2(SHOW_LOG_BUTTON, false)
        }
    }


}