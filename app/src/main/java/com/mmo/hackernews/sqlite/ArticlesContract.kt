package com.mmo.hackernews.sqlite

import android.provider.BaseColumns

object ArticlesContract {

    object Article : BaseColumns {
        const val TABLE_NAME = "article"
        const val COLUMN_NAME_OBJECTID = "objectID"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_AUTHOR = "author"
        const val COLUMN_NAME_URL = "url"
        const val COLUMN_NAME_CREATED_AT_TIMESTAMP = "createdAtTimestamp"
        const val COLUMN_NAME_SWIPED_OUT = "swipedOut"
    }
}