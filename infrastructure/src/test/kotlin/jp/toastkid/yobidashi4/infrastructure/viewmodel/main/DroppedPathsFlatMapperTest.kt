/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.viewmodel.main

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import okio.FileMetadata
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Path

class DroppedPathsFlatMapperTest {

    private lateinit var subject: DroppedPathsFlatMapper

    private lateinit var fakeFileSystem: FakeFileSystem

    @BeforeEach
    fun setUp() {
        fakeFileSystem = FakeFileSystem()
        subject = DroppedPathsFlatMapper(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        val folder = makePathWithFolder("jpg")
        val paths = listOf("zip", "txt", "md", "log", "java", "kt", "py", "jpg", "webp", "png", "gif")
            .map(::makePath)
            .union(folder)
        val results = mutableListOf<okio.Path>()

        paths.flatMap(subject::invoke)
            .forEach {
                results.add(it)
            }

        assertEquals(12, results.size)
    }

    @Test
    fun throwExceptionCase() {
        fakeFileSystem = spyk(fakeFileSystem)
        every { fakeFileSystem.listRecursively(any()) } throws IOException()
        subject = DroppedPathsFlatMapper(fakeFileSystem)

        assertTrue(
            listOf("other")
                .map(::makePathWithFolder).flatMap(subject::invoke)
                .isEmpty()
        )
    }

    @Test
    fun otherFileTypeCase() {
        fakeFileSystem = spyk(fakeFileSystem)
        val fileMetadata = mockk<FileMetadata>()
        every { fakeFileSystem.metadata(any()) } returns fileMetadata
        every { fileMetadata.isRegularFile } returns false
        every { fileMetadata.isDirectory } returns false
        subject = DroppedPathsFlatMapper(fakeFileSystem)

        assertTrue(
            listOf("other")
                .map(::makePath).flatMap(subject::invoke)
                .isEmpty()
        )
    }

    private fun makePathWithFolder(extension: String): Path {
        val folder = "folder".toPath()
        fakeFileSystem.createDirectory(folder)
        val path = folder / "test.$extension".toPath()
        fakeFileSystem.write(path){}
        return folder.toNioPath()
    }

    private fun makePath(extension: String): Path {
        val path = "test.$extension".toPath()
        fakeFileSystem.write(path){}
        return path.toNioPath()
    }

}