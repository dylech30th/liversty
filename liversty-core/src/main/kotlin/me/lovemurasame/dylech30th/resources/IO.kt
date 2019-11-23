package me.lovemurasame.dylech30th.resources

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths

fun getPath(vararg path: String): String {
    return Paths.get(path[0], *path.drop(1).toTypedArray()).toAbsolutePath().toString()
}

fun localAppData(projectName: String): String {
    return getPath(System.getenv("localappdata"), projectName)
}

fun projectConfigurationPath(projectName: String): String {
    return getPath(localAppData(projectName), "conf")
}

fun exist(path: String): Boolean {
    return Files.exists(Paths.get(path))
}

fun createDirectory(path: String) {
    if (!exist(path)) {
        Files.createDirectories(Paths.get(path).toAbsolutePath())
    }
}

fun openCreate(path: String): File {
    createDirectory(path.substring(0, path.lastIndexOf("\\")))
    if (!exist(path)) {
        Files.createFile(Paths.get(path))
    }
    return File(path)
}

fun doOnExist(path: String, operation: File.() -> Unit): Boolean {
    if (!exist(path)) return false
    with(openCreate(path)) {
        return try { operation(this); true } catch (e: Exception) { false }
    }
}

fun profilePath(verb: String): String {
    return with(Runtime.getRuntime().exec("""reg query "HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\Shell Folders" /v "$verb"""")) {
        waitFor()

        use(BufferedReader(InputStreamReader(inputStream))) {
            readText().split(' ').last().trim()
        }
    }
}

fun listFiles(path: String): Array<File>? = File(path).listFiles()




