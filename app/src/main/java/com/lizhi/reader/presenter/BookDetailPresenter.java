package com.lizhi.reader.presenter;

import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.lizhi.basemvplib.BasePresenterImpl;
import com.lizhi.basemvplib.BitIntentDataManager;
import com.lizhi.basemvplib.impl.IView;
import com.lizhi.reader.DbHelper;
import com.lizhi.reader.base.observer.MyObserver;
import com.lizhi.reader.bean.BookChapterBean;
import com.lizhi.reader.bean.BookShelfBean;
import com.lizhi.reader.bean.BookSourceBean;
import com.lizhi.reader.bean.SearchBookBean;
import com.lizhi.reader.bean.TwoDataBean;
import com.lizhi.reader.constant.RxBusTag;
import com.lizhi.reader.help.BookshelfHelp;
import com.lizhi.reader.help.ChangeSourceHelp;
import com.lizhi.reader.model.BookSourceManager;
import com.lizhi.reader.model.SavedSource;
import com.lizhi.reader.model.WebBookModel;
import com.lizhi.reader.presenter.contract.BookDetailContract;
import com.lizhi.reader.utils.RxUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BookDetailPresenter extends BasePresenterImpl<BookDetailContract.View> implements BookDetailContract.Presenter {
    public final static int FROM_BOOKSHELF = 1;
    public final static int FROM_SEARCH = 2;

    private int openFrom;
    private SearchBookBean searchBook;
    private BookShelfBean bookShelf;
    private List<BookChapterBean> chapterBeanList;
    private Boolean inBookShelf = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void initData(Intent intent) {
        openFrom = intent.getIntExtra("openFrom", FROM_BOOKSHELF);
        String key = intent.getStringExtra("data_key");
        if (openFrom == FROM_BOOKSHELF) {
            bookShelf = (BookShelfBean) BitIntentDataManager.getInstance().getData(key);
            if (bookShelf == null) {
                String noteUrl = intent.getStringExtra("noteUrl");
                if (!TextUtils.isEmpty(noteUrl)) {
                    bookShelf = BookshelfHelp.getBook(noteUrl);
                } else {
                    mView.finish();
                    return;
                }
            }
            inBookShelf = true;
            searchBook = new SearchBookBean();
            searchBook.setNoteUrl(bookShelf.getNoteUrl());
            searchBook.setTag(bookShelf.getTag());
        } else {
            initBookFormSearch((SearchBookBean) BitIntentDataManager.getInstance().getData(key));
        }
    }

    @Override
    public void initBookFormSearch(SearchBookBean searchBookBean) {
        if (searchBookBean == null) {
            mView.finish();
            return;
        }
        searchBook = searchBookBean;
        inBookShelf = BookshelfHelp.isInBookShelf(searchBookBean.getNoteUrl());
        bookShelf = BookshelfHelp.getBookFromSearchBook(searchBookBean);
    }

    @Override
    public Boolean getInBookShelf() {
        return inBookShelf;
    }

    @Override
    public int getOpenFrom() {
        return openFrom;
    }

    @Override
    public SearchBookBean getSearchBook() {
        return searchBook;
    }

    @Override
    public BookShelfBean getBookShelf() {
        return bookShelf;
    }

    @Override
    public List<BookChapterBean> getChapterList() {
        return chapterBeanList;
    }

    @Override
    public void getBookShelfInfo() {
        if (bookShelf == null || TextUtils.equals(BookShelfBean.LOCAL_TAG, bookShelf.getTag()))
            return;
        WebBookModel.getInstance().getBookInfo(bookShelf)
                .flatMap(bookShelfBean -> WebBookModel.getInstance().getChapterList(bookShelfBean))
                .flatMap(chapterBeans -> saveBookToShelfO(bookShelf, chapterBeans))
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new MyObserver<List<BookChapterBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(List<BookChapterBean> bookChapterBeans) {
                        chapterBeanList = bookChapterBeans;
                        mView.updateView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.toast(e.getLocalizedMessage());
                        mView.getBookShelfError();
                    }
                });
    }

    /**
     * 保存数据
     */
    private Observable<List<BookChapterBean>> saveBookToShelfO(BookShelfBean bookShelfBean, List<BookChapterBean> chapterBeans) {
        return Observable.create(e -> {
            if (inBookShelf) {
                BookshelfHelp.saveBookToShelf(bookShelfBean);
                if (!chapterBeans.isEmpty()) {
                    BookshelfHelp.delChapterList(bookShelfBean.getNoteUrl());
                    DbHelper.getDaoSession().getBookChapterBeanDao().insertOrReplaceInTx(chapterBeans);
                }
                RxBus.get().post(RxBusTag.HAD_ADD_BOOK, bookShelf);
            }
            e.onNext(chapterBeans);
            e.onComplete();
        });
    }

    @Override
    public void addToBookShelf() {
        if (bookShelf != null) {
            Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                BookshelfHelp.saveBookToShelf(bookShelf);
                searchBook.setIsCurrentSource(true);
                inBookShelf = true;
                e.onNext(true);
                e.onComplete();
            }).compose(RxUtils::toSimpleSingle)
                    .subscribe(new MyObserver<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(Boolean value) {
                            if (value) {
                                RxBus.get().post(RxBusTag.HAD_ADD_BOOK, bookShelf);
                                mView.updateView();
                            } else {
                                mView.toast("放入书架失败!");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mView.toast("放入书架失败!");
                        }
                    });
        }
    }

    @Override
    public void removeFromBookShelf() {
        if (bookShelf != null) {
            Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                BookshelfHelp.removeFromBookShelf(bookShelf);
                searchBook.setIsCurrentSource(false);
                inBookShelf = false;
                e.onNext(true);
                e.onComplete();
            }).compose(RxUtils::toSimpleSingle)
                    .subscribe(new MyObserver<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(Boolean value) {
                            if (value) {
                                RxBus.get().post(RxBusTag.HAD_REMOVE_BOOK, bookShelf);
                                mView.updateView();
                            } else {
                                mView.toast("删除书籍失败！");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mView.toast("删除书籍失败！");
                        }
                    });
        }
    }

    /**
     * 换源
     */
    @Override
    public void changeBookSource(SearchBookBean searchBookBean) {
        searchBookBean.setName(bookShelf.getBookInfoBean().getName());
        searchBookBean.setAuthor(bookShelf.getBookInfoBean().getAuthor());
        ChangeSourceHelp.changeBookSource(searchBookBean, bookShelf)
                .subscribe(new MyObserver<TwoDataBean<BookShelfBean, List<BookChapterBean>>>() {
                    @Override
                    public void onNext(TwoDataBean<BookShelfBean, List<BookChapterBean>> value) {
                        RxBus.get().post(RxBusTag.HAD_REMOVE_BOOK, bookShelf);
                        RxBus.get().post(RxBusTag.HAD_ADD_BOOK, value);
                        bookShelf = value.getData1();
                        chapterBeanList = value.getData2();
                        mView.updateView();
                        String tag = bookShelf.getTag();
                        try {
                            long currentTime = System.currentTimeMillis();
                            String bookName = bookShelf.getBookInfoBean().getName();
                            BookSourceBean bookSourceBean = BookSourceManager.getBookSourceByUrl(tag);
                            if (SavedSource.Instance.getBookSource() != null && currentTime - SavedSource.Instance.getSaveTime() < 60000 && SavedSource.Instance.getBookName().equals(bookName))
                                SavedSource.Instance.getBookSource().increaseWeight(-450);
                            BookSourceManager.saveBookSource(SavedSource.Instance.getBookSource());
                            SavedSource.Instance.setBookName(bookName);
                            SavedSource.Instance.setSaveTime(currentTime);
                            SavedSource.Instance.setBookSource(bookSourceBean);
                            assert bookSourceBean != null;
                            bookSourceBean.increaseWeightBySelection();
                            BookSourceManager.saveBookSource(bookSourceBean);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.updateView();
                        mView.toast(e.getMessage());
                    }
                });
    }

    @Override
    public void attachView(@NonNull IView iView) {
        super.attachView(iView);
        RxBus.get().register(this);
    }

    @Override
    public void detachView() {
        RxBus.get().unregister(this);
        compositeDisposable.dispose();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(RxBusTag.HAD_ADD_BOOK), @Tag(RxBusTag.UPDATE_BOOK_PROGRESS)})
    public void hadAddOrRemoveBook(BookShelfBean bookShelfBean) {
        bookShelf = bookShelfBean;
        mView.updateView();
    }
}
