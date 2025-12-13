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
import eu.kanade.presentation.util.ParcelableScreen
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import tachiyomi.core.common.util.lang.launchNonCancellable
import tachiyomi.domain.manga.interactor.GetManga
import tachiyomi.domain.manga.interactor.UpdateMangaNotes
import tachiyomi.domain.manga.model.Manga
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@Parcelize
data class MangaNotesScreen(
    private val mangaId: Long,
) : ParcelableScreen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = rememberScreenModel { Model(mangaId) }
        val state by screenModel.state.collectAsState()

        MangaNotesScreen(
            state = state,
            navigateUp = navigator::pop,
            onUpdate = screenModel::updateNotes,
        )
    }

    private class Model(
        private val mangaId: Long,
        private val getManga: GetManga = Injekt.get(),
        private val updateMangaNotes: UpdateMangaNotes = Injekt.get(),
    ) : StateScreenModel<State>(State(null, "")) {

        init {
            screenModelScope.launchNonCancellable {
                val manga = getManga.await(mangaId)
                mutableState.update {
                    State(manga, manga?.notes ?: "")
                }
            }
        }

        fun updateNotes(content: String) {
            val manga = state.value.manga ?: return
            if (content == state.value.notes) return

            mutableState.update {
                it.copy(notes = content)
            }

            screenModelScope.launchNonCancellable {
                updateMangaNotes(manga.id, content)
            }
        }
    }

    @Immutable
    data class State(
        val manga: Manga?,
        val notes: String,
    )
}
