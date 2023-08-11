package com.mohamedrejeb.richeditor.model

/**
 * A link in the [RichTextState].
 *
 * @see [RichTextState.addLink]
 * @see [RichTextState.getLink]
 */
data class Link(
    val text: String,
    val url: String,
)
