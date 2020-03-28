package com.mmo.hackernews

import com.mmo.hackernews.util.DateDisplayUtil
import com.mmo.hackernews.util.JSONUtil
import org.json.JSONObject
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testChooseNonEmptyTag() {
        val jsonString = "{'title' : 'null', 'story_title': 'backatcha'}"
        val jsonObject = JSONObject(jsonString)
        assertEquals("backatcha", JSONUtil.chooseNonEmptyTag(jsonObject, "title", "story_title"))
    }

    @Test
    fun testCompoundDuration() {

        val timeJustNow = 0L
        assertEquals("Just now", DateDisplayUtil.compoundDuration(timeJustNow))

        val twentySevenSecs = 27L
        assertEquals("27s", DateDisplayUtil.compoundDuration(twentySevenSecs))

        val threeMinutesInSecs = 190L
        assertEquals("3m", DateDisplayUtil.compoundDuration(threeMinutesInSecs))

        val hourInSecs = 3600L
        assertEquals("1h", DateDisplayUtil.compoundDuration(hourInSecs))

        val hourAndAHalfInSecs = 5400L
        assertEquals("1.5h",  DateDisplayUtil.compoundDuration(hourAndAHalfInSecs))

        val yesterdayInSecs = 86400L
        assertEquals("Yesterday",  DateDisplayUtil.compoundDuration(yesterdayInSecs))

        val twoWeeksInSecs = 1209800L
        assertEquals("2w" , DateDisplayUtil.compoundDuration(twoWeeksInSecs))
    }
}
