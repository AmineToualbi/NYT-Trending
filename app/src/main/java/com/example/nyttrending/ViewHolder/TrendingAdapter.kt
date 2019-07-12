package com.example.nyttrending.ViewHolder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nyttrending.Model.Article
import com.example.nyttrending.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.trending_item.view.*


class TrendingViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    var articleImage : ImageView? = null
    var articleTitle: TextView? = null
    var articlePublicationTime: TextView? = null
    var articleViews: TextView? = null

    init {
        articleImage = v.findViewById(R.id.articleImage) as ImageView
        articleTitle = v.findViewById(R.id.articleTitle) as TextView
        articlePublicationTime = v.findViewById(R.id.articlePublicationTime) as TextView
        articleViews = v.findViewById(R.id.articleViews) as TextView
    }

    override fun onClick(v: View?) {
        Log.e("TAG", "onClick() -> open article URL")
    }
}


class TrendingAdapter(listOfArticles: ArrayList<Article>, context: Context) : RecyclerView.Adapter<TrendingViewHolder>() {

    var listOfArticles = ArrayList<Article>()
    var context: Context? = null

    init {
        this.listOfArticles = listOfArticles
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {

        var inflater: LayoutInflater = LayoutInflater.from(context)
        var itemView: View = inflater.inflate(R.layout.trending_item, parent, false)
        return TrendingViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {

        Picasso.get().load(listOfArticles[position].imageURL).into(holder.articleImage)
        holder.articleTitle!!.text = listOfArticles[position].title
        holder.articlePublicationTime!!.text = listOfArticles[position].publicationTime
        holder.articleViews!!.text = listOfArticles[position].views + " views"

    }

    override fun getItemCount(): Int {
        return listOfArticles.size
    }
}