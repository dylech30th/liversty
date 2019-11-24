package me.lovemurasame.dylech30th.config

import me.lovemurasame.dylech30th.debugging.ILogger
import me.lovemurasame.dylech30th.resources.use

object Env {
    val COMIKAZE_LOGGER = with(
            use(InitializationManager()) {
                open(ComicHomeConfKeys.ComicConfReg.key)
                tryGetWithEntryDefault(ComicHomeConfKeys.DefaultLoggerReg)
            } as String
    ) {
        Class.forName(this).newInstance() as ILogger
    }
}