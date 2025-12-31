package eu.kanade.tachiyomi.ui.browse.extension.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.SourcePreferencesScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize
import tachiyomi.presentation.core.screens.LoadingScreen

@Parcelize
class SourcePreferencesScreen(val sourceId: Long) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SourcePreferencesScreenModel(sourceId = sourceId) }
        val state = screenModel.state.main

        if (state == null) {
            LoadingScreen()
            return
        }

        SourcePreferencesScreen(
            parentNavigator = navigator,
            screenModel = screenModel,
        )
    }
}
