package ru.arvata.pomor.util

import android.os.Build
import android.util.Log
import ru.arvata.pomor.BuildConfig

fun log(message: String) {
    printLog(message) {
        Log.d(tag, it)
    }
}

fun loge(message: String?, throwable: Throwable? = null) {
    if(message == null) {
        Log.e(tag, "", throwable)
        return
    }

    printLog(message) {
        val msg = if(it.isEmpty()) {
            " "
        } else {
            it
        }
        Log.e(tag, msg, throwable)
    }
}

private fun printLog(message: String, block: (part: String) -> Unit) {
    if(BuildConfig.DEBUG) {
        split(message).forEach {
            block(it)
        }
    }
}

private const val MAX_TAG_LENGTH = 23
private const val MAX_LOG_LENGTH = 4000

private fun split(message: String): List<String> {
    val length = message.length
    if(length <= MAX_LOG_LENGTH) return listOf(message)

    val list = mutableListOf<String>()
    var i = 0
    do {
        val end =  i + MAX_LOG_LENGTH
        if(end > length) {
            list.add(message.substring(i))
        } else {
            list.add(message.substring(i, end))
        }
        i = end
    } while ((i < length))
    return list
}

private inline val tag: String?
    get() {
        Throwable().stackTrace
            .first { !it.className.contains("LogHelper") }
            ?.let {
                val tag = "[${it.lineNumber}]${it.className.substringAfterLast(".")}#${it.methodName}"

                return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // Tag length limit was removed in API 24.
                    tag
                } else {
                    tag.substring(0, MAX_TAG_LENGTH)
                }
            }
        return "Unknown tag"
    }