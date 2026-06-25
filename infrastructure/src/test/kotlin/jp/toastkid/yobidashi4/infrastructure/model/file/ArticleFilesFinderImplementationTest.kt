/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.model.file

import io.mockk.unmockkAll
import jp.toastkid.yobidashi4.domain.model.file.ArticleFilesFinder
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val path3 = "data/『2021-01-02』".toPath()
        val path1 = "data/2021-01-02".toPath()
        val path2 = "data/Test".toPath()
        fakeFileSystem.createDirectories(folder)
        fakeFileSystem.write(path1) { writeUtf8("log_content") }
        fakeFileSystem.write(path2) { writeUtf8("log_content") }
        fakeFileSystem.write(path3) { writeUtf8("log_content") }

        val paths = subject.invoke(folder.toNioPath())

        assertEquals(3, paths.size)
    }

}