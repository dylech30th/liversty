package me.lovemurasame.dylech30th.command

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.pair
import com.github.ajalt.clikt.parameters.options.*
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import me.lovemurasame.dylech30th.ComicHomeLocalSyncService
import me.lovemurasame.dylech30th.Injector
import me.lovemurasame.dylech30th.ProjectDatabase
import me.lovemurasame.dylech30th.SubscriptionMarkedComicEntity
import me.lovemurasame.dylech30th.config.ComicHomeConfKeys
import me.lovemurasame.dylech30th.config.Env
import me.lovemurasame.dylech30th.config.InitializationManager
import me.lovemurasame.dylech30th.init.ComicHomeLocalFileComparatorInitService
import me.lovemurasame.dylech30th.resources.use
import me.lovemurasame.dylech30th.session.DatabaseHanding
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Desktop
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

abstract class NoExitCliktCommand : CliktCommand {
    constructor() : super()
    constructor(
            help: String = "",
            epilog: String = "",
            name: String? = null,
            invokeWithoutSubcommand: Boolean = false,
            printHelpOnEmptyArgs: Boolean = false,
            helpTags: Map<String, String> = emptyMap(),
            autoCompleteEnvvar: String? = ""
    ) : super(help, epilog, name, invokeWithoutSubcommand, printHelpOnEmptyArgs, helpTags, autoCompleteEnvvar)

    fun mainNoExit(argv: List<String>) {
        try {
            parse(argv)
        } catch (e: PrintHelpMessage) {
            echo(e.command.getFormattedHelp())
        } catch (e: PrintCompletionMessage) {
            val s = if (e.forceUnixLineEndings) "\n" else context.console.lineSeparator
            echo(e.message, lineSeparator = s)
        } catch (e: PrintMessage) {
            echo(e.message)
        } catch (e: UsageError) {
            echo(e.helpMessage(), err = true)
        } catch (e: CliktError) {
            echo(e.message, err = true)
        } catch (e: Abort) {
            echo("Aborted!", err = true)
        }
    }
}

object ClearDatabase : NoExitCliktCommand(printHelpOnEmptyArgs = false, help = "清空数据库, 该操作将不可逆的清除本地数据库中的所有订阅以及同步数据") {
    override fun run() {
        if (TermUi.confirm("确定要清空数据库吗？此操作无法取消或还原") == true) {
            DatabaseHanding.clear()
            TermUi.echo("已清空数据库")
        }
    }
}

object Subscribe : NoExitCliktCommand(printHelpOnEmptyArgs = true, help = "添加漫画订阅") {
    private val comic by
    option( "-c", "--comic", help = "添加动漫之家订阅，格式为<漫画名> <漫画地址>, 漫画地址即为漫画网址，如: -c 摇曳百合 https://manhua.dmzj.com/yaoyebaihe")
            .pair().default(Pair("", "")).validate {
        if (it.first.isEmpty() || it.second.isEmpty()) {
            message("请按照形如<key> <value>的格式输入订阅名以及订阅链接")
        }
    }
    override fun run() {
        transaction(ProjectDatabase) {
            if (comic.first.isEmpty() || comic.second.isEmpty()) {
                return@transaction
            }
            SubscriptionMarkedComicEntity.new(comic.first, comic.second)
            Env.COMIKAZE_LOGGER.log("添加订阅成功!")
        }
    }
}

object Conf : NoExitCliktCommand(printHelpOnEmptyArgs = true, help = "修改/添加配置文件") {
    override fun run() = Unit
}

object Modify : NoExitCliktCommand(printHelpOnEmptyArgs = true, help = "注册/更改配置项") {
    private val reg by
    option("-r", "--registry", help = "添加或更改配置文件, 对配置文件的更改无需重启服务, 格式: <key> <value>, 如: -r downloadPath C:\\My Pictures")
            .pair().default(Pair("", "")).validate {
                if (it.first.isEmpty() || it.second.isEmpty()) {
                    message("请按照形如<key> <value>的格式输入键值对")
                }
            }
    override fun run() {
        use(InitializationManager()) {
            open(ComicHomeConfKeys.ComicConfReg.key)
            writeString(reg.first, reg.second)
        }
        Env.COMIKAZE_LOGGER.log("成功更新/添加键为${reg.first}的项")
    }
}

object Remove : NoExitCliktCommand(printHelpOnEmptyArgs = true, help = "移除配置项") {
    private val key by option("-k", "--key").default("").validate {
        if (it.isEmpty()) {
            message("请输入要移除的键")
        }
    }
    override fun run() {
        use(InitializationManager()) {
            open(ComicHomeConfKeys.ComicConfReg.key)
            remove(key)
        }
        Env.COMIKAZE_LOGGER.log("成功移除键位${key}的项")
    }
}

object Clear : NoExitCliktCommand(printHelpOnEmptyArgs = false, help = "清空配置文件") {
    override fun run() {
        use(InitializationManager()) {
            open(ComicHomeConfKeys.ComicConfReg.key)
            clear()
        }
        Env.COMIKAZE_LOGGER.log("成功清除配置文件")
    }
}

object Pull : NoExitCliktCommand(printHelpOnEmptyArgs = false, help = "手动拉取并下载订阅漫画") {
    @KtorExperimentalAPI
    @ExperimentalContracts
    override fun run() {
        runBlocking(cContext) {
            ComicHomeLocalSyncService(cContext).pull()
        }
    }
}

object Sync : NoExitCliktCommand(help = "手动同步数据库与本地漫画资源") {
    override fun run() {
        runBlocking(cContext) {
            ComicHomeLocalFileComparatorInitService(cContext).doInit()
        }
    }
}

object Comikaze : NoExitCliktCommand() {
    override fun run() = Unit
}

private lateinit var cContext: CoroutineContext

fun main() {


    runBlocking {
        cContext = coroutineContext
        Injector.beforeServiceStart(coroutineContext)

        val cmdObj = Comikaze.subcommands(ClearDatabase, Subscribe, Conf.subcommands(Modify, Remove, Clear), Sync, Pull)

        while (true) {
            val str = with(readLine()!!) {
                split(" ").drop(1)
            }
            cmdObj.mainNoExit(str)
        }
    }
}