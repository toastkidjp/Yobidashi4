package jp.toastkid.yobidashi4.domain.service.archive

import java.awt.Dimension
import java.nio.file.Path
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField
import jp.toastkid.yobidashi4.domain.model.article.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class ArticleFinderService : KoinComponent {

    operator fun invoke(resultConsumer: (String, List<Path>) -> Unit = { _, _ -> }) {
        val (panel, fileFilter) = makeDialogContent()
        val keyword = JOptionPane.showInputDialog(null, panel)
        if (keyword.isNullOrBlank() && fileFilter.text.isNullOrBlank()) {
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            val articles = withContext(Dispatchers.IO) {
                KeywordSearch().invoke(keyword, fileFilter.text)
                    .asSequence()
                    .filter { it.isNotBlank() }
                    .map { Article.withTitle(it).path() }
                    .toList()
            }

            if (articles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Article which contains '$keyword' is not found.")
                return@launch
            }
            resultConsumer("'$keyword' find result ${articles.size}", articles)
        }
    }

    private fun makeDialogContent(): Pair<JPanel, JTextField> {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        val fileFilter = JTextField()
        fileFilter.preferredSize = Dimension(100, 24)
        panel.add(JLabel("Please would you input find query."))
        panel.add(JLabel("Title filter"))
        panel.add(fileFilter)
        panel.add(JLabel("Keyword"))
        return Pair(panel, fileFilter)
    }

}