package jp.toastkid.yobidashi4.presentation.editor.legacy.model

class Editing {

    private var editing = false

    private var previousCount = -1

    fun setCurrentSize(size: Int) {
        if (previousCount != size) {
            editing = previousCount != -1
            previousCount = size
        }
    }

    fun shouldShowIndicator() = editing

    fun clear() {
        editing = false
    }

}