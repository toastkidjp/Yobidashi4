/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.web.screenshot

import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.Component
import java.awt.Container
import java.awt.Rectangle
import java.io.OutputStream
import javax.imageio.ImageIO

class ScreenshotExporterTest {

    private lateinit var subject: ScreenshotExporter

    private lateinit var fileSystem: FileSystem

    @MockK
    private lateinit var component: Component

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        fileSystem = spyk(FakeFileSystem())

        mockkStatic(ImageIO::class)
        every { fileSystem.exists(any()) } returns true
        //every { fileSystem.createDirectories(any()) } returns mockk()
        every { ImageIO.write(any(), any(), any<OutputStream>()) } returns true
        every { component.bounds } returns Rectangle(1, 1)
        every { component.parent } returns Container()
        every { component.x } returns 1
        every { component.y } returns 1

        subject = ScreenshotExporter(fileSystem)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        fileSystem.createDirectories("user/screenshot".toPath())
        clearMocks(fileSystem)

        subject.invoke(component)

        verify { ImageIO.write(any(), any(), any<OutputStream>()) }
    }

    @Test
    fun makeDirectoriesIfNeed() {
        every { fileSystem.exists(any()) } returns false
        every { component.x } returns 0

        subject.invoke(component)

        verify(exactly = 1) { fileSystem.createDirectories(any()) }
        verify(inverse = true) { ImageIO.write(any(), any(), any<OutputStream>()) }
    }

    @Test
    fun noopWhenComponentYIsZero() {
        every { fileSystem.exists(any()) } returns false
        every { component.y } returns 0

        subject.invoke(component)

        verify(exactly = 1) { fileSystem.createDirectories(any()) }
        verify(inverse = true) { ImageIO.write(any(), any(), any<OutputStream>()) }
    }

}