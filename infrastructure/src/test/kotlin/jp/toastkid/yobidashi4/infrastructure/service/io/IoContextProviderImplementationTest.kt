package jp.toastkid.yobidashi4.infrastructure.service.io

import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class IoContextProviderImplementationTest {

    @Test
    fun invoke() {
        assertSame(Dispatchers.IO, IoContextProviderImplementation().invoke())
    }

}