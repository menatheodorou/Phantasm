package com.phantasm.phantasm.common.ui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.phantasm.phantasm.R;

/**
 * Created by kevinfinn on 5/22/15.
 */
public class WebViewActivity extends PTBaseActivity {
    public static final String EXTRA_URL = "url";
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webview);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);

        if (url != null)
            webView.loadUrl(url);
    }
}
