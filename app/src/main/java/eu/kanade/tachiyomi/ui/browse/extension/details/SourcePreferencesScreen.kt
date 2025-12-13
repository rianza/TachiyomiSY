
package eu.kanade.tachiyomi.ui.browse.extension.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.SourcePreferencesScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class SourcePreferencesScreen(val sourceId: Long) : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        SourcePreferencesScreen(
            sourceId = sourceId,
            navigateUp = navigator::pop,
        )
    }
}
