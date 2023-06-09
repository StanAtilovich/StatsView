package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributes,
    defStyleAttr,
    defStyleRes,
) {
    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWith = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributes, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_android_textSize, textSize)
            lineWith = getDimension(R.styleable.StatsView_lineWidth, lineWith.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            recalculatePercentages()
            invalidate()
        }

    private var percentages: List<Float> = emptyList()

    private fun recalculatePercentages() {
        if (data.isEmpty()) return
        val dataSum = data.sum()
        percentages = data.map { it * 100F / dataSum }
    }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWith.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWith
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }
        var startAngle = -90F
      data.forEachIndexed { indext, datum ->
          val angle = datum * 3.6F
          paint.color = colors.getOrElse(indext) { generateRandomColor() }
          canvas.drawArc(oval, startAngle, angle, false, paint)
         canvas.drawText(
             "%.2f%%".format(100F),
             center.x,
             center.y + textPaint.textSize / 4,
             textPaint
         )
          startAngle += angle
      }



        percentages.forEachIndexed { index, percentage ->
            val angle = percentage * 3.6F // 360 / 100 = 3.6
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startAngle, angle, false, paint)

            // рассчитываем координаты текста
            val textX = center.x + (radius + lineWith) * cos(Math.toRadians((startAngle + angle / 2).toDouble())).toFloat()
            val textY = center.y + (radius + lineWith) * sin(Math.toRadians((startAngle + angle / 2).toDouble())).toFloat()
            canvas.drawText(
                "%.2f%%".format(percentage),
                textX,
                textY + textPaint.textSize / 4,
                textPaint
            )
            startAngle += angle
        }

       // canvas.drawText(
       //     "%.2f%%".format(data.sum() * 100),
       //     center.x,
       //     center.y + textPaint.textSize / 4,
       //     textPaint
       // )
    }

    private fun generateRandomColor() = Random.nextInt(
        0xFF000000.toInt(), 0xFFFFFFFF.toInt()
    )
}