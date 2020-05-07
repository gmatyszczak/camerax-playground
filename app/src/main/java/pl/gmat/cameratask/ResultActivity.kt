package pl.gmat.cameratask

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        findViewById<ImageView>(R.id.imageView).setImageBitmap(ImageCache.bitmap)
    }
}