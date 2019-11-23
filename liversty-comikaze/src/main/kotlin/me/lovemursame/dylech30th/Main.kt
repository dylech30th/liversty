package me.lovemursame.dylech30th
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import me.lovemurasame.dylech30th.service.IInitService
import me.lovemursame.dylech30th.init.ComicHomeCachingInitService
import me.lovemursame.dylech30th.init.ComicHomeDatabaseInitService
import me.lovemursame.dylech30th.init.ComicHomeLocalFileComparatorInitService
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.contracts.ExperimentalContracts
import kotlin.coroutines.CoroutineContext

internal object Injector {
    private val initList = mutableListOf<Class<*>>(
            ComicHomeCachingInitService::class.java,
            ComicHomeDatabaseInitService::class.java,
            ComicHomeLocalFileComparatorInitService::class.java
    )

    suspend fun beforeServiceStart(coroutineContext: CoroutineContext) {
        for (clazz in initList) {
            (clazz.getConstructor(CoroutineContext::class.java).newInstance(coroutineContext) as IInitService).doInit()
        }
    }
}

@KtorExperimentalAPI
@ExperimentalContracts
fun main() = runBlocking {
    Injector.beforeServiceStart(coroutineContext)
    try {
        transaction(ProjectDatabase) {
            SubscriptionMarkedComicEntity.new("摇曳百合", "https://manhua.dmzj.com/yaoyebaihe")
        }
    } catch (ignored: UnsupportedOperationException) {
    }

    ComicHomeLocalSyncService(coroutineContext).pull()
}