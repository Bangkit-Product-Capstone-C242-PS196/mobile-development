package com.example.monev.ui.navigation

import MyBottomBar
import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.monev.sign_in.GoogleAuthUiClient
import com.example.monev.ui.screens.account.AccountScreen
import com.example.monev.ui.screens.auth.SignInScreen
import com.example.monev.ui.screens.chatbot.ChatbotScreen
import com.example.monev.ui.screens.history.CreateHistoryScreen
import com.example.monev.ui.screens.history.ListHistoryScreen
import com.example.monev.ui.screens.home.HomeScreen
import com.example.monev.ui.screens.result.ResultScreen
import com.example.monev.ui.screens.setting.SettingScreen
import com.example.monev.ui.screens.welcome.WelcomeScreen
import com.example.monev.viewmodel.auth.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val googleAuthUiClient = GoogleAuthUiClient(
        context = LocalContext.current.applicationContext,
        oneTapClient = Identity.getSignInClient(LocalContext.current.applicationContext)
    )

    // Rute yang membutuhkan BottomBar
    val screensWithBottomBar = listOf(
        Destinations.HomeScreen.route,
        Destinations.SettingScreen.route,
        Destinations.ChatbotScreen.route,
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
            startDestination = if (isUserSignedIn) Destinations.HomeScreen.route else Destinations.WelcomeScreen.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // WelcomeScreen (tanpa BottomBar)
            composable(Destinations.WelcomeScreen.route) {
                WelcomeScreen(onNextClick = {
                    navController.navigate(Destinations.SignInScreen.route) {
                        popUpTo(Destinations.SignInScreen.route) { inclusive = true }
                    }
                })
            }

            // SignIn Screen
            composable(Destinations.SignInScreen.route) {
                val viewModel = viewModel<SignInViewModel>()
                val state = viewModel.state.collectAsStateWithLifecycle().value

                LaunchedEffect(key1 = Unit) {
                    if (googleAuthUiClient.getSignedInUser() != null) {
                        navController.navigate(Destinations.HomeScreen.route)
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
                        }
                    }
                )

                LaunchedEffect(key1 = state.isSignInSuccesful) {
                    if (state.isSignInSuccesful) {
                        Toast.makeText(
                            context,
                            "Sign In Sukses",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(Destinations.HomeScreen.route)
                        viewModel.resetState()
                    }
                }

                SignInScreen(
                    state = state,
                    onSignInClick = {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                            )
                        }
                    }
                )
            }

            // history screen
            composable("create_history_screen") {
                CreateHistoryScreen(navController = navController)
            }
            composable("list_history_screen") {
                ListHistoryScreen(navController = navController)
            }

            // HomeScreen (dengan BottomBar)
            composable(Destinations.HomeScreen.route) {
                HomeScreen(
                    navController = navController,
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(
                                context,
                                "signed out",
                                Toast.LENGTH_LONG
                            ).show()

                            // Navigasi kembali ke WelcomeScreen setelah sign-out
                            navController.navigate(Destinations.WelcomeScreen.route) {
                                // Menghapus semua rute sebelumnya dari stack
                                popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                            }
                        }
                    },
                    onPredictionResult = { prediction, confidence ->  // Callback dengan dua parameter
                        // Navigasi ke ResultScreen dengan data prediksi
                        navController.navigate(
                            Destinations.ResultScreen.createRoute(prediction, confidence)
                        )
                    }
                )
            }

            // SettingScreen (dengan BottomBar)
            composable(Destinations.SettingScreen.route) {
                SettingScreen(
                    navController = navController,
                    onSignOut = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(
                                context,
                                "signed out",
                                Toast.LENGTH_LONG
                            ).show()

                            // Navigasi kembali ke WelcomeScreen setelah sign-out
                            navController.navigate(Destinations.WelcomeScreen.route) {
                                // Menghapus semua rute sebelumnya dari stack
                                popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // Chatbot Screen
            composable(Destinations.ChatbotScreen.route) {
                ChatbotScreen(navController = navController)
            }

            // Account Screen
            composable(Destinations.AccountScreen.route) {
                AccountScreen(
                    navController = navController,
                    userData = googleAuthUiClient.getSignedInUser(),
                    onSignOut = {
                        coroutineScope.launch {
                            googleAuthUiClient.signOut()
                            Toast.makeText(
                                context,
                                "signed out",
                                Toast.LENGTH_LONG
                            ).show()

                            // Navigasi kembali ke WelcomeScreen setelah sign-out
                            navController.navigate(Destinations.WelcomeScreen.route) {
                                // Menghapus semua rute sebelumnya dari stack
                                popUpTo(Destinations.WelcomeScreen.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // Result Screen
            composable(
                route = "ResultScreen/{predictionResult}/{confidence}",
                arguments = listOf(
                    navArgument("predictionResult") { type = NavType.StringType },
                    navArgument("confidence") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val predictionResult = backStackEntry.arguments?.getString("predictionResult") ?: "Unknown"
                val confidence = backStackEntry.arguments?.getFloat("confidence") ?: 0.0f

                ResultScreen(
                    navController = navController,
                    predictionResult = predictionResult,
                    confidence = confidence
                )
            }
        }
    }
}
