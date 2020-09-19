package gg.base.library.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gg.base.library.base.others.RunOperationImpl
import gg.base.library.base.others.LoadingViewStatus
import gg.base.library.base.others.IRunOperation
import gg.base.library.base.others.MenuData

/**
 * Created by guilin on 2020/8/19 20:04.
 * email gxw.coder@gmail.com
 */
open class BaseViewModel : ViewModel(),
                           IRunOperation by RunOperationImpl() {

    var toastErrMessage = MutableLiveData<String>()
    var loadingView = MutableLiveData<LoadingViewStatus>()

    /**
     * 提供刷新一个默认的字段，用来刷新BaseRecyclerView
     */
    var httpNewList = MutableLiveData<List<*>>()
    var httpErrMsg = MutableLiveData<String>()

    /**
     * 右上角菜单列表，是图片或者文字
     */
    var menuResList = MutableLiveData(mutableListOf<MenuData>())

    override fun onCleared() {
        super.onCleared()
        cancle()
    }

}
