package ru.arvata.pomor.util

import android.app.Activity
import android.content.Intent

// usage:
/*
startActivity<MainActivity>()
startActivity<MainActivity> {
  it.putExtra("extra_1", "extra value")
}
startActivity<MainActivity>(500)
*/

inline fun <reified T : Activity> Activity.startActivity(code: Int? = null,
                                                         noinline params: ((Intent) -> Unit)? = null) {
    val intent = Intent(this, T::class.java)
    params?.invoke(intent)
    if(code != null) {
        startActivityForResult(intent, code)
    } else {
        startActivity(intent)
    }
}