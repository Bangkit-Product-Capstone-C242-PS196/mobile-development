package com.example.monev.ui.screens.chatbot

import MyBottomBar
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.monev.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(navController: NavController) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // Initialize Text-to-Speech setup
    val tts = remember { TextToSpeech(context) { } }

    // Initialize SpeechRecognizer
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    // State variables for more dynamic interactions
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }

    // Advanced color animation
    val colorTransition = rememberInfiniteTransition(label = "")
    val color1 by colorTransition.animateColor(
        initialValue = colorScheme.primary,
        targetValue = colorScheme.secondary,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val color2 by colorTransition.animateColor(
        initialValue = colorScheme.secondary,
        targetValue = colorScheme.tertiary,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, delayMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val color3 by colorTransition.animateColor(
        initialValue = colorScheme.tertiary,
        targetValue = colorScheme.primary,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, delayMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Advanced animations for more fluid interactions
    val breathingAnimation = rememberInfiniteTransition(label = "")
    val size by breathingAnimation.animateFloat(
        initialValue = 150f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scaleAnimation by breathingAnimation.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Launcher for Speech Input
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                isListening = false
                isProcessing = true

                val userInput = matches[0]
                val prompt = "Kau adalah seorang developer aplikasi monev. Aplikasi yang bertujuan untuk tunanetra dalam melakukan scan nilai mata uang melalui kamera. Client bertanya: \"$userInput\""

                MainScope().launch {
                    try {
                        val aiResponse: GenerateContentResponse = generativeModel.generateContent(prompt)
                        val aiText = aiResponse.text?.replace("*", "") ?: "Maaf, saya tidak dapat menghasilkan respons."

                        isProcessing = false
                        isSpeaking = true

                        tts.speak(aiText, TextToSpeech.QUEUE_FLUSH, null, null)

                        // Automatically stop speaking after text is complete
                        delay(aiText.length * 100L)
                        isSpeaking = false

                    } catch (e: Exception) {
                        isProcessing = false
                        val errorMessage = "Terjadi kesalahan: ${e.message}"
                        tts.speak(errorMessage, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            }
        }
    }

    // Main Layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CHATBOT",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.background
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Central Button
                Box(
                    modifier = Modifier
                        .size(size.dp)
                        .scale(if (!isListening && !isSpeaking && !isProcessing) scaleAnimation else 1f)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(color1, color2, color3, color1)
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            // Reset all states
                            tts.stop()
                            isListening = false
                            isSpeaking = false
                            isProcessing = false

                            // Prepare for speech input
                            tts.speak("Mikrofon aktif. Silakan berbicara.", TextToSpeech.QUEUE_FLUSH, null, null)

                            MainScope().launch {
                                delay(2000)
                                isListening = true
                                speechLauncher.launch(speechRecognizerIntent)
                            }
                        }
                )
            }
        }
    }
}

// Initialize Gemini API client
val generativeModel = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = BuildConfig.API_KEY
)