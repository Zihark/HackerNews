package com.mmo.hackernews.async

import android.os.AsyncTask
import android.util.Log
import com.mmo.hackernews.`object`.Article
import com.mmo.hackernews.util.JSONUtil
import org.json.JSONException
import org.json.JSONObject

class GetJSONData(private val listener: OnDataAvailable) : AsyncTask<String, Void, ArrayList<Article>>() {

    private val TAG = "GetJSONData"

    interface OnDataAvailable {
        fun onDataAvailable(data: ArrayList<Article>)
        fun onError(exception: Exception)
    }

    override fun onPostExecute(result: ArrayList<Article>) {
        Log.d(TAG, "onPostExecute begins")
        super.onPostExecute(result)
        listener.onDataAvailable(result)
        Log.d(TAG, "onPostExecute ends")
    }

    override fun doInBackground(vararg params: String): ArrayList<Article> {
        Log.d(TAG, "doInBackground begins")

        val articlesList = ArrayList<Article>()

        try {
            val jsonResult = JSONObject(params[0])
            val hitsArray = jsonResult.getJSONArray("hits")

            for (i in 0 until hitsArray.length()) {
                val jsonArticle = hitsArray.getJSONObject(i)

                val objectID = jsonArticle.getString("objectID")
                val title = JSONUtil.chooseNonEmptyTag(jsonArticle, "title", "story_title")
                val author = jsonArticle.getString("author")
                val url = JSONUtil.chooseNonEmptyTag(jsonArticle, "url", "story_url")
                val createdAtTimestamp = jsonArticle.getLong("created_at_i")

                val article = Article(
                    objectID,
                    title,
                    author,
                    url,
                    createdAtTimestamp,
                    ""
                )

                Log.d(TAG, ".doInBackground, current article: $article")
                articlesList.add(article)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, ".doInBackground: Error processing JSON data. ${e.message}")
            cancel(true)
            listener.onError(e)
        }

        Log.d(TAG,".doInBackground ends")
        return articlesList
    }

}