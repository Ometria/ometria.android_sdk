package com.android.ometriasdk.core.network

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * An implementation of ThreadPoolExecutor to handle thread creation.
 * @see ThreadPoolExecutor
 */

private const val CORE_POOL_SIZE = 1
private const val MAX_POOL_SIZE = 2
private const val KEEP_ALIVE_TIME_MILLISECONDS = 0L

/**
 * @property CORE_POOL_SIZE The number of threads to keep in the pool
 * @property MAX_POOL_SIZE The maximum number of threads to allow in the pool
 * @property KEEP_ALIVE_TIME_MILLISECONDS When the number of threads is greater than the core,
 *           this is the maximum time that excess idle threads will wait for new tasks
 *           before terminating.
 * @property TimeUnit.MILLISECONDS the time unit for the {@code KEEP_ALIVE_TIME_MILLISECONDS} argument
 * @property LinkedBlockingQueue<Runnable>() the queue to use for holding tasks before they are
 *        executed.  This queue will hold only the {@code Runnable}
 *        tasks submitted by the {@code execute} method.
 */
class OmetriaThreadPoolExecutor : ThreadPoolExecutor(
    CORE_POOL_SIZE,
    MAX_POOL_SIZE,
    KEEP_ALIVE_TIME_MILLISECONDS,
    TimeUnit.MILLISECONDS,
    LinkedBlockingQueue<Runnable>()
)