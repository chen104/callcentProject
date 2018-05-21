package com.example.vmac.callCenter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebVewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=   getIntent();
       String url =  intent.getStringExtra("url");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_webview);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl(url);
       // webView.loadUrl("https://www.baidu.com/");
        WebSettings settings = webView.getSettings();

        settings.setUseWideViewPort(true);//设定支持viewport

        settings.setLoadWithOverviewMode(true);

        settings.setBuiltInZoomControls(true);

        settings.setSupportZoom(true);//设定支持缩放
        final   ProgressDialog dialog= ProgressDialog.show(this, "提示", "加载中", false);
        webView.setWebChromeClient(new WebChromeClient()
        {
            public void onProgressChanged(WebView view, int progress)
            {
                System.out.println("progress "+progress);
                //当进度走到100的时候做自己的操作，我这边是弹出dialog
                dialog.dismiss();
            }

        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                System.out.println("progress finish");

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
