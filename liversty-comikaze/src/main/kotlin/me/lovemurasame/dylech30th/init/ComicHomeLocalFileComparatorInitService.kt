package me.lovemurasame.dylech30th.init

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import me.lovemurasame.dylech30th.*
import me.lovemurasame.dylech30th.config.InitializationManager
import me.lovemurasame.dylech30th.resources.*
import me.lovemurasame.dylech30th.service.IInitService
import me.lovemurasame.dylech30th.config.ComicHomeConfKeys
import me.lovemurasame.dylech30th.config.Env
import me.lovemurasame.dylech30th.session.DatabaseHanding
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.coroutines.CoroutineContext

@Init(InitPriority.LOWEST)
class ComicHomeLocalFileComparatorInitService(override val coroutineContext: CoroutineContext) : IInitService {
    override suspend fun doInit() {
        val location = use(InitializationManager()) {
            open(ComicHomeConfKeys.ComicConfReg.key)
            tryGetWithEntryDefault(ComicHomeConfKeys.DefaultComicImagesLocationReg)
        } as String

        val allFolders = File(location).listFiles()

        if (allFolders == null || allFolders.isEmpty()) {
            DatabaseHanding.clear()
        } else {
            val chapters = createChapterMap(allFolders)
            sync(chapters.groupBy { it.subscribeName }.toMutableMap(), allFolders.toMutableList())
        }
    }

    private suspend fun createChapterMap(folders: Array<File>): Set<ComicHomeComicChapter> {
        val set = CopyOnWriteArraySet<ComicHomeComicChapter>()
        folders.map { folder ->
            launch(Dispatchers.IO) {
                if (!folder.isDirectory) return@launch
                folder.listFiles()!!.forEach {
                    set.add(ComicHomeComicChapter(folder.name, it.name, ComicHomeImageContents(immutableListOf())))
                }
            }
        }.joinAll()
        return set
    }

    private suspend fun sync(chapterMap: MutableMap<String, List<ComicHomeComicChapter>>, folders: MutableList<File>) {
        val tasks = CopyOnWriteArraySet<Job>()
        transaction(ProjectDatabase) {
            Env.COMIKAZE_LOGGER.log("正在将本地文件与数据库同步...")
            val iterator = chapterMap.entries.iterator()
            while (iterator.hasNext()) {
                val (subscribeName: String) = iterator.next()
                val serializedChapters = folders.firstOrNull { it.name == subscribeName }
                val marked = SubscriptionMarkedComicEntity.find { ComicSubscription.name eq subscribeName }.firstOrNull()
                val synced = SynchronizedComicEntity.find { Comic.name eq subscribeName }
                if (marked == null) {
                    iterator.remove()
                    tasks.add(launch(Dispatchers.IO) {
                        transaction(ProjectDatabase) { synced.forEach { it.delete() } }
                        folders.filter { it.name == subscribeName }.forEach { it.deleteRecursively() }
                    })
                } else if (synced.any() && serializedChapters != null) {
                    iterator.remove()
                    with(serializedChapters.listFiles()) {
                        val s = synced.firstOrNull()
                        if (this != null && s != null) {
                            val job = launch(Dispatchers.IO) {
                                val imgContents = s.imageContents.fromJson<MutableList<ComicHomeComicChapter>>()
                                if (this@with.count() > imgContents.count()) {
                                    val subtracted = map { it.name }.subtract(imgContents.map { it.chapterName })
                                    filter { subtracted.contains(it.name) }.forEach { it.deleteRecursively() }
                                }
                            }
                            tasks.add(job)
                        }
                    }
                }
            }
        }
        tasks.joinAll()
        Env.COMIKAZE_LOGGER.log("同步完成.")
    }
}