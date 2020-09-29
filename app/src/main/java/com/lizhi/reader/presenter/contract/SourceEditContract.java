package com.lizhi.reader.presenter.contract;

import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.basemvplib.impl.IView;
import com.lizhi.reader.bean.BookSourceBean;

import io.reactivex.Observable;

public interface SourceEditContract {
    interface Presenter extends IPresenter {

        Observable<Boolean> saveSource(BookSourceBean bookSource, BookSourceBean bookSourceOld);

        void copySource(String bookSource);

        void pasteSource();

        void setText(String bookSourceStr);
    }

    interface View extends IView {

        void setText(BookSourceBean bookSourceBean);

        String getBookSourceStr(boolean hasFind);
    }
}
