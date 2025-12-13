package eu.kanade.presentation.more.settings.screen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.more.settings.SettingsScreen
import eu.kanade.presentation.util.LocalBackPress
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.parcelize.Parcelize
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold

@Parcelize
data object SettingsMainScreen : ParcelableScreen {

    @Composable
    override fun Content() {
        Content(twoPane = false)
    }

    @Composable
    fun Content(twoPane: Boolean) {
        val navigator = LocalNavigator.currentOrThrow
        val onBackPressed = if (twoPane) LocalBackPress.currentOrThrow else null
        Scaffold(
            topBar = {
                if (!twoPane) {
                    AppBar(
                        title = stringResource(MR.strings.label_settings),
                        navigateUp = onBackPressed,
                        actions = {
                            AppBarActions(navigator = navigator)
                        },
                    )
                }
            },
        ) { paddingValues ->
            SettingsScreen(
                paddingValues = paddingValues,
                twoPane = twoPane,
                navigator = navigator,
            )
        }
    }

    @Composable
    private fun AppBarActions(navigator: Navigator) {
        IconButton(onClick = { navigator.push(SettingsSearchScreen()) }) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(MR.strings.action_search),
            )
        }
    }
}
