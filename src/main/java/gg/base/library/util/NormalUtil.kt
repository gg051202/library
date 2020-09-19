package gg.base.library.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import gg.base.library.Constants
import gg.base.library.base.BaseActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


/**
 * Created by sss on 2020/8/21 13:46.
 * email jkjkjk.com
 */
open class NormalUtil {
    companion object {

        private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')


        fun getRefreshHeader(context: Context, backgroundColor: Int = 0xffffff, textColor: Int = Constants.DEFAULT_GREY): ClassicsHeader {
            ClassicsHeader.REFRESH_HEADER_PULLING = "下拉以刷新"
            ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在刷新"
            ClassicsHeader.REFRESH_HEADER_LOADING = "正在加载"
            ClassicsHeader.REFRESH_HEADER_RELEASE = "释放以刷新"
            ClassicsHeader.REFRESH_HEADER_FINISH = ""
            ClassicsHeader.REFRESH_HEADER_FAILED = "数据获取失败，请重试"
            val classicsHeader = ClassicsHeader(context)
            classicsHeader.setBackgroundColor(backgroundColor)
            classicsHeader.setEnableLastTime(false)
            classicsHeader.setFinishDuration(1)
            classicsHeader.setTextSizeTitle(13f)
            classicsHeader.setAccentColor(textColor)
            classicsHeader.setDrawableSize(13f)
            classicsHeader.setDrawableMarginRight(10f)
            classicsHeader.setFinishDuration(300)

            return classicsHeader
        }

        fun getRefreshFooter(context: Context, backgroundColor: Int = 0xffffff, textColor: Int = Constants.DEFAULT_GREY): ClassicsFooter {
            val classicsFooter = ClassicsFooter(context)
            ClassicsFooter.REFRESH_FOOTER_PULLING = "上拉加载更多"
            ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载"
            ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载..."
            ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新..."
            ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成"
            ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败"
            ClassicsFooter.REFRESH_FOOTER_NOTHING = "没有更多数据了"

            classicsFooter.setBackgroundColor(backgroundColor)
            classicsFooter.setFinishDuration(1)
            classicsFooter.setTextSizeTitle(13f)
            classicsFooter.setAccentColor(textColor)
            classicsFooter.setDrawableSize(13f)
            classicsFooter.setDrawableMarginRight(10f)
            classicsFooter.setFinishDuration(300)

            return classicsFooter
        }

        fun isOver6_0(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }


        fun autoRecordActivity(baseActivity: BaseActivity) {
            if (!Constants.isDevelop()) {
                return
            }

            //当是开发者模式时，如果停留在一个页面5秒，那么下次在进入APP时，会自动重新打开这个界面
            baseActivity.post(5000) {
                val activityName: String = baseActivity::class.java.name
                SPUtils2.put("develop_last_activity_name", activityName)
                //                LL.i("记录 Activity：$activityName")
                val extras = baseActivity.intent.extras
                if (extras != null) {
                    val dataList: MutableList<BundleData> = ArrayList()
                    for (s in extras.keySet()) {
                        dataList.add(BundleData(s!!, extras[s]!!))
                    }
                    SPUtils2.put("develop_last_activity_bundle", Gson().toJson(dataList))
                } else {
                    SPUtils2.put("develop_last_activity_bundle", "")
                }
                //                LL.i("参数：" + SPUtils2["develop_last_activity_bundle", ""])
            }
        }


        fun autoGo(baseActivity: BaseActivity) {
            if (!Constants.isDevelop()) {
                return
            }
            try {
                if (baseActivity::class.java.name.contains("HomePageActivity")) {
                    val activityName = SPUtils2["develop_last_activity_name", ""]
                    val bundle = Bundle()
                    val values = SPUtils2["develop_last_activity_bundle", ""]
                    //                    LL.i("获取路径：$activityName")
                    //                    LL.i("获取参数：$values")
                    if (!TextUtils.isEmpty(values)) {
                        val list = Gson().fromJson<List<*>>(values, object : TypeToken<List<BundleData?>?>() {}.type)
                        for (data in list) {
                            if (data is BundleData) {
                                LL.i(data.key + "," + data.value)
                                when (data.value) {
                                    is String -> {
                                        bundle.putString(data.key, data.value as String)
                                    }
                                    is Int -> {
                                        bundle.putInt(data.key, (data.value as Int))
                                    }
                                    is Float -> {
                                        bundle.putFloat(data.key, (data.value as Float))
                                    }
                                    is Double -> {
                                        bundle.putDouble(data.key, (data.value as Double))
                                    }
                                    is Boolean -> {
                                        bundle.putBoolean(data.key, (data.value as Boolean))
                                    }
                                    is Long -> {
                                        bundle.putLong(data.key, (data.value as Long))
                                    }
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(activityName) && !activityName.contains("BaseActivity") && !activityName.contains("LoginAcitivity")) {
                        val activityClass = Class.forName(activityName)
                        if (!AppManager.getAppManager().checkActivity(activityClass)) {
                            toast("自动跳转至上次停留页面")
                            baseActivity.goActivity(activityClass, bundle)
                        }
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        internal class BundleData(var key: String, var value: Any)


        fun getSingleId(): String? {
            var serial: String? = null
            val m_szDevIDShort = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10 //13 位
            try {
                serial = Build::class.java.getField("SERIAL")[null].toString()
                //API>=9 使用serial号
                return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
            } catch (exception: Exception) {
                //serial需要一个初始化
                serial = "serial" // 随便一个初始化
            }
            //使用硬件信息拼凑出来的15位号码
            return UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        }


        /**
         * 将字符串转md5，小写
         */
        fun MD5(inStr: String): String {
            val inStrBytes = inStr.toByteArray()
            try {
                val MD = MessageDigest.getInstance("MD5")
                MD.update(inStrBytes)
                val mdByte = MD.digest()
                val str = CharArray(mdByte.size * 2)
                var k = 0
                for (i in mdByte.indices) {
                    val temp = mdByte[i]
                    str[k++] = hexDigits[temp.toInt() ushr 4 and 0xf]
                    str[k++] = hexDigits[temp.toInt() and 0xf]
                }
                return String(str)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ""
        }

    }
}

fun hidePhone(phone: String?): String {
    phone?.let {
        return if (phone.length > 8) {
            phone.substring(0, 3) + " **** " + phone.substring(phone.length - 4)
        } else {
            phone
        }
    }
    return ""

}


fun View.setGone(visable: Boolean) {
    visibility = if (visable) View.VISIBLE else View.GONE
}

fun View.setVisable(visable: Boolean) {
    visibility = if (visable) View.VISIBLE else View.INVISIBLE
}

fun toast(msg: String) {
    ToastUtils.showShort(msg)
}


private fun showKeyboard(activity: Activity, isShow: Boolean) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (isShow) {
        if (activity.currentFocus == null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } else {
            imm.showSoftInput(activity.currentFocus, 0)
        }
    } else {
        if (activity.currentFocus != null) {
            imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}

fun hideKeyboard(activity: Activity) {
    showKeyboard(activity, false)
}

fun showKeyboard(activity: BaseActivity, focus: View) {
    focus.requestFocus()
    activity.post(1000) {
        if (focus.isFocused) {
            showKeyboard(activity, true)
        }
    }

}

fun toGson(o: Any): String {
    return Gson().toJson(o)
}
