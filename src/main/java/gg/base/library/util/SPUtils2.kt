package gg.base.library.util

import android.content.Context
import android.content.SharedPreferences
import com.blankj.utilcode.util.Utils
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class SPUtils2 {
    companion object {
        /**
         * 保存在手机里面的文件名
         */
        var FILE_NAME = "mysp"

        /**
         * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
         *
         * @param key
         * @param object
         */
        fun put(key: String, ob: Any) {
            val sp = Utils.getApp().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            val editor = sp.edit()
            when (ob) {
                is String -> {
                    editor.putString(key, ob)
                }
                is Int -> {
                    editor.putInt(key, ob)
                }
                is Boolean -> {
                    editor.putBoolean(key, ob)
                }
                is Float -> {
                    editor.putFloat(key, ob)
                }
                is Long -> {
                    editor.putLong(key, ob)
                }
                else -> {
                    editor.putString(key, ob.toString())
                }
            }
            SharedPreferencesCompat.apply(editor)
        }

        /**
         * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
         *
         * @param key
         * @param defaultObject
         */
        inline operator fun <reified T> get(key: String, defaultObject: T): T {
            val sp = Utils.getApp().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            when (defaultObject) {
                is String -> {
                    return sp.getString(key, defaultObject.toString()) as T
                }
                is Int -> {
                    return sp.getInt(key, defaultObject.toInt()) as T
                }
                is Boolean -> {
                    return sp.getBoolean(key, (defaultObject as Boolean)) as T
                }
                is Float -> {
                    return sp.getFloat(key, (defaultObject as Float)) as T
                }
                is Long -> {
                    return sp.getLong(key, (defaultObject as Long)) as T
                }
                else -> return 1 as T
            }
        }

        /**
         * 移除某个key值已经对应的值
         *
         * @param key
         */
        fun remove(key: String?) {
            val sp = Utils.getApp().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove(key)
            SharedPreferencesCompat.apply(editor)
        }

        /**
         * 清除所有数据
         */
        fun clear(context: Context) {
            val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.clear()
            SharedPreferencesCompat.apply(editor)
        }

        /**
         * 查询某个key是否已经存在
         *
         * @param key
         */
        fun contains(key: String?): Boolean {
            val sp = Utils.getApp().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            return sp.contains(key)
        }

        /**
         * 返回所有的键值对
         */
        fun all(context: Context): Map<String, *> {
            val sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            return sp.all
        }

        init {
            FILE_NAME = "mysp"
            //
            //        switch (BuildConfig.FLAVOR) {
            //            case "_develop":
            //                FILE_NAME = "mysp_develop";
            //                break;
            //            case "_test":
            //                FILE_NAME = "mysp_test";
            //                break;
            //            case "_product":
            //                break;
            //            default:
            //                break;
            //        }
        }
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        /**
         * 反射查找apply的方法
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz: Class<*> = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
            }
            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
            editor.commit()
        }
    }

    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }
}