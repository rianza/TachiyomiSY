package mihon.feature.upcoming

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.upcoming.UpcomingScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class UpcomingScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { UpcomingScreenModel() }
        val state by screenModel.state.collectAsState()
        UpcomingScreen(
            state = state,
            navigateUp = navigator::pop,
            onClickUpcoming = { navigator.push(MangaScreen(it.mangaId)) },
        )
    }
}
