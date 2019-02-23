package sinbro.randomimages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ItemDetailActivity : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val fragment = ItemDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        ItemDetailFragment.DESCRIPTION,
                        intent.getStringExtra(ItemDetailFragment.DESCRIPTION)
                    )
                    putString(
                        ItemDetailFragment.IMAGE_URL,
                        intent.getStringExtra(ItemDetailFragment.IMAGE_URL)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.item_detail_container, fragment)
                .commit()
        }
    }
}
