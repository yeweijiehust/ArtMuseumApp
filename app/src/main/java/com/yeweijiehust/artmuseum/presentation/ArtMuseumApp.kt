package com.yeweijiehust.artmuseum.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yeweijiehust.artmuseum.R
import com.yeweijiehust.artmuseum.presentation.localization.LocalAppStrings
import com.yeweijiehust.artmuseum.presentation.localization.stringsFor
import com.yeweijiehust.artmuseum.presentation.ui.AuthScreen
import com.yeweijiehust.artmuseum.presentation.ui.DetailScreen
import com.yeweijiehust.artmuseum.presentation.ui.EditScreen
import com.yeweijiehust.artmuseum.presentation.ui.GalleryScreen
import com.yeweijiehust.artmuseum.presentation.ui.LoadingState
import com.yeweijiehust.artmuseum.presentation.ui.MineScreen
import com.yeweijiehust.artmuseum.presentation.ui.SettingsScreen
import com.yeweijiehust.artmuseum.presentation.ui.UploadScreen
import com.yeweijiehust.artmuseum.presentation.viewmodel.AppViewModel

private object Routes {
    const val Gallery = "gallery"
    const val Upload = "upload"
    const val Mine = "mine"
    const val Settings = "settings"
    const val Detail = "detail/{id}"
    const val Edit = "edit/{id}"
    const val Login = "login/{destination}"
    const val Register = "register/{destination}"
}

private data class Destination(val route: String, val icon: Int, val label: @Composable () -> String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtMuseumApp(viewModel: AppViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val strings = stringsFor(state.language)
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route.orEmpty()
    val topLevel = route in setOf(Routes.Gallery, Routes.Upload, Routes.Mine, Routes.Settings)
    val destinations = listOf(
        Destination(Routes.Gallery, R.drawable.ic_gallery) { strings.gallery },
        Destination(Routes.Upload, R.drawable.ic_upload) { strings.upload },
        Destination(Routes.Mine, R.drawable.ic_person) { strings.myMuseum },
        Destination(Routes.Settings, R.drawable.ic_settings) { strings.settings }
    )

    CompositionLocalProvider(LocalAppStrings provides strings) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when {
                                route == Routes.Gallery -> strings.appName
                                route == Routes.Upload -> strings.upload
                                route == Routes.Mine -> strings.myMuseum
                                route == Routes.Settings -> strings.settings
                                route.startsWith("login") -> strings.login
                                route.startsWith("register") -> strings.register
                                route.startsWith("edit") -> strings.edit
                                else -> strings.gallery
                            }
                        )
                    },
                    navigationIcon = {
                        if (!topLevel && route.isNotBlank()) {
                            IconButton(onClick = navController::navigateUp) {
                                Icon(painterResource(R.drawable.ic_back), strings.back)
                            }
                        }
                    },
                    actions = {}
                )
            },
            bottomBar = {
                if (topLevel) {
                    NavigationBar {
                        destinations.forEach { destination ->
                            NavigationBarItem(
                                modifier = Modifier.testTag("nav_${destination.route}"),
                                selected = route == destination.route,
                                onClick = {
                                    val target = if (
                                        destination.route in setOf(Routes.Upload, Routes.Mine) && state.user == null
                                    ) {
                                        "login/${destination.route}"
                                    } else destination.route
                                    navController.navigate(target) {
                                        if (target in destinations.map { it.route }) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { Icon(painterResource(destination.icon), destination.label()) },
                                label = { Text(destination.label()) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            if (state.restoring) {
                LoadingState(Modifier.padding(padding))
            } else {
                AppNavHost(navController, state, viewModel, Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    state: com.yeweijiehust.artmuseum.presentation.viewmodel.AppUiState,
    appViewModel: AppViewModel,
    modifier: Modifier
) {
    NavHost(navController, startDestination = Routes.Gallery, modifier = modifier) {
        composable(Routes.Gallery) {
            GalleryScreen(onImage = { navController.navigate("detail/$it") }, viewModel = hiltViewModel())
        }
        composable(Routes.Detail, arguments = listOf(navArgument("id") {})) {
            DetailScreen(viewModel = hiltViewModel())
        }
        composable(Routes.Upload) {
            if (state.user == null) {
                ProtectedRedirect(navController, Routes.Upload)
            } else {
                UploadScreen(onSuccess = {
                    navController.navigate(Routes.Mine) {
                        popUpTo(Routes.Upload) { inclusive = true }
                    }
                }, viewModel = hiltViewModel())
            }
        }
        composable(Routes.Mine) {
            if (state.user == null) {
                ProtectedRedirect(navController, Routes.Mine)
            } else {
                MineScreen(onImage = { navController.navigate("edit/$it") }, viewModel = hiltViewModel())
            }
        }
        composable(Routes.Edit, arguments = listOf(navArgument("id") {})) {
            if (state.user == null) {
                ProtectedRedirect(navController, Routes.Mine)
            } else {
                EditScreen(onDone = navController::navigateUp, viewModel = hiltViewModel())
            }
        }
        composable(Routes.Settings) {
            SettingsScreen(
                state = state,
                onEndpoint = appViewModel::saveEndpoint,
                onLanguage = appViewModel::setLanguage,
                onLogout = appViewModel::logout
            )
        }
        composable(Routes.Login, arguments = listOf(navArgument("destination") {})) { entry ->
            val destination = entry.arguments?.getString("destination") ?: Routes.Gallery
            AuthScreen(
                register = false,
                onSuccess = {
                    navController.navigate(destination) {
                        popUpTo("login/$destination") { inclusive = true }
                    }
                },
                onAlternate = { navController.navigate("register/$destination") },
                viewModel = hiltViewModel()
            )
        }
        composable(Routes.Register, arguments = listOf(navArgument("destination") {})) { entry ->
            val destination = entry.arguments?.getString("destination") ?: Routes.Gallery
            AuthScreen(
                register = true,
                onSuccess = {
                    navController.navigate(destination) {
                        popUpTo("register/$destination") { inclusive = true }
                    }
                },
                onAlternate = { navController.navigate("login/$destination") },
                viewModel = hiltViewModel()
            )
        }
    }
}

@Composable
private fun ProtectedRedirect(navController: NavHostController, destination: String) {
    LaunchedEffect(destination) {
        navController.navigate("login/$destination") {
            popUpTo(destination) { inclusive = true }
        }
    }
    LoadingState()
}
