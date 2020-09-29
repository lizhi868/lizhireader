package com.lizhi.reader.presenter.contract;

import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.basemvplib.impl.IView;

public interface MainContract {

    interface View extends IView {

        void initImmersionBar();

        /**
         * 取消弹出框
         */
        void dismissHUD();

        /**
         * 恢复数据
         */
        void onRestore(String msg);

        void recreate();

        void toast(String msg);

        void toast(int strId);

        int getGroup();

        void showSnackBar(String msg, int length);

        void refreshBookSource();
    }

    interface Presenter extends IPresenter {

        void backupData();

        void restoreData();

        void addBookUrl(String bookUrl);

        void importBookSourceLocal(String path);

        void removeAllSource();
    }

}
