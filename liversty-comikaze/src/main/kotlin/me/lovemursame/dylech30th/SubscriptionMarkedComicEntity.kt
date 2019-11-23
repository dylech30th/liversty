package me.lovemursame.dylech30th

import me.lovemurasame.dylech30th.resources.checkAndThrow
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.lang.UnsupportedOperationException

class SubscriptionMarkedComicEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, SubscriptionMarkedComicEntity>(ComicSubscription) {
        fun new(name: String, url: String): SubscriptionMarkedComicEntity {
            checkAndThrow<EntityClass<Int, SubscriptionMarkedComicEntity>, UnsupportedOperationException>(this) { all().any { it.name == name } }
            return super.new(null) {
                this.name = name
                subscriptionUrl = url
            }
        }

        override fun new(id: Int?, init: SubscriptionMarkedComicEntity.() -> Unit): SubscriptionMarkedComicEntity {
            throw UnsupportedOperationException()
        }

        override fun new(init: SubscriptionMarkedComicEntity.() -> Unit): SubscriptionMarkedComicEntity {
            throw UnsupportedOperationException()
        }
    }

    var name by ComicSubscription.name
    var subscriptionUrl by ComicSubscription.subscribeUrl
}