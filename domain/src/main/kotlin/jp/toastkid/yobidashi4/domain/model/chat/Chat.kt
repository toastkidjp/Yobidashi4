package jp.toastkid.yobidashi4.domain.model.chat

data class Chat(private val texts: MutableList<ChatMessage> = mutableListOf()) {

    private val contentsHolder = mutableListOf<Pair<String, List<String>>>()

    fun addUserText(text: String) {
        texts.add(ChatMessage("user", text))
        //contentsHolder.add("user" to listOf(text))
    }

    fun addModelText(text: String) {
        if (texts.isEmpty() || texts.last().role != "model") {
            texts.add(ChatMessage("model", text))
            return
        }

        val element = texts.last()
        texts.set(texts.lastIndex, element.copy(text = element.text + text))
    }

    @Deprecated("Delete soon")
    fun addModelTexts(texts: List<String>) {
        if (texts.isEmpty()) {
            contentsHolder.removeLast()
            return
        }
        contentsHolder.add("model" to texts)
    }

    fun removeLastUserText() {
        if (texts.isNotEmpty()) {
            texts.removeLast()
        }
        if (contentsHolder.isNotEmpty()) {
            contentsHolder.removeLast()
        }
    }

    fun list(): List<ChatMessage> = texts

    fun makeContent() = """
      {
        "contents": [
          ${texts.map { "{\"role\":\"${it.role}\", \"parts\":[ ${it.text.map { "{ \"text\": \"${it}\"}" }.joinToString(",")} ]}" }.joinToString(",")}
        ],
        "safetySettings": [
            {
                "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                "threshold": "BLOCK_ONLY_HIGH"
            }
        ]
      }
    """.trimIndent()

}