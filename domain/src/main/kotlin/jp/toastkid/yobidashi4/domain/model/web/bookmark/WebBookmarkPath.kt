package jp.toastkid.yobidashi4.domain.model.web.bookmark

import java.nio.file.Path

class WebBookmarkPath {

    fun getPath(): Path = path

}

private val path = Path.of("user/bookmark/list.tsv")
