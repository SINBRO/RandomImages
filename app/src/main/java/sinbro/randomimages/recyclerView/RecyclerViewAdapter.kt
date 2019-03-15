package sinbro.randomimages.recyclerView

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_list_content.view.*
import sinbro.randomimages.ItemDetailActivity
import sinbro.randomimages.ItemDetailFragment
import sinbro.randomimages.ItemListActivity
import sinbro.randomimages.R

class RecyclerViewAdapter(
    private val parentActivity: ItemListActivity,
    private val values: List<ItemListActivity.Item>,
    private val twoPane: Boolean
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as ItemListActivity.Item
            if (twoPane) {
                val fragment = ItemDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ItemDetailFragment.DESCRIPTION, item.text)
                        putString(ItemDetailFragment.IMAGE_URL, item.imageUrl)
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                    putExtra(ItemDetailFragment.DESCRIPTION, item.text)
                    putExtra(ItemDetailFragment.IMAGE_URL, item.imageUrl)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.textPreview.text = item.text

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textPreview: TextView = view.text_preview
    }
}