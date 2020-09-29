package com.lizhi.reader.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.reader.MApplication;
import com.lizhi.reader.R;
import com.lizhi.reader.base.MBaseActivity;
import com.lizhi.reader.utils.theme.ThemeStore;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GKF on 2017/12/15.
 * 关于
 */

public class AboutActivity extends MBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.action_bar)
    AppBarLayout actionBar;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_opensource)
    TextView tvOpensource;
    @BindView(R.id.tv_disclaimer)
    TextView tvDisclaimer;

    public static void startThis(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar(getString(R.string.about));
    }

    //设置ToolBar
    public void setupActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    //菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initData() {
        //当前版本
        tvVersion.setText(getString(R.string.version_name, MApplication.getVersionName()));

        //免责声明
        tvOpensource.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("https://github.com/lizhi868/lizhireader");
            intent.setData(content_url);
            startActivity(intent);
        });

        //免责声明
        tvDisclaimer.setOnClickListener(v -> {
            DisclaimerActivity.startThis(getContext());
        });
    }
}
