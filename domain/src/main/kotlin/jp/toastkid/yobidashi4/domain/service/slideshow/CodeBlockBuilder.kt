package jp.toastkid.yobidashi4.domain.service.slideshow

import java.util.concurrent.atomic.AtomicReference
import jp.toastkid.yobidashi4.domain.model.slideshow.data.CodeBlockLine

class CodeBlockBuilder {

    private var isInCodeBlock = false

    private val code = StringBuilder()

    private val codeFormat = AtomicReference("")

    fun append(line: String) {
        code.append(if (code.isNotEmpty()) LINE_SEPARATOR else "").append(line)
    }

    fun shouldAppend(line: String): Boolean {
        return isInCodeBlock && !line.startsWith("```")
    }

    fun build(): CodeBlockLine {
        return CodeBlockLine(code.toString(), codeFormat.get())
    }

    fun inCodeBlock() = isInCodeBlock

    fun initialize() {
        code.setLength(0)
        codeFormat.set("")
        isInCodeBlock = false
    }

    fun startCodeBlock() {
        this.isInCodeBlock = true
    }

    fun setCodeFormat(codeFormat: String) {
        this.codeFormat.set(codeFormat)
    }

    companion object {

        private val LINE_SEPARATOR = System.lineSeparator()

    }

}