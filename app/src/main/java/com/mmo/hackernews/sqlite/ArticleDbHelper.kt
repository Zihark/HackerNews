package com.mmo.hackernews.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.mmo.hackernews.`object`.Article

class ArticleDbHelper (context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TAG = "ArticleDbHelper"

    private val SQL_CREATE_ARTICLES =
        "CREATE TABLE ${ArticlesContract.Article.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${ArticlesContract.Article.COLUMN_NAME_OBJECTID} TEXT, " +
                "${ArticlesContract.Article.COLUMN_NAME_TITLE} TEXT, " +
                "${ArticlesContract.Article.COLUMN_NAME_AUTHOR} TEXT, " +
                "${ArticlesContract.Article.COLUMN_NAME_URL} TEXT, " +
                "${ArticlesContract.Article.COLUMN_NAME_CREATED_AT_TIMESTAMP} INTEGER, "+
                "${ArticlesContract.Article.COLUMN_NAME_SWIPED_OUT} TEXT)"

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, ".onCreate called")
        db.execSQL(SQL_CREATE_ARTICLES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, ".onUpgrade called")
        db.execSQL("DROP TABLE IF EXISTS ${ArticlesContract.Article.TABLE_NAME}")
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "HackerNews.db"
    }

    //CRUD


    //gets every article with swiped_out equal to swipedOut value
    // "" indicates elements that have not been swiped out and should be loaded on the view
    // "Y" indicates elements swiped out from the view, these should be filtered out of the view
    fun getArticles(swipedOut: String) : ArrayList<Article> {
            Log.d(TAG, "fetching data from articles table")
            val listArticles = ArrayList<Article>()
            val selectQuery = "SELECT * FROM ${ArticlesContract.Article.TABLE_NAME} WHERE ${ArticlesContract.Article.COLUMN_NAME_SWIPED_OUT} = '$swipedOut'"
            val db : SQLiteDatabase = this.writableDatabase
            val cursor : Cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                do{
                    val article = Article(
                        cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_OBJECTID)),
                        cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_TITLE)),
                        cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_AUTHOR)),
                        cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_URL)),
                        cursor.getLong(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_CREATED_AT_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_SWIPED_OUT))
                    )

                    listArticles.add(article)
                } while(cursor.moveToNext())
            }
            db.close()
            return listArticles
        }

    fun addArticles(listArticles : ArrayList<Article>){
        Log.d(TAG, ".listArticles, populating table with several articles")

        for (article in listArticles) {
            val dbArticle = getArticleByObjectID(article.objectID)
            //this filters out duplicate articles from being stored
            if(dbArticle.objectID != article.objectID)
                addArticle(article)
        }
        Log.d(TAG, ".listArticles ends")
    }

    fun getArticleByObjectID(objectID: String): Article {
        Log.d(TAG, ".getArticleByObjectID begins")

        val selectQuery = "SELECT * FROM ${ArticlesContract.Article.TABLE_NAME} WHERE ${ArticlesContract.Article.COLUMN_NAME_OBJECTID} = '$objectID'"
        val db : SQLiteDatabase = this.readableDatabase
        val cursor : Cursor = db.rawQuery(selectQuery, null)
        var article = Article("", "", "", "", 0L, "")
        if(cursor.moveToFirst()){
            do{
                article = Article(
                    cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_OBJECTID)),
                    cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_TITLE)),
                    cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_AUTHOR)),
                    cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_URL)),
                    cursor.getLong(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_CREATED_AT_TIMESTAMP)),
                    cursor.getString(cursor.getColumnIndex(ArticlesContract.Article.COLUMN_NAME_SWIPED_OUT))
                )
            } while(cursor.moveToNext())
        }
        db.close()
        Log.d(TAG, "Article obtained = $article")
        return article
    }

    fun addArticle(article : Article) {
        Log.d(TAG, ".addArticle begins")
        val articleByObjectID = getArticleByObjectID(article.objectID)
        if(null != articleByObjectID && articleByObjectID.objectID == article.objectID) {
            Log.d(TAG, "article with objectID: ${articleByObjectID.objectID} is already stored in table, addArticle ends")
            return
        }

        val db : SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(ArticlesContract.Article.COLUMN_NAME_OBJECTID, article.objectID)
        values.put(ArticlesContract.Article.COLUMN_NAME_TITLE, article.title)
        values.put(ArticlesContract.Article.COLUMN_NAME_AUTHOR, article.author)
        values.put(ArticlesContract.Article.COLUMN_NAME_URL, article.url)
        values.put(ArticlesContract.Article.COLUMN_NAME_CREATED_AT_TIMESTAMP, article.createdAtTimestamp)
        values.put(ArticlesContract.Article.COLUMN_NAME_SWIPED_OUT, "")

        db.insert(ArticlesContract.Article.TABLE_NAME, null, values)
        db.close()
    }

    fun deleteArticleFromList(article: Article) : Int{
        val db : SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put(ArticlesContract.Article.COLUMN_NAME_SWIPED_OUT, "Y")
        Log.d(TAG, "Article ${article.objectID} will be marked as swiped out")
        return db.update(ArticlesContract.Article.TABLE_NAME, values, "${ArticlesContract.Article.COLUMN_NAME_OBJECTID}=?", arrayOf(article.objectID))
    }
}
