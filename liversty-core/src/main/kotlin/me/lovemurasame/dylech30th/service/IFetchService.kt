package me.lovemurasame.dylech30th.service

import kotlinx.coroutines.CoroutineScope

interface IFetchService<T> : CoroutineScope {
    suspend fun fetch(key: Any): T
}