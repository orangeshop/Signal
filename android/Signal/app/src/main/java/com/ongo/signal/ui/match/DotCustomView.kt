package com.ongo.signal.ui.match

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ongo.signal.R
import com.ongo.signal.data.model.match.Dot
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class DotCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val originDots = mutableListOf<Dot>()

    private val pointPaint = Paint().apply {
        isAntiAlias = true
    }

    private val pointRadius = 24f

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        originDots.forEach { dot ->
            pointPaint.alpha = (dot.alpha * 255).toInt()
            pointPaint.color = if (dot.isFocused) {
                ContextCompat.getColor(context, R.color.point_dot)
            } else {
                ContextCompat.getColor(context, R.color.normal_dot)
            }
            canvas.drawCircle(dot.x, dot.y, pointRadius, pointPaint)
        }

    }

    fun setDotFocused(userId: Long) {
        originDots.forEach { dot ->
            dot.isFocused = false
        }

        originDots.find { it.userId == userId }?.let { dot ->
            dot.isFocused = true
            originDots.remove(dot)
            originDots.add(dot)
            showProfilePopup(dot)
        }

        invalidate()
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
                loadImage(newDot)
                originDots.add(newDot)
            }
        }
        Timber.d("$originDots")

        invalidate()
    }

    private fun loadImage(dot: Dot) {
        Glide.with(context)
            .asBitmap()
            .load(dot.profileImage)
            .circleCrop()
            .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    dot.profileBitmap = resource
                    invalidate()
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private fun calculatePosition(dot: Dot) {
        val centerX = width / 2f
        val verticalBias = 0.435f
        val centerY = height * verticalBias

        val circleRadius = centerX - pointRadius
        val innerMargin = pointRadius * 2
        val adjustedCircleRadius = circleRadius - innerMargin
        val scaledDistance = (dot.distance / 10) * adjustedCircleRadius


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

    private fun startFadeInAnimation(dot: Dot) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            dot.alpha = animation.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    private fun showProfilePopup(dot: Dot) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.profile_popup, null)

        val popupWindow = PopupWindow(popupView, WRAP_CONTENT, WRAP_CONTENT, false)

        val textView = popupView.findViewById<TextView>(R.id.tv_popup_user_name)
        val imageView = popupView.findViewById<ImageView>(R.id.iv_popup_profile)

        textView.text = dot.userName
        dot.profileBitmap?.let { bitmap ->
            imageView.setImageBitmap(bitmap)
        }

        val location = IntArray(2)
        this.getLocationOnScreen(location)
        val x = location[0] + dot.x.toInt()
        val y = location[1] + dot.y.toInt() - popupWindow.height

        popupWindow.setBackgroundDrawable(null)
        popupWindow.isOutsideTouchable = true
        popupWindow.isTouchable = false


        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val xOffset = (screenWidth * 0.18 + dot.userName.length.toDouble()).toInt()
        val yOffset = (screenHeight * 0.12).toInt()

        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, x - xOffset, y - yOffset)
    }
}