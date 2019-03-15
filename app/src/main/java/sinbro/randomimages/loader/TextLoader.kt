package sinbro.randomimages.loader

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import sinbro.randomimages.ItemListActivity
import java.net.URL

class TextLoader : IntentService("JsonLoader") {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var callback: (String) -> Unit

    override fun onHandleIntent(intent: Intent) {
        val url = URL(intent.getStringExtra(ItemListActivity.URL_LINK))

        val result = url.openConnection().run {
            connect()
            getInputStream().bufferedReader().readLines().joinToString("")
        }

        handler.post { callback(result) }
    }

    override fun onBind(intent: Intent): IBinder? {
        return MyBinder(this)
    }

    class MyBinder(private val service: TextLoader) : Binder() {
        fun setCallback(callback: (String) -> Unit) {
            service.callback = callback
        }
    }
}