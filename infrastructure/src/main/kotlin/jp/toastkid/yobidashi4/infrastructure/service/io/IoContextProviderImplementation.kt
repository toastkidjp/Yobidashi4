package jp.toastkid.yobidashi4.infrastructure.service.io

import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single

@Single
class IoContextProviderImplementation : IoContextProvider {

    override fun invoke() = Dispatchers.IO

}
