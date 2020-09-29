package com.lizhi.reader.presenter.contract;

import android.content.Intent;

import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.basemvplib.impl.IView;
import com.lizhi.reader.bean.BookChapterBean;
import com.lizhi.reader.bean.BookShelfBean;
import com.lizhi.reader.bean.SearchBookBean;

import java.util.List;

public interface BookDetailContract {
    interface Presenter extends IPresenter {
        void initData(Intent intent);

        int getOpenFrom();

        SearchBookBean getSearchBook();

        BookShelfBean getBookShelf();

        List<BookChapterBean> getChapterList();

        Boolean getInBookShelf();

        void initBookFormSearch(SearchBookBean searchBookBean);

        void getBookShelfInfo();

        void addToBookShelf();

        void removeFromBookShelf();

        void changeBookSource(SearchBookBean searchBookBean);
    }

    interface View extends IView {
        /**
         * 更新书籍详情UI
         */
        void updateView();

        /**
         * 数据获取失败
         */
        void getBookShelfError();

        void finish();

        void toast(String msg);
    }
}
