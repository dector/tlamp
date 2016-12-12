package io.github.dector.tlamp.color_wheel

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class ColorWheelView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    interface OnColorSelectedListener {
        fun onColorSelected(color: Int)
    }

    var colorSelectedListener: OnColorSelectedListener? = null

    private val colors: Array<Int> = arrayOf(
            0xfefe33, 0xfabc02, 0xfb9902,
            0xfd5308, 0xfe2712, 0xa7194b,
            0x8601af, 0x3d01a4, 0x0247fe,
            0x0392ce, 0x66b032, 0xd0ea2b)

    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt()
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val bounds = RectF()

    private val startAngle = -90 - (180) / colors.size
    private val sweep = 360F / colors.size
    private val stepping = 8F
    private val strokeWidth = 72F

    private var selectedColor = 0

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val x = e.x - bounds.centerX()
            val y = height - e.y - 1 - bounds.centerY()

            val x2 = x * x
            val y2 = y * y
            val dist = Math.sqrt(x2.toDouble() + y2.toDouble())
            val r = bounds.width() / 2
            if (r - strokeWidth <= dist && dist <= r) {
                val rawPhi = Math.atan2(x.toDouble(), y.toDouble())
                val rawPhiDeg = Math.toDegrees(rawPhi) + sweep / 2
                val phi = if (rawPhiDeg < 0) rawPhiDeg + 360 else rawPhiDeg
                val index = (phi / sweep).toInt()

                selectedColor = colors[index]

                colorSelectedListener?.onColorSelected(selectedColor)

                invalidate()
            }

            return true
        }

        override fun onDown(e: MotionEvent?) = true
    }
    private val gestureDetector = GestureDetector(context, gestureListener)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(LAYER_TYPE_SOFTWARE, null)

        if (isInEditMode) {
            selectedColor = 0xfd5308
        }
    }

    fun setSelectedColor(color: Int) {
        selectedColor = color

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bounds.setInt(left = paddingLeft,
                right = w - paddingRight,
                top = paddingTop,
                bottom = h - paddingBottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)

        val size = Math.min(w, h)

        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0..(colors.size - 1)) {
            p.color = colors[i].solid()

            canvas.drawArc(bounds, startAngle + i * sweep, sweep, true, p)
        }

        canvas.drawCircle(bounds.centerX(), bounds.centerY(), (bounds.width() - 2 * strokeWidth) / 2, clearPaint)

        p.color = selectedColor.solid()
        canvas.drawCircle(bounds.centerY(), bounds.centerY(), 96F, p)
    }

    override fun onTouchEvent(event: MotionEvent?) = gestureDetector.onTouchEvent(event)
}

fun RectF.setInt(left: Int, right: Int, top: Int, bottom: Int) {
    set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun Int.solid() = this or 0xFF000000.toInt()

fun Int.noAlpha() = this and 0xFFFFFF