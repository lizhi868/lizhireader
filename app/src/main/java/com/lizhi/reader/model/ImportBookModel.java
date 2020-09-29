
package com.lizhi.reader.model;

import com.lizhi.reader.DbHelper;
import com.lizhi.reader.R;
import com.lizhi.reader.base.BaseModelImpl;
import com.lizhi.reader.bean.BookInfoBean;
import com.lizhi.reader.bean.BookShelfBean;
import com.lizhi.reader.bean.LocBookShelfBean;
import com.lizhi.reader.help.BookshelfHelp;

import java.io.File;

import io.reactivex.Observable;

import static com.lizhi.reader.utils.StringUtils.getString;

public class ImportBookModel extends BaseModelImpl {

    public static ImportBookModel getInstance() {
        return new ImportBookModel();
    }

    public Observable<LocBookShelfBean> importBook(final File file) {
        return Observable.create(e -> {
            //判断文件是否存在

            boolean isNew = false;

            BookShelfBean bookShelfBean = BookshelfHelp.getBook(file.getAbsolutePath());
            if (bookShelfBean == null) {
                isNew = true;
                bookShelfBean = new BookShelfBean();
                bookShelfBean.setHasUpdate(true);
                bookShelfBean.setFinalDate(System.currentTimeMillis());
                bookShelfBean.setDurChapter(0);
                bookShelfBean.setDurChapterPage(0);
                bookShelfBean.setGroup(3);
                bookShelfBean.setTag(BookShelfBean.LOCAL_TAG);
                bookShelfBean.setNoteUrl(file.getAbsolutePath());
                bookShelfBean.setAllowUpdate(false);

                BookInfoBean bookInfoBean = bookShelfBean.getBookInfoBean();
                String fileName = file.getName();
                int lastDotIndex = file.getName().lastIndexOf(".");
                if (lastDotIndex > 0)
                    fileName = fileName.substring(0, lastDotIndex);
                int authorIndex = fileName.indexOf("作者");
                if (authorIndex != -1) {
                    bookInfoBean.setAuthor(fileName.substring(authorIndex));
                    fileName = fileName.substring(0, authorIndex).trim();
                } else {
                    bookInfoBean.setAuthor("");
                }
                int smhStart = fileName.indexOf("《");
                int smhEnd = fileName.indexOf("》");
                if (smhStart != -1 && smhEnd != -1) {
                    bookInfoBean.setName(fileName.substring(smhStart + 1, smhEnd));
                } else {
                    bookInfoBean.setName(fileName);
                }
                bookInfoBean.setFinalRefreshData(file.lastModified());
                bookInfoBean.setCoverUrl("");
                bookInfoBean.setNoteUrl(file.getAbsolutePath());
                bookInfoBean.setTag(BookShelfBean.LOCAL_TAG);
                bookInfoBean.setOrigin(getString(R.string.local));

                DbHelper.getDaoSession().getBookInfoBeanDao().insertOrReplace(bookInfoBean);
                DbHelper.getDaoSession().getBookShelfBeanDao().insertOrReplace(bookShelfBean);
            }
            e.onNext(new LocBookShelfBean(isNew, bookShelfBean));
            e.onComplete();
        });
    }

}
