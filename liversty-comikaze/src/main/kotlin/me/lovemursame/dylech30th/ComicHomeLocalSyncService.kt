package me.lovemursame.dylech30th

import com.google.common.collect.ImmutableList
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.*
import me.lovemurasame.dylech30th.config.InitializationManager
import me.lovemurasame.dylech30th.resources.*
import me.lovemurasame.dylech30th.service.IFetchService
import me.lovemurasame.dylech30th.service.SyncService
import me.lovemursame.dylech30th.config.ComicHomeConfKeys
import me.lovemursame.dylech30th.config.Env
import me.lovemursame.dylech30th.resources.saveUrlContent
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.CoroutineContext

private typealias UnaryPair<T> = Pair<T, T>
private typealias FixedChapters = List<ComicHomeComicChapter>
private typealias MutableChapters = MutableList<ComicHomeComicChapter>
private typealias ContentsMap = ConcurrentHashMap<String, UnaryPair<FixedChapters>>
private typealias TaskList<T> = CopyOnWriteArraySet<T>

class ComicHomeLocalSyncService(override val coroutineContext: CoroutineContext) : SyncService() {
    override val fetchService: IFetchService<ImmutableList<ComicHomeComicChapter>> = ComicHomeFetchService(coroutineContext)

    @KtorExperimentalAPI
    @ExperimentalContracts
    override suspend fun pull() {
        val fetchRequires = transaction(ProjectDatabase) {
            SubscriptionMarkedComicEntity.all().distinctBy { it.name }
        }
        val updateRequired = updateRequired(fetchRequires.map { async(Dispatchers.IO) { fetchService.fetch(it.name) } }.awaitAll().flatten())
        for ((name, old, new) in updateRequired) {
            incrementInternal(name, new.subtract(old))
        }
    }

    @KtorExperimentalAPI
    private suspend fun incrementInternal(name: String, c: Set<ComicHomeComicChapter>) {
        val success = downloadChapter(c)
        transaction(ProjectDatabase) {
            with(SynchronizedComicEntity.find { Comic.name eq name }.firstOrNull()) {
                if (this == null) SynchronizedComicEntity.new(name, success.toJson())
                else {
                    imageContents = imageContents.fromJson<MutableChapters>()
                            .apply { addAll(success) }
                            .toJson()
                }
            }
        }
    }

    override suspend fun updateRequired(entities: Any): ContentsMap {
        @Suppress("UNCHECKED_CAST") val fetchMap = (entities as FixedChapters).groupBy { it.subscribeName }
        val comparerMap = ContentsMap()

        val taskList = TaskList<Job>()
        for ((subscribeName, newContents) in fetchMap) {
            val job = comparerMap.addFilterRequiredContents(subscribeName, newContents)
            taskList.add(job)
        }
        taskList.joinAll()
        return comparerMap
    }
    private fun ContentsMap.addFilterRequiredContents(subscribeName: String, newContents: FixedChapters): Job = launch(Dispatchers.IO) {
        transaction(ProjectDatabase) {
            with(SynchronizedComicEntity.find { Comic.name eq subscribeName }.firstOrNull()) {
                if (this == null) this@addFilterRequiredContents[subscribeName] = immutableListOf<ComicHomeComicChapter>() to newContents
                else {
                    val oldContents = imageContents.fromJson<FixedChapters>()
                    if (!oldContents.sizeEquals(newContents)) {
                        this@addFilterRequiredContents[subscribeName] = oldContents to newContents
                    }
                }
            }
        }
    }

    private val downloadPath
        get() = use(InitializationManager()) {
                    open(ComicHomeConfKeys.ComicConfReg.key)
                    tryGetWithEntryDefault(ComicHomeConfKeys.DefaultComicImagesLocationReg)
                } as String


    @KtorExperimentalAPI
    private suspend fun downloadChapter(c: Set<ComicHomeComicChapter>): Set<ComicHomeComicChapter> {
        var chapterCounter = 0
        val set = CopyOnWriteArraySet<ComicHomeComicChapter>()
        for ((subscribeName, chapterName, imageContents) in c) {
            val urls = imageContents.imageUrls
            val path = getPath(downloadPath, subscribeName, chapterName)
            with(ProgressIndicator()) {
                urls.mapIndexed { index, s -> launch(Dispatchers.IO) {
                    try {
                        saveUrlContent(s, getPath(path, "$index.jpg"))
                        updateProgressString(urls.size.toLong(), buildProgress(chapterName))
                    } catch (e: Exception) { Env.COMIKAZE_LOGGER.logPrefixed("\n", "在下载章节 $chapterName 时出现异常: ${e.message}") }
                } }.joinAll()
                set.add(ComicHomeComicChapter(subscribeName, chapterName, imageContents))
            }
            Env.COMIKAZE_LOGGER.logPrefixed("\n", "章节 $chapterName 下载完成, 下载路径位于: $path , 这是所有任务的第 ${++chapterCounter}/${c.size} 项")
        }
        return set
    }
    private fun buildProgress(chapterName: String): String {
        return "Comikize Downloader正在下载$chapterName: "
    }
}