package eu.kanade.tachiyomi.ui.browse.extension.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.SourceSettingsScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourcePreferencesScreen(val sourceId: Long) : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { SourcePreferencesScreenModel(sourceId) }

        SourceSettingsScreen(
            navigateUp = navigator::pop,
            state = screenModel.state,
            onClickReset = { screenModel.reset() },
            onClickLogin = { screenModel.login() },
            onClickLogout = { screen-model.logout() },
            onPrefChange = { screenModel.onPrefChange(it) },
        )
    }
}
