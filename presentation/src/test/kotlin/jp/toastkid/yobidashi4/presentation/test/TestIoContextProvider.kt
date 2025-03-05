package jp.toastkid.yobidashi4.presentation.test

import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single

@Single
class TestIoContextProvider : IoContextProvider {

    override fun invoke() = Dispatchers.Unconfined

}