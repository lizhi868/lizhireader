package com.lizhi.reader.presenter.contract;


import com.google.android.material.snackbar.Snackbar;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.basemvplib.impl.IView;
import com.lizhi.reader.bean.ReplaceRuleBean;

import java.util.List;

public interface ReplaceRuleContract {
    interface Presenter extends IPresenter {

        void saveData(List<ReplaceRuleBean> replaceRuleBeans);

        void delData(ReplaceRuleBean replaceRuleBean);

        void delData(List<ReplaceRuleBean> replaceRuleBeans);

        void importDataSLocal(String uri);

        void importDataS(String text);
    }

    interface View extends IView {

        void refresh();

        Snackbar getSnackBar(String msg, int length);

    }

}
