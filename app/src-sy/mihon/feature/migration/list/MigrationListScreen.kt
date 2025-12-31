package mihon.feature.migration.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.migration.MigrationListScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class MigrationListScreen(private val mangaIds: Collection<Long>, private val extraSearchQuery: String?) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MigrationListScreenModel(mangaIds, extraSearchQuery) }
        val state by screenModel.state.collectAsState()

        MigrationListScreen(
            navigateUp = navigator::pop,
            state = state,
            onClickItem = { navigator.push(MigrationMangaScreen(it.id)) },
        )
    }
}
