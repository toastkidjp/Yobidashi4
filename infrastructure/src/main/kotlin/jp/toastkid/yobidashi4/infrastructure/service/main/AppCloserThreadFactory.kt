package jp.toastkid.yobidashi4.infrastructure.service.main

class AppCloserThreadFactory {

    operator fun invoke() = Thread {
        AppCloserAction().invoke()
    }

}