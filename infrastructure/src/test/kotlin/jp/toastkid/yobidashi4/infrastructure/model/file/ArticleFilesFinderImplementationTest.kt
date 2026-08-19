/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.model.file

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.file.ArticleFilesFinder
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

class ArticleFilesFinderImplementationTest {

    private lateinit var subject: ArticleFilesFinder

    private lateinit var fakeFileSystem: FileSystem

    @BeforeEach
    fun setUp() {
        fakeFileSystem = FakeFileSystem()

        subject = ArticleFilesFinderImplementation(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        val folder = "data/".toPath()
        val path3 = "data/『2021-01-02』3".toPath()
        val path1 = "data/2021-01-02".toPath()
        val path2 = "data/Test".toPath()
        val path4 = "data/path4".toPath()

        val now = LocalDateTime.now()

        fakeFileSystem = spyk(fakeFileSystem)
        fakeFileSystem.createDirectories(folder)
        fakeFileSystem.write(path1) { writeUtf8("log_content") }
        fakeFileSystem.write(path2) { writeUtf8("log_content") }
        fakeFileSystem.write(path3) { writeUtf8("log_content") }
        fakeFileSystem.write(path4) { writeUtf8("log_content") }
        every { fakeFileSystem.metadata(any()) } answers {
            if (arg<Path>(0).name.endsWith("2"))
                makeFakeMetadata(now.minusDays(8))
            else if (arg<Path>(0).name.endsWith("3"))
                makeFakeMetadata(now.minusDays(9))
            else if (arg<Path>(0).name.endsWith("4")) {
                val makeFakeMetadata = makeFakeMetadata(now.minusDays(9))
                every { makeFakeMetadata.lastModifiedAtMillis } returns null
                makeFakeMetadata
            } else
                makeFakeMetadata(now)
        }

        val paths = subject.invoke(folder.toNioPath())

        assertEquals(4, paths.size)
    }

    private fun makeFakeMetadata(dateTime: LocalDateTime): FileMetadata {
        val fileMetadata2 = mockk<FileMetadata>()
        every { fileMetadata2.lastModifiedAtMillis } returns dateTime
            .toInstant(OffsetDateTime.now().offset)
            .toKotlinInstant()
            .toEpochMilliseconds()
        return fileMetadata2
    }

}