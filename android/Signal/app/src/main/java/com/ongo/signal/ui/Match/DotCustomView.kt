package com.ongo.signal.ui.match

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ongo.signal.data.model.match.Dot
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class DotCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val originDots = mutableListOf<Dot>()

    private val pointPaint = Paint().apply {
        color = 0xD1180B.toInt()
//        color = 0xFF64FFCE.toInt()
        isAntiAlias = true
    }

    private val pointRadius = 24f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        originDots.forEach { dot ->
            pointPaint.alpha = (dot.alpha * 255).toInt()
            canvas.drawCircle(dot.x, dot.y, pointRadius, pointPaint)
        }
    }

    fun addDot(dots: List<Dot>) {
        val newUserIds = dots.map { it.userId }.toSet()
        val iterator = originDots.iterator()

        while (iterator.hasNext()) {
            val dot = iterator.next()
            if (dot.userId !in newUserIds) {
                iterator.remove()
            }
        }

        originDots.forEach { it.alpha = 1f }


        dots.forEach { newDot ->
            if (originDots.none { it.userId == newDot.userId }) {
                calculatePosition(newDot)
                startFadeInAnimation(newDot)
                originDots.add(newDot)
            }
        }
        Timber.d("$originDots")

        invalidate()
    }

    private fun calculatePosition(dot: Dot) {
        val centerX = width / 2f
        val centerY = height / 2f

        val circleRadius = centerX - pointRadius
        val scaledDistance = (dot.distance / 10) * circleRadius

        val angle = when (dot.quadrant) {
            1 -> Random.nextDouble(0.0, Math.PI / 2)
            2 -> Random.nextDouble(Math.PI / 2, Math.PI)
            3 -> Random.nextDouble(Math.PI, 3 * Math.PI / 2)
            4 -> Random.nextDouble(3 * Math.PI / 2, 2 * Math.PI)
            else -> Random.nextDouble(0.0, 2 * Math.PI)
        }

        val x = centerX + scaledDistance * cos(angle)
        val y = centerY - scaledDistance * sin(angle)

        dot.x = x.toFloat()
        dot.y = y.toFloat()

    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
    }

    private fun startFadeInAnimation(dot: Dot) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            dot.alpha = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }
}