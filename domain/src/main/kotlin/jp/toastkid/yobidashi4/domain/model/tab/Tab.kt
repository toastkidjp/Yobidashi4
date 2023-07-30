package jp.toastkid.yobidashi4.domain.model.tab

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface Tab {

    fun title(): String

    fun closeable(): Boolean = true

    fun iconPath(): String? = null

    fun update(): Flow<Long> = emptyFlow()

}