
package eu.kanade.tachiyomi.ui.browse.migration.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.MigrateSearchScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class MigrateSearchScreen(private val mangaId: Long) : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MigrateSearchScreenModel(mangaId = mangaId) }
        val state by screenModel.state.collectAsState()

        MigrateSearchScreen(
            navigateUp = navigator::pop,
            title = state.manga?.title ?: "",
            state = state,
            getManga = screenModel::getManga,
            onChangeCover = {
                screenModel.changeCover(it)
                navigator.pop()
            },
            onMigrate = {
                screenModel.migrate(it)
                navigator.popUntil { screen -> screen is MangaScreen }
            },
            onToggleFavorite = screenModel::toggleFavorite,
            onSearch = screenModel::search,
            onSearchQueryChange = screenModel::setSearchQuery,
        )
    }
}
