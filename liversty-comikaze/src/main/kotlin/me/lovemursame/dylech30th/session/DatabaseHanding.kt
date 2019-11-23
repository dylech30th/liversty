package me.lovemursame.dylech30th.session

import me.lovemursame.dylech30th.ProjectDatabase
import me.lovemursame.dylech30th.SynchronizedComicEntity
import me.lovemursame.dylech30th.SubscriptionMarkedComicEntity
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseHanding {
    fun clear() {
        transaction(ProjectDatabase) {
            SynchronizedComicEntity.all().forEach(SynchronizedComicEntity::delete)
            SubscriptionMarkedComicEntity.all().forEach(SubscriptionMarkedComicEntity::delete)
        }
    }
}