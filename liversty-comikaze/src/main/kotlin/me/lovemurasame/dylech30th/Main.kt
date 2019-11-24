package me.lovemurasame.dylech30th

import me.lovemurasame.dylech30th.config.Env
import me.lovemurasame.dylech30th.service.IInitService
import me.lovemurasame.dylech30th.init.ComicHomeCachingInitService
import me.lovemurasame.dylech30th.init.ComicHomeDatabaseInitService
import me.lovemurasame.dylech30th.init.ComicHomeLocalFileComparatorInitService
import me.lovemurasame.dylech30th.resources.Init
import kotlin.coroutines.CoroutineContext

internal object Injector {
    private val initList = mutableListOf<Class<*>>(
            ComicHomeCachingInitService::class.java,
            ComicHomeDatabaseInitService::class.java,
            ComicHomeLocalFileComparatorInitService::class.java
    ).sortedBy { it.getAnnotation(Init::class.java).priority }

    suspend fun beforeServiceStart(coroutineContext: CoroutineContext) {
        println("Comikaze: 正在初始化服务")
        for (clazz in initList) {
            (clazz.getConstructor(CoroutineContext::class.java).newInstance(coroutineContext) as IInitService).doInit()
        }
        Env.COMIKAZE_LOGGER.log("服务初始化完成! 您可以使用了")
    }
}