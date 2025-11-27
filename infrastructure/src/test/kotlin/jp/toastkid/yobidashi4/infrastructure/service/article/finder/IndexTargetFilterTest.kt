/*
 * Copyright (c) 2025 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.infrastructure.service.article.finder

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.util.stream.Stream
import kotlin.io.path.nameWithoutExtension

class IndexTargetFilterTest {

    private lateinit var subject: IndexTargetFilter

    @MockK
    private lateinit var folder: Path

    @MockK
    private lateinit var item1: Path

    @MockK
    private lateinit var item2: Path

    @MockK
    private lateinit var item3: Path

    @MockK
    private lateinit var segment1: Path

    @MockK
    private lateinit var segment2: Path

    @MockK
    private lateinit var segmentExceptionCase: Path

    @MockK
    private lateinit var notSegment: Path

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Files::class)
        every { Files.isDirectory(any()) } returns false
        every { Files.isReadable(any()) } returns true
        every { Files.list(folder) } answers { Stream.of(segment1, notSegment, segment2, segmentExceptionCase) }
        every { segment1.nameWithoutExtension } returns "segments_1"
        every { segment2.nameWithoutExtension } returns "segments_2"
        every { notSegment.nameWithoutExtension } returns "test"
        every { segmentExceptionCase.nameWithoutExtension } returns "segments_3"
        every { item1.nameWithoutExtension } returns "item.txt"
        every { item2.nameWithoutExtension } returns "item.md"
        every { item3.nameWithoutExtension } returns "item.jar"
        every { Files.getLastModifiedTime(item1) } returns FileTime.fromMillis(4)
        every { Files.getLastModifiedTime(item2) } returns FileTime.fromMillis(3)
        every { Files.getLastModifiedTime(segmentExceptionCase) } throws IOException()
        every { Files.getLastModifiedTime(segment1) } returns FileTime.fromMillis(3)
        every { Files.getLastModifiedTime(segment2) } returns FileTime.fromMillis(1)
        subject = IndexTargetFilter(folder)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun invoke() {
        assertTrue(subject.invoke(item1))
        assertFalse(subject.invoke(item2))
        assertFalse(subject.invoke(item3))
    }

    @Test
    fun isDirectory() {
        every { Files.isDirectory(any()) } returns true

        assertFalse(subject.invoke(item1))
    }

    @Test
    fun isNotReadable() {
        every { Files.isReadable(any()) } returns false

        assertFalse(subject.invoke(item1))
    }

    @Test
    fun indexIsEmpty() {
        every { Files.list(folder) } returns Stream.empty()
        subject = IndexTargetFilter(folder)

        assertTrue(subject.invoke(item1))
        assertTrue(subject.invoke(item2))
    }

}