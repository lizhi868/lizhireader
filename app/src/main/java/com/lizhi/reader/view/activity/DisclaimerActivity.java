package com.lizhi.reader.view.activity;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lizhi.basemvplib.BaseActivity;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.reader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisclaimerActivity extends BaseActivity {
    @BindView(R.id.web_view)
    WebView webView;

    public static void startThis(Context context) {
        context.startActivity(new Intent(context, DisclaimerActivity.class));
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        setContentView(R.layout.activity_disclaimer);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        webView.loadUrl("file:///android_asset/disclaimer.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
}
