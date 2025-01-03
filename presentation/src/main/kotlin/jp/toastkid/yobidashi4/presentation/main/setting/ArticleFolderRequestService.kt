package jp.toastkid.yobidashi4.presentation.main.setting

import jp.toastkid.yobidashi4.domain.model.setting.Setting
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import javax.swing.JFileChooser

class ArticleFolderRequestService : KoinComponent {

    private val setting: Setting by inject()

    operator fun invoke() {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Would you like to choose article folder?"
        fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val result = fileChooser.showOpenDialog(null)
        if (result != JFileChooser.APPROVE_OPTION) {
            return
        }

        setting.setArticleFolderPath(fileChooser.selectedFile.absolutePath)
        setting.save()
    }

}