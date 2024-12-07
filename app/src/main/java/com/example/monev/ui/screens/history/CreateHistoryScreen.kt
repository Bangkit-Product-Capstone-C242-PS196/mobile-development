package com.example.monev.ui.screens.history

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.monev.data.model.History
import com.example.monev.viewmodel.history.HistoryViewModel

@Composable
fun CreateHistoryScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    dataViewModel: HistoryViewModel = viewModel()
) {
    // State untuk menyimpan input
    var nominal by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf("") }



    val context = LocalContext.current

    fun handleSubmit() {
        if (nominal.isNotBlank() && photo.isNotBlank()) {
            // Membuat objek History tanpa firestoreId dan userId
            val history = History(
                nominal = nominal,
                date = System.currentTimeMillis().toString(), // Mengonversi Long ke String
                photo = photo
            )
            dataViewModel.addHistory(history)

            // Tampilkan Toast setelah berhasil menyimpan data
            Toast.makeText(context, "History saved successfully", Toast.LENGTH_SHORT).show()

            // Reset input field setelah data berhasil disubmit
            nominal = ""
            photo = ""
        } else {
            // Tampilkan pesan error jika ada field kosong
            Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
        }
    }


    // UI untuk input name, age, dan passion
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Input untuk name
        TextField(
            value = nominal,
            onValueChange = { nominal = it },
            label = { Text("Nominal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Input untuk passion
        TextField(
            value = photo,
            onValueChange = { photo = it },
            label = { Text("link photo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol submit
        Button(
            onClick = { handleSubmit() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Submit")
        }
    }
}
