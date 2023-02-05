package jp.toastkid.yobidashi4.domain.service.archive

class KeywordSearchFilter(private val input: String?) {

    private val keywords: MutableSet<String> = mutableSetOf()

    private val useAndSearch: Boolean

    init {
        if (input == null) {
            useAndSearch = false
        } else if (input.contains("\"")) {
            keywords.add(input.replace("\"", ""))
            useAndSearch = false
        } else if (input.contains("*")) {
            input.split("*").forEach { keywords.add(it) }
            useAndSearch = true
        } else if (input.contains(" ")) {
            input.split(" ").forEach { keywords.add(it) }
            useAndSearch = false
        } else {
            keywords.add(input)
            useAndSearch = false
        }
    }

    operator fun invoke(text: String?): Boolean {
        if (text == null) {
            return false
        }

        if (keywords.isEmpty()) {
            return true
        }

        return if (useAndSearch) {
            keywords.all { actualKeyword -> text.contains(actualKeyword) }
        } else {
            keywords.any { actualKeyword -> text.contains(actualKeyword) }
        }
    }

}