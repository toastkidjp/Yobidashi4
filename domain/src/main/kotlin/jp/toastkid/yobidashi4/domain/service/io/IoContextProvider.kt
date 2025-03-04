package jp.toastkid.yobidashi4.domain.service.io

import kotlinx.coroutines.CoroutineDispatcher

interface IoContextProvider {

    operator fun invoke(): CoroutineDispatcher

}