package jp.toastkid.yobidashi4.domain.model.chat

data class Chat(private val texts: MutableList<ChatMessage> = mutableListOf()) {

    private val contentsHolder = mutableListOf<Pair<String, List<String>>>()

    fun addUserText(text: String) {
        texts.add(ChatMessage("user", text))
        contentsHolder.add("user" to listOf(text))
    }

    fun addModelText(text: String) {
        val element = ChatMessage("model", text)
        texts.add(element)
    }

    fun addModelTexts(texts: List<String>) {
        if (texts.isEmpty()) {
            contentsHolder.removeLast()
            return
        }
        contentsHolder.add("model" to texts)
    }

    fun list(): List<ChatMessage> = texts

    fun makeContent() = """
      {
        "contents": [
          ${contentsHolder.map { "{\"role\":\"${it.first}\", \"parts\":[ ${it.second.map { "{ \"text\": \"${it}\"}" }.joinToString(",")} ]}" }.joinToString(",")}
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