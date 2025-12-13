package eu.kanade.tachiyomi.ui.base.screen

import android.os.Parcelable
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
actual abstract class ParcelableScreen : Screen, Parcelable
