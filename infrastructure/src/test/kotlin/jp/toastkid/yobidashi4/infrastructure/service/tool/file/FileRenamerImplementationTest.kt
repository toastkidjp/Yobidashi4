/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.tool.file

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.service.io.IoContextProvider
import jp.toastkid.yobidashi4.domain.service.tool.file.FileRenamer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.nio.file.Path
import java.text.DecimalFormat
import kotlin.io.path.extension

class FileRenamerImplementationTest {

    private lateinit var subject: FileRenamer

    private lateinit var fileSystem: FakeFileSystem

    @MockK
    private lateinit var ioContextProvider: IoContextProvider

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier=null) { ioContextProvider } bind(IoContextProvider::class)
                }
            )
        }

        fileSystem = spyk(FakeFileSystem())
        subject = FileRenamerImplementation(fileSystem)

        every { fileSystem.copy(any(), any()) } returns mockk()
        every { ioContextProvider.invoke() } returns Dispatchers.Unconfined
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun emptyCase() {
        runBlocking {
            subject.invoke(emptyList(), "test", false, System::lineSeparator)

            verify(inverse = true) { fileSystem.copy(any(), any()) }
        }
    }

    @Test
    fun invoke() {
        val value = mockk<Path>()
        every { value.resolveSibling(any<String>()) } returns mockk()
        every { value.extension } returns "png"
        every { value.parent } returns value

        val path = "folder/present.jpg".toPath()
        fileSystem.createDirectories("folder".toPath())
        fileSystem.write(path) {}

        runBlocking {
            subject.invoke(listOf(path.toNioPath()), "test", false, System::lineSeparator)

            verify { fileSystem.copy(any(), any()) }
        }
    }

    @Test
    fun makeRenamedFileName() {
        assertEquals(
            "test_11.webp",
            subject.makeRenamedFileName(
                DecimalFormat("0".repeat(10.toString().length)),
                "test",
                10,
                "webp"
            )
        )
    }

}