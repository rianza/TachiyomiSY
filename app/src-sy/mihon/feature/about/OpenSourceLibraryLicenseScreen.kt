package eu.kanade.presentation.more.settings.screen.about

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mikepenz.aboutlibraries.entity.Library
import eu.kanade.presentation.more.settings.screen.OpenSourceLibraryLicenseScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class OpenSourceLibraryLicenseScreen(private val library: Library) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        OpenSourceLibraryLicenseScreen(
            navigateUp = navigator::pop,
            library = library,
        )
    }
}
