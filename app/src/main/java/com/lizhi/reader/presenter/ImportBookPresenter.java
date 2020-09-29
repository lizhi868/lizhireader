package com.lizhi.reader.presenter;

import com.hwangjr.rxbus.RxBus;
import com.lizhi.basemvplib.BasePresenterImpl;
import com.lizhi.reader.bean.LocBookShelfBean;
import com.lizhi.reader.constant.RxBusTag;
import com.lizhi.reader.model.ImportBookModel;
import com.lizhi.reader.presenter.contract.ImportBookContract;
import com.lizhi.reader.utils.RxUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ImportBookPresenter extends BasePresenterImpl<ImportBookContract.View> implements ImportBookContract.Presenter {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void importBooks(List<File> books) {
        Observable.fromIterable(books)
                .flatMap(file -> ImportBookModel.getInstance().importBook(file))
                .compose(RxUtils::toSimpleSingle)
                .subscribe(new Observer<LocBookShelfBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(LocBookShelfBean value) {
                        if (value.getNew()) {
                            RxBus.get().post(RxBusTag.HAD_ADD_BOOK, value.getBookShelfBean());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mView.addError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        mView.addSuccess();
                    }
                });
    }


    @Override
    public void detachView() {
        compositeDisposable.dispose();
    }
}
