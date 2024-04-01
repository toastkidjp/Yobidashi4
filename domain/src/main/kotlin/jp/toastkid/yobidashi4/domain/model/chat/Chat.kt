package jp.toastkid.yobidashi4.domain.model.chat

data class Chat(private val texts: MutableList<ChatMessage> = mutableListOf()) {

    fun addUserText(text: String) {
        texts.add(ChatMessage("user", text))
    }

    fun addModelText(text: String) {
        texts.add(ChatMessage("model", text))
    }

    fun list(): List<ChatMessage> = texts

    fun makeContent() = """
      {
        "contents": [
          ${list().map { "{\"role\":\"${it.role}\", \"parts\":[{ \"text\": \"${it.text}\"}]}" }.joinToString(",")}
        ]
      }
    """.trimIndent()

}