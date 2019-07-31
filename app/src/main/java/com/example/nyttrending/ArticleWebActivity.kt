package com.example.nyttrending

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_article_web.*
import android.webkit.WebViewClient



class ArticleWebActivity : AppCompatActivity() {

    var articleURL : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_web)

        if(intent != null) {
            articleURL = intent.getStringExtra("ArticleURL")
        }
        articleWebView.settings.javaScriptEnabled = true
        articleWebView.webViewClient = MyWebViewClient()
        Log.e("TAG", "Opening " + articleURL)
        articleWebView.loadUrl(articleURL)
    }
}

class MyWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}
