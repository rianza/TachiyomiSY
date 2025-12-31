package exh.md.follows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.MangaDexFollowsScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class MangaDexFollowsScreen(private val sourceId: Long) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MangaDexFollowsScreenModel(sourceId) }
        val state by screenModel.state.collectAsState()

        MangaDexFollowsScreen(
            state = state,
            navigateUp = navigator::pop,
            onClickManga = { navigator.push(MangaScreen(it, true)) },
            onPageChange = screenModel::getManga,
            onChecked = screenModel::toggleManga,
            onBatchAdd = screenModel::batchAdd,
        )
    }
}
