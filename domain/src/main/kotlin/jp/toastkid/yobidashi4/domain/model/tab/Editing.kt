package jp.toastkid.yobidashi4.domain.model.tab

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class Editing {

    private val editing = AtomicBoolean(false)

    private val previousCount = AtomicInteger(-1)

    fun setCurrentSize(size: Int) {
        val previousCount = this.previousCount.get()
        if (previousCount != size) {
            editing.set(previousCount != -1)
            this.previousCount.set(size)
        }
    }

    fun shouldShowIndicator() = editing.get()

    fun clear() {
        editing.set(false)
    }

}