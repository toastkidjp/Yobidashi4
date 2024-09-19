package jp.toastkid.yobidashi4.domain.model.input

data class InputHistory(
    val word: String,
    val timestamp: Long
) {

    fun toTsv() = "${word}\t${timestamp}"

    companion object {
        fun from(it: String?): InputHistory? {
            if (it == null || it.contains(DELIMITER).not()) {
                return null
            }

            val split = it.split(DELIMITER)
            val timestamp = try {
                split[1].toLong()
            } catch (e: NumberFormatException) {
                System.currentTimeMillis()
            }
            return InputHistory(split[0], timestamp)
        }

        fun withWord(word: String) = InputHistory(word, System.currentTimeMillis())

    }

}

private const val DELIMITER = "\t"
