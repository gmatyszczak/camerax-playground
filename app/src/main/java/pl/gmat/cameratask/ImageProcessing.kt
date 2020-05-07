package pl.gmat.cameratask

import android.graphics.*
import androidx.camera.core.ImageProxy
import androidx.core.graphics.rotationMatrix
import java.nio.ByteBuffer

fun ImageProxy.toBitmap(): Bitmap {
    val byteArray = planes[0].buffer.toByteArray()
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    val rotationMatrix = rotationMatrix(imageInfo.rotationDegrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotationMatrix, true)
}

private fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val byteArray = ByteArray(capacity())
    get(byteArray)
    return byteArray
}

fun Bitmap.process(horizontalMargin: Int, verticalMargin: Int): Bitmap {
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val ellipseRect = RectF(
        horizontalMargin.toFloat(),
        verticalMargin.toFloat(),
        (width - horizontalMargin).toFloat(),
        (height - verticalMargin).toFloat()
    )

    val path = Path().apply {
        addArc(ellipseRect, 0f, 360f)
        close()
    }

    canvas.apply {
        drawColor(Color.RED)
        clipPath(path)
        drawBitmap(this@process, 0f, 0f, paint)
    }
    return output
}