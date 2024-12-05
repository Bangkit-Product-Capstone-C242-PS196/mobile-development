//package com.example.monev.ui.screens.auth
//
//import android.app.Activity.RESULT_OK
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.monev.sign_in.GoogleAuthUiClient
//import com.example.monev.sign_in.SignInScreen
//import com.example.monev.viewmodel.auth.SignInViewModel
//import com.example.monev.ui.navigation.Destinations
//import com.example.monev.ui.screens.home.HomeScreen
//import com.example.monev.ui.screens.profile.ProfileScreen
//import com.google.android.gms.auth.api.identity.Identity
//import kotlinx.coroutines.launch
//
//@Composable
//fun RegisLoginScreen() {
//    val context = LocalContext.current
//    val googleAuthUiClient = GoogleAuthUiClient(
//        context = context.applicationContext,
//        oneTapClient = Identity.getSignInClient(context.applicationContext)
//    )
//
//    val navController = rememberNavController()
//    val coroutineScope = rememberCoroutineScope()  // Menggunakan rememberCoroutineScope()
//
//    NavHost(navController = navController, startDestination = "sign_in") {
//        composable("sign_in") {
//            val viewModel = viewModel<SignInViewModel>()
//            val state = viewModel.state.collectAsStateWithLifecycle().value
//
//            LaunchedEffect(key1 = Unit) {
//                if (googleAuthUiClient.getSignedInUser() != null) {
//                    navController.navigate("profile")
//                }
//            }
//
//            val launcher = rememberLauncherForActivityResult(
//                contract = ActivityResultContracts.StartIntentSenderForResult(),
//                onResult = { result ->
//                    if (result.resultCode == RESULT_OK) {
//                        // Memanggil fungsi suspend dari dalam coroutine
//                        coroutineScope.launch {  // Gunakan coroutineScope di sini
//                            val signInResult = googleAuthUiClient.SignInWithIntent(
//                                intent = result.data ?: return@launch
//                            )
//                            viewModel.onSignInResult(signInResult)
//                        }
//                    }
//                }
//            )
//
//            LaunchedEffect(key1 = state.isSignInSuccesful) {
//                if (state.isSignInSuccesful) {
//                    Toast.makeText(
//                        context,
//                        "Sign In Sukses",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    navController.navigate(Destinations.HomeScreen.route)
//                    viewModel.resetState()
//                }
//            }
//
//            SignInScreen(
//                state = state,
//                onSignInClick = {
//                    coroutineScope.launch {
//                        val signInIntentSender = googleAuthUiClient.signIn()
//                        launcher.launch(
//                            IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
//                        )
//                    }
//                }
//            )
//        }
//
//        composable("profile") {
//            ProfileScreen(
//                userData = googleAuthUiClient.getSignedInUser(),
//                onSignOut = {
//                    coroutineScope.launch {  // Gunakan coroutineScope di sini
//                        googleAuthUiClient.signOut()
//                        Toast.makeText(
//                            context,
//                            "signed out",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        navController.popBackStack()
//                    }
//                }
//            )
//        }
//
//    }
//}
