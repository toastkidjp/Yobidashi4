/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.model.file

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.file.LatestFileFinder
import kotlinx.datetime.toKotlinInstant
import okio.FileMetadata
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.OffsetDateTime

class LatestFileFinderImplementationTest {

    private lateinit var subject: LatestFileFinder

    private lateinit var fakeFileSystem: FileSystem

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        fakeFileSystem = spyk(FakeFileSystem())

        subject = LatestFileFinderImplementation(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        val path = "data/path1".toPath()
        val path2 = "data/path2".toPath()
        val path3 = "data/path3".toPath()

        val toPath = "data".toPath()
        fakeFileSystem.createDirectories(toPath)

        val now = LocalDateTime.now()

        fakeFileSystem.write(path) { writeUtf8("log_content") }
        fakeFileSystem.write(path2) { writeUtf8("log_content") }
        val fileMetadata2 = mockk<FileMetadata>()
        every { fileMetadata2.lastModifiedAtMillis } returns now.minusDays(8)
            .toInstant(OffsetDateTime.now().offset)
            .toKotlinInstant()
            .toEpochMilliseconds()

        fakeFileSystem.write(path3) { writeUtf8("log_content") }
        val fileMetadata3 = mockk<FileMetadata>()
        every { fileMetadata3.lastModifiedAtMillis } returns now.minusDays(9)
            .toInstant(OffsetDateTime.now().offset)
            .toKotlinInstant()
            .toEpochMilliseconds()
        every { fakeFileSystem.metadata(path3) } returns fileMetadata3

        val fileMetadata = mockk<FileMetadata>()
        every { fileMetadata.lastModifiedAtMillis } returns now
            .toInstant(OffsetDateTime.now().offset)
            .toKotlinInstant()
            .toEpochMilliseconds()

        every { fakeFileSystem.metadata(any()) } answers {
            if (arg<Path>(0).name.endsWith("2"))
                fileMetadata2
            else if (arg<Path>(0).name.endsWith("3"))
                fileMetadata3
            else
                fileMetadata
        }

        val paths = subject.invoke(toPath.toNioPath(), now.minusDays(7))

        assertEquals(1, paths.size)
    }

}