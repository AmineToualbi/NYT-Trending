package com.example.nyttrending.Model

import android.os.Parcel
import android.os.Parcelable

data class Article(
    var title: String,
    var author: String,
    var articleURL: String,
    var imageURL: String?,
    var views: Int
) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Article> {
            override fun createFromParcel(parcel: Parcel) = Article(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Article>(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        title = parcel.readString()!!,
        author = parcel.readString()!!,
        articleURL = parcel.readString()!!,
        imageURL = parcel.readString(),
        views = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel!!.writeString(title)
        parcel.writeString(author)
        parcel.writeString(articleURL)
        parcel.writeString(imageURL)
        parcel.writeInt(views)
    }

    override fun describeContents() = 0
}