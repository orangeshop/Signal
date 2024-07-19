package com.ongo.signal.util

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.StringRes
import com.ongo.signal.R

class RadarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val DEFAULT_FORMAT = "%1$.0f"
    }

    private var mIsScaning = false
    private var mIsRadar = false
    private var mPaint: Paint = Paint()
    private var mInsideStrokeWidth: Float = 0f
    private var mOutsideStrokeWidth: Float = 0f
    private var mLineStrokeWidth: Float = 0f
    private var mCircleCenterX: Float = 0f
    private var mCircleCenterY: Float = 0f
    private var mRadius: Float = 0f
    private var mInsideRadius: Float = 0f
    private var mCircleColor: Int = Color.parseColor("#52fff9")
    private var mLineColor: Int = Color.parseColor("#1ecdf4")
    private var mSideColor: Int = Color.parseColor("#52fff9")
    private var mOutsideBackgroundColor: Int = Color.parseColor("#1bb8f2")
    private var mInsideBackgroundColor: Int = Color.WHITE
    private var mTextColor: Int = Color.parseColor("#01b0f1")
    private var mLabelTextColor: Int = Color.parseColor("#01b0f1")
    private var mTextSize: Float = 0f
    private var mLabelTextSize: Float = 0f
    private var mCircleShader: Shader? = null
    private var mScanShader: Shader? = null
    private var mMatrix: Matrix = Matrix()
    private var mRotate: Int = 0
    private var mIsShowLine: Boolean = true
    private var mTextOffsetY: Float = 0f
    private var mLabelTextOffseY: Float = 0f
    private var mFormat: String = DEFAULT_FORMAT
    private var mIsShowLabel: Boolean = true
    private var mIsShowText: Boolean = true
    private var mIsShowAnim: Boolean = true
    private var mLabelText: String? = null
    private var mValue: Float = 0f
    private var mText: String? = null
    private var mDuration: Int = 500
    private var mScanTime: Int = 2
    private var mLastTime: Float = 0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RadarView)

        mTextSize = a.getDimension(
            R.styleable.RadarView_android_textSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40f, resources.displayMetrics)
        )
        mTextColor = a.getColor(R.styleable.RadarView_android_textColor, mTextColor)
        mText = a.getString(R.styleable.RadarView_android_text)

        mLabelTextSize = a.getDimension(
            R.styleable.RadarView_labelTextSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, resources.displayMetrics)
        )
        mLabelTextColor = a.getColor(R.styleable.RadarView_labelTextColor, mTextColor)
        mLabelText = a.getString(R.styleable.RadarView_labelText)
        mFormat = a.getString(R.styleable.RadarView_format) ?: DEFAULT_FORMAT

        mSideColor = a.getColor(R.styleable.RadarView_sideColor, mSideColor)
        mOutsideBackgroundColor =
            a.getColor(R.styleable.RadarView_outsideBackgroundColor, mOutsideBackgroundColor)
        mInsideBackgroundColor =
            a.getColor(R.styleable.RadarView_insideBackgroundColor, mInsideBackgroundColor)

        mDuration = a.getInt(R.styleable.RadarView_duration, mDuration)

        mTextOffsetY = a.getDimension(
            R.styleable.RadarView_textOffsetY,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics)
        )
        mLabelTextOffseY = a.getDimension(
            R.styleable.RadarView_labelTextOffsetY,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, resources.displayMetrics)
        )
        mCircleColor = a.getColor(R.styleable.RadarView_circleColor, mCircleColor)
        mLineColor = a.getColor(R.styleable.RadarView_lineColor, mLineColor)
        mRotate = a.getInt(R.styleable.RadarView_rotate, mRotate)
        mIsShowLine = a.getBoolean(R.styleable.RadarView_showLine, mIsShowLine)
        mIsShowText = a.getBoolean(R.styleable.RadarView_showText, mIsShowText)
        mIsShowLabel = a.getBoolean(R.styleable.RadarView_showLabel, mIsShowLabel)
        mScanTime = a.getInt(R.styleable.RadarView_scanTime, mScanTime)

        mInsideStrokeWidth = a.getDimension(
            R.styleable.RadarView_insideStrokeWidth,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)
        )
        mOutsideStrokeWidth = a.getDimension(
            R.styleable.RadarView_outsideStrokeWidth,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
        )
        mLineStrokeWidth = a.getDimension(
            R.styleable.RadarView_lineStrokeWidth,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.3f, resources.displayMetrics)
        )
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val defaultValue = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 200f, resources.displayMetrics
        ).toInt()
        val defaultWidth = defaultValue + paddingLeft + paddingRight
        val defaultHeight = defaultValue + paddingTop + paddingBottom

        val width = measureHandler(widthMeasureSpec, defaultWidth)
        val height = measureHandler(heightMeasureSpec, defaultHeight)

        mCircleCenterX = (width + paddingLeft - paddingRight) /2.0f
        mCircleCenterY = (height + paddingTop - paddingBottom) / 2.0f

        mRadius = (width - paddingLeft - paddingRight - mOutsideStrokeWidth) / 2.0f
        mInsideRadius = mRadius / 3

        setMeasuredDimension(width, height)
    }

    private fun measureHandler(measureSpec: Int, defaultSize: Int): Int {
        val result: Int
        val measureMode = MeasureSpec.getMode(measureSpec)
        val measureSize = MeasureSpec.getSize(measureSpec)
        result = when (measureMode) {
            MeasureSpec.UNSPECIFIED -> defaultSize
            MeasureSpec.AT_MOST -> Math.min(defaultSize, measureSize)
            else -> measureSize
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mIsRadar) {
            drawRadar(canvas)
        } else {
            drawScore(canvas)
        }
    }

    private fun drawRadar(canvas: Canvas) {
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE

        if (mIsShowLine) {
            mPaint.color = mLineColor
            mPaint.strokeWidth = mLineStrokeWidth

            val lineRadius = mRadius - mOutsideStrokeWidth / 2
            canvas.drawLine(
                mCircleCenterX - lineRadius, mCircleCenterY,
                mCircleCenterX + lineRadius, mCircleCenterY, mPaint
            )
            canvas.drawLine(
                mCircleCenterX, mCircleCenterY - lineRadius,
                mCircleCenterX, mCircleCenterY + lineRadius, mPaint
            )

            var radian = Math.toRadians(45.0)
            var startX = (mCircleCenterX + lineRadius * Math.cos(radian)).toFloat()
            var startY = (mCircleCenterY + lineRadius * Math.sin(radian)).toFloat()
            radian = Math.toRadians(225.0)
            var endX = (mCircleCenterX + lineRadius * Math.cos(radian)).toFloat()
            var endY = (mCircleCenterY + lineRadius * Math.sin(radian)).toFloat()
            canvas.drawLine(startX, startY, endX, endY, mPaint)

            radian = Math.toRadians(135.0)
            startX = (mCircleCenterX + lineRadius * Math.cos(radian)).toFloat()
            startY = (mCircleCenterY + lineRadius * Math.sin(radian)).toFloat()
            radian = Math.toRadians(315.0)
            endX = (mCircleCenterX + lineRadius * Math.cos(radian)).toFloat()
            endY = (mCircleCenterY + lineRadius * Math.sin(radian)).toFloat()
            canvas.drawLine(startX, startY, endX, endY, mPaint)
        }

        mMatrix.setRotate(mRotate.toFloat(), mCircleCenterX, mCircleCenterY)
        if (mCircleShader == null) {
            mCircleShader = SweepGradient(mCircleCenterX, mCircleCenterY, Color.TRANSPARENT, mCircleColor)
        }
        mCircleShader!!.setLocalMatrix(mMatrix)
        mPaint.shader = mCircleShader

        mPaint.strokeWidth = mInsideStrokeWidth
        mPaint.color = mCircleColor
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mInsideRadius, mPaint)
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mInsideRadius * 2, mPaint)

        mPaint.strokeWidth = mOutsideStrokeWidth
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mRadius, mPaint)

        if (mScanShader == null) {
            mScanShader = SweepGradient(mCircleCenterX, mCircleCenterY, intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, mCircleColor), null)
        }
        mScanShader!!.setLocalMatrix(mMatrix)
        mPaint.shader = mScanShader
        mPaint.style = Paint.Style.FILL

        val radius = mRadius + mOutsideStrokeWidth / 2
        canvas.drawCircle(mCircleCenterX, mCircleCenterX, radius, mPaint)
    }

    private fun drawScore(canvas: Canvas) {
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mInsideStrokeWidth
        mPaint.color = mSideColor
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mRadius, mPaint)

        mPaint.style = Paint.Style.FILL
        mPaint.color = mOutsideBackgroundColor
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mRadius, mPaint)

        mPaint.color = mInsideBackgroundColor
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mInsideRadius * 2, mPaint)

        mPaint.typeface = Typeface.DEFAULT
        mPaint.textAlign = Paint.Align.CENTER

        if (mIsShowLabel && !mLabelText.isNullOrEmpty()) {
            mPaint.color = mLabelTextColor
            mPaint.textSize = mLabelTextSize
            val x = mCircleCenterX
            val y = mCircleCenterY + mLabelTextOffseY
            canvas.drawText(mLabelText!!, x, y, mPaint)
        }

        if (mIsShowText && !mText.isNullOrEmpty()) {
            mPaint.color = mTextColor
            mPaint.textSize = mTextSize
            mPaint.isFakeBoldText = true
            val x = mCircleCenterX
            val y = mCircleCenterY + mTextOffsetY
            canvas.drawText(mText!!, x, y, mPaint)
        }
    }

    private fun getFormatText(value: Float, format: String?): String {
        return format?.let { String.format(it, value) } ?: value.toString()
    }

    fun showText(text: String) {
        mIsRadar = false
        setText(text)
    }

    fun showScore(value: Float) {
        showScore(value, mDuration)
    }

    fun showScore(value: Float, duration: Int) {
        showScore(0f, value, duration)
    }

    fun showScore(from: Float, to: Float, duration: Int) {
        showScore(from, to, duration, mIsShowAnim)
    }

    fun showScore(from: Float, to: Float, duration: Int, isShowAnim: Boolean) {
        showScore(from, to, duration, mFormat, isShowAnim)
    }

    fun showScore(from: Float, to: Float, duration: Int, format: String?, isShowAnim: Boolean) {
        mIsRadar = false
        mDuration = duration
        mFormat = format ?: DEFAULT_FORMAT
        mIsShowAnim = isShowAnim
        if (mIsShowAnim) {
            val valueAnimator = ValueAnimator.ofFloat(from, to)
            valueAnimator.duration = duration.toLong()
            valueAnimator.addUpdateListener { animation ->
                mValue = animation.animatedValue as Float
                setText(mValue, mFormat)
            }
            valueAnimator.start()
        } else {
            mValue = to
            setText(mValue, mFormat)
        }
    }

    fun setText(@StringRes resId: Int) {
        mText = resources.getString(resId)
        if (!mIsRadar) {
            invalidate()
        }
    }

    fun setText(text: String) {
        mText = text
        if (!mIsRadar) {
            invalidate()
        }
    }

    fun setText(value: Float, format: String?) {
        mValue = value
        mFormat = format ?: DEFAULT_FORMAT
        mText = getFormatText(value, format)
        if (!mIsRadar) {
            invalidate()
        }
    }

    fun setLabelText(@StringRes resId: Int) {
        mLabelText = resources.getString(resId)
        if (!mIsRadar) {
            invalidate()
        }
    }

    fun setLabelText(text: String) {
        mLabelText = text
        if (!mIsRadar) {
            invalidate()
        }
    }

    fun start() {
        mIsRadar = true
        mIsScaning = true
        updateScan()
    }

    fun start(vararg colors: Int) {
        setScanColor(*colors)
        start()
    }

    private fun updateScan() {
        val curTime = System.currentTimeMillis().toFloat()
        if (curTime >= mLastTime + mScanTime && mIsScaning) {
            mLastTime = curTime
            removeCallbacks(mRunnable)
            postDelayed(mRunnable, mScanTime.toLong())
        }
    }

    private val mRunnable = Runnable {
        mRotate++
        if (mRotate >= 360) {
            mRotate = 0
        }
        invalidate()
        updateScan()
    }

    fun stop() {
        mIsScaning = false
    }

    fun setInsideStrokeWidth(insideStrokeWidth: Float) {
        mInsideStrokeWidth = insideStrokeWidth
    }

    fun setOutsideStrokeWidth(outsideStrokeWidth: Float) {
        mOutsideStrokeWidth = outsideStrokeWidth
    }

    fun setLineStrokeWidth(lineStrokeWidth: Float) {
        mLineStrokeWidth = lineStrokeWidth
    }

    fun setCircleColor(circleColor: Int) {
        mCircleColor = circleColor
    }

    fun setLineColor(lineColor: Int) {
        mLineColor = lineColor
    }

    fun setSideColor(sideColor: Int) {
        mSideColor = sideColor
    }

    fun setOutsideColor(outsideColor: Int) {
        mOutsideBackgroundColor = outsideColor
    }

    fun setInsideColor(insideColor: Int) {
        mInsideBackgroundColor = insideColor
    }

    fun setTextColor(textColor: Int) {
        mTextColor = textColor
    }

    fun setLabelTextColor(labelTextColor: Int) {
        mLabelTextColor = labelTextColor
    }

    fun setTextSize(textSize: Float) {
        mTextSize = textSize
    }

    fun setLabelTextSize(labelTextSize: Float) {
        mLabelTextSize = labelTextSize
    }

    fun setRotate(rotate: Int) {
        mRotate = rotate
    }

    fun setShowLine(isShowLine: Boolean) {
        mIsShowLine = isShowLine
    }

    fun setTextOffsetY(textOffsetY: Float) {
        mTextOffsetY = textOffsetY
    }

    fun setShowLabel(showLabel: Boolean) {
        mIsShowLabel = showLabel
    }

    fun setShowText(showText: Boolean) {
        mIsShowText = showText
    }

    fun setShowAnim(isShowAnim: Boolean) {
        mIsShowAnim = isShowAnim
    }

    fun setDuration(duration: Int) {
        mDuration = duration
    }

    fun setScanTime(scanTime: Int) {
        mScanTime = scanTime
    }

    fun isRadar(): Boolean {
        return mIsRadar
    }

    fun getCircleColor(): Int {
        return mCircleColor
    }

    fun getLineColor(): Int {
        return mLineColor
    }

    fun getSideColor(): Int {
        return mSideColor
    }

    fun getOutsideColor(): Int {
        return mOutsideBackgroundColor
    }

    fun setRadar(isRadar: Boolean) {
        mIsRadar = isRadar
    }

    fun getInsideColor(): Int {
        return mInsideBackgroundColor
    }

    fun getTextColor(): Int {
        return mTextColor
    }

    fun getLabelTextColor(): Int {
        return mLabelTextColor
    }

    fun setScanColor(vararg colors: Int) {
        mScanShader = SweepGradient(mCircleCenterX, mCircleCenterY, colors, null)
    }

    fun setCircleColor(vararg colors: Int) {
        mCircleShader = SweepGradient(mCircleCenterX, mCircleCenterY, colors, null)
    }
}