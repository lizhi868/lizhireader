package com.lizhi.basemvplib;

import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.basemvplib.impl.IView;

import androidx.annotation.NonNull;

public abstract class BasePresenterImpl<T extends IView> implements IPresenter {
    protected T mView;

    @Override
    public void attachView(@NonNull IView iView) {
        mView = (T) iView;
    }
}
