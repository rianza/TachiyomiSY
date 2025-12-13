package eu.kanade.tachiyomi.ui.manga.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.manga.MangaNotesScreen
import eu.kanade.tachiyomi.ui.base.screen.ParcelableScreen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import tachiyomi.core.common.util.lang.launchNonCancellable
import tachiyomi.domain.manga.interactor.GetManga
import tachiyomi.domain.manga.interactor.UpdateMangaNotes
import tachiyomi.domain.manga.model.Manga
import tachiyomi.presentation.core.screens.LoadingScreen
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Parcelize
data class MangaNotesScreen(
    private val mangaId: Long,
) : ParcelableScreen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = rememberScreenModel { Model(mangaId) }
        val state by screenModel.state.collectAsState()

        if (state is State.Success) {
            MangaNotesScreen(
                state = state as State.Success,
                navigateUp = navigator::pop,
                onUpdate = screenModel::updateNotes,
            )
        } else {
            LoadingScreen()
        }
    }

    private class Model(
        private val mangaId: Long,
        private val getManga: GetManga = Injekt.get(),
        private val updateMangaNotes: UpdateMangaNotes = Injekt.get(),
    ) : StateScreenModel<State>(State.Loading) {

        init {
            screenModelScope.launch {
                val manga = getManga.await(mangaId)
                if (manga != null) {
                    mutableState.update {
                        State.Success(manga = manga, notes = manga.notes)
                    }
                }
            }
        }

        fun updateNotes(content: String) {
            val currentState = state.value
            if (currentState !is State.Success || content == currentState.notes) return

            mutableState.update {
                (it as State.Success).copy(notes = content)
            }

            screenModelScope.launchNonCancellable {
                updateMangaNotes(mangaId, content)
            }
        }
    }

    @Immutable
    sealed interface State {
        data object Loading : State
        data class Success(
            val manga: Manga,
            val notes: String,
        ) : State
    }
}
