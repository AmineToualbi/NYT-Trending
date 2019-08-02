package com.example.nyttrending.ViewHolder

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nyttrending.Interface.ItemClickListener
import com.example.nyttrending.Model.Article
import com.example.nyttrending.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maps.view.*
import kotlinx.android.synthetic.main.trending_item.view.*
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


class TrendingViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    var articleImage : ImageView? = null
    var articleTitle: TextView? = null
    var articleViews: TextView? = null
    var articleAuthor: TextView? = null
    var itemClickListener: ItemClickListener? = null

    init {
        articleImage = v.findViewById(R.id.articleImage) as ImageView
        articleTitle = v.findViewById(R.id.articleTitle) as TextView
        articleViews = v.findViewById(R.id.articleViews) as TextView
        articleAuthor = v.findViewById(R.id.articleAuthor) as TextView
        v.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        Log.e("TAG", "onClick() -> open article URL")
        itemClickListener!!.onClick(v!!, adapterPosition, false)
    }
}


class TrendingAdapter(listOfArticles: ArrayList<Article>, font: Typeface, context: Context) : RecyclerView.Adapter<TrendingViewHolder>() {

    var listOfArticles = ArrayList<Article>()
    var context: Context? = null
    var font: Typeface? = null

    init {
        this.listOfArticles = listOfArticles
        this.context = context
        this.font = font
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {

        var inflater: LayoutInflater = LayoutInflater.from(context)
        var itemView: View = inflater.inflate(R.layout.trending_item, parent, false)
        return TrendingViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {

        Picasso.get().load(listOfArticles[position].imageURL).into(holder.articleImage)
        holder.articleTitle!!.text = listOfArticles[position].title
        holder.articleTitle!!.setTypeface(font)
        holder.articleViews!!.text = "${listOfArticles[position].views}  views"
        holder.articleAuthor!!.text = listOfArticles[position].author
        holder.articleAuthor!!.setTypeface(font)

        holder.itemClickListener = (object : ItemClickListener {
            override fun onClick(view: View, position: Int, isLongClick: Boolean) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(listOfArticles[position].articleURL))
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(context!!, browserIntent, null)
            }
        })

    }

    override fun getItemCount(): Int {
        return listOfArticles.size
    }
}