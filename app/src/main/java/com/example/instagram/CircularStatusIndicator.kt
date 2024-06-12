package com.example.instagram

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CircularStatusIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint: Paint = Paint()
    private var isActive: Boolean = false
    private var statusColor: Int = ContextCompat.getColor(context, android.R.color.holo_red_light)

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CircularStatusIndicator, 0, 0)
            statusColor = typedArray.getColor(R.styleable.CircularStatusIndicator_statusColor, statusColor)
            isActive = typedArray.getBoolean(R.styleable.CircularStatusIndicator_statusActive, isActive)
            typedArray.recycle()
        }
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isActive) {
            paint.color = statusColor
            val radius = width / 2f
            canvas.drawCircle(radius, radius, radius, paint)
        }
    }

    fun setStatusActive(active: Boolean) {
        isActive = active
        invalidate()
    }

    fun setStatusColor(color: Int) {
        statusColor = color
        invalidate()
    }
}
