package com.mmo.hackernews.util

import android.util.Log
import org.json.JSONObject

object JSONUtil{

    private const val TAG = "JSONUtil"

    fun chooseNonEmptyTag(jsonObject: JSONObject, firstTag: String, secondTag: String) : String {
        Log.d(TAG, ".chooseNonEmptyTag called")

        val firstTagContent = jsonObject.getString(firstTag)
        val secondTagContent = jsonObject.getString(secondTag)

        Log.d(TAG, "jsonObject.$firstTag : $firstTagContent")
        Log.d(TAG, "jsonObject.$secondTag : $secondTagContent")

        if (firstTagContent.isEmpty() || "null" == firstTagContent)
            return secondTagContent
        return firstTagContent
    }
}
