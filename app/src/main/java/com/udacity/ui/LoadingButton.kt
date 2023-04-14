package com.udacity.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var valueAnimatorLoadButton = ValueAnimator()
    private var textDisplay: String

    private var paintButton: Paint

    private var paintCircleProgress: Paint

    private var paintText: Paint

    private val paintProgressLoading = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create("", Typeface.BOLD)
        textSize = resources.getDimension(R.dimen.textSize)
        color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    }

    private var width = 0f
    private var progress = 0f
    private var rect = Rect()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, newState ->
        when (newState) {
            ButtonState.Clicked   -> {
                textDisplay = "Download"
                resetUI()
                invalidate()
            }
            ButtonState.Loading   -> {
                textDisplay = "We are loading"
                animateLoadingState()
                invalidate()
            }
            ButtonState.Completed -> {
                textDisplay = "Download"
                valueAnimatorLoadButton.removeAllListeners()
                valueAnimatorLoadButton.end()
                resetUI()
                invalidate()
            }
        }
    }

    private fun resetUI() {
        width = 0f
        progress = 0f
        requestLayout()
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)

        textDisplay = typedArray.getString(R.styleable.LoadingButton_loadingButtonText) ?: ""

        paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4f
            color = typedArray.getColor(
                R.styleable.LoadingButton_loadingButtonBackgroundColor,
                ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
            )
        }

        paintCircleProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4f
            color = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
        }

        paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create("", Typeface.BOLD)
            textSize = resources.getDimension(R.dimen.textSize)
            color = ResourcesCompat.getColor(resources, R.color.white, null)
        }

        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLoadingButton(canvas)
        drawText(canvas)
        drawArc(canvas)
    }

    private fun animateLoadingState() {
        valueAnimatorLoadButton = ValueAnimator.ofFloat(0F, widthSize.toFloat()).apply {
            duration = 2000L
            addUpdateListener { animation ->
                width = animation.animatedValue as Float
                progress = (360f * width) / widthSize.toFloat()
                invalidate()
            }
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    buttonState = ButtonState.Completed
                    width = 0f
                    progress = 0f
                    invalidate()
                }
            })
            start()
        }
    }

    private fun drawArc(canvas: Canvas) {
        val left = (widthSize / 2) + (rect.width() / 2) + 20f
        val top = (heightSize * 0.2).toFloat()
        val right = (widthSize / 2) + (rect.width() / 2) + 100f
        val bottom = ((heightSize / 2) + rect.height()).toFloat()
        canvas.drawArc(left, top, right, bottom, 0f, progress, true, paintCircleProgress)
    }

    private fun drawLoadingButton(canvas: Canvas) {
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintButton)
        //Progress
        canvas.drawRect(0f, 0f, width, heightSize.toFloat(), paintProgressLoading)
    }

    private fun drawText(canvas: Canvas) {
        paintText.getTextBounds(textDisplay, 0, textDisplay.length, rect)
        val centerX = canvas.width / 2f
        val centerY = (canvas.height / 2 - (paintText.descent() + paintText.ascent()) / 2f)
        canvas.drawText(
            textDisplay,
            centerX,
            centerY,
            paintText
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setNewStateForButton(state: ButtonState) {
        buttonState = state
    }
}
