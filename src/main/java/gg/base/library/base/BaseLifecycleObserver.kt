package gg.base.library.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import gg.base.library.util.toast

/**
 * Created by sss on 2020/8/19 13:55.
 * email jkjkjk.com
 */
class BaseLifecycleObserver : LifecycleObserver {

//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onStart() {
//    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        toast("123")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun onCreate() {
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    fun onDestory() {
//    }

}