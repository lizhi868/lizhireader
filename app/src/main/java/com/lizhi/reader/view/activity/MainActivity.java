package com.lizhi.reader.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hwangjr.rxbus.RxBus;
import com.lizhi.reader.DbHelper;
import com.lizhi.reader.MApplication;
import com.lizhi.reader.R;
import com.lizhi.reader.base.BaseTabActivity;
import com.lizhi.reader.constant.RxBusTag;
import com.lizhi.reader.help.FileHelp;
import com.lizhi.reader.help.permission.Permissions;
import com.lizhi.reader.help.permission.PermissionsCompat;
import com.lizhi.reader.model.UpLastChapterModel;
import com.lizhi.reader.presenter.MainPresenter;
import com.lizhi.reader.presenter.contract.MainContract;
import com.lizhi.reader.service.WebService;
import com.lizhi.reader.utils.ReadAssets;
import com.lizhi.reader.utils.StringUtils;
import com.lizhi.reader.utils.theme.ATH;
import com.lizhi.reader.utils.theme.ThemeStore;
import com.lizhi.reader.view.fragment.BookListFragment;
import com.lizhi.reader.view.fragment.FindBookFragment;
import com.lizhi.reader.widget.explosion_field.Utils;
import com.lizhi.reader.widget.modialog.InputDialog;
import com.lizhi.reader.widget.modialog.MoDialogHUD;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;

import static com.lizhi.reader.utils.NetworkUtils.isNetWorkAvailable;

