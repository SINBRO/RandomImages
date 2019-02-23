package sinbro.randomimages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_detail.view.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.URL


class ItemDetailFragment : Fragment() {
    private var item: ItemListActivity.Item? = null
    private lateinit var rootView : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        arguments?.let {
            if (it.containsKey(DESCRIPTION) && it.containsKey(IMAGE_URL)) {
                item = ItemListActivity.Item(it[DESCRIPTION] as String, it[IMAGE_URL] as String)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.item_detail, container, false)

        item?.let {
            rootView.description.text = it.text
            DownloadAsyncTask(WeakReference(this)).execute(URL(it.imageUrl))
        }

        return rootView
    }

    private class DownloadAsyncTask(val activity: WeakReference<ItemDetailFragment>) : AsyncTask<URL, Int, Bitmap>() {
        override fun onPostExecute(result: Bitmap?) {
            activity.get()?.rootView?.image?.setImageBitmap(result)
        }

        override fun doInBackground(vararg params: URL): Bitmap {
            var res: ByteArray?
            try {
                val url = params[0]
                var connection = url.openConnection()
                connection.connect()
                while (connection.contentLength < 0) {
                    connection = url.openConnection()
                    connection.connect()
                }
                res = ByteArray(connection.contentLength)
                connection.getInputStream().use { iss ->
                    var p = 0
                    var r: Int = iss.read(res, p, res!!.size - p)
                    while (r > 0){
                        p += r
                        r = iss.read(res, p, res!!.size - p)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                res = null
            }

            return BitmapFactory.decodeByteArray(res, 0, res!!.size)
        }
    }

    companion object {
        const val DESCRIPTION : String = "description"
        const val IMAGE_URL : String = "imageUrl"
    }
}
