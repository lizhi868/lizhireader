package com.lizhi.reader.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lizhi.basemvplib.AppActivityManager;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.reader.Config;
import com.lizhi.reader.DbHelper;
import com.lizhi.reader.MApplication;
import com.lizhi.reader.R;
import com.lizhi.reader.base.BaseModelImpl;
import com.lizhi.reader.base.MBaseActivity;
import com.lizhi.reader.base.observer.MyObserver;
import com.lizhi.reader.bean.UpDateUrlBean;
import com.lizhi.reader.model.impl.IHttpGetApi;
import com.lizhi.reader.presenter.ReadBookPresenter;
import com.lizhi.reader.widget.AgreementDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class WelcomeActivity extends MBaseActivity {

    @BindView(R.id.iv_icon)
    ImageView ivIcon;

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_welcome);
        AsyncTask.execute(DbHelper::getDaoSession);
        ButterKnife.bind(this);
//        ivBg.setColorFilter(ThemeStore.accentColor(this));
        new Handler().postDelayed(() -> {
            if (preferences.getBoolean(getString(R.string.pk_auto_agreement), false)) {
                toOtherActivity();
            } else {
                AgreementDialog dialog = new AgreementDialog(this);
                dialog.setListener(new AgreementDialog.DialogClickListener() {
                    @Override
                    public void left() {
                        AppActivityManager.getAppManager().AppExit();
                    }

                    @Override
                    public void right() {
                        preferences.edit()
                                .putBoolean(getString(R.string.pk_auto_agreement), true)
                                .apply();
                        toOtherActivity();
                    }
                });
                dialog.show();
            }
        }, 1500);
    }

    private void toOtherActivity() {
        if (preferences.getBoolean(getString(R.string.pk_default_read), false)) {
            startReadActivity();
        } else {
            startBookshelfActivity();
        }
        finish();
    }

    private void startBookshelfActivity() {
        startActivityByAnim(new Intent(this, MainActivity.class), android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void startReadActivity() {
        Intent intent = new Intent(this, ReadBookActivity.class);
        intent.putExtra("openFrom", ReadBookPresenter.OPEN_FROM_APP);
        startActivity(intent);
    }

    @Override
    protected void initData() {

    }

}
