package com.lizhi.reader.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lizhi.basemvplib.BaseFragment;
import com.lizhi.basemvplib.impl.IPresenter;
import com.lizhi.basemvplib.impl.IView;
import com.lizhi.reader.MApplication;

public abstract class MBaseFragment<T extends IPresenter> extends BaseFragment<T> implements IView {
    public final SharedPreferences preferences = MApplication.getConfigPreferences();
    protected T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = initInjector();
        attachView();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(createLayoutId(), container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detachView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    /**
     * @return LayoutId
     */
    public abstract int createLayoutId();

    /**
     * P层绑定   若无则返回null;
     */
    protected abstract T initInjector();

    /**
     * P层绑定V层
     */
    private void attachView() {
        if (null != mPresenter) {
            mPresenter.attachView(this);
        }
    }

    /**
     * P层解绑V层
     */
    private void detachView() {
        if (null != mPresenter) {
            mPresenter.detachView();
        }
    }

    public void toast(String msg) {
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void toast(int id) {
        Toast.makeText(this.getActivity(), getString(id), Toast.LENGTH_SHORT).show();
    }
}
