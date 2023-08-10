package com.mohamedrejeb.richeditor.parser.html

import com.mohamedrejeb.richeditor.model.RichSpan
import com.mohamedrejeb.richeditor.model.RichSpanStyle
import com.mohamedrejeb.richeditor.model.RichTextState
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
        val spanHtml = decodeHtmlElementFromRichSpan(richSpan)
        val tagName = spanHtml.first
        val tagAttributes = spanHtml.second

        // Convert attributes map to HTML string
        val tagAttributesStringBuilder = StringBuilder()
        tagAttributes.forEach { (key, value) ->
            tagAttributesStringBuilder.append(" $key=\"$value\"")
        }

        val isRequireOpeningTag = tagName.isNotEmpty() || tagAttributes.isNotEmpty()

        // Append HTML element with attributes
        if (isRequireOpeningTag) {
            stringBuilder.append("<$tagName$tagAttributesStringBuilder>")
        }

        // Append text
        stringBuilder.append(richSpan.text)

        // Append children
        richSpan.children.fastForEach { child ->
            stringBuilder.append(decodeRichSpanToHtml(child))
        }

        // Append closing HTML element
        if (isRequireOpeningTag) {
            stringBuilder.append("</$tagName>")
        }

        return stringBuilder.toString()
    }

    /**
     * Decodes HTML elements from [RichSpan].
     */
    private fun decodeHtmlElementFromRichSpan(
        richSpan: RichSpan,
    ): Pair<String, Map<String, String>> {
        return when (val richSpanStyle = richSpan.style) {
            is RichSpanStyle.Link -> {
                return "a" to mapOf(
                    "href" to richSpanStyle.url,
                    "target" to "_blank"
                )
            }
            else -> htmlElementsSpanStyleDecodeMap[richSpan.spanStyle].orEmpty() to emptyMap()
        }
    }

}