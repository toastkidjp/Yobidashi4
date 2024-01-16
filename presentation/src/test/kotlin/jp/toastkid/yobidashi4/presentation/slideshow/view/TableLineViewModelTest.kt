package jp.toastkid.yobidashi4.presentation.slideshow.view

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TableLineViewModelTest {

    private lateinit var subject: TableLineViewModel

    @BeforeEach
    fun setUp() {
        subject = TableLineViewModel()
    }

    @Test
    fun start() {
        assertTrue(subject.tableData().isEmpty())

        subject.start(
            listOf(
                listOf(2, 33.2, "and"),
                listOf(1, 101.3, "test")
            )
        )

        assertEquals(2,  subject.tableData().size)

        subject.clickHeaderColumn(1)

        assertEquals(1, subject.tableData()[0][0])
        assertEquals(101.3, subject.tableData()[0][1])

        subject.clickHeaderColumn(0)
        subject.clickHeaderColumn(2)
        subject.clickHeaderColumn(1)
        subject.clickHeaderColumn(0)
        subject.clickHeaderColumn(2)
    }

}