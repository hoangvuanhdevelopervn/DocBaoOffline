package hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.Model.Article;
import hoangvuanhdeveloper.wordpress.com.rssofflinemoneylover.R;

/**
 * Created by HoangVuAnh on 8/27/17.
 */

public class DatabaseSaveArticle extends SQLiteOpenHelper {
    private static final String DB_NAME = "ARTICLE.DB";
    private static final String DB_TABLE = "ARTICLE";
    private static final int DB_VERSION = 1;
    private static final String AR_IMAGE = "arImage";
    private static final String AR_PUB_DATE = "arPubDate";
    private static final String AR_TITLE = "arTitle";
    private static final String AR_DES = "arDescription";
    private static final String AR_LINK = "arLink";

    @SuppressLint("StaticFieldLeak")
    private static DatabaseSaveArticle databaseSaveArticle;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseSaveArticle(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }


    public static DatabaseSaveArticle getInstance(Context context) {
        if (databaseSaveArticle == null) {
            databaseSaveArticle = new DatabaseSaveArticle(context);
        }
        return databaseSaveArticle;
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists '" + DB_TABLE + "' (id integer primary key,'" + AR_IMAGE + "' VARCHAR, '" + AR_PUB_DATE + "' VARCHAR, '" + AR_TITLE + "' VARCHAR, '" + AR_DES + "' TEXT, '" + AR_LINK + "' VARCHAR)");
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertArticle(String arImage, String arPubDate, String arTitle, String arDes, String arLink) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(AR_IMAGE, arImage);
            contentValues.put(AR_PUB_DATE, arPubDate);
            contentValues.put(AR_TITLE, arTitle);
            contentValues.put(AR_DES, arDes);
            contentValues.put(AR_LINK, arLink);
            sqLiteDatabase.insert(DB_TABLE, null, contentValues);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    public boolean removeArticle(String arLink) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            String[] strArr = new String[DB_VERSION];
            strArr[0] = arLink;
            sqLiteDatabase.delete(DB_TABLE, "arLink = ?", strArr);
            return true;
        } catch (SQLException e) {
            Log.d("remove", e.toString());
            return false;
        }
    }

    public List<Article> getAllArticles() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        List<Article> articleList = new ArrayList();
        try {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from '" + DB_TABLE + "' order by id desc", null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Article article = new Article();
                    article.setArImage(cursor.getString(cursor.getColumnIndex(AR_IMAGE)));
                    article.setArPubDate(cursor.getString(cursor.getColumnIndex(AR_PUB_DATE)));
                    article.setArTitle(cursor.getString(cursor.getColumnIndex(AR_TITLE)));
                    article.setArDescription(cursor.getString(cursor.getColumnIndex(AR_DES)));
                    article.setArLink(cursor.getString(cursor.getColumnIndex(AR_LINK)));
                    articleList.add(article);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            sqLiteDatabase.close();
            return articleList;
        } catch (SQLException e) {
            Toast.makeText(context, R.string.no_data, Toast.LENGTH_SHORT).show();
            return null;
        }
    }


}
