package com.mmo.hackernews.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mmo.hackernews.`object`.Article
import com.mmo.hackernews.R
import com.mmo.hackernews.sqlite.ArticleDbHelper
import com.mmo.hackernews.util.DateDisplayUtil

class ArticleViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    var titleTextView : TextView = view.findViewById(R.id.title)
    var authorCreatedAtTextView : TextView = view.findViewById(R.id.author_created_at)
}

class ArticleRecyclerViewAdapter(private var context: Context, private var articlesList : ArrayList<Article>) : RecyclerView.Adapter<ArticleViewHolder>() {

    private val TAG = "ArtRecyclerViewAdapt"

    internal lateinit var db: ArticleDbHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        Log.d(TAG, ".onCreateViewHolder begins")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        return ArticleViewHolder(view)
    }

    fun loadNewData(newArticles : ArrayList<Article>){
        //filters out articles in the "deleted" table
        //returns newArticles
        articlesList = newArticles
        notifyDataSetChanged()
    }

    fun getArticle(index: Int) : Article? {
        return if(articlesList.isNotEmpty()) articlesList[index] else null
    }

    fun removeItemFromView(viewHolder: RecyclerView.ViewHolder) {
        removeArticle(viewHolder.adapterPosition)
    }

    fun removeArticle(index: Int) {
        val articleToBeRemoved = articlesList.get(index)

        db = ArticleDbHelper(this.context)
        articlesList.removeAt(index)
        db.deleteArticleFromList(articleToBeRemoved)

        notifyItemRemoved(index)
        notifyItemRangeChanged(index, articlesList.size)

    }

    override fun getItemCount(): Int {
        return if (articlesList.isNotEmpty()) articlesList.size else 0
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, index: Int) {

        val articleItem = articlesList[index]
        Log.d(TAG, ".onBindViewHolder: ${articleItem.title} ->  $index" )
        holder.titleTextView.text = articleItem.title

        //transform timestamp into timelapse
        val createdAtTimestamp = articleItem.createdAtTimestamp
        val timeDiffInSeconds = System.currentTimeMillis()/1000 - createdAtTimestamp
        //createdAt should be a string, for example: 39s, 1h, Yesterday, 2.5h, 2d 2w...
        val createdAt = DateDisplayUtil.compoundDuration(timeDiffInSeconds)
        //set "author - created at" 's text to the TextView
        holder.authorCreatedAtTextView.text = "${articleItem.author} - $createdAt"

    }
}