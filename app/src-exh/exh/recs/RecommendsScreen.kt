package exh.recs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.RecommendsScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class RecommendsScreen(private val args: Args) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { RecommendsScreenModel(args) }
        RecommendsScreen(
            navigateUp = navigator::pop,
            screenModel = screenModel,
            onMangaClick = { navigator.push(MangaScreen(it.id, true)) },
        )
    }
}
