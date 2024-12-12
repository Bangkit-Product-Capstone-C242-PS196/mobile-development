package com.example.monev.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.monev.R
import com.example.monev.sign_in.SignInState

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val activity = context as? Activity
    // Tangani tombol back
    BackHandler {
        activity?.finishAffinity()  // Menutup aplikasi sepenuhnya
    }

    // Menggunakan key1 yang berupa state.signInError
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Silahkan\nLogin",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .semantics {
                            contentDescription = "Silahkan login terlebih dahulu dengan klik tombol yang ada di bawah"
                        }
                )
                Text(
                    text = buildAnnotatedString {
                        append("Silahkan login terlebih dahulu")
                        append(" dengan klik tombol yang ada di pojok bawah")
                    },
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.login_image),
                contentDescription = "Welcome Character",
                modifier = Modifier
                    .size(300.dp)

            )

            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Login with Google",
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.primary
                    ),
                    modifier = Modifier
                        .semantics {
                            contentDescription = "Klik untuk login via akun google"
                        }
                )
            }
        }
    }
}