package com.kaiwolfram.nozzle.ui.app.views.profile.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModelState

@Composable
fun EditProfileRoute(
    profileViewModel: ProfileViewModel,
) {
    val uiState by profileViewModel.uiState.collectAsState()

    EditProfileRoute(
        uiState = uiState,
    )
}

@Composable
private fun EditProfileRoute(
    uiState: ProfileViewModelState,
) {
    EditProfileScreen(
        uiState = uiState,
    )
}