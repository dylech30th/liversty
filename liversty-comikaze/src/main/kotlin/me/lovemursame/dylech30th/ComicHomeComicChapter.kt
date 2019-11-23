package me.lovemursame.dylech30th

import com.google.common.collect.ImmutableList

/**
 * every instance of [ComicHomeComicChapter] represents a chapter of any comic
 * @param subscribeName the subscribe name of the comic in the database
 * @param chapterName the name of current chapter
 * @param imageContents the images of current chapter
 */
data class ComicHomeComicChapter(val subscribeName: String, val chapterName: String, val imageContents: ComicHomeImageContents)

data class ComicHomeImageContents(val imageUrls: ImmutableList<String>)