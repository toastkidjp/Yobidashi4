package jp.toastkid.yobidashi4.presentation.chat

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.toastkid.yobidashi4.domain.model.chat.Source
import jp.toastkid.yobidashi4.presentation.component.HoverHighlightRow
import jp.toastkid.yobidashi4.presentation.component.LoadIcon

@Composable
internal fun MessageContent(
    text: String,
    base64Image: String? = null,
    sources: List<Source>,
    modifier: Modifier
) {
    val viewModel = remember { MessageContentViewModel() }

    Column(modifier) {
        text.split("\n").forEach {
            val listLine = it.startsWith("* ")
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (listLine) {
                    Text(
                        "・ ",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Text(
                    viewModel.lineText(listLine, it),
                    fontSize = 16.sp
                )
            }
        }

        if (base64Image != null) {
            DisableSelection {
                ContextMenuArea(
                    {
                        listOf(
                            ContextMenuItem("Store image") {
                                viewModel.storeImage(base64Image)
                            }
                        )
                    },
                    state = viewModel.contextMenuState()
                ) {
                    Image(
                        viewModel.image(base64Image),
                        contentDescription = text,
                    )
                }
            }
        }

        if (sources.isNotEmpty()) {
            SourceArea(
                sources,
                { viewModel.openLink(it) },
                { viewModel.openLinkOnBackground(it) },
                viewModel.horizontalSourceScrollState()
            )
        }
    }
}

@Composable
private fun SourceArea(
    sources: List<Source>,
    openLink: (String) -> Unit,
    openLinkOnBackground: (String) -> Unit,
    horizontalSourceScrollState: ScrollState
) {
    Text(
        "Sources",
        fontSize = 16.sp,
        modifier = Modifier.padding(top = 12.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
    )

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.horizontalScroll(horizontalSourceScrollState)
        ) {
            sources.forEachIndexed { index, source ->
                Surface(
                    elevation = 2.dp,
                    modifier = Modifier.padding(4.dp)
                ) {
                    HoverHighlightRow(
                        modifier = Modifier
                            .padding(4.dp)
                            .combinedClickable(
                                enabled = true,
                                onClick = { openLink(source.url) },
                                onLongClick = { openLinkOnBackground(source.url) }
                            )
                            .semantics { contentDescription = "$index,${source}" }
                    ) {
                        LoadIcon(
                            "https://${source.title}",
                            modifier = Modifier.size(32.dp).padding(horizontal = 4.dp)
                        )
                        Text(source.title)
                    }
                }
            }
        }

        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(horizontalSourceScrollState),
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
        )
    }
}