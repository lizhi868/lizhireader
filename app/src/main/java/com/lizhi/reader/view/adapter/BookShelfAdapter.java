package com.lizhi.reader.view.adapter;

import com.lizhi.reader.bean.BookShelfBean;
import com.lizhi.reader.help.ItemTouchCallback;
import com.lizhi.reader.view.adapter.base.OnItemClickListenerTwo;

import java.util.HashSet;
import java.util.List;

public interface BookShelfAdapter {

    void setArrange(boolean isArrange);

    void selectAll();

    ItemTouchCallback.OnItemTouchCallbackListener getItemTouchCallbackListener();

    List<BookShelfBean> getBooks();

    void replaceAll(List<BookShelfBean> newDataS, String bookshelfPx);

    void refreshBook(String noteUrl);

    void setItemClickListener(OnItemClickListenerTwo itemClickListener);

    HashSet<String> getSelected();

}
