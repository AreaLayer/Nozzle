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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kaiwolfram.nozzle.AppContainer
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.ui.app.feed.FeedViewModel
import com.kaiwolfram.nozzle.ui.app.messages.MessagesViewModel
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleDrawer
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleNavActions
import com.kaiwolfram.nozzle.ui.app.navigation.NozzleRoute
import com.kaiwolfram.nozzle.ui.app.profile.ProfileViewModel
import com.kaiwolfram.nozzle.ui.app.search.SearchViewModel
import com.kaiwolfram.nozzle.ui.theme.NozzleTheme
import kotlinx.coroutines.launch

@Composable
fun NozzleApp(appContainer: AppContainer) {
    NozzleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val vmContainer = VMContainer(
                profileViewModel = viewModel(
                    factory = ProfileViewModel.provideFactory(
                        defaultProfilePicture = painterResource(R.drawable.ic_default_profile),
                        imageLoader = appContainer.imageLoader,
                        context = LocalContext.current
                    )
                ),
                feedViewModel = viewModel(
                    factory = FeedViewModel.provideFactory()
                ),
                searchViewModel = viewModel(
                    factory = SearchViewModel.provideFactory()
                ),
                messagesViewModel = viewModel(
                    factory = MessagesViewModel.provideFactory()
                ),
            )

            val navController = rememberNavController()
            val navActions = remember(navController) {
                NozzleNavActions(navController)
            }

            val coroutineScope = rememberCoroutineScope()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute =
                navBackStackEntry?.destination?.route ?: NozzleRoute.FEED

            val drawerState = rememberDrawerState(DrawerValue.Closed)

            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    NozzleDrawer(
                        currentRoute = currentRoute,
                        navigateToProfile = navActions.navigateToProfile,
                        navigateToFeed = navActions.navigateToFeed,
                        navigateToSearch = navActions.navigateToSearch,
                        navigateToMessages = navActions.navigateToMessages,
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
                        navController = navController
                    )
                }
            }
        }
    }
}
