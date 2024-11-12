package io.github.xechoz

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import android.view.View

// todo: add indicator animation

/**
 * draw your indicator on BoringTabLayout's canvas
 *
 * [canvas] BoringTabLayout's canvas
 * [position], [positionOffset], [positionOffsetPixels] is from ViewPager's onPageScrolled
 */
typealias DrawIndicator = (canvas: Canvas,
               position: Int,
               positionOffset: Float,
               positionOffsetPixels: Int) -> Unit

private const val TAG = "Indicator"
// simple indicator

fun LineIndicator(tabs:List<View>, width: Int, height: Int, color: Int, radius: Float = 0f,
                  marginBottom: Float = 0f): DrawIndicator {
    val rect = RectF()
    val paint = Paint()
    paint.style = Paint.Style.FILL
    paint.isAntiAlias = true
    paint.color = color

    return fun (canvas: Canvas,
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {
        // update rect
        val tab = tabs[position]
        val bottom = canvas.height.toFloat() - marginBottom

        rect.set(tab.centerX - width/2f,
            bottom - height,
            tab.centerX + width/2f,
            bottom)

        canvas.drawRoundRect(rect, radius, radius, paint)
    }
}

fun AnimateIndicator(tabs:List<View>, drawIndicator: DrawIndicator): DrawIndicator {
    var fromOffset = 0f

    return fun (canvas: Canvas,
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {
        Log.d(TAG, "position $position, $positionOffset, $positionOffsetPixels")
        // direction
        val toLeft = if (fromOffset == 0f) {
            positionOffset > 0.5f
        } else {
            positionOffset > fromOffset
        }

        fromOffset = positionOffset

        canvas.save()

        if (position+1 <= tabs.size-1) {
            val fromView = if (toLeft) tabs[position + 1] else tabs[position]
            val toView = if (toLeft) tabs[position] else tabs[position + 1]

            canvas.translate(
                (toView.centerX - fromView.centerX) * positionOffset * if (toLeft) -1 else 1,
                0f
            )
        }

        drawIndicator(canvas, position, positionOffset, positionOffsetPixels)
        canvas.restore()
    }
}
private val View.centerX:Float
    get() = left + measuredWidth/2f