package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var buttonText: String
    private var widthSize = 0
    private var heightSize = 0
    private var CProgress = 0f
    private var BProgress = 0f
    private var buttonBackgroundColor = R.attr.buttonBackgroundColor
    private val loadingBackgroundColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private var progress: Float = 0f
    private val textColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private var valueAnimator = ValueAnimator()
    private val textRect = Rect()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, oldValue, newValue ->
        when(newValue) {
            ButtonState.Clicked -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.duration = 2500
                valueAnimator.addUpdateListener {
                    CProgress = it.animatedValue as Float
                    BProgress = (widthSize.toFloat() / 500) * CProgress
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        CProgress = 0f
                        BProgress = 0f
                    }
                })

                valueAnimator.start()
                invalidate()
            }
            ButtonState.Loading -> {
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                buttonText = resources.getString(R.string.download)
                invalidate()
            }
        }
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {

            try {
                buttonText = getString(R.styleable.LoadingButton_text).toString()
                buttonBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
            } finally {
                recycle()
            }
        }
    }

    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = textColor
        textSize = resources.getDimension(R.dimen.default_text_size)
    }


    init {
        buttonText = resources.getString(R.string.download)

    }

    private val paint = Paint().apply {
        isAntiAlias = true
        color = backgroundColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawButton(canvas)
        drawProgress(canvas)
        drawText(canvas)

    }


    private fun drawButton(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawText(canvas: Canvas?) {
        val textWidth = textPaint.measureText(buttonText)
        canvas?.drawText(
            buttonText,
            widthSize / 2 - textWidth / 2,
            heightSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
            textPaint
        )

    }

    private fun drawProgress(canvas: Canvas?) {
        val bPaint = Paint().apply {
            isAntiAlias = true
            color = loadingBackgroundColor
        }
        canvas?.drawRect(0f, 0f, BProgress, heightSize.toFloat(), bPaint)

        val textWidth = textPaint.measureText(resources.getString(R.string.button_loading))
        val x = widthSize / 2 + textWidth / 1.5f
        val y = heightSize / 2 - textWidth / 4

        val oval = RectF(
            x, y,
            width.toFloat() - x / 7, height.toFloat() - y
        )

        val cPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.YELLOW
            strokeWidth = 3f
        }

        canvas?.drawArc(oval, 0f, CProgress, true, cPaint)
    }




    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }



}