package com.lizhi.reader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.lizhi.reader.dao.BookChapterBeanDao;
import com.lizhi.reader.dao.BookInfoBeanDao;
import com.lizhi.reader.dao.BookShelfBeanDao;
import com.lizhi.reader.dao.BookSourceBeanDao;
import com.lizhi.reader.dao.BookmarkBeanDao;
import com.lizhi.reader.dao.CookieBeanDao;
import com.lizhi.reader.dao.DaoMaster;
import com.lizhi.reader.dao.DaoSession;
import com.lizhi.reader.dao.ReplaceRuleBeanDao;
import com.lizhi.reader.dao.SearchHistoryBeanDao;
import com.lizhi.reader.dao.TxtChapterRuleBeanDao;

import org.greenrobot.greendao.database.Database;

import java.util.Locale;

public class DbHelper {
    private static DbHelper instance;
    private SQLiteDatabase db;
    private DaoSession mDaoSession;

    private DbHelper() {
        DaoOpenHelper mHelper = new DaoOpenHelper(MApplication.getInstance(), "monkebook_db", null);
        db = mHelper.getWritableDatabase();
        db.setLocale(Locale.CHINESE);
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public static DbHelper getInstance() {
        if (null == instance) {
            synchronized (DbHelper.class) {
                if (null == instance) {
                    instance = new DbHelper();
                }
            }
        }
        return instance;
    }

    public static DaoSession getDaoSession() {
        return getInstance().mDaoSession;
    }

    public static SQLiteDatabase getDb() {
        return getInstance().db;
    }

    public class DaoOpenHelper extends DaoMaster.OpenHelper {
        DaoOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            MigrationHelper.migrate(db,
                    new MigrationHelper.ReCreateAllTableListener() {
                        @Override
                        public void onCreateAllTables(Database db, boolean ifNotExists) {
                            DaoMaster.createAllTables(db, ifNotExists);
                        }

                        @Override
                        public void onDropAllTables(Database db, boolean ifExists) {
                            DaoMaster.dropAllTables(db, ifExists);
                        }
                    },
                    BookShelfBeanDao.class, BookInfoBeanDao.class, BookChapterBeanDao.class,
                    SearchHistoryBeanDao.class, BookSourceBeanDao.class,
                    ReplaceRuleBeanDao.class, BookmarkBeanDao.class, CookieBeanDao.class,
                    TxtChapterRuleBeanDao.class
            );
        }
    }

}
