package jp.toastkid.yobidashi4.infrastructure.service.notification
/*
 * Copyright (c) 2026 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import io.mockk.verify
import jp.toastkid.yobidashi4.domain.model.notification.NotificationEvent
import jp.toastkid.yobidashi4.domain.repository.notification.NotificationEventRepository
import jp.toastkid.yobidashi4.domain.service.notification.NotificationEventExporter
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class NotificationEventExporterImplementationTest {

    private lateinit var subject: NotificationEventExporter

    private lateinit var fakeFileSystem: FakeFileSystem

    @MockK
    private lateinit var notificationEventRepository: NotificationEventRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        startKoin {
            modules(
                module {
                    single(qualifier = null) { notificationEventRepository } bind (NotificationEventRepository::class)
                }
            )
        }

        every { notificationEventRepository.readAll() } returns listOf(NotificationEvent.makeDefault())

        fakeFileSystem = FakeFileSystem()

        subject = NotificationEventExporterImplementation(fakeFileSystem)
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        unmockkAll()
    }

    @Test
    fun invoke() {
        subject.invoke()

        verify { notificationEventRepository.readAll() }
    }

}