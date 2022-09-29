package com.github.serivesmejia.deltautils

import kotlin.concurrent.thread

object TestUtil {

    fun spawnTimeoutThread(callback: () -> Unit, timeoutSecs: Double) {
        val t = thread {
            try {
                callback()
            } catch(e: InterruptedException) {}
            Thread.currentThread().interrupt()
        }

        val millisTimeout = System.currentTimeMillis() + timeoutSecs * 1000
        while(System.currentTimeMillis() < millisTimeout && !t.isInterrupted);

        t.interrupt()
    }

}