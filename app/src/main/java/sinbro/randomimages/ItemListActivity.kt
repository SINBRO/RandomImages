package sinbro.randomimages

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlinx.android.synthetic.main.item_list.*
import sinbro.randomimages.loader.TextLoader
import sinbro.randomimages.recyclerView.RecyclerViewAdapter

class ItemListActivity : AppCompatActivity() {
    private var imageUrls = ArrayList<String>()
    private var descriptions = ArrayList<String>()
    private var recyclerElements = ArrayList<Item>()
    private var twoPane = false
    private var serviceBind = false
    private var binder : TextLoader.MyBinder? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            serviceBind = true
            binder = service as TextLoader.MyBinder
            binder!!.setCallback { p -> parseJSON(p) }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBind = false
            binder = null
        }
    }

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
            val intent = Intent(this, TextLoader::class.java)
            intent.putExtra(URL_LINK, DOWNLOAD_LINK)
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            startService(intent)
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

    private fun parseJSON(unparsedData: String) {
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
