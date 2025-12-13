
package eu.kanade.tachiyomi.ui.browse.migration.manga

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.MigrateMangaScreen
import eu.kanade.presentation.util.ParcelableScreen
import eu.kanade.tachiyomi.ui.browse.migration.advanced.design.PreMigrationScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class MigrateMangaScreen(private val mangaId: Long) : ParcelableScreen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        MigrateMangaScreen(
            navigateUp = navigator::pop,
            onClickItem = { source ->
                navigator.push(
                    PreMigrationScreen(
                        sourceId = source.id,
                        mangaId = mangaId,
                    ),
                )
            },
            onLongClickItem = { source ->
                navigator.push(
                    PreMigrationScreen(
                        sourceId = source.id,
                        mangaId = mangaId,
                    ),
                )
            },
        )
    }
}