public class MainActivity extends BaseTabActivity<MainContract.Presenter> implements MainContract.View
        , BookListFragment.CallbackValue, BookListFragment.PageHandListener {
    private final int requestSource = 14;
    private String[] mTitles;

    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.navigation_view)
    LinearLayout navigationView;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.iv_head)
    ImageView ivHead;

    private AppCompatImageView vwNightTheme;
    private int group;
    private ActionBarDrawerToggle mDrawerToggle;
    private MoDialogHUD moDialogHUD;
    private long exitTime = 0;
    private boolean resumed = false;
    private Handler handler = new Handler();
    private int versionCode;//是否第一次进入

    @Override
    protected MainContract.Presenter initInjector() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        group = preferences.getInt("bookshelfGroup", 0);
        versionCode = preferences.getInt("versionCode", -1);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("resumed", resumed);
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        String shared_url = preferences.getString("shared_url", "");
        if (shared_url.length() > 1) {
            InputDialog.builder(this)
                    .setTitle(getString(R.string.add_book_url))
                    .setDefaultValue(shared_url)
                    .setCallback(new InputDialog.Callback() {
                        @Override
                        public void setInputText(String inputText) {
                            inputText = StringUtils.trim(inputText);
                            mPresenter.addBookUrl(inputText);
                        }

                        @Override
                        public void delete(String value) {

                        }
                    }).show();
            preferences.edit()
                    .putString("shared_url", "")
                    .apply();
        }
    }


    /**
     * 沉浸状态栏
     */
    @Override
    public void initImmersionBar() {
        super.initImmersionBar();
    }

    @Override
    protected void initData() {
        mTitles = new String[]{"书架", "书库"};
    }

    @Override
    public boolean isRecreate() {
        return isRecreate;
    }

    @Override
    public int getGroup() {
        return group;
    }

    @Override
    public void showSnackBar(String msg, int length) {
        showSnackBar(getViewPager(), msg, length);
    }

    @Override
    public void refreshBookSource() {
        FindBookFragment findBookFragment = getFindFragment();
        if (findBookFragment != null) {
            findBookFragment.refreshData();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected List<Fragment> createTabFragments() {
        BookListFragment bookListFragment = null;
        FindBookFragment findBookFragment = null;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BookListFragment) {
                bookListFragment = (BookListFragment) fragment;
            } else if (fragment instanceof FindBookFragment) {
                findBookFragment = (FindBookFragment) fragment;
            }
        }
        if (bookListFragment == null) {
            bookListFragment = new BookListFragment();
        }
        if (findBookFragment == null)
            findBookFragment = new FindBookFragment();
        return Arrays.asList(bookListFragment, findBookFragment);
    }

    @Override
    protected List<String> createTabTitles() {
        return Arrays.asList(mTitles);
    }

    @Override
    protected void bindView() {
        super.bindView();
        setupActionBar();
        initDrawer();
        initTabLayout();
        upGroup(group);
        moDialogHUD = new MoDialogHUD(this);
        //点击跳转搜索页
        tvSearch.setOnClickListener(view -> startActivityByAnim(new Intent(this, SearchBookActivity.class),
                tvSearch, "sharedView", android.R.anim.fade_in, android.R.anim.fade_out));
        ivHead.setOnClickListener(view ->
                        drawer.openDrawer(GravityCompat.START)
//                CrashReport.testJavaCrash()
        );
        tvVersion.setText("Version " + MApplication.getVersionName());
    }

    //初始化TabLayout和ViewPager
    private void initTabLayout() {
        mTlIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.main_color));
        mTlIndicator.setSelectedTabIndicator(R.drawable.bg_indicator_line);
        modifyTabIndicatorWidth(mTlIndicator, Utils.dp2Px(6), Utils.dp2Px(6));
        //TabLayout使用自定义Item
        for (int i = 0; i < mTlIndicator.getTabCount(); i++) {
            TabLayout.Tab tab = mTlIndicator.getTabAt(i);
            if (tab == null) return;
            tab.setCustomView(tab_icon(mTitles[i]));
        }
        mTlIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) return;
                TextView tv = customView.findViewById(R.id.tabtext);
                tv.setTextSize(22);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) return;
                TextView tv = customView.findViewById(R.id.tabtext);
                tv.setTextSize(16);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TabLayout.Tab tab = mTlIndicator.getTabAt(1);
        if (tab != null) {
            View customView = tab.getCustomView();
            if (customView == null) return;
            TextView tv = customView.findViewById(R.id.tabtext);
            tv.setTextSize(16);
        }
    }

    private View tab_icon(String name) {
        @SuppressLint("InflateParams")
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_view_icon_right, null);
        TextView tv = tabView.findViewById(R.id.tabtext);
        tv.setText(name);
        return tabView;
    }

    public ViewPager getViewPager() {
        return mVp;
    }

    public BookListFragment getBookListFragment() {
        try {
            return (BookListFragment) mFragmentList.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public FindBookFragment getFindFragment() {
        try {
            return (FindBookFragment) mFragmentList.get(1);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 这个必须要，没有的话进去的默认是个箭头。。正常应该是三横杠的
        mDrawerToggle.syncState();
        if (vwNightTheme != null) {
            upThemeVw();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    // 添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_local:
                new PermissionsCompat.Builder(this)
                        .addPermissions(Permissions.READ_EXTERNAL_STORAGE, Permissions.WRITE_EXTERNAL_STORAGE)
                        .rationale(R.string.import_per)
                        .onGranted((requestCode) -> {
                            startActivity(new Intent(MainActivity.this, ImportBookActivity.class));
                            return Unit.INSTANCE;
                        })
                        .request();
                break;
            case R.id.action_add_url:
                InputDialog.builder(this)
                        .setTitle(getString(R.string.add_book_url))
                        .setCallback(new InputDialog.Callback() {
                            @Override
                            public void setInputText(String inputText) {
                                inputText = StringUtils.trim(inputText);
                                mPresenter.addBookUrl(inputText);
                            }

                            @Override
                            public void delete(String value) {

                            }
                        }).show();
                break;
            case R.id.action_download_all:
                if (!isNetWorkAvailable())
                    toast(R.string.network_connection_unavailable);
                else
                    RxBus.get().post(RxBusTag.DOWNLOAD_ALL, 10000);
                break;
            case R.id.menu_bookshelf_layout:
                selectBookshelfLayout();
                break;
            case R.id.action_arrange_bookshelf:
                if (getBookListFragment() != null) {
                    getBookListFragment().setArrange(true);
                }
                break;
            case R.id.action_web_start:
                WebService.startThis(this);
                break;
            case android.R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers();
                } else {
                    drawer.openDrawer(GravityCompat.START, !MApplication.isEInkMode);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //初始化侧边栏
    private void initDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerToggle.syncState();
        drawer.addDrawerListener(mDrawerToggle);

        setUpNavigationView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    private void upGroup(int group) {
        if (this.group != group) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("bookshelfGroup", group);
            editor.apply();
        }
        this.group = group;
        RxBus.get().post(RxBusTag.UPDATE_GROUP, group);
        RxBus.get().post(RxBusTag.REFRESH_BOOK_LIST, false);
    }

    /**
     * 侧边栏按钮
     */
    private void setUpNavigationView() {
        navigationView.setBackgroundColor(ThemeStore.backgroundColor(this));
//        NavigationViewUtil.setItemIconColors(navigationView, getResources().getColor(R.color.tv_text_default), ThemeStore.accentColor(this));
//        NavigationViewUtil.disableScrollbar(navigationView);
//        @SuppressLint("InflateParams") View headerView = LayoutInflater.from(this).inflate(R.layout.navigation_header, null);
//        AppCompatImageView imageView = headerView.findViewById(R.id.iv_read);
//        imageView.setColorFilter(ThemeStore.accentColor(this));
//        navigationView.addHeaderView(headerView);
//        Menu drawerMenu = navigationView.getMenu();
//        vwNightTheme = drawerMenu.findItem(R.id.action_setting).getActionView().findViewById(R.id.iv_theme_day_night);
        navigationView.setOnClickListener(view -> {
        });
        vwNightTheme = navigationView.findViewById(R.id.iv_theme_day_night);
        upThemeVw();
        //黑夜模式
        vwNightTheme.setOnClickListener(view -> {
            drawer.closeDrawer(GravityCompat.START, !MApplication.isEInkMode);
            setNightTheme(!isNightTheme());
        });
        //下载任务
        navigationView.findViewById(R.id.action_download).setOnClickListener(view -> {
            drawer.closeDrawer(GravityCompat.START, !MApplication.isEInkMode);
            DownloadActivity.startThis(this);
        });
        //设置
        navigationView.findViewById(R.id.action_setting).setOnClickListener(view -> {
            drawer.closeDrawer(GravityCompat.START, !MApplication.isEInkMode);
            SettingActivity.startThis(this);
        });
        //关于
        navigationView.findViewById(R.id.action_about).setOnClickListener(view -> {
            drawer.closeDrawer(GravityCompat.START, !MApplication.isEInkMode);
            AboutActivity.startThis(this);
        });
    }

    /**
     * 更新主题切换按钮
     */
    private void upThemeVw() {
        if (isNightTheme()) {
            vwNightTheme.setImageResource(R.drawable.ic_daytime);
            vwNightTheme.setContentDescription(getString(R.string.click_to_day));
        } else {
            vwNightTheme.setImageResource(R.drawable.ic_brightness);
            vwNightTheme.setContentDescription(getString(R.string.click_to_night));
        }
        vwNightTheme.getDrawable().mutate().setColorFilter(ThemeStore.accentColor(this), PorterDuff.Mode.SRC_ATOP);
    }

    private void selectBookshelfLayout() {
        new AlertDialog.Builder(this)
                .setTitle("选择书架布局")
                .setItems(R.array.bookshelf_layout, (dialog, which) -> {
                    preferences.edit().putInt("bookshelfLayout", which).apply();
                    recreate();
                }).show();
    }

    /**
     * 备份
     */
    private void backup() {
        new PermissionsCompat.Builder(this)
                .addPermissions(Permissions.READ_EXTERNAL_STORAGE, Permissions.WRITE_EXTERNAL_STORAGE)
                .rationale(R.string.backup_permission)
                .onGranted((requestCode) -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.backup_confirmation)
                            .setMessage(R.string.backup_message)
                            .setPositiveButton(R.string.ok, (dialog, which) -> mPresenter.backupData())
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                    ATH.setAlertDialogTint(alertDialog);
                    return Unit.INSTANCE;
                }).request();
    }

    /**
     * 恢复
     */
    private void restore() {
        new PermissionsCompat.Builder(this)
                .addPermissions(Permissions.READ_EXTERNAL_STORAGE, Permissions.WRITE_EXTERNAL_STORAGE)
                .rationale(R.string.restore_permission)
                .onGranted((requestCode) -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.restore_confirmation)
                            .setMessage(R.string.restore_message)
                            .setPositiveButton(R.string.ok, (dialog, which) -> mPresenter.restoreData())
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                    ATH.setAlertDialogTint(alertDialog);
                    return Unit.INSTANCE;
                }).request();
    }

    @Override
    protected void firstRequest() {
        if (!isRecreate) {
            if (versionCode != MApplication.getVersionCode()) {
                mPresenter.removeAllSource();
                mPresenter.importBookSourceLocal(ReadAssets.getText(this, "bookSource.txt"));
                versionCode = MApplication.getVersionCode();
                preferences.edit().putInt("versionCode", versionCode).apply();
            }
        }
        if (!Objects.equals(MApplication.downloadPath, FileHelp.getFilesPath())) {
            new PermissionsCompat.Builder(this)
                    .addPermissions(Permissions.READ_EXTERNAL_STORAGE, Permissions.WRITE_EXTERNAL_STORAGE)
                    .rationale(R.string.get_storage_per)
                    .request();
        }

//            mPresenter.importBookSourceLocal(getAssets().open("bookSource.txt"));
//        handler.postDelayed(() -> {
//            UpLastChapterModel.getInstance().startUpdate();
//            if (BuildConfig.DEBUG) {
//                ProcessTextHelp.setProcessTextEnable(false);
//            }
//        }, 60 * 1000);
    }

    @Override
    public void dismissHUD() {
        moDialogHUD.dismiss();
    }

    public void onRestore(String msg) {
        moDialogHUD.showLoading(msg);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Boolean mo = moDialogHUD.onKeyDown(keyCode, event);
        if (mo) {
            return true;
        } else if (mTlIndicator.getSelectedTabPosition() != 0) {
            Objects.requireNonNull(mTlIndicator.getTabAt(0)).select();
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START, !MApplication.isEInkMode);
                    return true;
                }
                exit();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 退出
     */
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void onDestroy() {
        UpLastChapterModel.destroy();
        DbHelper.getDaoSession().getBookContentBeanDao().deleteAll();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == requestSource) {
                FindBookFragment findBookFragment = getFindFragment();
                if (findBookFragment != null) {
                    findBookFragment.refreshData();
                }
            }
        }
    }

    @Override
    public void pageHand() {
        mVp.setCurrentItem(1, true);
    }
}
