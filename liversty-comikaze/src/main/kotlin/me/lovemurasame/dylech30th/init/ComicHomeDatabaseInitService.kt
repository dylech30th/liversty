package me.lovemurasame.dylech30th.init

import me.lovemurasame.dylech30th.resources.Init
import me.lovemurasame.dylech30th.resources.InitPriority
import me.lovemurasame.dylech30th.service.IInitService
import me.lovemurasame.dylech30th.Comic
import me.lovemurasame.dylech30th.ComicSubscription
import me.lovemurasame.dylech30th.ProjectDatabase
import me.lovemurasame.dylech30th.config.Env
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.coroutines.CoroutineContext

@Init(InitPriority.COMMON)
class ComicHomeDatabaseInitService(override val coroutineContext: CoroutineContext) : IInitService {
    override suspend fun doInit() {
        Env.COMIKAZE_LOGGER.log("正在初始化数据库...")
        transaction(ProjectDatabase) {
            SchemaUtils.create(Comic, ComicSubscription)
        }
        Env.COMIKAZE_LOGGER.log("数据库初始化完成")
    }
}