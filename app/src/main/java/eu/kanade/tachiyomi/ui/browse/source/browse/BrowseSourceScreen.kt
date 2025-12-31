package eu.kanade.tachiyomi.ui.browse.source.browse

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.model.rememberScreenModel
import eu.kanade.presentation.browse.BrowseSourceScreen
import eu.kanade.presentation.util.AssistContentScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class BrowseSourceScreen(
    private val sourceId: Long,
    private val query: String? = null,
) : ParcelableScreen(), AssistContentScreen {

    private var assistUrl: String? = null

    override fun onProvideAssistUrl() = assistUrl

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { BrowseSourceScreenModel(sourceId, query) }
        val source = screenModel.source

        BrowseSourceScreen(
            screenModel = screenModel,
            navigateUp = navigator::pop,
            onFabClick = { navigator.push(SourcesFilterScreen()) },
            onMangaClick = { navigator.push(MangaScreen(it.id, true)) },
            onMangaLongClick = { manga ->
                // TODO: Add to library
            },
            onWebViewClick = {
                source?.let {
                    val url = screenModel.getWebviewUrl() ?: return@let
                    navigator.push(
                        WebViewScreen(
                            url = url,
                            initialTitle = source.name,
                            sourceId = source.id,
                        ),
                    )
                }
            },
            // SY -->
            onHelpClick = { navigator.push(HelpScreen()) },
            onRandomClick = {
                scope.launch {
                    val randomMangaUrl = screenModel.getRandomManga()
                    if (randomMangaUrl == null) {
                        snackbarHostState.showSnackbar(context.stringResource(MR.strings.information_no_entries_found))
                    } else {
                        val manga = screenModel.getManga(randomMangaUrl) ?: return@launch
                        navigator.push(MangaScreen(manga.id, true))
                    }
                }
            },
            // SY <--
        )

        LaunchedEffect(screenModel.mangaClicked) {
            screenModel.mangaClicked?.let {
                navigator.push(MangaScreen(it, true))
            }
        }
    }
}
