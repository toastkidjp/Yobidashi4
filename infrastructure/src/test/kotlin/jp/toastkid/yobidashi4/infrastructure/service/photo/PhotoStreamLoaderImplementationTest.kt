/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.photo

import jp.toastkid.yobidashi4.domain.service.photo.PhotoStreamLoader
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PhotoStreamLoaderImplementationTest {

    private lateinit var photoStreamLoader: PhotoStreamLoader

    private lateinit var fileSystem: FileSystem

    @BeforeEach
    fun setUp() {
        fileSystem = FakeFileSystem()
        photoStreamLoader = PhotoStreamLoaderImplementation(fileSystem)
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun invoke() {
        val path = "test.png".toPath()
        fileSystem.write(path) {}

        photoStreamLoader.invoke(path.toNioPath()).use {  }
    }

}