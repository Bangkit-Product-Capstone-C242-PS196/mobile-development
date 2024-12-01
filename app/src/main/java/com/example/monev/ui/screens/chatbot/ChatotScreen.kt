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
import androidx.compose.material.icons.filled.Mic
import com.google.ai.client.generativeai.type.ResponseStoppedException

@Composable
fun ChatbotScreen() {
    var userInput by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val tts = remember { TextToSpeech(context) { } }
    val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "keymu kang"
    )

    val predefinedPrompts = mapOf(
        "Hello" to "Hi there! How can I help you today?",
        "How are you?" to "I'm an AI, so I don't have feelings, but thanks for asking!",
        "What is your name?" to "I am Gemini AI, your virtual assistant."
    )
    fun generateResponse(prompt: String): String {
        return predefinedPrompts[prompt] ?: "I'm sorry, I don't understand that prompt."
    }
    fun sendMessage() {
        if (userInput.isNotEmpty()) {
            val prompt = userInput
            messages = messages + Message(prompt, MessageType.USER)
            userInput = ""

            MainScope().launch {
                try {
                    if (tts.isSpeaking) {
                        tts.stop()
                    }
                    val aiResponse = generateResponse(prompt)
                    messages = messages + Message(aiResponse, MessageType.AI)
                    tts.speak(aiResponse, TextToSpeech.QUEUE_FLUSH, null, null)
                } catch (e: ResponseStoppedException) {
                    val warningMessage = "Content generation stopped due to safety reasons."
                    messages = messages + Message(warningMessage, MessageType.AI)
                    tts.speak(warningMessage, TextToSpeech.QUEUE_FLUSH, null, null)
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
                        Icons.Default.Mic,
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