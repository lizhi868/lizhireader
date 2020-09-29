package com.lizhi.reader.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.lizhi.basemvplib.BitIntentDataManager;
import com.lizhi.reader.R;
import com.lizhi.reader.base.MBaseFragment;
import com.lizhi.reader.base.observer.MySingleObserver;
import com.lizhi.reader.bean.BookShelfBean;
import com.lizhi.reader.help.BookshelfHelp;
import com.lizhi.reader.help.ItemTouchCallback;
import com.lizhi.reader.presenter.BookDetailPresenter;
import com.lizhi.reader.presenter.BookListPresenter;
import com.lizhi.reader.presenter.ReadBookPresenter;
import com.lizhi.reader.presenter.contract.BookListContract;
import com.lizhi.reader.utils.NetworkUtils;
import com.lizhi.reader.utils.RxUtils;
import com.lizhi.reader.utils.theme.ATH;
import com.lizhi.reader.view.activity.BookDetailActivity;
import com.lizhi.reader.view.activity.ReadBookActivity;
import com.lizhi.reader.view.adapter.BookShelfAdapter;
import com.lizhi.reader.view.adapter.BookShelfGridAdapter;
import com.lizhi.reader.view.adapter.base.OnItemClickListenerTwo;
import com.lizhi.reader.widget.explosion_field.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;

