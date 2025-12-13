package eu.kanade.tachiyomi.ui.deeplink

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.GlobalSearchScreen
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.parcelize.Parcelize
import tachiyomi.presentation.core.screens.LoadingScreen

@Parcelize
data class DeepLinkScreen(
    private val query: String,
) : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DeepLinkScreenModel(query = query) }

        val (manga, source) = screenModel.manga
        if (manga != null && source != null) {
            LaunchedEffect(Unit) {
                navigator.replace(MangaScreen(manga.id, true))
            }
        } else {
            LaunchedEffect(Unit) {
                navigator.replace(GlobalSearchScreen(query))
            }
        }

        LoadingScreen()
    }
}
