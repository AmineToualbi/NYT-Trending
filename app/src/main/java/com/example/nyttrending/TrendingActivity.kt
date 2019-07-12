package com.example.nyttrending

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nyttrending.Model.Article
import com.example.nyttrending.ViewHolder.TrendingAdapter

class TrendingActivity : AppCompatActivity() {

    var recyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var trendingAdapter: TrendingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trending)

        recyclerView = findViewById(R.id.trendingRecycler) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView!!.layoutManager = layoutManager

        //Get article info passed from intent to make the API call.

        //Make the API call & check if value returned is not null. If not null => loadArticles();
        loadArticles()

    }

    private fun loadArticles() {

        val article1 =
            Article("Title of Article 1", "https://blog.hubspot.com/hubfs/image8-2.jpg", "09/07/2019", "8122")
        val article2 = Article("Title of Article 2", "https://facebookbrand.com/wp-content/uploads/2019/04/f_logo_RGB-Hex-Blue_512.png",
            "03/06/2019", "7412")
        var listOfArticles = ArrayList<Article>()
        listOfArticles.add(article1)
        listOfArticles.add(article2)
        trendingAdapter = TrendingAdapter(listOfArticles, this)
        recyclerView!!.adapter = trendingAdapter

    }
}
