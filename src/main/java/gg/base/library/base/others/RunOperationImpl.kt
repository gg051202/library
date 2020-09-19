package gg.base.library.base.others

import kotlinx.coroutines.*

/**
 * Created by sss on 2020/8/19 09:45.
 * email jkjkjk.com
 */
class RunOperationImpl : IRunOperation {

    private val viewModelJob = SupervisorJob()

    //作用域，限定为当前Activity有效
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //保存当前所有的job，当重新执行时，自动cancle
    private val jobMap = mutableMapOf<String, Job>()

    override fun run2(tag: String, block: suspend CoroutineScope.() -> Unit) {
        jobMap[tag]?.cancel()
        jobMap[tag] = uiScope.launch(block = block)
    }

    /**
     * @param delayTime 单位，毫秒
     */
    override fun post(delayTime: Long, tag: String, callback: () -> Unit) {
        run2(tag) {
            withContext(Dispatchers.Default) {
                delay(delayTime)
            }
            callback()
        }
    }

    override fun loop(delayTime: Long, tag: String, needRunAtFirst: Boolean, callback: () -> Unit) {
        run2(tag) {
            if (needRunAtFirst) {
                callback()
                withContext(Dispatchers.Default) {
                    delay(delayTime)
                }
            } else {
                withContext(Dispatchers.Default) {
                    delay(delayTime)
                }
                callback()
            }
            loop(delayTime, tag, needRunAtFirst, callback)
        }
    }


    override fun cancle(tag: String) {
        jobMap[tag]?.cancel()
    }

    override fun cancle() {
        viewModelJob.cancel()
    }

}