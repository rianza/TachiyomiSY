package eu.kanade.tachiyomi.ui.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.more.onboarding.OnboardingScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.ui.base.activity.BaseActivity
import kotlinx.parcelize.Parcelize

@Parcelize
class OnboardingScreen : ParcelableScreen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { OnboardingScreenModel() }
        val state by screenModel.state

        OnboardingScreen(
            state = state,
            onClickAnim = screenModel::toggle,
            onClickNext = {
                screenModel.onComplete()
                (context as? BaseActivity)?.onOnboardingProcceed()
                navigator.pop()
            },
        )
    }
}
