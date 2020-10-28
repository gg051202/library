/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gg.base.library.util

import com.blankj.utilcode.util.EncodeUtils
import gg.base.library.Constants
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors] or as a [OkHttpClient.networkInterceptors].
 *
 * The format of the logs created by this class should not be considered stable and may
 * change slightly between releases. If you need a stable logging format, use your own interceptor.
 */
class MyLogInterceptor : Interceptor {
    private val utf8: Charset = Charset.forName("UTF-8")


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!Constants.SHOW_LOG) {
            return chain.proceed(chain.request())
        }

        val toString = chain.request().url.toString()
        val list = listOf("cardIM/app/IMAccount",
                "api/message/listInfo",
                "ardIM/app/merchantSign",
                "api/serviceNotice/list")
        list.forEach {
            if (toString.contains(it)) {
                return chain.proceed(chain.request())
            }
        }

        val request = chain.request()
        val requestBody = request.body
        //        if (level == Level.NONE) {
        //            return chain.proceed(request)
        //        }
        //
        //        val logBody = level == Level.BODY
        //        val logHeaders = logBody || level == Level.HEADERS
        //

        //
        //        val connection = chain.connection()
        //        var requestStartMessage = ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        //        if (!logHeaders && requestBody != null) {
        //            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        //        }
        //        logger.log(requestStartMessage)
        //

        val startNs = System.nanoTime()
        val response: Response
        response = chain.proceed(request)

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        LL.i("【地址】${request.method.toUpperCase(Locale.ROOT)} ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)($bodySize body)")


        val headers = request.headers

        //        if (requestBody != null) {
        //            // Request body headers are only present when installed as a network interceptor. When not
        //            // already present, force them to be included (if available) so their values are known.
        //            requestBody.contentType()?.let {
        //                if (headers["Content-Type"] == null) {
        //                    LL.i("Content-Type: $it")
        //                }
        //            }
        //            if (requestBody.contentLength() != -1L) {
        //                if (headers["Content-Length"] == null) {
        //                    LL.i("Content-Length: ${requestBody.contentLength()}")
        //                }
        //            }
        //        }

        logHeader(headers)

        //打印请求体
        if (requestBody == null) {
            LL.i("--> END ${request.method} 不需要打印：requestBody == null")
        } else if (bodyHasUnknownEncoding(request.headers)) {
            LL.i("--> END ${request.method} (encoded body omitted) 不需要打印：bodyHasUnknownEncoding")
        } else if (requestBody.isDuplex()) {
            LL.i("--> END ${request.method} (duplex request body omitted) 不需要打印：requestBody.isDuplex")
        } else if (requestBody.isOneShot()) {
            LL.i("--> END ${request.method} (one-shot body omitted) 不需要打印：requestBody.isOneShot()")
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)

            val contentType = requestBody.contentType()
            val charset: Charset = contentType?.charset(utf8) ?: utf8
            //打印请求体
            if (buffer.isProbablyUtf8()) {
                val s = buffer.readString(charset)
                try {
                    s.split("&").forEach {
                        if (it.contains("info=")) {
                            LL.i(EncodeUtils.urlDecode("【参数】$it"))
                        }
                    }
                } catch (e: Exception) {

                }
                //                LL.i("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
            } else {
                LL.i("--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted) 不需要打印：！buffer.isProbablyUtf8")
            }
        }

        if (!response.promisesBody()) {
            LL.i("<-- END HTTP不需要打印返回体")
        } else if (bodyHasUnknownEncoding(response.headers)) {
            LL.i("<-- END HTTP (encoded body omitted)不需要打印返回体")
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            var gzippedLength: Long? = null
            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(utf8) ?: utf8

            if (!buffer.isProbablyUtf8()) {
                LL.i("<-- END HTTP (binary ${buffer.size}-byte body omitted)不需要打印!buffer.isProbablyUtf8()")
                return response
            }

            //打印response
            if (contentLength != 0L) {
                val readString = buffer.clone().readString(charset)
                LL.i("【结果】${JsonFormatTool.formatJson(CommonUtils.unicodeToUTF_8(readString))} \n\n")
            }

            //            if (gzippedLength != null) {
            //                LL.i("<-- END HTTP (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
            //            } else {
            //                LL.i("<-- END HTTP (${buffer.size}-byte body)")
            //            }
        }

        return response
    }

    private fun logHeader(headers: Headers) {
        val sb = StringBuilder("【头部】")

        headers.forEach {
            when (it.first) {
                "Cache-Control", "Content-Type", "X-Powered-By", "Date", "Content-Length", "Server", "X-AspNet-Version" -> 1
                else -> sb.append("${it.first}:${it.second}\n")
            }
        }

        LL.i(sb.toString())
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) && !contentEncoding.equals("gzip",
                ignoreCase = true)
    }
}


fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}
