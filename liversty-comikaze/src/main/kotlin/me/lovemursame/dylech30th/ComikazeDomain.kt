package me.lovemursame.dylech30th

import me.lovemurasame.dylech30th.resources.getPath
import me.lovemurasame.dylech30th.resources.localAppData
import me.lovemursame.dylech30th.config.ComicHomeConfKeys
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

val ProjectDatabase: Database by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    Database.connect("jdbc:sqlite:${getPath(localAppData(ComicHomeConfKeys.PROJECT_NAME), "comikaze.db")}", driver = "org.sqlite.JDBC").apply {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}