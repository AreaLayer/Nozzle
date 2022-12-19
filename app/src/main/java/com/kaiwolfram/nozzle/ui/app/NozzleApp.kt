package com.kaiwolfram.nozzle.ui.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.AppContainer
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerRoute
import com.kaiwolfram.nozzle.ui.app.views.drawer.NozzleDrawerViewModel
import com.kaiwolfram.nozzle.ui.app.views.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.views.keys.KeysViewModel
import com.kaiwolfram.nozzle.ui.app.views.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.views.settings.SettingsViewModel
import com.kaiwolfram.nozzle.ui.app.views.thread.ThreadViewModel
import com.kaiwolfram.nozzle.ui.theme.NozzleTheme
import kotlinx.coroutines.launch

@Composable
fun NozzleApp(appContainer: AppContainer) {
    NozzleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            val navActions = remember(navController) {
                NozzleNavActions(navController)
            }

            val vmContainer = VMContainer(
                drawerViewModel = viewModel(
                    factory = NozzleDrawerViewModel.provideFactory(
                        profileStorageReader = appContainer.profilePreferences,
                    )
                ),
                profileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        nostrRepository = appContainer.nostrRepository,
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                        profileDao = appContainer.roomDb.profileDao(),
                        eventDao = appContainer.roomDb.eventDao(),
                    )
                ),
                keysViewModel = viewModel(
                    factory = KeysViewModel.provideFactory(
                        profileStorage = appContainer.profilePreferences,
                        context = LocalContext.current,
                        clip = LocalClipboardManager.current,
                    )
                ),
                feedViewModel = viewModel(
                    factory = FeedViewModel.provideFactory(
                        nostrRepository = appContainer.nostrRepository,
                        profileStorageReader = appContainer.profilePreferences,
                    )
                ),
                settingsViewModel = viewModel(
                    factory = SettingsViewModel.provideFactory(
                        personalProfileStorage = appContainer.profilePreferences,
                        context = LocalContext.current,
                    )
                ),
                threadViewModel = viewModel(
                    factory = ThreadViewModel.provideFactory(
                        nostrRepository = appContainer.nostrRepository,
                    )
                )
            )

            val coroutineScope = rememberCoroutineScope()
            val drawerState = rememberDrawerState(DrawerValue.Closed)

            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    NozzleDrawerRoute(
                        nozzleDrawerViewModel = vmContainer.drawerViewModel,
                        navActions = navActions,
                        onSetPubkey = vmContainer.profileViewModel.onSetPubkey,
                        closeDrawer = { coroutineScope.launch { drawerState.close() } },
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                },
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    NozzleScaffold(
                        vmContainer = vmContainer,
                        navController = navController,
                        navActions = navActions,
                        drawerState = drawerState,
                    )
                }
            }
        }
    }
}
