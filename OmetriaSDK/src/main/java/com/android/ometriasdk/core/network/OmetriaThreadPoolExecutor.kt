package com.android.ometriasdk.core.network

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Document thread pool
 *
 * Created by cristiandregan
 * on 21/08/2020.
 */

private const val CORE_POOL_SIZE: Int = 1
private const val MAX_POOL_SIZE = 2
private const val KEEP_ALIVE_TIME_MILLISECONDS = 0L

class OmetriaThreadPoolExecutor : ThreadPoolExecutor(
    CORE_POOL_SIZE,
    MAX_POOL_SIZE,
    KEEP_ALIVE_TIME_MILLISECONDS,
    TimeUnit.MILLISECONDS,
    LinkedBlockingQueue<Runnable>()
)