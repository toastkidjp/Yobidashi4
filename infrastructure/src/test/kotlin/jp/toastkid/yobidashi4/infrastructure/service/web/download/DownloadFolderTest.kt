/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web.download

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.download.DownloadFolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name

class DownloadFolderTest {

    private lateinit var subject: DownloadFolder

    @BeforeEach
    fun setUp() {
        mockkStatic(Files::class)

        subject = DownloadFolder()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun noopMakeIfNotNeed() {
        every { Files.exists(any()) } returns true
        every { Files.createDirectories(any()) } returns mockk()

        subject.makeIfNeed()

        verify { Files.exists(any()) }
        verify(inverse = true) { Files.createDirectories(any()) }
    }

    @Test
    fun makeIfNeed() {
        every { Files.exists(any()) } returns false
        every { Files.createDirectories(any()) } returns mockk()

        subject.makeIfNeed()

        verify { Files.exists(any()) }
        verify { Files.createDirectories(any()) }
    }

    @Test
    fun assignAbsolutePath() {
        assertNull(subject.assignAbsolutePath(null))
        val assignAbsolutePath = subject.assignAbsolutePath("test")
        assertTrue(assignAbsolutePath?.endsWith("test_000") == true)
        assertTrue(subject.assignAbsolutePath("test.jpg")?.endsWith("test_000.jpg") ?: false)
    }

    @Test
    fun assignQuickStorePath() {
        assertTrue(subject.assignQuickStorePath("test.png").name.endsWith("_000.png"))
        assertTrue(subject.assignQuickStorePath("test.jpg").name.endsWith("_000.jpg"))
        assertTrue(subject.assignQuickStorePath("test").name.endsWith("_000.png"))
    }

    @ParameterizedTest
    @CsvSource(value = [
        "test.png,_001.png",
        "https://test-img.jp/images/v3/FUTqMOrVt9rHy.jpg?errorImage=false,_000.jpg",
        "https://test-img.jp/images/v3/FUTqMOrVt9rHy.jpg?errorImage=false&w=100&h=110,_000.jpg",
        "https://test-img.jp/images/v3/FUTqMOrVt9rHy.jpg&w=100&h=110\",_000.jpg",
    ])
    fun assignQuickStorePathWithExists(
        parameter: String,
        expected: String
    ) {
        every { Files.exists(any()) } answers {
            val path = this.args.get(0) as? Path ?: return@answers false
            return@answers path.name.endsWith("_000.png")
        }

        assertTrue(subject.assignQuickStorePath(parameter).name.endsWith(expected))
    }

    @Test
    fun getPath() {
        assertSame(subject.getPath(), subject.getPath())
    }

}