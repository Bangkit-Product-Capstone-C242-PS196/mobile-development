package com.example.monev.ui.screens.chatbot

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.material.icons.filled.Person
import com.example.monev.BuildConfig
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.ResponseStoppedException

@Composable
fun ChatbotScreenDua() {
    var userInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Text-to-Speech setup
    val tts = remember { TextToSpeech(context) { } }

    // Initialize Gemini API client
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = BuildConfig.API_KEY
    )

    fun sendMessage() {
        if (userInput.isNotEmpty()) {
            // Menggabungkan input pengguna dengan prompt khusus dalam bahasa Indonesia
            val prompt = "Kau adalah seorang developer aplikasi monev. Aplikasi yang bertujuan untuk tunanetra dalam melakukan scan nilai mata uang melalui kamera, yang nanti akan memberikan suara untuk nominal mata uang yang discan, sehingga memudahkan dalam jual beli dan memvalidasi nilai mata uang para tunanetra. kau selalu merespon pertanyaan client dengan singkat dan sesuai pengtahuanmu mengenai monev. jika user mengajukan pertanyaan diluar monev, ya tetep kau jawab sesuai pengetahuan yang ada gausah dibatasi mengenai monev doang. untuk menggunakannya cukup akses ikon kamera di tengah layar di halaman beranda, lalu foto uang dan akan muncul suara  nominal uang itu  .Client bertanya: \"$userInput\""

            messages = messages + Message(userInput, MessageType.USER)
            userInput = ""

            MainScope().launch {
                try {
                    if (tts.isSpeaking) {
                        tts.stop()
                    }

                    // Menggunakan API Gemini untuk menghasilkan response dengan prompt yang disesuaikan
                    val aiResponse: GenerateContentResponse = generativeModel.generateContent(prompt)

                    // Mengakses properti 'text' untuk mendapatkan konten yang dihasilkan
                    val aiText = aiResponse.text ?: "Maaf, saya tidak dapat menghasilkan respons."

                    // Menambahkan respons dari AI ke dalam pesan
                    messages = messages + Message(aiText, MessageType.AI)
                    tts.speak(aiText, TextToSpeech.QUEUE_FLUSH, null, null)
                } catch (e: ResponseStoppedException) {
                    val warningMessage = "Pembuatan konten dihentikan karena alasan keamanan."
                    messages = messages + Message(warningMessage, MessageType.AI)
                    tts.speak(warningMessage, TextToSpeech.QUEUE_FLUSH, null, null)
                } catch (e: Exception) {
                    val errorMessage = "Terjadi kesalahan: ${e.message}"
                    messages = messages + Message(errorMessage, MessageType.AI)
                    tts.speak(errorMessage, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }


    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechRecognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                userInput = matches[0]
                sendMessage()
            }
        }
    }



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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gemini AI",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { messages = emptyList() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), shape = CircleShape)
                ) {
                    Icon(
                        Icons.Outlined.Clear,
                        contentDescription = "Clear Chat",
                        tint = Color.White
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages.size) { index ->
                    val message = messages[index]
                    MessageBubble(message)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    placeholder = {
                        Text(
                            "Type a message...",
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4285F4),
                                    Color(0xFF34A853)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable { sendMessage() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4285F4),
                                    Color(0xFF34A853)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable { speechLauncher.launch(speechRecognizerIntent) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Voice Input",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

// Enum for message type
enum class MessageType {
    USER, AI
}

// Data class for message
data class Message(
    val text: String,
    val type: MessageType
)

@Composable
fun MessageBubble(message: Message) {
    // Chat bubble with different design for user and AI
    val isUserMessage = message.type == MessageType.USER

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = if (isUserMessage)
                        Color(0xFF4285F4).copy(alpha = 0.7f)
                    else
                        Color.Gray.copy(alpha = 0.3f)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                modifier = Modifier.alpha(0.9f)
            )
        }
    }
}