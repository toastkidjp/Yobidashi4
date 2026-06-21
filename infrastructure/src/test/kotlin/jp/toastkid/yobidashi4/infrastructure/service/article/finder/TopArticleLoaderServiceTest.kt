/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.setting.Setting
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

class TopArticleLoaderServiceTest {

    private lateinit var topArticleLoaderService: TopArticleLoaderServiceImplementation

    private lateinit var fakeFileSystem: FakeFileSystem

    @MockK
    private lateinit var setting: Setting

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        fakeFileSystem = FakeFileSystem()

        startKoin {
            modules(
                module {
                    single(qualifier=null) { setting } bind(Setting::class)
                }
            )
        }

        val folder = "test".toPath()

        every { setting.articleFolderPath() }.returns(folder.toNioPath())

        val path1 = "test/test.md".toPath()
        val path2 = "test/test.jar".toPath()
        val path3 = "test/test.txt".toPath()
        fakeFileSystem.createDirectory(folder)
        fakeFileSystem.write(path1) {}
        fakeFileSystem.write(path2) {}
        fakeFileSystem.write(path3) {}

        topArticleLoaderService = TopArticleLoaderServiceImplementation(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        val paths = topArticleLoaderService.invoke()

        verify { setting.articleFolderPath() }
        assertEquals(2, paths.size)
    }

}