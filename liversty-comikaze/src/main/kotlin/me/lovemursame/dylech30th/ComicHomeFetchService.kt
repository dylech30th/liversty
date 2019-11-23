package me.lovemursame.dylech30th

import com.google.common.collect.ImmutableList
import kotlinx.coroutines.*
import me.lovemurasame.dylech30th.resources.*
import me.lovemurasame.dylech30th.service.IFetchService
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class ComicHomeFetchService(override val coroutineContext: CoroutineContext) : IFetchService<ImmutableList<ComicHomeComicChapter>> {
    private val prefix = "https://manhua.dmzj.com/"
    private val imgPrefix = "https://img.dmzj.com/"

    override suspend fun fetch(key: Any): ImmutableList<ComicHomeComicChapter> {
        val document = transaction(ProjectDatabase) {
            with(SubscriptionMarkedComicEntity
                    .find { ComicSubscription.name eq key as String}
                    .apply { checkAndThrow<SizedIterable<SubscriptionMarkedComicEntity>, SubscriptionNotRegisteredException>(this) { empty() } }
            ) {
                Jsoup.connect(first().subscriptionUrl).get()
            }
        }

        val chapterList = document.getElementsByClass("cartoon_online_border").flatMap { it.select("ul > li") }
        val taskList = chapterList.map { element ->
            async(Dispatchers.IO) {
                with(element.select("a[href]")) {
                    val imgContents = getComicImageContents("$prefix${attr("href")}")
                    ComicHomeComicChapter(key as String, attr("title"), imgContents)
                }
            }
        }.toImmutable()

        return ImmutableList.copyOf(taskList.awaitAll())
    }

    private fun getComicImageContents(url: String): ComicHomeImageContents {
        val pages = Jsoup.connect(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3956.0 Safari/537.36 Edg/80.0.328.4")
                .get()
                .getElementsByTag("script")[0]
                .html().split("\n")[2]
                .builder()
                .append(";pages")
                .toString().eval()
        val urls = pages.fromJson<Array<String>>().map { imgPrefix compose it }
        return ComicHomeImageContents(ImmutableList.copyOf(urls.sorted()))
    }
}

class SubscriptionNotRegisteredException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)
}
