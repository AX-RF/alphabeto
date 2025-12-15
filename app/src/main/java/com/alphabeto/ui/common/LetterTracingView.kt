package com.alphabeto.ui.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withSave
import kotlin.math.max

class LetterTracingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33000000")
        textSize = 320f
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private val tracePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF4C7DF0")
        style = Paint.Style.STROKE
        strokeWidth = 28f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#22000000")
        style = Paint.Style.STROKE
        strokeWidth = 32f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#11000000")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val tracePath = Path()
    private val currentSegment = Path()
    private val clipRect = Rect()

    private var lastX = 0f
    private var lastY = 0f
    private var strokeCount = 0
    private var totalLength = 0f

    var letterCharacter: String = ""
        set(value) {
            field = value
            invalidate()
        }

    var listener: TracingListener? = null

    fun clearTracing() {
        tracePath.reset()
        currentSegment.reset()
        strokeCount = 0
        totalLength = 0f
        invalidate()
        listener?.onTracingCleared()
    }

    fun restartTracing() {
        clearTracing()
    }

    fun getStrokeCount(): Int = strokeCount

    fun getTotalLength(): Float = totalLength

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.getClipBounds(clipRect)
        val width = clipRect.width().toFloat()
        val height = clipRect.height().toFloat()

        canvas.drawRect(clipRect, borderPaint)

        if (letterCharacter.isNotBlank()) {
            val xPos = width / 2f
            val yPos = height / 2f - (guidePaint.descent() + guidePaint.ascent()) / 2f
            canvas.drawText(letterCharacter, xPos, yPos, guidePaint)
        }

        canvas.withSave {
            drawPath(tracePath, shadowPaint)
            drawPath(tracePath, tracePaint)
            drawPath(currentSegment, tracePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                startSegment(event.x, event.y)
                invalidate()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                addPoint(event.x, event.y)
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                finishSegment()
                parent.requestDisallowInterceptTouchEvent(false)
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startSegment(x: Float, y: Float) {
        currentSegment.reset()
        currentSegment.moveTo(x, y)
        lastX = x
        lastY = y
    }

    private fun addPoint(x: Float, y: Float) {
        val midX = (x + lastX) / 2f
        val midY = (y + lastY) / 2f
        currentSegment.quadTo(lastX, lastY, midX, midY)
        lastX = x
        lastY = y
    }

    private fun finishSegment() {
        tracePath.addPath(currentSegment)
        val pathLength = measurePath(currentSegment)
        if (pathLength > MIN_STROKE_LENGTH) {
            strokeCount += 1
            totalLength += pathLength
            listener?.onStrokeCompleted(strokeCount, totalLength)
        }
        currentSegment.reset()
    }

    private fun measurePath(path: Path): Float {
        val measure = PathMeasure(path, false)
        var length = 0f
        do {
            length += measure.length
        } while (measure.nextContour())
        return max(length, MIN_STROKE_LENGTH)
    }

    interface TracingListener {
        fun onStrokeCompleted(strokes: Int, totalLength: Float)
        fun onTracingCleared()
    }

    companion object {
        private const val MIN_STROKE_LENGTH = 24f
    }
}
