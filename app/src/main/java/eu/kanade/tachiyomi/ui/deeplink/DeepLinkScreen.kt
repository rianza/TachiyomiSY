package eu.kanade.tachiyomi.ui.deeplink

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.tachiyomi.ui.base.screen.ParcelableScreen
import kotlinx.parcelize.Parcelize
import tachiyomi.presentation.core.screens.LoadingScreen

@Parcelize
data class DeepLinkScreen(
    private val query: String,
) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DeepLinkScreenModel(query) }

        LoadingScreen()

        LaunchedEffect(Unit) {
            val screen = screenModel.getScreen()
            if (screen != null) {
                navigator.replace(screen)
            } else {
                navigator.pop()
            }
        }
    }
}
