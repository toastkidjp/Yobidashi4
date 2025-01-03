package jp.toastkid.yobidashi4.presentation.time

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorldTimeView(modifier: Modifier) {
    val viewModel = remember { WorldTimeViewModel() }

    Surface(
        color = MaterialTheme.colors.surface.copy(alpha = 0.75f),
        modifier = modifier
    ) {
        Box {
            LazyColumn(state = viewModel.listState()) {
                items(viewModel.items(), { it }) {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                            .animateItem()
                            .onClick {
                                viewModel.onClickItem(it)
                            }
                            .semantics { contentDescription = it.timeZone() }
                    ) {
                        Text(
                            viewModel.label(it.timeZone()),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(it.time, fontSize = 14.sp)
                    }

                    Divider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }

            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(viewModel.listState()),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }

    SideEffect {
        viewModel.start()
    }
}