package jp.toastkid.yobidashi4.presentation.editor

import androidx.compose.runtime.mutableStateOf
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface TextLines {
    val size: Int
    fun get(index: Int): String

    companion object {
        private val empty = object : TextLines {
            override val size: Int
                get() = 0

            override fun get(index: Int): String = ""
        }

        fun empty() = empty

        fun loadLines(path: Path, scope: CoroutineScope): TextLines {
            var byteBufferSize: Int
            val byteBuffer = Files.readAllBytes(path).let { array ->
                byteBufferSize = array.size
                ByteBuffer.allocate(byteBufferSize).put(array)
            }

            val lineStartPositions = IntList()

            val size = mutableStateOf(0)

            val refreshJob = scope.launch {
                delay(100)
                size.value = lineStartPositions.size
                while (true) {
                    delay(1000)
                    size.value = lineStartPositions.size
                }
            }

            scope.launch(Dispatchers.IO) {
                path.readLinePositions(lineStartPositions)
                refreshJob.cancel()
                size.value = lineStartPositions.size
            }

            return object : TextLines {
                override val size get() = size.value

                override fun get(index: Int): String {
                    val startPosition = lineStartPositions[index]
                    val length = if (index + 1 < size.value) lineStartPositions[index + 1] - startPosition else
                        byteBufferSize - startPosition
                    // Only JDK since 13 has slice() method we need, so do ugly for now.
                    byteBuffer.position(startPosition)
                    val slice = byteBuffer.slice()
                    slice.limit(length)
                    return StandardCharsets.UTF_8.decode(slice).toString()
                }
            }
        }
    }
}

private fun Path.readLinePositions(
    starts: IntList
) {
    require(Files.size(this) <= Int.MAX_VALUE) {
        "Files with size over ${Int.MAX_VALUE} aren't supported"
    }

    val averageLineLength = 200
    starts.clear(Files.size(this).toInt() / averageLineLength)

    try {
        FileChannel.open(this).use { channel ->
            val ib = channel.map(
                FileChannel.MapMode.READ_ONLY, 0, channel.size()
            )
            var isBeginOfLine = true
            var position = 0L
            while (ib.hasRemaining()) {
                val byte = ib.get()
                if (isBeginOfLine) {
                    starts.add(position.toInt())
                }
                isBeginOfLine = byte.toInt().toChar() == '\n'
                position++
            }
            channel.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        starts.clear(1)
        starts.add(0)
    }

    starts.compact()
}

/**
 * Compact version of List<Int> (without unboxing Int and using IntArray under the hood)
 */
private class IntList(initialCapacity: Int = 16) {
    @Volatile
    private var array = IntArray(initialCapacity)

    @Volatile
    var size: Int = 0
        private set

    fun clear(capacity: Int) {
        array = IntArray(capacity)
        size = 0
    }

    fun add(value: Int) {
        if (size == array.size) {
            doubleCapacity()
        }
        array[size++] = value
    }

    operator fun get(index: Int) = array[index]

    private fun doubleCapacity() {
        val newArray = IntArray(array.size * 2 + 1)
        System.arraycopy(array, 0, newArray, 0, size)
        array = newArray
    }

    fun compact() {
        array = array.copyOfRange(0, size)
    }
}
