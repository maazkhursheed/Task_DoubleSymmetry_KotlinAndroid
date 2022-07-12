package com.rsmnm.Fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.R
import com.rsmnm.Views.TitleBar
import kotlinx.android.synthetic.main.fragment_terms.*

class WebviewContentFragment : BaseFragment() {

    var filePath: String? = null
    var strTitle: String? = null

    companion object {
        fun newInstance(title: String, path: String) = WebviewContentFragment().apply {
            arguments = Bundle(2).apply {
                strTitle = title
                filePath = path
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_terms

    override fun getTitleBar(titleBar: TitleBar?) {
        titleBar?.resetTitleBar()?.enableBack()?.setTitle(strTitle!!)
    }

    override fun activityCreated(savedInstanceState: Bundle?) {

    }

    override fun inits() {
        webView.settings.setJavaScriptEnabled(true)
        webView.loadUrl(filePath)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoader()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoader()
            }
        }
    }

    override fun setEvents() {
    }

}