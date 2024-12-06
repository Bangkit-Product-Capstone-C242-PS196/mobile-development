package com.example.monev.ui.screens.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.monev.R
import com.example.monev.viewmodel.history.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ListHistoryScreen(
    modifier: Modifier = Modifier,
    dataViewModel: HistoryViewModel = viewModel(),
    navController: NavController
) {
    // Mengamati list data dari ViewModel
    val getData by dataViewModel.histories.collectAsState()

    // Menampilkan daftar history dengan LazyColumn
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(getData) { history ->
            // Menampilkan setiap history dalam List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (history.photo.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = history.photo,
                            placeholder = painterResource(id = R.drawable.ic_launcher_background), // Gambar placeholder jika gagal
                            error = painterResource(id = R.drawable.ic_launcher_background) // Gambar error jika gagal memuat
                        ),
                        contentDescription = "History Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Menampilkan Nominal
                Text(text = "Nominal: ${history.nominal}")

                // Menampilkan tanggal dengan format yang lebih manusiawi
                val formattedDate = try {
                    // Pastikan history.date adalah angka yang valid sebelum dikonversi ke Date
                    val timestamp = history.date.toLongOrNull() ?: 0L
                    val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date(timestamp)) // Mengonversi timestamp menjadi format tanggal
                    formatted
                } catch (e: Exception) {
                    // Jika terjadi kesalahan, tampilkan fallback text
                    "Invalid Date"
                }

                Text(text = "Date: $formattedDate")

                // Menampilkan link foto
                Text(text = "Link Photo: ${history.photo}")
            }
        }
    }
}

