package ru.arvata.pomor.util

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.TankColor
import ru.arvata.pomor.ui.setTankColor

@Suppress("DEPRECATION")
class TankView : TextView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        background = ContextCompat.getDrawable(this.context, R.drawable.round_background)
        gravity = Gravity.CENTER
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            super.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Large)
        } else {
            super.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large)
        }

        setTankColor(TankColor.Unknown)
    }
}
