package ru.arvata.pomor.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

private const val TAG = "Preferences"

fun showSimpleDemo(context: Context) {
    val s = getPreferences(context).getString("id_string", null)
    editPreferences(context) {
        it.putString("id_string", "example")
    }
    val s2 = getPreferences(context).getString("id_string", "default")
    Log.i(TAG, "s: $s, s2: $s2")
}

/* Simple Preferences */

fun editPreferences(context: Context, f: (SharedPreferences.Editor) -> Unit) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
        f(this)
        apply()
    }
}

fun getPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
