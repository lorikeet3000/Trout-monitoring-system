package ru.arvata.pomor.util

import java.io.IOException
import java.nio.charset.StandardCharsets

fun getJsonFromAssets(fileName: String): String? {
    return try {
        val input = App.appContext.assets.open(fileName)
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        String(buffer, StandardCharsets.UTF_8)
    } catch (ex: IOException) {
        ex.printStackTrace()
        null
    }
}