package jp.toastkid.yobidashi4.main.handler

import jp.toastkid.yobidashi4.infrastructure.service.main.AppCloserAction
import org.slf4j.LoggerFactory

class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        logger.error(t?.name, e)

        AppCloserAction().invoke()
    }
}