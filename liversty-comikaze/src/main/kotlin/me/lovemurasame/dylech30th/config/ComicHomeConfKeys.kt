package me.lovemurasame.dylech30th.config

import me.lovemurasame.dylech30th.resources.getPath
import me.lovemurasame.dylech30th.resources.profilePath
import me.lovemurasame.dylech30th.resources.projectConfigurationPath
import me.lovemurasame.dylech30th.resources.registryOf
import me.lovemurasame.dylech30th.StdOutputComikazeLogger

internal object ComicHomeConfKeys {
    const val PROJECT_NAME = "comikaze"

    val ComicConfReg = registryOf("comichome", getPath(projectConfigurationPath(PROJECT_NAME), "comic_home.ini"))

    val DefaultComicImagesLocationReg = registryOf("comikaze.download", getPath(profilePath("my pictures"), PROJECT_NAME))

    val DefaultLoggerReg = registryOf("comikaze.logger", StdOutputComikazeLogger::class.java.name)
}