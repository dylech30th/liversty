package me.lovemurasame.dylech30th.service

import kotlinx.coroutines.CoroutineScope

interface IInitService : CoroutineScope {
    suspend fun doInit()
}