package eu.kanade.presentation.more.settings.screen.about

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.OpenSourceLicensesScreen
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class OpenSourceLicensesScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        OpenSourceLicensesScreen(
            navigateUp = navigator::pop,
            onClickLicense = { navigator.push(OpenSourceLibraryLicenseScreen(it)) },
        )
    }
}
