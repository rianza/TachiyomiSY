package eu.kanade.tachiyomi.ui.setting

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.settings.screen.SettingsMainScreen
import eu.kanade.tachiyomi.ui.base.screen.ParcelableScreen
import kotlinx.parcelize.Parcelize
import tachiyomi.presentation.core.components.material.Scaffold

@Parcelize
data class SettingsScreen(
    val destination: Destination? = null,
) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            content = { contentPadding ->
                SettingsMainScreen(
                    modifier = Modifier.padding(contentPadding),
                    navigator = navigator,
                    destination = destination,
                )
            },
        )
    }

    enum class Destination {
        DataAndStorage,
        About,
    }
}
