package jp.toastkid.yobidashi4.domain.model.chat

enum class GenerativeAiModel(
    private val label: String,
    private val urlParameter: String,
    private val versionPath: String,
    private val image: Boolean = false
) {

    GEMINI_2_0_FLASH(
        "Gemini 2.0 Flash",
        "gemini-2.0-flash",
        "v1"
    ),
    GEMINI_2_0_FLASH_IMAGE(
        "Image generation",
        "gemini-2.0-flash-preview-image-generation",
        "v1beta",
        true
    );

    fun label(): String = label

    fun url(): String {
        return "https://generativelanguage.googleapis.com/${versionPath}/models/${urlParameter}:streamGenerateContent?alt=sse&key="
    }

    fun image() = image

}