package me.lovemurasame.dylech30th.session

import me.lovemurasame.dylech30th.ProjectDatabase
import me.lovemurasame.dylech30th.SynchronizedComicEntity
import me.lovemurasame.dylech30th.SubscriptionMarkedComicEntity
import me.lovemurasame.dylech30th.config.Env
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseHanding {
    fun clear() {
        transaction(ProjectDatabase) {
            SynchronizedComicEntity.all().forEach { it.delete() }
            SubscriptionMarkedComicEntity.all().forEach { it.delete() }
        }
    }
}