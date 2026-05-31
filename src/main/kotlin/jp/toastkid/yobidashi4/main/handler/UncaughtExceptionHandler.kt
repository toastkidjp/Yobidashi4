package jp.toastkid.yobidashi4.main.handler

import jp.toastkid.yobidashi4.infrastructure.service.main.AppCloserAction
import org.slf4j.LoggerFactory

class UncaughtExceptionHandler : Thread.UncaughtExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        logger.error(t?.name, e)

        try {
            AppCloserAction().invoke()
        } catch (koinError: IllegalStateException) {
            // Koinが死んでいたら、最低限のログだけ出して強制終了させる
            logger.error("Koin context was lost during crash handling", koinError)
        }
    }
}