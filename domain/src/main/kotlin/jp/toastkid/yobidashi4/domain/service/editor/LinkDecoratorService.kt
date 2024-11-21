package jp.toastkid.yobidashi4.domain.service.editor

interface LinkDecoratorService {

    operator fun invoke(link: String): String

}
