package com.lizhi.reader.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.reader.R;
import com.lizhi.reader.base.MBaseActivity;
import com.lizhi.reader.bean.DownloadBookBean;
import com.lizhi.reader.service.DownloadService;
import com.lizhi.reader.utils.theme.ThemeStore;
import com.lizhi.reader.view.adapter.DownloadAdapter;
import com.lizhi.reader.widget.explosion_field.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lizhi.reader.service.DownloadService.addDownloadAction;
import static com.lizhi.reader.service.DownloadService.finishDownloadAction;
import static com.lizhi.reader.service.DownloadService.obtainDownloadListAction;
import static com.lizhi.reader.service.DownloadService.progressDownloadAction;
import static com.lizhi.reader.service.DownloadService.removeDownloadAction;

public class DownloadActivity extends MBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.tv_none)
    TextView tvNone;

    private DownloadAdapter adapter;
    private DownloadReceiver receiver;

    public static void startThis(Activity activity) {
        Intent intent = new Intent(activity, DownloadActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    /**
     * P层绑定   若无则返回null;
     */
    @Override
    protected IPresenter initInjector() {
        return null;
    }

    /**
     * 布局载入  setContentView()
     */
    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_recycler_vew);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar();
    }

    /**
     * 数据初始化
     */
    @Override
    protected void initData() {
        receiver = new DownloadReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(addDownloadAction);
        filter.addAction(removeDownloadAction);
        filter.addAction(progressDownloadAction);
        filter.addAction(obtainDownloadListAction);
        filter.addAction(finishDownloadAction);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void bindView() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = Utils.dp2Px(12);
                }
            }
        });
        adapter = new DownloadAdapter(this);
        adapter.setListener(number -> {
            if (number == 0) {
                if (tvNone.getVisibility() == View.GONE){
                    tvNone.setVisibility(View.VISIBLE);
                }
            }else {
                if (tvNone.getVisibility() == View.VISIBLE){
                    tvNone.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        DownloadService.obtainDownloadList(this);
    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.download_offline);
        }
    }

    // 添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_download, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_cancel:
                DownloadService.cancelDownload(this);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class DownloadReceiver extends BroadcastReceiver {

        WeakReference<DownloadActivity> ref;

        public DownloadReceiver(DownloadActivity activity) {
            this.ref = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadAdapter adapter = ref.get().adapter;
            if (adapter == null || intent == null) {
                return;
            }
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case addDownloadAction:
                        DownloadBookBean downloadBook = intent.getParcelableExtra("downloadBook");
                        adapter.addData(downloadBook);
                        break;
                    case removeDownloadAction:
                        downloadBook = intent.getParcelableExtra("downloadBook");
                        adapter.removeData(downloadBook);
                        break;
                    case progressDownloadAction:
                        downloadBook = intent.getParcelableExtra("downloadBook");
                        adapter.upData(downloadBook);
                        break;
                    case finishDownloadAction:
                        adapter.upDataS(null);
                        break;
                    case obtainDownloadListAction:
                        ArrayList<DownloadBookBean> downloadBooks = intent.getParcelableArrayListExtra("downloadBooks");
                        adapter.upDataS(downloadBooks);
                        break;

                }
            }
        }
    }
}
