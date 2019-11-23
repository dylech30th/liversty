package me.lovemursame.dylech30th.init

import me.lovemurasame.dylech30th.resources.Init
import me.lovemurasame.dylech30th.resources.InitPriority
import me.lovemurasame.dylech30th.service.IInitService
import me.lovemursame.dylech30th.Comic
import me.lovemursame.dylech30th.ComicSubscription
import me.lovemursame.dylech30th.ProjectDatabase
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.CoroutineContext

@Init(InitPriority.COMMON)
class ComicHomeDatabaseInitService(override val coroutineContext: CoroutineContext) : IInitService {
    override suspend fun doInit() {
        transaction(ProjectDatabase) {
            addLogger(Slf4jSqlDebugLogger)
            SchemaUtils.create(Comic, ComicSubscription)
        }
    }
}