/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi4.presentation.main.content.data

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension

class FileListItemTest {

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test() {
        val fileListItem = FileListItem(mockk(), true, true, "test-sub", System.currentTimeMillis())
        assertTrue(fileListItem.selected)
        val unselect = fileListItem.unselect()
        assertFalse(unselect.selected)
        val reverseSelection = fileListItem.reverseSelection()
        assertFalse(reverseSelection.selected)
        assertAll(
            { assertEquals(fileListItem.subText(), unselect.subText()) },
            { assertEquals(fileListItem.sortKey(), unselect.sortKey()) },
            { assertEquals(fileListItem.subText(), reverseSelection.subText()) },
            { assertEquals(fileListItem.sortKey(), reverseSelection.sortKey()) }
        )
    }

    @Test
    fun testEditable() {
        val fileListItem = FileListItem(mockk(), selected = true, editable = true)
        assertTrue(fileListItem.selected)
        assertFalse(fileListItem.unselect().selected)
        assertFalse(fileListItem.reverseSelection().selected)
        assertTrue(fileListItem.editable)
        assertTrue(fileListItem.reverseSelection().editable)
    }

    @Test
    fun name() {
        val path = mockk<Path>()
        every { path.nameWithoutExtension } returns "test"
        val fileListItem = FileListItem(path, selected = true, editable = true)

        val name = fileListItem.name()

        assertEquals("test", name)
    }

}