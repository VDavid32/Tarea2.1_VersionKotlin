package com.example.pm1e2_grupo5

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class Lienzo(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var x: Float = 0f
    var y: Float = 0f
    var opc: Int = 0
    var paint: Paint = Paint()
    var path: Path = Path()
    var canvas: Canvas? = null // Agregamos una referencia al objeto Canvas

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.color = Color.BLACK
    }

    override fun onDraw(canvas: Canvas) {
        this.canvas = canvas // Establecemos la referencia al objeto Canvas
        canvas.drawColor(Color.WHITE)
        if (opc == 1) {
            path.moveTo(x, y)
        }
        if (opc == 2) {
            path.lineTo(x, y)
        }
        canvas.drawPath(path, paint)
    }

    // Método para limpiar el lienzo
    fun limpiarLienzo() {
        path.reset() // Reiniciamos el Path para limpiar el dibujo
        invalidate() // Invalidamos la vista para que se repinte
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val accion = event.action
        x = event.x
        y = event.y
        if (accion == MotionEvent.ACTION_DOWN) {
            opc = 1
        }
        if (accion == MotionEvent.ACTION_MOVE) {
            opc = 2
        }
        invalidate()
        return true
    }
}
