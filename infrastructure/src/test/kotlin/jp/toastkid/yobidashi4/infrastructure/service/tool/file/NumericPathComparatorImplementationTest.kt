package jp.toastkid.yobidashi4.infrastructure.service.tool.file

import okio.Path.Companion.toPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.io.path.nameWithoutExtension

class NumericPathComparatorImplementationTest {

    private lateinit var subject: NumericPathComparatorImplementation

    @BeforeEach
    fun setUp() {
        subject = NumericPathComparatorImplementation()
    }

    @Test
    fun compare() {
        val path1 = "test_02".toPath().toNioPath()
        val path2 = "test_01".toPath().toNioPath()
        val path3 = "test_101".toPath().toNioPath()
        val path4 = "test_15".toPath().toNioPath()
        val path5 = "test_04".toPath().toNioPath()

        val paths = listOf(path1, path2, path3, path4, path5)
            .sortedWith(subject)
            .map { it.nameWithoutExtension }

        assertAll(
            { assertEquals("test_01", paths[0]) },
            { assertEquals("test_02", paths[1]) },
            { assertEquals("test_04", paths[2]) },
            { assertEquals("test_15", paths[3]) },
            { assertEquals("test_101", paths[4]) }
        )
    }

}