package com.example.monev.ui.screens.chatbot

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import com.google.ai.client.generativeai.type.GenerateContentResponse
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import com.google.ai.client.generativeai.BuildConfig

@Composable
fun ChatbotScreen() {
    val context = LocalContext.current

    // Initialize Text-to-Speech setup
    val tts = remember { TextToSpeech(context) { } }

    // Initialize SpeechRecognizer
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    // Variable for animation while bot is speaking
    var isSpeaking by remember { mutableStateOf(false) }

    // Animations for "wave" effect
    val size by animateDpAsState(
        targetValue = if (isSpeaking) 170.dp else 150.dp, // Increase size during speech
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val opacity by animateFloatAsState(
        targetValue = if (isSpeaking) 0.6f else 1f, // Opacity decreases when speaking
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Launcher for Speech Input
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                // If there's a match, speak the result using TextToSpeech
                val userInput = matches[0]
                val prompt = "Kau adalah seorang developer aplikasi monev. Aplikasi yang bertujuan untuk tunanetra dalam melakukan scan nilai mata uang melalui kamera. Client bertanya: \"$userInput\""

                // Menggunakan API Gemini untuk menghasilkan response dengan prompt yang disesuaikan
                MainScope().launch {
                    try {
                        // Menggunakan API Gemini untuk menghasilkan response
                        val aiResponse: GenerateContentResponse = generativeModel.generateContent(prompt)
                        val aiText = aiResponse.text?.replace("*", "") ?: "Maaf, saya tidak dapat menghasilkan respons."

                        // Menyuarakan response menggunakan TTS
                        isSpeaking = true // Start animation when bot is speaking
                        tts.speak(aiText, TextToSpeech.QUEUE_FLUSH, null, null)

                        // After TTS finishes speaking, stop the animation
                        delay(aiText.length * 100L) // Wait for approximate duration of the speech
                        isSpeaking = false

                    } catch (e: Exception) {
                        val errorMessage = "Terjadi kesalahan: ${e.message}"
                        tts.speak(errorMessage, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            }
        }
    }

    // Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF121212)
                    )
                )
            )
    ) {
        // Centering the button in the middle of the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // The large button to trigger speech input
            Box(
                modifier = Modifier
                    .size(size) // Use the animated size here
                    .alpha(opacity) // Use animated opacity here
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4285F4),
                                Color(0xFF34A853)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable {
                        // Stop current speaking before handling new user input
                        tts.stop()

                        // Play notification sound indicating that the microphone is active
                        tts.speak("Mikrofon aktif. Silakan berbicara.", TextToSpeech.QUEUE_FLUSH, null, null)

                        // Add a small delay before starting the microphone recording
                        MainScope().launch {
                            delay(3000) // Wait for 2 seconds to allow the TTS message to finish

                            // Launch speech input after delay
                            speechLauncher.launch(speechRecognizerIntent)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Voice Input",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp) // Adjust icon size for better visibility
                )
            }
        }
    }
}

// Initialize Gemini API client
val generativeModel = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = ""
)
