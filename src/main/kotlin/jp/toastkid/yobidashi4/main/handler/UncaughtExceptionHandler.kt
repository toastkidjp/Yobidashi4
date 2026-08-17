package jp.toastkid.yobidashi4.main.handler

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UncaughtExceptionHandler(
    private val logger: Logger = LoggerFactory.getLogger(UncaughtExceptionHandler::class.java)
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        logger.error(t?.name, e)
    }
}