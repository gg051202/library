package com.dongjin.mylibrary

import org.junit.Test
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.reflect.UndeclaredThrowableException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(membersOf<Student>().joinToString("\n"))
    }
    inline fun <reified T> membersOf() = T::class.members

    class Student(val name:String )
}