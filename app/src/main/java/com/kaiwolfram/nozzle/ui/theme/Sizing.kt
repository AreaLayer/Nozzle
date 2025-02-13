package com.kaiwolfram.nozzle.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class Sizing(
    /**
     * 16 dp
     */
    val smallIcon: Dp = 16.dp,
    /**
     * 24 dp
     */
    val mediumIcon: Dp = 24.dp,
    /**
     * 32 dp
     */
    val smallProfilePicture: Dp = 32.dp,
    /**
     * 40 dp
     */
    val profilePicture: Dp = 40.dp,
    /**
     * 60 dp
     */
    val largeProfilePicture: Dp = 60.dp,
)

val LocalSizing = compositionLocalOf { Sizing() }

val sizing: Sizing
    @Composable
    @ReadOnlyComposable
    get() = LocalSizing.current
