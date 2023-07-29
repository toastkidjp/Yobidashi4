package jp.toastkid.yobidashi4.domain.service.article

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ArticleTitleGeneratorTest {

    @Test
    fun test() {
        assertEquals(
            "2020-03-24(Tue)",
            ArticleTitleGenerator().invoke(LocalDate.of(2020, 3, 24))
        )
    }

}