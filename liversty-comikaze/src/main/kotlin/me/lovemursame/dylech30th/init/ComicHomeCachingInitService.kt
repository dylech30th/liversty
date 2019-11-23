package me.lovemursame.dylech30th.init

import me.lovemurasame.dylech30th.config.InitializationManager
import me.lovemurasame.dylech30th.resources.*
import me.lovemurasame.dylech30th.service.IInitService
import me.lovemursame.dylech30th.config.ComicHomeConfKeys
import kotlin.coroutines.CoroutineContext

@Init(InitPriority.HIGHEST)
class ComicHomeCachingInitService(override val coroutineContext: CoroutineContext) : IInitService {
    override suspend fun doInit() {
        InitializationManager.addFileMapping(ComicHomeConfKeys.ComicConfReg)
        withAll(localAppData(ComicHomeConfKeys.PROJECT_NAME), projectConfigurationPath(ComicHomeConfKeys.PROJECT_NAME), getPath(ComicHomeConfKeys.DefaultComicImagesLocationReg.value)) {
            createDirectory(this)
        }
    }
}
