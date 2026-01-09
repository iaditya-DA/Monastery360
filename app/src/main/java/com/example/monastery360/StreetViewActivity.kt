package com.example.monastery360

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class StreetViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)

        val webView = findViewById<WebView>(R.id.webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        // Get iframe URL from Intent
        val iframeUrl = intent.getStringExtra("iframe_url") ?: ""

        val htmlData = """
            <html>
            <body style="margin:0;padding:0;overflow:hidden;">
                <iframe 
                    width="100%" 
                    height="100%" 
                    frameborder="0" 
                    style="border:0;"
                    allowfullscreen
                    src="$iframeUrl">
                </iframe>
            </body>
            </html>
        """

        webView.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null)
    }
}
