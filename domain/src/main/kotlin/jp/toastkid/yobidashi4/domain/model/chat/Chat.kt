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

    fun list(): List<ChatMessage> = texts

    fun makeContent() = """
      {
        "contents": [
          ${texts.map { "{\"role\":\"${it.role}\", \"parts\":[ { \"text\": \"${it.text}\"} ]}" }.joinToString(",")}
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