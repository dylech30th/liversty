package me.lovemurasame.dylech30th

import me.lovemurasame.dylech30th.resources.checkAndThrow
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import java.lang.UnsupportedOperationException

class SynchronizedComicEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, SynchronizedComicEntity>(Comic) {
        fun new(name: String, imageContents: String): SynchronizedComicEntity {
            checkAndThrow<EntityClass<Int, SynchronizedComicEntity>, UnsupportedOperationException>(this) { all().any { it.name == name } }
            return super.new(null) {
                this.name = name
                this.imageContents = imageContents
            }
        }

        override fun new(id: Int?, init: SynchronizedComicEntity.() -> Unit): SynchronizedComicEntity {
            throw UnsupportedOperationException()
        }

        override fun new(init: SynchronizedComicEntity.() -> Unit): SynchronizedComicEntity {
            throw UnsupportedOperationException()
        }
    }

    var name by Comic.name
    var imageContents by Comic.imageContents
}