package me.lovemursame.dylech30th

import org.jetbrains.exposed.dao.IntIdTable

object Comic : IntIdTable() {
    val name = varchar("comic_name", 50)
    val imageContents = text("img_urls")
}