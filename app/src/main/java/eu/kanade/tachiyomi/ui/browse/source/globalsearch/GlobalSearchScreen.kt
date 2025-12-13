
package eu.kanade.tachiyomi.ui.browse.source.globalsearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.GlobalSearchScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.ui.manga.MangaScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class GlobalSearchScreen(val query: String = "") : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { GlobalSearchScreenModel(initialQuery = query) }
        val state by screenModel.state.collectAsState()

        GlobalSearchScreen(
            state = state,
            navigateUp = navigator::pop,
            onChangeSearchQuery = screenModel::setSearchQuery,
            onSearch = screenModel::search,
            onClickSource = {
                // TODO: Maybe not pop until we have better stacks
                navigator.pop()
                navigator.push(MangaScreen(it.id))
            },
            onClickItem = {
                // TODO: Maybe not pop until we have better stacks
                navigator.pop()
                navigator.push(MangaScreen(it.id))
            },
            onLongClickItem = {
                // TODO: Maybe not pop until we have better stacks
                navigator.pop()
                navigator.push(MangaScreen(it.id))
            },
        )
    }
}
