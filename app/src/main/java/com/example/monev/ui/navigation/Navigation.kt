package com.example.monev.ui.navigation


import MyBottomBar
import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.monev.sign_in.GoogleAuthUiClient
import com.example.monev.ui.screens.about.AboutScreen
import com.example.monev.ui.screens.account.AccountScreen
import com.example.monev.ui.screens.auth.SignInScreen
import com.example.monev.ui.screens.chatbot.ChatbotScreen
import com.example.monev.ui.screens.history.ListHistoryScreen
import com.example.monev.ui.screens.home.HomeScreen
import com.example.monev.ui.screens.result.ResultScreen
import com.example.monev.ui.screens.setting.SettingScreen
import com.example.monev.ui.screens.welcome.WelcomeScreen
import com.example.monev.ui.splash.SplashScreen
import com.example.monev.viewmodel.auth.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animatedComposable(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300, easing = EaseOut)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300, easing = EaseOut)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300, easing = EaseOut)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300, easing = EaseOut)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) { navBackStackEntry ->
        content(navBackStackEntry)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val googleAuthUiClient = remember {
        GoogleAuthUiClient(
            context = context.applicationContext,
            oneTapClient = Identity.getSignInClient(context.applicationContext)
        )
    }

    // Rute yang membutuhkan BottomBar
    val screensWithBottomBar = listOf(
        Destinations.HomeScreen.route,
        Destinations.SettingScreen.route,
        Destinations.ChatbotScreen.route
    )

    // Cek apakah pengguna sudah login
    val isUserSignedIn = googleAuthUiClient.getSignedInUser() != null

    // State untuk menyimpan rute saat ini
    val currentRoute = remember { mutableStateOf<String?>(null) }

    // Update currentRoute setiap kali destinasi berubah
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = destination.route
        }
    }

    Scaffold(
        bottomBar = {
            // Tampilkan BottomBar hanya di rute tertentu
            if (currentRoute.value in screensWithBottomBar) {
                MyBottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Destinations.SplashScreen.route,
            modifier = Modifier
                .padding(paddingValues)
        ) {
            // SplashScreen: Tentukan rute setelah splash berdasarkan login
            animatedComposable(Destinations.SplashScreen.route) { splashBackStackEntry ->
                SplashScreen(
                    onSplashComplete = {
                        val isUserSignedIn = googleAuthUiClient.getSignedInUser() != null
                        if (isUserSignedIn) {
                            navController.navigate(Destinations.HomeScreen.route) {
                                popUpTo(Destinations.SplashScreen.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Destinations.WelcomeScreen.route) {
                                popUpTo(Destinations.SplashScreen.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // WelcomeScreen (tanpa BottomBar)
            animatedComposable(Destinations.WelcomeScreen.route) {
                WelcomeScreen(
                    onNextClick = {
                        navController.navigate(Destinations.SignInScreen.route) {
                            popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                        }
                    }
                )
            }

            // SignIn Screen
            animatedComposable(Destinations.SignInScreen.route) {
                val viewModel = viewModel<SignInViewModel>()
                val state = viewModel.state.collectAsStateWithLifecycle().value

                LaunchedEffect(Unit) {
                    if (googleAuthUiClient.getSignedInUser() != null) {
                        navController.navigate(Destinations.HomeScreen.route) {
                            popUpTo(Destinations.SignInScreen.route) { inclusive = true }
                        }
                    }
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                    onResult = { result ->
                        if (result.resultCode == RESULT_OK) {
                            coroutineScope.launch {
                                val signInResult = googleAuthUiClient.SignInWithIntent(
                                    intent = result.data ?: return@launch
                                )
                                viewModel.onSignInResult(signInResult)
                            }
                        } else {
                            Toast.makeText(context, "Sign In Canceled", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                LaunchedEffect(state.isSignInSuccesful) {
                    if (state.isSignInSuccesful) {
                        Toast.makeText(context, "Sign In Success", Toast.LENGTH_LONG).show()
                        navController.navigate(Destinations.HomeScreen.route) {
                            popUpTo(Destinations.SignInScreen.route) { inclusive = true }
                        }
                        viewModel.resetState()
                    }
                }

                SignInScreen(
                    state = state,
                    onSignInClick = {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            if (signInIntentSender != null) {
                                launcher.launch(
                                    IntentSenderRequest.Builder(signInIntentSender).build()
                                )
                            } else {
                                Toast.makeText(context, "Sign In Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            // HomeScreen (dengan BottomBar)
            animatedComposable(Destinations.HomeScreen.route) {
                HomeScreen(
                    navController = navController,
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show()
                            navController.navigate(Destinations.WelcomeScreen.route) {
                                popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                            }
                        }
                    },
                    onPredictionResult = { predictionResult, confidence ->
                        navController.navigate(
                            Destinations.ResultScreenArgs.createRoute(
                                predictionResult,
                                confidence
                            )
                        )
                    }
                )
            }

            // SettingScreen (dengan BottomBar)
            animatedComposable(Destinations.SettingScreen.route) {
                SettingScreen(
                    navController = navController,
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show()
                            navController.navigate(Destinations.WelcomeScreen.route) {
                                popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // Chatbot Screen (dengan BottomBar)
            animatedComposable(Destinations.ChatbotScreen.route) {
                ChatbotScreen(navController = navController)
            }

            // About Screen
            animatedComposable(Destinations.AboutScreen.route) {
                AboutScreen(navController = navController)
            }

            // History Screen (dengan BottomBar)
            animatedComposable(Destinations.HistoryScreen.route) {
                ListHistoryScreen(navController = navController)
            }

            // Account Screen
            animatedComposable(Destinations.AccountScreen.route) {
                AccountScreen(
                    navController = navController,
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(context, "Signed out", Toast.LENGTH_LONG).show()
                            navController.navigate(Destinations.WelcomeScreen.route) {
                                popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // Result Screen dengan argumen
            composable(
                route = Destinations.ResultScreenArgs.route,
                arguments = listOf(
                    navArgument("predictionResult") { type = NavType.StringType },
                    navArgument("confidence") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val predictionResult =
                    backStackEntry.arguments?.getString("predictionResult") ?: "Unknown"
                val confidence = backStackEntry.arguments?.getFloat("confidence") ?: 0f

                ResultScreen(
                    navController = navController,
                    predictionResult = predictionResult,
                    confidence = confidence
                )
            }

            // Opsional: Jika ingin ResultScreen tanpa argumen
            animatedComposable(Destinations.ResultScreen.route) {
                ResultScreen(
                    navController = navController,
                    predictionResult = "Unknown",
                    confidence = 0f
                )
            }
        }
    }
}
