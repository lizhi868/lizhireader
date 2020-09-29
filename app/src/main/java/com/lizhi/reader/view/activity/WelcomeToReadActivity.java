package com.lizhi.reader.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;

import com.lizhi.basemvplib.AppActivityManager;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.reader.R;
import com.lizhi.reader.base.MBaseActivity;
import com.lizhi.reader.presenter.ReadBookPresenter;
import com.lizhi.reader.widget.AgreementDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeToReadActivity extends MBaseActivity {

    @BindView(R.id.iv_icon)
    ImageView ivIcon;

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
//        ivBg.setColorFilter(ThemeStore.accentColor(this));
        new Handler().postDelayed(() -> {
            if (preferences.getBoolean(getString(R.string.pk_auto_agreement),false)){
                toOtherActivity();
            }else {
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
        },1500);
    }

    private void toOtherActivity() {
        startReadActivity();
        finish();
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
