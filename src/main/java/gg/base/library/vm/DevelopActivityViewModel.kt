package gg.base.library.vm

import androidx.lifecycle.MutableLiveData
import gg.base.library.base.BaseViewModel

class DevelopActivityViewModel : BaseViewModel() {

    var testUrl = MutableLiveData<Boolean>()
    var showLogButton = MutableLiveData<Boolean>()

//    fun sendHttp() {
//        send(callFun = { it.getWeather() }, succ = { _ ->
//
//        }, fail = { _, msg, _ ->
//            toastErrMessage.postValue(msg)
//        })
//    }
}