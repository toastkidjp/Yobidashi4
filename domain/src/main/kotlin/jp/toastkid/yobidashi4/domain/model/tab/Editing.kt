package jp.toastkid.yobidashi4.domain.model.tab

import java.util.concurrent.atomic.AtomicBoolean

class Editing {

    private val editing = AtomicBoolean(false)

    private var previousCount = -1

    fun setCurrentSize(size: Int) {
        if (previousCount != size) {
            editing.set(previousCount != -1)
            previousCount = size
        }
    }

    fun shouldShowIndicator() = editing.get()

    fun clear() {
        editing.set(false)
    }

}