package com.mmo.hackernews

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mmo.hackernews.`object`.Article
import com.mmo.hackernews.activity.ArticleWebViewActivity
import com.mmo.hackernews.adapter.ArticleRecyclerViewAdapter
import com.mmo.hackernews.async.DownloadStatus
import com.mmo.hackernews.async.GetJSONData
import com.mmo.hackernews.async.GetRawData
import com.mmo.hackernews.listener.RecyclerItemClickListener
import com.mmo.hackernews.sqlite.ArticleDbHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), GetRawData.OnDownloadComplete, GetJSONData.OnDataAvailable,
    RecyclerItemClickListener.OnRecyclerClickListener{

    private val TAG = "MainActivity"

    private val ARTICLE_URL = "URL"

    private val articleRecyclerViewAdapter =
        ArticleRecyclerViewAdapter(this, ArrayList())

    private lateinit var db: ArticleDbHelper

    private lateinit var mHandler : Handler
    private lateinit var mRunnable : Runnable

    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF0000"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recycler_view.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        recycler_view.addOnItemTouchListener(RecyclerItemClickListener(this, recycler_view, this))
        recycler_view.adapter = articleRecyclerViewAdapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //swipe right to delete functionality
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                articleRecyclerViewAdapter.removeItemFromView(viewHolder)
            }

            //draws red rectangle while swiping
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if(dX < 0) {
                    swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                }

                swipeBackground.draw(c)

                c.save()

                if(dX < 0)
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)

        Log.d(TAG,"onCreate called")

        db = ArticleDbHelper(this)

        val isConnectedToInternet = verifyAvailableNetwork(this)

        //gets data from internet or SQLite table
        getArticleData(isConnectedToInternet)

        //set swipe refresh listener
        mHandler = Handler()

        swipe_refresh.setOnRefreshListener {

            mRunnable = Runnable {
                getArticleData(isConnectedToInternet)
                swipe_refresh.isRefreshing = false
            }

            mHandler.postDelayed(mRunnable, 1000)
        }

        Log.d(TAG, "onCreate done!")
    }

    //checks internet connection
    // - if disconnected, the app loads data from SQLite
    // - if connected, it loads the data from the given API
    private fun getArticleData(
        isConnectedToInternet: Boolean
    ) {
        val getRawData = GetRawData(this)
        if (isConnectedToInternet)
            getRawData.execute("https://hn.algolia.com/api/v1/search_by_date?query=android")
        else {
            val toast = Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT)
            toast.show()
            val articleList = db.getArticles("")
            articleRecyclerViewAdapter.loadNewData(articleList)
        }
    }

    override fun onDownloadComplete(results: String, status: DownloadStatus) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDownloadComplete called, data is $results")
            val getJSONData = GetJSONData(this)
            getJSONData.execute(results)
        } else {
            Log.d(TAG, "onDownloadComplete failed with status $status, error message: $results")
        }
    }

    override fun onDataAvailable(articleList: ArrayList<Article>) {
        Log.d(TAG, ".onDataAvailable called")
        //remove deleted data
        var articlesList = filterSwipedOutArticles(articleList)
        db.addArticles(articlesList)
        articleRecyclerViewAdapter.loadNewData(articlesList)
        Log.d(TAG, ".onDataAvailable ends")
    }

    override fun onError(exception: Exception) {
        Log.d(TAG, "onError called, ${exception.message}")
    }

    //filters out items that have been removed from the feed via swipe
    private fun filterSwipedOutArticles(articleList: ArrayList<Article>) : ArrayList<Article> {
        Log.d(TAG, ".removeDeletedArticlesFromAPIResult begins")

        var articles = ArrayList<Article>()

        val deletedArticles = db.getArticles("Y")
        Log.d(TAG, "Retrieved ${deletedArticles.size} deleted articles")
        if(deletedArticles.isEmpty())
            return articleList

        for(article in articleList) {
            val dbArticle = db.getArticleByObjectID(article.objectID)
            if(!"Y".equals(dbArticle.swipedOut)) {
                Log.d(TAG, "article should be added to articlesList")
                articles.add(article)
            }

        }
        return articles
    }

    //checks if the device is connected to the internet
    private fun verifyAvailableNetwork(activity: AppCompatActivity) : Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG, ".onItemClick starts")

        //launch webView
        val article = articleRecyclerViewAdapter.getArticle(position)
        val intent = Intent(this, ArticleWebViewActivity::class.java)
        if (article != null) {
            intent.putExtra(ARTICLE_URL, article.url)
        }
        startActivity(intent)

    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG, ".onItemLongClick starts")
    }


}
