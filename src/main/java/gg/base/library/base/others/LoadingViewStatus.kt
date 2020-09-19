package gg.base.library.base.others


/**
 * Created by guilinlin on 2018/10/28 12:08.
 * email 973635949@qq.com
 */
class LoadingViewStatus(var type: Type = Type.LINE,
                        var isShowing: Boolean = true,
                        var msg: String = "正在加载...") {
    enum class Type {
        ROUND_CIRCLE,
        LINE
    }
}

