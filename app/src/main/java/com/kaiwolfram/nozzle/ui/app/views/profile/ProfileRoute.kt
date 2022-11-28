package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ProfileRoute(
    profileViewModel: ProfileViewModel,
    navToEditProfile: () -> Unit,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    ProfileRoute(
        uiState = uiState,
        navToEditProfile = navToEditProfile,
    )
}

@Composable
private fun ProfileRoute(
    uiState: ProfileViewModelState,
    navToEditProfile: () -> Unit,
) {
    ProfileScreen(
        uiState = uiState,
        navToFollowing = { /*TODO*/ },
        navToFollowers = { /*TODO*/ },
        navToEditProfile = navToEditProfile,
    )
}
