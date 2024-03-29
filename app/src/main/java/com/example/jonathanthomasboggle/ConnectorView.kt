package com.example.jonathanthomasboggle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class ConnectorView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint()

    var startX : Float = 0.0f
    var startY : Float = 0.0f
    var endX : Float = 0.0f
    var endY : Float = 0.0f
    var width: Float = 0.0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.BLACK
        paint.strokeWidth = width

        // Draw a line from (100, 100) to (300, 300)
        canvas.drawLine(startX, startY, endX, endY, paint)
    }

    private fun dpToPixels(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }

}
