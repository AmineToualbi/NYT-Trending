package com.example.nyttrending

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nyttrending.Model.Article
import com.example.nyttrending.ViewHolder.TrendingAdapter
import kotlinx.android.synthetic.main.activity_trending.*
import kotlinx.android.synthetic.main.activity_trending.view.*

class TrendingActivity : AppCompatActivity() {

    var recyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var trendingAdapter: TrendingAdapter? = null
    private lateinit var mTopToolbar : androidx.appcompat.widget.Toolbar
    var listOfArticles : ArrayList<Article>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trending)
        Log.e("TAG", "TrendingView created.")

        mTopToolbar = findViewById(R.id.trendingToolbar)
        val font : Typeface = Typeface.createFromAsset(assets, "fonts/NYTCheltenhamExtraBold.otf")
        mTopToolbar.activityHeader.setTypeface(font)
        setSupportActionBar(mTopToolbar)

        recyclerView = findViewById(R.id.trendingRecycler) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager

        backBtn.setOnClickListener {
            onBackPressed()
            finish()
        }

        if(intent != null) {
            val articles = intent.getParcelableArrayListExtra<Article>("articles")
            listOfArticles = articles
            Log.e("TAG", articles.toString())
        }

        loadArticles()

    }

    private fun loadArticles() {

        val font : Typeface = Typeface.createFromAsset(assets, "fonts/NYTCheltenhamExtraBold.otf")
        trendingAdapter = TrendingAdapter(listOfArticles!!, font, applicationContext)
        recyclerView!!.adapter = trendingAdapter

    }
}
