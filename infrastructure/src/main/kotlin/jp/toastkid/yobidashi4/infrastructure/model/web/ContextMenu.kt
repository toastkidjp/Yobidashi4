package jp.toastkid.yobidashi4.infrastructure.model.web

enum class ContextMenu(
    val id: Int,
    val text: String,
    val context: Context = Context.NONE
) {
    RELOAD(401, "リロード"),
    ADD_BOOKMARK(408, "ブックマークに追加"),

    OPEN_OTHER_TAB(402, "別タブで開く", Context.LINK),
    OPEN_BACKGROUND(403, "バックグラウンドで開く", Context.LINK),
    CLIP_LINK(404, "リンクをコピー", Context.LINK),

    DOWNLOAD(407, "ダウンロード", Context.IMAGE),
    QUICK_STORE_IMAGE(418, "画像を保存", Context.IMAGE),
    CLIP_IMAGE(409, "画像をコピー", Context.IMAGE),
    SEARCH_WITH_IMAGE(415, "この画像を検索", Context.IMAGE),

    CLIP_PAGE_LINK(410, "ページのリンクをコピー", Context.LINK),
    CLIP_AS_MARKDOWN_LINK(411, "Markdown のリンクをコピー", Context.LINK),

    CLIP_TEXT(416, "テキストをコピー", Context.PLAIN_TEXT),
    SEARCH_WITH_SELECTED_TEXT(405, "選択したテキストを検索", Context.PLAIN_TEXT),

    OPEN_WITH_OTHER_BROWSER(414, "ブラウザーで開く"),
    RESET_ZOOM(406, "ズーム率をリセット"),
    SAVE_AS_PDF(412, "PDF で保存"),
    DEVELOPER_TOOL(417, "Developer tool");

}