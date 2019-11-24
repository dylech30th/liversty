package me.lovemurasame.dylech30th.init

import me.lovemurasame.dylech30th.config.InitializationManager
import me.lovemurasame.dylech30th.resources.*
import me.lovemurasame.dylech30th.service.IInitService
import me.lovemurasame.dylech30th.config.ComicHomeConfKeys
import me.lovemurasame.dylech30th.config.Env
import kotlin.coroutines.CoroutineContext

@Init(InitPriority.HIGHEST)
class ComicHomeCachingInitService(override val coroutineContext: CoroutineContext) : IInitService {
    override suspend fun doInit() {
        InitializationManager.addFileMapping(ComicHomeConfKeys.ComicConfReg)
        Env.COMIKAZE_LOGGER.log("正在创建配置文件夹...")
        withAll(localAppData(ComicHomeConfKeys.PROJECT_NAME), projectConfigurationPath(ComicHomeConfKeys.PROJECT_NAME), getPath(ComicHomeConfKeys.DefaultComicImagesLocationReg.value)) {
            createDirectory(this)
        }
        Env.COMIKAZE_LOGGER.log("配置文件夹创建完成")
    }
}
