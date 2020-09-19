package gg.base.library.base.others

import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * Created by sss on 2020/8/19 09:42.
 * email jkjkjk.com
 * @desc 自定义个一个便捷实用携程的工具
 */
interface IRunOperation {

    fun run2(tag: String = "defaultTask", block: suspend CoroutineScope.() -> Unit)

    fun post(delayTime: Long, tag: String = "task${Random.nextInt()}", callback: () -> Unit)

    fun loop(delayTime: Long,
             tag: String = "task${Random.nextInt()}",
             needRunAtFirst: Boolean = false,
             callback: () -> Unit)

    fun cancle(tag: String)

    fun cancle()
}
