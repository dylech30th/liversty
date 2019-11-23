package me.lovemursame.dylech30th.resources

import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.cio.writeChannel
import kotlinx.coroutines.io.copyAndClose
import me.lovemurasame.dylech30th.resources.createDirectory
import java.io.File

@KtorExperimentalAPI
suspend fun HttpClientCall.saveContent(path: String) {
    createDirectory(path.substring(0, path.lastIndexOf(File.separatorChar)))
    response.content.copyAndClose(File(path).writeChannel())
}

@KtorExperimentalAPI
suspend fun saveUrlContent(url: String, path: String) {
    HttpClient(CIO).call {
        url(url)
        HttpMethod.Get
    }.saveContent(path)
}
