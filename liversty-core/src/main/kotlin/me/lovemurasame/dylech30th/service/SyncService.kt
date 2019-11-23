package me.lovemurasame.dylech30th.service

import kotlinx.coroutines.CoroutineScope

abstract class SyncService : CoroutineScope {
    protected abstract val fetchService: IFetchService<*>

    abstract suspend fun pull()

    protected abstract suspend fun updateRequired(entities: Any): Any
}