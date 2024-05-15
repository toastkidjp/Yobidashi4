package jp.toastkid.yobidashi4.presentation.main.handler

import org.slf4j.LoggerFactory

class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        logger.error(t?.name, e)
    }
}