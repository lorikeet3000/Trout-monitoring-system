package ru.arvata.pomor.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import ru.arvata.pomor.R


class ZonedLineChart : LineChart {
    private val redZonePaint: Paint = Paint()
    private val yellowZonePaint: Paint = Paint()
    private val greenZonePaint: Paint = Paint()
    private val pts = FloatArray(4)
    private var upperZoneRed: Boolean = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        redZonePaint.style = Paint.Style.FILL
        redZonePaint.color = ContextCompat.getColor(context, R.color.colorRedExtraLight)

        yellowZonePaint.style = Paint.Style.FILL
        yellowZonePaint.color = ContextCompat.getColor(context, R.color.colorYellowExtraLight)

        greenZonePaint.style = Paint.Style.FILL
        greenZonePaint.color = ContextCompat.getColor(context, R.color.colorGreenExtraLight)
    }

    override fun init() {
        super.init()
        mGridBackgroundPaint.color = Color.rgb(240, 240, 240)
    }

    fun setUpperZoneRed() {
        this.upperZoneRed = true
    }

    fun setUpperZoneGreen() {
        this.upperZoneRed = false
    }

    override fun onDraw(canvas: Canvas) {
        if(!isEmpty) {
            val limitLines = mAxisLeft.limitLines

            if (limitLines == null || limitLines.size != 2)
                return

            val l1 = limitLines[0].limit
            val l2 = limitLines[1].limit

            if(l1 > l2) {
                pts[1] = l1
                pts[3] = l2
            } else {
                pts[1] = l2
                pts[3] = l1
            }

            mLeftAxisTransformer.pointValuesToPixel(pts)

            val upperZonePaint: Paint
            val bottomZonePaint: Paint
            val middleZonePaint = yellowZonePaint

            if(upperZoneRed) {
                upperZonePaint = redZonePaint
                bottomZonePaint = greenZonePaint
            } else {
                upperZonePaint = greenZonePaint
                bottomZonePaint = redZonePaint
            }

            canvas.drawRect(
                mViewPortHandler.contentLeft(),
                mViewPortHandler.contentTop(),
                mViewPortHandler.contentRight(),
                pts[3],
                upperZonePaint
            )

            canvas.drawRect(
                mViewPortHandler.contentLeft(),
                pts[1],
                mViewPortHandler.contentRight(),
                pts[3],
                middleZonePaint
            )

            canvas.drawRect(
                mViewPortHandler.contentLeft(),
                pts[3],
                mViewPortHandler.contentRight(),
                mViewPortHandler.contentBottom(),
                bottomZonePaint
            )
        }

        super.onDraw(canvas)
    }
}