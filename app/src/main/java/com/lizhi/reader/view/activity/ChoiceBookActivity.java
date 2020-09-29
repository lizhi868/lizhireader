package com.lizhi.reader.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lizhi.basemvplib.BitIntentDataManager;
import com.lizhi.reader.R;
import com.lizhi.reader.base.MBaseActivity;
import com.lizhi.reader.bean.SearchBookBean;
import com.lizhi.reader.presenter.BookDetailPresenter;
import com.lizhi.reader.presenter.ChoiceBookPresenter;
import com.lizhi.reader.presenter.contract.ChoiceBookContract;
import com.lizhi.reader.utils.theme.ThemeStore;
import com.lizhi.reader.view.adapter.ChoiceBookAdapter;
import com.lizhi.reader.widget.recycler.refresh.OnLoadMoreListener;
import com.lizhi.reader.widget.recycler.refresh.RefreshRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChoiceBookActivity extends MBaseActivity<ChoiceBookContract.Presenter> implements ChoiceBookContract.View {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rfRv_search_books)
    RefreshRecyclerView rfRvSearchBooks;

    private ChoiceBookAdapter searchBookAdapter;
    private View viewRefreshError;

    @Override
    protected ChoiceBookContract.Presenter initInjector() {
        return new ChoiceBookPresenter(getIntent());
    }

    @Override
    protected void onCreateActivity() {
        getWindow().getDecorView().setBackgroundColor(ThemeStore.backgroundColor(this));
        setContentView(R.layout.activity_book_choice);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void initData() {
        searchBookAdapter = new ChoiceBookAdapter(this);
    }

    @SuppressLint("InflateParams")
    @Override
    protected void bindView() {
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        setupActionBar();

        rfRvSearchBooks.setRefreshRecyclerViewAdapter(searchBookAdapter, new LinearLayoutManager(this));

        viewRefreshError = LayoutInflater.from(this).inflate(R.layout.view_refresh_error, null);
        viewRefreshError.findViewById(R.id.tv_refresh_again).setOnClickListener(v -> {
            searchBookAdapter.replaceAll(null);
            //刷新失败 ，重试
            mPresenter.initPage();
            mPresenter.toSearchBooks(null);
            startRefreshAnim();
        });
        rfRvSearchBooks.setNoDataAndRefreshErrorView(LayoutInflater.from(this).inflate(R.layout.view_refresh_no_data, null),
                viewRefreshError);
    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mPresenter.getTitle());
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
    protected void bindEvent() {
        searchBookAdapter.setCallback((animView, position, searchBookBean) -> {
            String dataKey = String.valueOf(System.currentTimeMillis());
            Intent intent = new Intent(ChoiceBookActivity.this, BookDetailActivity.class);
            intent.putExtra("openFrom", BookDetailPresenter.FROM_SEARCH);
            intent.putExtra("data_key", dataKey);
            BitIntentDataManager.getInstance().putData(dataKey, searchBookBean);
            startActivity(intent);
        });

        rfRvSearchBooks.setBaseRefreshListener(() -> {
            mPresenter.initPage();
            mPresenter.toSearchBooks(null);
            startRefreshAnim();
        });
        rfRvSearchBooks.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void startLoadMore() {
                mPresenter.toSearchBooks(null);
            }

            @Override
            public void loadMoreErrorTryAgain() {
                mPresenter.toSearchBooks(null);
            }
        });
    }

    @Override
    public void refreshSearchBook(List<SearchBookBean> books) {
        searchBookAdapter.replaceAll(books);
    }

    @Override
    public void refreshFinish(Boolean isAll) {
        rfRvSearchBooks.finishRefresh(isAll, true);
    }

    @Override
    public void loadMoreFinish(Boolean isAll) {
        rfRvSearchBooks.finishLoadMore(isAll, true);
    }

    @Override
    public void loadMoreSearchBook(final List<SearchBookBean> books) {
        if (books.size() <= 0) {
            loadMoreFinish(true);
            return;
        }
        searchBookAdapter.addAll(books);
        loadMoreFinish(false);
    }

    @Override
    public void searchBookError(String msg) {
        if (mPresenter.getPage() > 1) {
            rfRvSearchBooks.finishLoadMore(true, true);
        } else {
            //刷新失败
            rfRvSearchBooks.refreshError();
            if (msg != null) {
                ((TextView) viewRefreshError.findViewById(R.id.tv_error_msg)).setText(msg);
            } else {
                ((TextView) viewRefreshError.findViewById(R.id.tv_error_msg)).setText(R.string.get_data_error);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void addBookShelfFailed(String massage) {
        toast(massage, ERROR);
    }

    @Override
    public void startRefreshAnim() {
        rfRvSearchBooks.startRefresh();
    }

    @Override
    protected void firstRequest() {
        super.firstRequest();
    }

}