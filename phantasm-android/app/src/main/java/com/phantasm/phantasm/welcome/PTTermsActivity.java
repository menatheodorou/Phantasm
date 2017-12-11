package com.phantasm.phantasm.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.phantasm.phantasm.R;
import com.phantasm.phantasm.common.ui.PTBaseActivity;

/**
 * Modified by Joseph Luns on 1/12/16.
 */
public class PTTermsActivity extends PTBaseActivity {
    WebView mWebView;
    RelativeLayout mAgreeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        initView();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

    public void initView() {
        initActionBar();

        mWebView = (WebView) findViewById(R.id.webViewTerms);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://terms.phantasm.wtf/");
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                mAgreeLayout.setVisibility(View.VISIBLE);
            }
        });

        mAgreeLayout = (RelativeLayout) findViewById(R.id.agree_layout);
        mAgreeLayout.setVisibility(View.INVISIBLE);
        mAgreeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deprecated_menu_terms, menu);
        return true;
    }
}
