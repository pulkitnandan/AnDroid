package com.eudessess.webbrowser.main;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by eudessess on 16-01-2015.
 */

public class Web extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url);
    }
}
