package com.ongo.signal.ui.match

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ongo.signal.data.model.match.Dot

class DotCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val pointPaint = Paint().apply {
        color = 0xD1180B.toInt()
//        color = 0xFF64FFCE.toInt()
        isAntiAlias = true
    }

    private val dots = mutableListOf<Dot>()
    private var alphaValue = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val pointRadius = 24f

        dots.forEach { dot ->
            pointPaint.alpha = (dot.alpha * 255).toInt()
            canvas.drawCircle(dot.x, dot.y, pointRadius, pointPaint)
        }
    }

    fun addDot(x: Float, y: Float) {
        val dot = Dot(x, y)
        dots.add(dot)
        startFadeInAnimation(dot)
    }

    private fun startFadeInAnimation(dot: Dot) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            dot.alpha = animation.animatedValue as Float
            invalidate() // 뷰를 다시 그림
        }
        animator.start()
    }
}