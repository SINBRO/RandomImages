package sinbro.randomimages

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.item_list.*
import sinbro.randomimages.recyclerView.RecyclerViewAdapter
import java.lang.ref.WeakReference
import java.net.URL

class ItemListActivity : AppCompatActivity() {
    private var imageUrls = ArrayList<String>()
    private var descriptions = ArrayList<String>()
    private var recyclerElements = ArrayList<Item>()
    private var twoPane = false
    private var unparsedData: String? = null

    private val DOWNLOAD_LINK =
        "https://api.unsplash.com/photos/random/?count=50&client_id=6f0cf0edd57ca1315fb05e69ca9df7e84298d224541952bb8b6dd595c757dd1c"

    companion object {
        const val URL_LINK = "url_link"
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        if (item_detail_container != null) {
            twoPane = true
        }

        if (savedInstanceState != null) {
            imageUrls = savedInstanceState.getStringArrayList("imageUrls")
            descriptions = savedInstanceState.getStringArrayList("descriptions")
            setupRecyclerView(item_list)
        } else {
            ItemListActivity.DownloadAsyncTask(WeakReference(this)).execute(URL(DOWNLOAD_LINK))
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putStringArrayList("imageUrls", imageUrls)
        outState?.putStringArrayList("descriptions", descriptions)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerElements.clear()
        for (i in 0 until descriptions.size) {
            recyclerElements.add(Item(descriptions[i], imageUrls[i]))
        }
        recyclerView.adapter = RecyclerViewAdapter(this, recyclerElements, twoPane)
    }

    private class DownloadAsyncTask(val activity: WeakReference<ItemListActivity>) : AsyncTask<URL, Int, String>() {
        override fun onPostExecute(res: String?) {
            activity.get()?.let {
                if (res != null) {
                    it.unparsedData = res
                    it.parseJSON()
                }
            }
        }

        override fun doInBackground(vararg params: URL): String {
            return params[0].openConnection().run {
                connect()
                getInputStream().bufferedReader().readLines().joinToString("")
            }
        }
    }

    private fun parseJSON() {
        val images = Gson().fromJson<List<Image>>(unparsedData, object : TypeToken<List<Image>>() {}.type)
        for (image in images) {
            descriptions.add(image.user.name!!)
            imageUrls.add(image.urls.regular!!)
        }
        setupRecyclerView(item_list)
    }

    data class Image(val urls: Url, val user: Description)
    data class Url(val regular: String?)
    data class Description(val name: String?)

    class Item(var text: String, var imageUrl: String)
}
