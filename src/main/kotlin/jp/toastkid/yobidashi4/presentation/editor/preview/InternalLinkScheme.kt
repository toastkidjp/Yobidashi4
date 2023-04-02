package jp.toastkid.yobidashi4.presentation.editor.preview

class InternalLinkScheme {

    fun makeLink(title: String?) = "[$title]($INTERNAL_LINK_SCHEME${title?.replace(" ", "%20")})"

    fun isInternalLink(url: String): Boolean {
        return url.startsWith(INTERNAL_LINK_SCHEME)
    }

    fun extract(url: String): String {
        if (url.length <= INTERNAL_LINK_SCHEME.length || !url.startsWith(INTERNAL_LINK_SCHEME)) {
            return url
        }
        return url.substring(INTERNAL_LINK_SCHEME.length).replace("%20", " ")
    }

    companion object {

        private const val INTERNAL_LINK_SCHEME = "https://internal/"

    }

}