package com.mohamedrejeb.richeditor.parser.html

import com.mohamedrejeb.richeditor.model.RichSpan
import com.mohamedrejeb.richeditor.model.RichSpanStyle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.parser.utils.BoldSpanStyle
import com.mohamedrejeb.richeditor.parser.utils.H1SPanStyle
import com.mohamedrejeb.richeditor.parser.utils.H2SPanStyle
import com.mohamedrejeb.richeditor.parser.utils.H3SPanStyle
import com.mohamedrejeb.richeditor.parser.utils.H4SPanStyle
import com.mohamedrejeb.richeditor.parser.utils.H5SPanStyle
import com.mohamedrejeb.richeditor.parser.utils.H6SPanStyle
import com.mohamedrejeb.richeditor.parser.utils.ItalicSpanStyle
import com.mohamedrejeb.richeditor.parser.utils.MarkSpanStyle
import com.mohamedrejeb.richeditor.parser.utils.SmallSpanStyle
import com.mohamedrejeb.richeditor.parser.utils.StrikethroughSpanStyle
import com.mohamedrejeb.richeditor.parser.utils.SubscriptSpanStyle
import com.mohamedrejeb.richeditor.parser.utils.SuperscriptSpanStyle
import com.mohamedrejeb.richeditor.parser.utils.UnderlineSpanStyle
import com.mohamedrejeb.richeditor.utils.fastForEach
import com.mohamedrejeb.richeditor.utils.fastForEachIndexed

internal object RichTextStateBasicHtmlParser : RichTextStateHtmlParser() {

    override fun decode(richTextState: RichTextState): String {
        val builder = StringBuilder()

        var lastParagraphGroupTagName: String? = null

        richTextState.richParagraphList.fastForEachIndexed { index, richParagraph ->
            val paragraphGroupTagName = decodeHtmlElementFromRichParagraphType(richParagraph.type)

            // Close last paragraph group tag if needed
            if (
                (lastParagraphGroupTagName == "ol" || lastParagraphGroupTagName == "ul") &&
                (lastParagraphGroupTagName != paragraphGroupTagName)
            ) builder.append("</$lastParagraphGroupTagName>")

            // Open new paragraph group tag if needed
            if (
                (paragraphGroupTagName == "ol" || paragraphGroupTagName == "ul") &&
                lastParagraphGroupTagName != paragraphGroupTagName
            ) builder.append("<$paragraphGroupTagName>")

            // Create paragraph tag name
            val paragraphTagName =
                if (paragraphGroupTagName == "ol" || paragraphGroupTagName == "ul") "li"
                else "p"

            // Append paragraph opening tag
            builder.append("<$paragraphTagName>")

            // Append paragraph children
            richParagraph.children.fastForEach { richSpan ->
                builder.append(decodeRichSpanToHtml(richSpan))
            }

            // Append paragraph closing tag
            builder.append("</$paragraphTagName>")

            // Save last paragraph group tag name
            lastParagraphGroupTagName = paragraphGroupTagName

            // Close last paragraph group tag if needed
            if (
                (lastParagraphGroupTagName == "ol" || lastParagraphGroupTagName == "ul") &&
                index == richTextState.richParagraphList.lastIndex
            ) builder.append("</$lastParagraphGroupTagName>")
        }

        return builder.toString()
    }

    private fun decodeRichSpanToHtml(richSpan: RichSpan): String {
        val stringBuilder = StringBuilder()

        // Check if span is empty
        if (richSpan.isEmpty()) return ""

        // Get HTML element and attributes
        val htmlElements = decodeHtmlElementsFromRichSpan(richSpan)
        val hasOpeningTags = htmlElements.isNotEmpty()
        for (htmlElement in htmlElements) {
            val tagNames = htmlElement.first
            val tagAttributes = htmlElement.second

            // Convert attributes map to HTML string
            val tagAttributesStringBuilder = StringBuilder()
            tagAttributes.forEach { (key, value) ->
                tagAttributesStringBuilder.append(" $key=\"$value\"")
            }

            // Append HTML element with attributes
            stringBuilder.append("<$tagNames$tagAttributesStringBuilder>")
        }

        // Append text
        stringBuilder.append(richSpan.text)

        // Append children
        richSpan.children.fastForEach { child ->
            stringBuilder.append(decodeRichSpanToHtml(child))
        }

        // Append closing HTML elements
        if (hasOpeningTags) {
            for (htmlElement in htmlElements) {
                val tagNames = htmlElement.first
                stringBuilder.append("</$tagNames>")
            }
        }

        return stringBuilder.toString()
    }

    /**
     * Decodes HTML elements from [RichSpan].
     */
    private fun decodeHtmlElementsFromRichSpan(
        richSpan: RichSpan,
    ): List<Pair<String, Map<String, String>>> {
        return when (val richSpanStyle = richSpan.style) {
            is RichSpanStyle.Link -> {
                return listOf(
                    "a" to mapOf(
                        "href" to richSpanStyle.url,
                        "target" to "_blank"
                    )
                )
            }

            else -> {
                val tags = mutableListOf<String>()
                if (BoldSpanStyle.fontWeight == richSpan.spanStyle.fontWeight) {
                    tags.add("b")
                }
                if (ItalicSpanStyle.fontStyle == richSpan.spanStyle.fontStyle) {
                    tags.add("i")
                }
                if (UnderlineSpanStyle.textDecoration == richSpan.spanStyle.textDecoration) {
                    tags.add("u")
                }
                if (StrikethroughSpanStyle.textDecoration == richSpan.spanStyle.textDecoration) {
                    tags.add("s")
                }
                if (SubscriptSpanStyle.baselineShift == richSpan.spanStyle.baselineShift) {
                    tags.add("sub")
                }
                if (SuperscriptSpanStyle.baselineShift == richSpan.spanStyle.baselineShift) {
                    tags.add("sup")
                }
                if (MarkSpanStyle.background == richSpan.spanStyle.background) {
                    tags.add("mark")
                }
                if (SmallSpanStyle.fontSize == richSpan.spanStyle.fontSize) {
                    tags.add("small")
                }
                if (H1SPanStyle.fontSize == richSpan.spanStyle.fontSize && H1SPanStyle.fontWeight == richSpan.spanStyle.fontWeight) {
                    tags.add("h1")
                }
                if (H2SPanStyle.fontSize == richSpan.spanStyle.fontSize && H2SPanStyle.fontWeight == richSpan.spanStyle.fontWeight) {
                    tags.add("h2")
                }
                if (H3SPanStyle.fontSize == richSpan.spanStyle.fontSize && H3SPanStyle.fontWeight == richSpan.spanStyle.fontWeight) {
                    tags.add("h3")
                }
                if (H4SPanStyle.fontSize == richSpan.spanStyle.fontSize && H4SPanStyle.fontWeight == richSpan.spanStyle.fontWeight) {
                    tags.add("h4")
                }
                if (H5SPanStyle.fontSize == richSpan.spanStyle.fontSize && H5SPanStyle.fontWeight == richSpan.spanStyle.fontWeight) {
                    tags.add("h5")
                }
                if (H6SPanStyle.fontSize == richSpan.spanStyle.fontSize && H6SPanStyle.fontWeight == richSpan.spanStyle.fontWeight) {
                    tags.add("h6")
                }
                return tags.map { it to emptyMap() }
            }
        }
    }

}