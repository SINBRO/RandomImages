package sinbro.randomimages.loader

import android.app.IntentService
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import sinbro.randomimages.ItemDetailFragment
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ImageLoader : IntentService("ImageLoader") {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var callback: (Bitmap) -> Unit

    override fun onHandleIntent(intent: Intent?) {
        val path = intent!!.getStringExtra(ItemDetailFragment.IMAGE_URL)
        val url = URL(path)
        url.openConnection()
        val bitmap: Bitmap

        val file = File(filesDir, path.hashCode().toString() + ".jpg")

        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.absolutePath)
        } else {
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            file.createNewFile()
            val stream: FileOutputStream? = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream?.close()
        }

        handler.post {
            callback(bitmap)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return MyBinder(this)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        callback = { }
        return super.onUnbind(intent)
    }

    class MyBinder(private val service: ImageLoader) : Binder() {
        fun setCallback(callback: (Bitmap) -> Unit) {
            service.callback = callback
        }
    }
}
