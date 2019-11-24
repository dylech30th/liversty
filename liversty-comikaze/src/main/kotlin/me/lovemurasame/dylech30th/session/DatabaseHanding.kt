package me.lovemurasame.dylech30th.session

import me.lovemurasame.dylech30th.ProjectDatabase
import me.lovemurasame.dylech30th.SynchronizedComicEntity
import me.lovemurasame.dylech30th.SubscriptionMarkedComicEntity
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseHanding {
    fun clear() {
        transaction(ProjectDatabase) {
            SynchronizedComicEntity.all().forEach(SynchronizedComicEntity::delete)
            SubscriptionMarkedComicEntity.all().forEach(SubscriptionMarkedComicEntity::delete)
        }
    }
}