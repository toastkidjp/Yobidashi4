package jp.toastkid.yobidashi4.domain.model.input

data class InputHistory(
    val word: String,
    val timestamp: Long
) {

    fun toTsv() = "${word}\t${timestamp}"

    companion object {
        fun from(it: String?): InputHistory? {
            if (it == null || it.contains("\t").not()) {
                return null
            }

            val split = it.split("\t")
            return InputHistory(split[0], split[1].toLong())
        }
    }

}