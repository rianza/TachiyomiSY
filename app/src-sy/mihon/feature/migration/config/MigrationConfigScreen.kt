package mihon.feature.migration.config

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.migration.MigrationConfigScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class MigrationConfigScreen(private val mangaIds: Collection<Long>) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { MigrationConfigScreenModel(mangaIds) }
        val state by screenModel.state.collectAsState()

        MigrationConfigScreen(
            navigateUp = navigator::pop,
            state = state,
            onClickSelection = screenModel::onMangaSelected,
            onClickSource = screenModel::onSourceSelected,
            onClickExtra = screenModel::toggleExtra,
            onMigrate = screenModel::onMigrate,
        )
    }
}
