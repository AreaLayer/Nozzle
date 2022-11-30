package com.kaiwolfram.nozzle.ui.app.views.drawer


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions

@Composable
fun NozzleDrawerRoute(
    nozzleDrawerViewModel: NozzleDrawerViewModel,
    navActions: NozzleNavActions,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by nozzleDrawerViewModel.uiState.collectAsState()

    NozzleDrawerScreen(
        modifier = modifier,
        uiState = uiState,
        navActions = navActions,
        closeDrawer = closeDrawer,
    )
}
