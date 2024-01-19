package jp.toastkid.yobidashi4.domain.service.slideshow

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TableBuilderTest {

    @Test
    fun elseCase() {
        assertTrue(TableBuilder().build().header.isEmpty())
    }

}