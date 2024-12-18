package com.example.monev.ui.screens.welcome

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monev.R

@Composable
fun WelcomeScreen(
    onNextClick: () -> Unit
) {
    val colorScheme = colorScheme

    val context = LocalContext.current
    val activity = context as? Activity
    // Tangani tombol back
    BackHandler {
        activity?.finishAffinity()  // Menutup aplikasi sepenuhnya
    }

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
                    text = "Selamat\nDatang",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Monev")
                        }
                        append(", aplikasi untuk tunanetra yang")
                        append(" mendeteksi nilai mata uang dengan fitur scan dan text-to-speech.")
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
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "Welcome Character",
                modifier = Modifier
                    .size(450.dp)
                    .padding(vertical = 32.dp)
            )

            Button(
                onClick = onNextClick,
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
                    text = "Selanjutnya",
                    fontWeight = FontWeight.Bold,

                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.primary
                    )
                )
            }
        }
    }
}

