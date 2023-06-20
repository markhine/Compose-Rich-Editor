package com.mohamedrejeb.richeditor.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.TextRange
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RichSpanTest {
    private val paragraph = RichParagraph(key = 0)
    private val richSpan = RichSpan(
        key = 0,
        paragraph = paragraph,
        text = "012",
        textRange = TextRange(0, 3),
        children = mutableStateListOf(
            RichSpan(
                key = 10,
                paragraph = paragraph,
                text = "34",
                textRange = TextRange(3, 5),
            ),
            RichSpan(
                key = 11,
                paragraph = paragraph,
                text = "567",
                textRange = TextRange(5, 8),
            ),
        )
    )

    @Test
    fun testGetSpanStyleByTextIndex() {
        assertEquals(
            0,
            richSpan.getRichSpanByTextIndex(textIndex = 0).second?.key,
        )
        assertEquals(
            0,
            richSpan.getRichSpanByTextIndex(textIndex = 2).second?.key,
        )
        assertEquals(
            null,
            richSpan.getRichSpanByTextIndex(textIndex = 8).second?.key,
        )
        assertEquals(
            null,
            richSpan.getRichSpanByTextIndex(textIndex = 9).second?.key,
        )

        assertEquals(
            10,
            richSpan.getRichSpanByTextIndex(textIndex = 3).second?.key,
        )
        assertEquals(
            11,
            richSpan.getRichSpanByTextIndex(textIndex = 6).second?.key,
        )

        assertEquals(
            11,
            richSpan.getRichSpanByTextIndex(textIndex = 7).second?.key,
        )
    }

    @Test
    fun testRemoveTextRangeStart() {
        // Remove all start text
        val removeAllStartText = richSpan.removeTextRange(TextRange(0, 3))
        assertEquals(
            "",
            removeAllStartText?.text
        )
        assertEquals(
            2,
            removeAllStartText?.children?.size
        )
        assertEquals(
            "34",
            removeAllStartText?.children?.first()?.text
        )
    }

    @Test
    fun testRemoveTextRangeStartPart() {
        // Remove part of start text
        val removePartOfStartText = richSpan.removeTextRange(TextRange(1, 3))
        assertEquals(
            "0",
            removePartOfStartText?.text
        )
        assertEquals(
            2,
            removePartOfStartText?.children?.size
        )
        assertEquals(
            "34",
            removePartOfStartText?.children?.first()?.text
        )
    }

    @Test
    fun testRemoveTextRangeFirstChild() {
        // Remove first child
        val removeFirstChild = richSpan.removeTextRange(TextRange(3, 7))
        assertEquals(
            "012",
            removeFirstChild?.text
        )
        assertEquals(
            1,
            removeFirstChild?.children?.size
        )
        assertEquals(
            "7",
            removeFirstChild?.children?.first()?.text
        )
    }

    @Test
    fun testRemoveTextRangeLastChild() {
        // Remove last child
        val removeLastChild = richSpan.removeTextRange(TextRange(5, 8))
        assertEquals(
            "012",
            removeLastChild?.text
        )
        assertEquals(
            1,
            removeLastChild?.children?.size
        )
        assertEquals(
            "34",
            removeLastChild?.children?.first()?.text
        )
    }

    @Test
    fun testRemoveTextRangeTwoChildren() {
        // Remove the two children
        val removeTwoChildren = richSpan.removeTextRange(TextRange(3, 8))
        assertEquals(
            "012",
            removeTwoChildren?.text
        )
        assertEquals(
            0,
            removeTwoChildren?.children?.size
        )
    }

    @Test
    fun testRemoveTextRangeAlLText() {
        // Remove all the text
        val removeAllText = richSpan.removeTextRange(TextRange(0, 20))
        assertEquals(
            null,
            removeAllText
        )
    }

}