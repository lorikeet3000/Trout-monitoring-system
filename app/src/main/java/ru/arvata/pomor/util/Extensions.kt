package ru.arvata.pomor.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun <T1: Any, T2: Any, R: Any> let2(p1: T1?, p2: T2?, block: (T1, T2)->R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

fun <T1: Any, T2: Any, T3: Any, R: Any> let3(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3)->R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

fun <T1, T2, R> combine2(source1: LiveData<T1>, source2: LiveData<T2>, combineFunc: (T1?, T2?) -> R?): LiveData<R?> {
    val result = MediatorLiveData<R>()
    result.addSource(source1) {
        result.value = combineFunc(it, source2.value)
    }
    result.addSource(source2) {
        result.value = combineFunc(source1.value, it)
    }
    return result
}

fun <T1, T2, T3, R> combine3(source1: LiveData<T1>, source2: LiveData<T2>, source3: LiveData<T3>, combineFunc: (T1?, T2?, T3?) -> R?): LiveData<R?> {
    val result = MediatorLiveData<R>()
    result.addSource(source1) {
        result.value = combineFunc(it, source2.value, source3.value)
    }
    result.addSource(source2) {
        result.value = combineFunc(source1.value, it, source3.value)
    }
    result.addSource(source3) {
        result.value = combineFunc(source1.value, source2.value, it)
    }
    return result
}

fun <T, R> LiveData<T>.map(mapper: (T) -> R): LiveData<R> = Transformations.map(this, mapper)

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged(editable.toString())
        }
    })
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onTextChanged(p0?.toString() ?: "")
        }

        override fun afterTextChanged(editable: Editable?) {}
    })
}

val Int.dpToPx: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.pxToDp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dpToPx: Float
    get() = this / Resources.getSystem().displayMetrics.density

val Float.pxToDp: Float
    get() = this * Resources.getSystem().displayMetrics.density
