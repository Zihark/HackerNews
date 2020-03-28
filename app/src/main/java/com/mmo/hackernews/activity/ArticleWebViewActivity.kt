package com.mmo.hackernews.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mmo.hackernews.R

class ArticleWebViewActivity : AppCompatActivity() {

    private val ARTICLE_URL = "URL"

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_web_view)

        webView = findViewById(R.id.article_webview)

        val url = intent.getStringExtra(ARTICLE_URL)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webView.loadUrl(url)
    }
}
