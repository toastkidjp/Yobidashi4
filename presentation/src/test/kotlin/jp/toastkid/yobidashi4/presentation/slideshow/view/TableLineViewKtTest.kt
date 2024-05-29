package jp.toastkid.yobidashi4.presentation.slideshow.view

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runDesktopComposeUiTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.slideshow.data.TableLine
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TableLineViewKtTest {

    @BeforeEach
    fun setUp() {
        mockkConstructor(TableLineViewModel::class)

        every { anyConstructed<TableLineViewModel>().start(any()) } just Runs
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun tableLineView() {
        every { anyConstructed<TableLineViewModel>().tableData() } returns listOf(
            listOf("2023-03-03", "12"),
            listOf("2023-03-04", "14")
        )

        runDesktopComposeUiTest {
            setContent {
                TableLineView(
                    TableLine(
                        listOf("key", "value"),
                        listOf(
                            listOf("2023-03-03", "12"),
                            listOf("2023-03-04", "14")
                        )
                    )
                )
            }

            onNode(hasText("key"), true)
                .performMouseInput {
                    click()
                    click()
                    enter()
                    exit()
                }
            onNode(hasText("value"), true)
                .performMouseInput {
                    click()
                    click()
                    enter()
                    exit()
                }
            onNode(hasText("2023-03-03"), true)
                .performMouseInput {
                    longClick()
                    enter()
                    exit()
                }
        }
    }
}