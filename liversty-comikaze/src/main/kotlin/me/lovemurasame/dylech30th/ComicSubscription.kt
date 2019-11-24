package me.lovemurasame.dylech30th

import org.jetbrains.exposed.dao.IntIdTable

object ComicSubscription : IntIdTable() {
    val name = varchar("subscribed_comic_name", 100)
    val subscribeUrl = varchar("subscribe_url", 120)
}