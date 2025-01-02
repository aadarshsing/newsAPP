package com.example.newsly

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        var detailWebView = findViewById<WebView>(R.id.detailWebView)
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val url = intent.getStringExtra("URL")
        Log.d("ashuBitWise",url.toString())
        if(url!=null){
            detailWebView.settings.javaScriptEnabled = true
            detailWebView.webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                    detailWebView.visibility = View.VISIBLE


                }
            }
            detailWebView.loadUrl(url)
        }


    }
}