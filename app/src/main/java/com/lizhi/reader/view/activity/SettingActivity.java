package com.lizhi.reader.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.reader.MApplication;
import com.lizhi.reader.R;
import com.lizhi.reader.base.MBaseActivity;
import com.lizhi.reader.help.BookshelfHelp;
import com.lizhi.reader.help.ReadBookControl;
import com.lizhi.reader.service.ReadAloudService;
import com.lizhi.reader.utils.theme.ATH;
import com.lizhi.reader.utils.theme.ThemeStore;
import com.lizhi.reader.widget.ThreadNumDialog;
import com.lizhi.reader.widget.check_box.SmoothCheckBox;
import com.lizhi.reader.widget.views.ATESeekBar;
import com.lizhi.reader.widget.views.ATESwitch;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by GKF on 2017/12/16.
 * 设置
 */

public class SettingActivity extends MBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.action_bar)
    AppBarLayout actionBar;
    @BindView(R.id.checkbox_flow_sys)
    SmoothCheckBox checkboxFlowSys;//阅读语速跟随系统
    @BindView(R.id.seek_read_speed)
    ATESeekBar seekReadSpeed;//阅读语速
    @BindView(R.id.tv_auto_next_time)
    TextView tvAutoNextTime;//自动翻阅时间
    @BindView(R.id.seek_auto_next)
    ATESeekBar seekAutoNext;//自动翻阅
    @BindView(R.id.ll_thread_num)
    LinearLayout llThreadNum;//下载线程数
    @BindView(R.id.tv_thread_num)
    TextView tvThreadNum;//下载线程数
    @BindView(R.id.checkbox_only_wifi)
    CheckBox checkboxOnlyWifi;
    @BindView(R.id.tv_clear_cache)
    TextView tvClearCache;//清除缓存
    @BindView(R.id.tv_screen_direction)
    TextView tvScreenDirection;//屏幕方向
    @BindView(R.id.ll_screen_direction)
    LinearLayout llScreenDirection;
    @BindView(R.id.tv_screen_timeout)
    TextView tvScreenTimeout;//屏幕超时
    @BindView(R.id.ll_screen_timeout)
    LinearLayout llScreenTimeout;
    @BindView(R.id.tv_conversion)
    TextView tvConversion;//简繁转换
    @BindView(R.id.ll_conversion)
    LinearLayout llConversion;
    @BindView(R.id.sw_click_page)
    ATESwitch swClickPage;//点击翻页
    @BindView(R.id.sw_volume_page)
    ATESwitch swVolumePage;//音量键翻页
    @BindView(R.id.sw_read_volume_page)
    ATESwitch swReadVolumePage;
    @BindView(R.id.sw_hide_status_bar)
    ATESwitch swHideStatusBar;//隐藏状态栏
    @BindView(R.id.sw_time)
    ATESwitch swTime;//显示时间和电量

    private ReadBookControl readBookControl = ReadBookControl.getInstance();
    private ThreadNumDialog threadNumDialog;
    private SharedPreferences preferences;

    public static void startThis(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    protected IPresenter initInjector() {
        return null;
    }

    @Override
    protected void onCreateActivity() {
//        setOrientation(readBookControl.getScreenDirection());
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar(getString(R.string.setting));
    }

    @Override
    protected void initData() {
        //设置朗读语速
        if (readBookControl.isSpeechRateFollowSys()) {
            checkboxFlowSys.setChecked(true);
            seekReadSpeed.setEnabled(false);
        } else {
            checkboxFlowSys.setChecked(false);
            seekReadSpeed.setEnabled(true);
        }
        seekReadSpeed.changeTheme(this);
        seekReadSpeed.setProgress(readBookControl.getSpeechRate() - 5);
        checkboxFlowSys.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked) {
                //跟随系统
                seekReadSpeed.setEnabled(false);
                seekReadSpeed.changeTheme(this);
                readBookControl.setSpeechRateFollowSys(true);
                if (ReadAloudService.running) {
                    ReadAloudService.stop(this);
                }
            } else {
                //不跟随系统
                seekReadSpeed.setEnabled(true);
                seekReadSpeed.changeTheme(this);
                readBookControl.setSpeechRateFollowSys(false);
                if (ReadAloudService.running) {
                    ReadAloudService.pause(this);
                    ReadAloudService.resume(this);
                }
            }
        });
        seekReadSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                readBookControl.setSpeechRate(seekBar.getProgress() + 5);
                if (ReadAloudService.running) {
                    ReadAloudService.pause(SettingActivity.this);
                    ReadAloudService.resume(SettingActivity.this);
                }
            }
        });
        //自动翻页速度
        seekAutoNext.setMax(180);
        seekAutoNext.setProgress(readBookControl.getClickSensitivity());
        tvAutoNextTime.setText(String.format("%ss", readBookControl.getClickSensitivity()));
        //自动翻页速度
        seekAutoNext.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvAutoNextTime.setText(String.format("%ss", i));
                readBookControl.setClickSensitivity(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //下载线程数
        preferences = MApplication.getConfigPreferences();
        int number = preferences.getInt(this.getString(R.string.pk_threads_num), 6);
        tvThreadNum.setText(getString(R.string.threads_num, Integer.toString(number)));
        llThreadNum.setOnClickListener(v -> {
            if (threadNumDialog == null) {
                threadNumDialog = new ThreadNumDialog(SettingActivity.this);
                threadNumDialog.init(number);
                threadNumDialog.setListener(new ThreadNumDialog.DialogClickListener() {
                    @Override
                    public void confirm(int num) {
                        tvThreadNum.setText(getString(R.string.threads_num, Integer.toString(num)));
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(getString(R.string.pk_threads_num), num);
                        editor.apply();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
            threadNumDialog.show();
        });
        //清除缓存
        tvClearCache.setOnClickListener(view -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.clear_cache)
                    .setMessage(getString(R.string.sure_del_download_book))
                    .setPositiveButton(R.string.yes, (dialog, which) -> BookshelfHelp.clearCaches(true))
                    .setNegativeButton(R.string.no, (dialogInterface, i) -> BookshelfHelp.clearCaches(false))
                    .show();
            ATH.setAlertDialogTint(alertDialog);
        });
        //屏幕方向
        String[] screenDirections = getResources().getStringArray(R.array.screen_direction_list_title);
        tvScreenDirection.setText(screenDirections[readBookControl.getScreenDirection()]);
        llScreenDirection.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.screen_direction))
                    .setSingleChoiceItems(screenDirections, readBookControl.getScreenDirection(), (dialogInterface, i) -> {
                        readBookControl.setScreenDirection(i);
                        upScreenDirection(i);
                        dialogInterface.dismiss();
//                        SettingActivity.this.recreate();
                    })
                    .create();
            dialog.show();
            ATH.setAlertDialogTint(dialog);
        });
        //屏幕超时
        tvScreenTimeout.setText(getResources().getStringArray(R.array.screen_time_out)[readBookControl.getScreenTimeOut()]);
        llScreenTimeout.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.keep_light))
                    .setSingleChoiceItems(getResources().getStringArray(R.array.screen_time_out), readBookControl.getScreenTimeOut(), (dialogInterface, i) -> {
                        readBookControl.setScreenTimeOut(i);
                        tvScreenTimeout.setText(getResources().getStringArray(R.array.screen_time_out)[i]);
                        dialogInterface.dismiss();
                    })
                    .create();
            dialog.show();
            ATH.setAlertDialogTint(dialog);
        });
        //简繁转换
        tvConversion.setText(getResources().getStringArray(R.array.convert_s)[readBookControl.getTextConvert()]);
        llConversion.setOnClickListener(view -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.jf_convert))
                    .setSingleChoiceItems(getResources().getStringArray(R.array.convert_s), readBookControl.getTextConvert(), (dialogInterface, i) -> {
                        readBookControl.setTextConvert(i);
                        tvConversion.setText(getResources().getStringArray(R.array.convert_s)[i]);
                        dialogInterface.dismiss();
                    })
                    .create();
            dialog.show();
            ATH.setAlertDialogTint(dialog);
        });
        //点击翻页
        swClickPage.setChecked(readBookControl.getCanClickTurn());
        swClickPage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                readBookControl.setCanClickTurn(isChecked);
                upView();
            }
        });
        //音量键翻页
        swVolumePage.setChecked(readBookControl.getCanKeyTurn());
        swVolumePage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                readBookControl.setCanKeyTurn(isChecked);
                upView();
            }
        });
        //朗读时音量键翻页
//        swReadVolumePage
        swReadVolumePage.setChecked(readBookControl.getAloudCanKeyTurn());
        swReadVolumePage.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                readBookControl.setAloudCanKeyTurn(b);
            }
        });
        //隐藏状态栏
        swHideStatusBar.setChecked(readBookControl.getHideStatusBar());
        swHideStatusBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                readBookControl.setHideStatusBar(isChecked);
//                callback.recreate();
            }
        });
        //显示时间和电量
        swTime.setChecked(readBookControl.getShowTimeBattery());
        swTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                readBookControl.setShowTimeBattery(isChecked);
            }
        });
    }

    private void upView() {
        if (readBookControl.getCanKeyTurn()) {
            swReadVolumePage.setEnabled(true);
        } else {
            swReadVolumePage.setEnabled(false);
        }
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

    private void upScreenDirection(int screenDirection) {
        String[] screenDirectionListTitle = getResources().getStringArray(R.array.screen_direction_list_title);
        if (screenDirection >= screenDirectionListTitle.length) {
            tvScreenDirection.setText(screenDirectionListTitle[0]);
        } else {
            tvScreenDirection.setText(screenDirectionListTitle[screenDirection]);
        }
    }
}
