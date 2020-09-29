package com.lizhi.reader.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import com.google.android.material.snackbar.Snackbar;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.lizhi.basemvplib.BasePresenterImpl;
import com.lizhi.basemvplib.impl.IView;
import com.lizhi.reader.DbHelper;
import com.lizhi.reader.R;
import com.lizhi.reader.base.observer.MyObserver;
import com.lizhi.reader.bean.BookChapterBean;
import com.lizhi.reader.bean.BookInfoBean;
import com.lizhi.reader.bean.BookShelfBean;
import com.lizhi.reader.bean.BookSourceBean;
import com.lizhi.reader.bean.TxtChapterRuleBean;
import com.lizhi.reader.constant.RxBusTag;
import com.lizhi.reader.dao.BookSourceBeanDao;
import com.lizhi.reader.help.BookshelfHelp;
import com.lizhi.reader.help.DataBackup;
import com.lizhi.reader.help.DataRestore;
import com.lizhi.reader.help.DocumentHelper;
import com.lizhi.reader.model.BookSourceManager;
import com.lizhi.reader.model.TxtChapterRuleManager;
import com.lizhi.reader.model.WebBookModel;
import com.lizhi.reader.presenter.contract.MainContract;
import com.lizhi.reader.utils.GsonUtils;
import com.lizhi.reader.utils.RxUtils;
import com.lizhi.reader.utils.StringUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.text.TextUtils.isEmpty;

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    @Override
    public void backupData() {
        DataBackup.getInstance().run();
    }

    @Override
    public void restoreData() {
        mView.onRestore(mView.getContext().getString(R.string.on_restore));
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            if (DataRestore.getInstance().run()) {
                e.onNext(true);
            } else {
                e.onNext(false);
            }
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        mView.dismissHUD();
                        mView.toast(R.string.restore_success);
                        //更新书架并刷新
                        mView.recreate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.dismissHUD();
                        mView.toast(R.string.restore_fail);
                    }
                });
    }

    @Override
    public void addBookUrl(String bookUrls) {
        bookUrls = bookUrls.trim();
        if (TextUtils.isEmpty(bookUrls)) return;

        String[] urls = bookUrls.split("\\n");

        Observable.fromArray(urls)
                .flatMap(this::addBookUrlO)
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new MyObserver<BookShelfBean>() {
                    @Override
                    public void onNext(BookShelfBean bookShelfBean) {
                        getBook(bookShelfBean);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast(e.getMessage());
                    }
                });
    }

    @Override
    public void importBookSourceLocal(String json) {
//        if (TextUtils.isEmpty(path)) {
//            mView.toast(R.string.read_file_error);
//            return;
//        }
//        String json;
//        DocumentFile file;
//        try {
//            file = DocumentFile.fromFile(new File(path));
//        } catch (Exception e) {
//            mView.toast(path + "无法打开！");
//            return;
//        }
//        json = DocumentHelper.readString(file);
        if (!isEmpty(json)) {
            mView.showSnackBar("正在导入书源", Snackbar.LENGTH_INDEFINITE);
            importBookSource(json);
        } else {
            mView.toast(R.string.read_file_error);
        }
    }

    public void importBookSource(String text) {
        mView.showSnackBar("正在导入书源", Snackbar.LENGTH_INDEFINITE);
        Observable<List<BookSourceBean>> observable = BookSourceManager.importSource(text);
        if (observable != null) {
            observable.subscribe(getImportObserver());
        } else {
            mView.showSnackBar("格式不对", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void removeAllSource() {
        BookSourceManager.removeAllSource();
    }

    private MyObserver<List<BookSourceBean>> getImportObserver() {
        return new MyObserver<List<BookSourceBean>>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onNext(List<BookSourceBean> bookSourceBeans) {
                if (bookSourceBeans.size() > 0) {
                    mView.refreshBookSource();
//                    mView.showSnackBar(String.format("导入成功%d个书源", bookSourceBeans.size()), Snackbar.LENGTH_SHORT);
                    mView.showSnackBar("导入完毕", Snackbar.LENGTH_SHORT);
//                    mView.setResult(RESULT_OK);
                } else {
                    mView.showSnackBar("格式不对", Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.showSnackBar(e.getMessage(), Snackbar.LENGTH_SHORT);
            }
        };
    }

    private Observable<BookShelfBean> addBookUrlO(String bookUrl) {
        return Observable.create(e -> {
            if (StringUtils.isTrimEmpty(bookUrl)) {
                e.onComplete();
                return;
            }
            BookInfoBean temp = DbHelper.getDaoSession().getBookInfoBeanDao().load(bookUrl);
            if (temp != null) {
                e.onError(new Throwable("已在书架中"));
                return;
            } else {
                String baseUrl = StringUtils.getBaseUrl(bookUrl);
                BookSourceBean bookSourceBean = DbHelper.getDaoSession().getBookSourceBeanDao().load(baseUrl);
                if (bookSourceBean == null) {
                    List<BookSourceBean> sourceBeans = DbHelper.getDaoSession().getBookSourceBeanDao().queryBuilder()
                            .where(BookSourceBeanDao.Properties.RuleBookUrlPattern.isNotNull(), BookSourceBeanDao.Properties.RuleBookUrlPattern.notEq("")).list();
                    for (BookSourceBean sourceBean : sourceBeans) {
                        if (bookUrl.matches(sourceBean.getRuleBookUrlPattern())) {
                            bookSourceBean = sourceBean;
                            break;
                        }
                    }
                }
                if (bookSourceBean != null) {
                    BookShelfBean bookShelfBean = new BookShelfBean();
                    bookShelfBean.setTag(bookSourceBean.getBookSourceUrl());
                    bookShelfBean.setNoteUrl(bookUrl);
                    bookShelfBean.setDurChapter(0);
                    bookShelfBean.setGroup(mView.getGroup() % 4);
                    bookShelfBean.setDurChapterPage(0);
                    bookShelfBean.setFinalDate(System.currentTimeMillis());
                    e.onNext(bookShelfBean);
                } else {
                    e.onError(new Throwable("未找到对应书源"));
                    return;
                }
            }
            e.onComplete();
        });
    }

    private void getBook(BookShelfBean bookShelfBean) {
        WebBookModel.getInstance()
                .getBookInfo(bookShelfBean)
                .flatMap(bookShelfBean1 -> WebBookModel.getInstance().getChapterList(bookShelfBean1))
                .flatMap(chapterBeanList -> saveBookToShelfO(bookShelfBean, chapterBeanList))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<BookShelfBean>() {
                    @Override
                    public void onNext(BookShelfBean value) {
                        if (value.getBookInfoBean().getChapterUrl() == null) {
                            mView.toast("添加书籍失败");
                        } else {
                            //成功   //发送RxBus
                            RxBus.get().post(RxBusTag.HAD_ADD_BOOK, bookShelfBean);
                            mView.toast("添加书籍成功");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.toast("添加书籍失败" + e.getMessage());
                    }
                });
    }

    /**
     * 保存数据
     */
    private Observable<BookShelfBean> saveBookToShelfO(BookShelfBean bookShelfBean, List<BookChapterBean> chapterBeanList) {
        return Observable.create(e -> {
            BookshelfHelp.saveBookToShelf(bookShelfBean);
            DbHelper.getDaoSession().getBookChapterBeanDao().insertOrReplaceInTx(chapterBeanList);
            e.onNext(bookShelfBean);
            e.onComplete();
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
        RxBus.get().register(this);
    }

    @Override
    public void detachView() {
        RxBus.get().unregister(this);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.IMMERSION_CHANGE)})
    public void initImmersionBar(Boolean immersion) {
        mView.initImmersionBar();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.RECREATE)})
    public void recreate(Boolean recreate) {
        mView.recreate();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = {@Tag(RxBusTag.AUTO_BACKUP)})
    public void autoBackup(Boolean backup) {
        DataBackup.getInstance().autoSave();
    }
}