public class BookListFragment extends MBaseFragment<BookListContract.Presenter> implements BookListContract.View {

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.local_book_rv_content)
    RecyclerView rvBookshelf;
    @BindView(R.id.tv_go)
    TextView tvGo;
    @BindView(R.id.rl_empty_view)
    LinearLayout rlEmptyView;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.action_bar)
    LinearLayout actionBar;
    @BindView(R.id.tv_select_count)
    TextView tvSelectCount;
    @BindView(R.id.iv_del)
    ImageView ivDel;
    @BindView(R.id.iv_select_all)
    ImageView ivSelectAll;

    private CallbackValue callbackValue;
    private Unbinder unbinder;
    private String bookPx;
    private boolean resumed = false;
    private boolean isRecreate;
    private int group;

    private BookShelfAdapter bookShelfAdapter;

    public interface PageHandListener {
        void pageHand();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resumed = savedInstanceState.getBoolean("resumed");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public int createLayoutId() {
        return R.layout.fragment_book_list;
    }

    @Override
    protected BookListContract.Presenter initInjector() {
        return new BookListPresenter();
    }

    @Override
    protected void initData() {
        callbackValue = (CallbackValue) getActivity();
        bookPx = preferences.getString(getString(R.string.pk_bookshelf_px), "0");
        isRecreate = callbackValue != null && callbackValue.isRecreate();
    }

    @Override
    protected void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
        rvBookshelf.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvBookshelf.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int pos = parent.getChildAdapterPosition(view);
                if (pos % 3 == 0) {
                    outRect.left = Utils.dp2Px(20);
                } else {
                    outRect.left = Utils.dp2Px(16);
                }
                if ((pos + 1) % 3 == 0) {
                    outRect.right = Utils.dp2Px(20);
                } else {
                    outRect.right = Utils.dp2Px(16);
                }
                if (pos < 3) {
                    outRect.top = Utils.dp2Px(15);
                }
                outRect.bottom = Utils.dp2Px(24);
            }
        });
        bookShelfAdapter = new BookShelfGridAdapter(getActivity());
        rvBookshelf.setAdapter((RecyclerView.Adapter) bookShelfAdapter);
        refreshLayout.setColorSchemeResources(R.color.main_color);
        tvGo.setOnClickListener(v -> {
            if (getActivity() instanceof PageHandListener) {
                ((PageHandListener) getActivity()).pageHand();
            }
        });
    }

    @Override
    protected void firstRequest() {
        group = preferences.getInt("bookshelfGroup", 0);
        if (preferences.getBoolean(getString(R.string.pk_auto_refresh), false)
                && !isRecreate && NetworkUtils.isNetWorkAvailable() && group != 2) {
            mPresenter.queryBookShelf(true, group);
        } else {
            mPresenter.queryBookShelf(false, group);
        }
    }

    @Override
    protected void bindEvent() {
        refreshLayout.setOnRefreshListener(() -> {
            mPresenter.queryBookShelf(NetworkUtils.isNetWorkAvailable(), group);
            if (!NetworkUtils.isNetWorkAvailable()) {
                Toast.makeText(getContext(), R.string.network_connection_unavailable, Toast.LENGTH_SHORT).show();
            }
            refreshLayout.setRefreshing(false);
        });
        ItemTouchCallback itemTouchCallback = new ItemTouchCallback();
        itemTouchCallback.setSwipeRefreshLayout(refreshLayout);
        itemTouchCallback.setViewPager(callbackValue.getViewPager());
        if (bookPx.equals("2")) {
            itemTouchCallback.setDragEnable(true);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rvBookshelf);
        } else {
            itemTouchCallback.setDragEnable(false);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rvBookshelf);
        }
        bookShelfAdapter.setItemClickListener(getAdapterListener());
        itemTouchCallback.setOnItemTouchCallbackListener(bookShelfAdapter.getItemTouchCallbackListener());
        ivBack.setOnClickListener(v -> setArrange(false));
        ivDel.setOnClickListener(v -> {
            if (bookShelfAdapter.getSelected().size() == bookShelfAdapter.getBooks().size()) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.delete)
                        .setMessage(getString(R.string.sure_del_all_book))
                        .setPositiveButton(R.string.yes, (dialog, which) -> delSelect())
                        .setNegativeButton(R.string.no, null)
                        .show();
                ATH.setAlertDialogTint(alertDialog);
            } else {
                delSelect();
            }
        });
        ivSelectAll.setOnClickListener(v -> bookShelfAdapter.selectAll());
    }

    private OnItemClickListenerTwo getAdapterListener() {
        return new OnItemClickListenerTwo() {
            @Override
            public void onClick(View view, int index) {
                if (actionBar.getVisibility() == View.VISIBLE) {
                    upSelectCount();
                    return;
                }
                BookShelfBean bookShelfBean = bookShelfAdapter.getBooks().get(index);
                Intent intent = new Intent(getContext(), ReadBookActivity.class);
                intent.putExtra("openFrom", ReadBookPresenter.OPEN_FROM_APP);
                String key = String.valueOf(System.currentTimeMillis());
                String bookKey = "book" + key;
                intent.putExtra("bookKey", bookKey);
                BitIntentDataManager.getInstance().putData(bookKey, bookShelfBean.clone());
                startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onLongClick(View view, int index) {
                BookShelfBean bookShelfBean = bookShelfAdapter.getBooks().get(index);
                String key = String.valueOf(System.currentTimeMillis());
                BitIntentDataManager.getInstance().putData(key, bookShelfBean.clone());
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                intent.putExtra("openFrom", BookDetailPresenter.FROM_BOOKSHELF);
                intent.putExtra("data_key", key);
                intent.putExtra("noteUrl", bookShelfBean.getNoteUrl());
                startActivityByAnim(intent, android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumed) {
            resumed = false;
            stopBookShelfRefreshAnim();
        }
    }

    @Override
    public void onPause() {
        resumed = true;
        super.onPause();
    }

    private void stopBookShelfRefreshAnim() {
        if (bookShelfAdapter.getBooks() != null && bookShelfAdapter.getBooks().size() > 0) {
            for (BookShelfBean bookShelfBean : bookShelfAdapter.getBooks()) {
                if (bookShelfBean.isLoading()) {
                    bookShelfBean.setLoading(false);
                    refreshBook(bookShelfBean.getNoteUrl());
                }
            }
        }
    }

    @Override
    public void refreshBookShelf(List<BookShelfBean> bookShelfBeanList) {
        bookShelfAdapter.replaceAll(bookShelfBeanList, bookPx);
        if (rlEmptyView == null) return;
        if (bookShelfBeanList.size() > 0) {
            rlEmptyView.setVisibility(View.GONE);
        } else {
            rlEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refreshBook(String noteUrl) {
        bookShelfAdapter.refreshBook(noteUrl);
    }

    @Override
    public void updateGroup(Integer group) {
        this.group = group;
    }

    @Override
    public void refreshError(String error) {
        toast(error);
    }

    @Override
    public SharedPreferences getPreferences() {
        return preferences;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setArrange(boolean isArrange) {
        if (bookShelfAdapter != null) {
            bookShelfAdapter.setArrange(isArrange);
            if (isArrange) {
                actionBar.setVisibility(View.VISIBLE);
                upSelectCount();
            } else {
                actionBar.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void upSelectCount() {
        tvSelectCount.setText(String.format("%d/%d", bookShelfAdapter.getSelected().size(), bookShelfAdapter.getBooks().size()));
    }

    private void delSelect() {
        Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            for (String noteUrl : bookShelfAdapter.getSelected()) {
                BookshelfHelp.removeFromBookShelf(BookshelfHelp.getBook(noteUrl));
            }
            bookShelfAdapter.getSelected().clear();
            emitter.onSuccess(true);
        }).compose(RxUtils::toSimpleSingle)
                .subscribe(new MySingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        mPresenter.queryBookShelf(false, group);
                    }
                });
    }

    public interface CallbackValue {
        boolean isRecreate();

        int getGroup();

        ViewPager getViewPager();
    }

}
