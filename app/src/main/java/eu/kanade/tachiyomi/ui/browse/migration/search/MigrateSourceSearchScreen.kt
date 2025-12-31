package eu.kanade.tachiyomi.ui.browse.migration.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.MigrateSearchScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class MigrateSourceSearchScreen(private val sourceId: Long) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel {
            MigrateSearchScreenModel(sourceId = sourceId)
        }
        MigrateSearchScreen(
            navigateUp = navigator::pop,
            screenModel = screenModel,
        )
    }
}
