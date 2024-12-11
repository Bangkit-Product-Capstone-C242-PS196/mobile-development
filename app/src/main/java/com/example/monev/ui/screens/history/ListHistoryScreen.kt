package com.example.monev.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import android.R
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.monev.data.model.History
import com.example.monev.viewmodel.history.HistoryViewModel
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListHistoryScreen(
    modifier: Modifier = Modifier,
    dataViewModel: HistoryViewModel = viewModel(),
    navController: NavController
) {

    // warna
    val colorScheme = MaterialTheme.colorScheme

    val allData by dataViewModel.histories.collectAsState()

    // State untuk dropdown menu
    var expandedMonth by remember { mutableStateOf(false) }
    var selectedMonth by remember { mutableStateOf("All") }

    // State untuk dropdown tanggal
    var expandedDate by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("All") }

    // Daftar bulan
    val months = listOf(
        "All", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    // Daftar tanggal
    val days = (1..31).map { it.toString() }

    // Filter data berdasarkan bulan dan tanggal yang dipilih
    val filteredData = allData.filter { history ->
        val timestamp = history.date.toLongOrNull() ?: 0L
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }

        val matchMonth = if (selectedMonth == "All") {
            true
        } else {
            val monthNumber = getMonthNumber(selectedMonth)
            calendar.get(Calendar.MONTH) + 1 == monthNumber
        }

        val matchDate = if (selectedDate == "All") {
            true
        } else {
            calendar.get(Calendar.DAY_OF_MONTH).toString() == selectedDate
        }

        matchMonth && matchDate
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Riwayat Scan", color = colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Points Card
//            PointsCard(totalItems = allData.size)

            // Header dengan Dropdown Menu untuk Bulan dan Tanggal
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Filter Bulan
                Box {
                    OutlinedButton(
                        onClick = { expandedMonth = true },
                        modifier = Modifier
                            .width(150.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(text = selectedMonth)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false },
                        modifier = Modifier
                            .width(150.dp)
                    ) {
                        months.forEach { month ->
                            DropdownMenuItem(
                                text = { Text(text = month) },
                                onClick = {
                                    selectedMonth = month
                                    expandedMonth = false
                                }
                            )
                        }
                    }
                }

                // Filter Tanggal
                Box {
                    OutlinedButton(
                        onClick = { expandedDate = true },
                        modifier = Modifier
                            .width(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(text = selectedDate)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expandedDate,
                        onDismissRequest = { expandedDate = false },
                        modifier = Modifier
                            .width(100.dp)
                    ) {
                        days.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(text = day) },
                                onClick = {
                                    selectedDate = day
                                    expandedDate = false
                                }
                            )
                        }
                    }
                }

                // reset
                Box {
                    // Tombol Reset Filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            selectedMonth = "All"
                            selectedDate = "All"
                        }) {
                            Text("Reset Filter")
                        }
                    }
                }
            }

            // Header Teks
            Text(
                text = "Riwayat Scan",
                color = colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Daftar History
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredData) { history ->
                    HistoryItem(history)
                }
            }
        }
    }
}

@Composable
fun PointsCard(totalItems: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Your Points",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "700 points",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "Latest update: 5 March, 2023",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun HistoryItem(history: History) {
    // Deskripsi konten untuk TalkBack
    var contentDescription = "Nominal: ${history.nominal} , Confidence: ${"%.2f".format(history.confidence * 100)}%, Tanggal: ${formatDate(history.date)}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {  })
            .semantics {
                contentDescription = contentDescription
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Menampilkan Nominal
            Text(
                text = "Nominal: ${history.nominal} ",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Menampilkan Confidence
            Text(
                text = "Confidence: ${"%.2f".format(history.confidence * 100)}%",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )

            // Menampilkan Tanggal
            val formattedDate = formatDate(history.date)
            Text(
                text = "Tanggal: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Fungsi untuk memformat tanggal menjadi string yang mudah dibaca
 */
fun formatDate(date: String): String {
    return try {
        val timestamp = date.toLongOrNull() ?: 0L
        val sdf = java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Invalid Date"
    }
}


// Fungsi untuk mendapatkan nomor bulan dari nama bulan
fun getMonthNumber(monthName: String): Int? {
    return when (monthName) {
        "Januari" -> 1
        "Februari" -> 2
        "Maret" -> 3
        "April" -> 4
        "Mei" -> 5
        "Juni" -> 6
        "Juli" -> 7
        "Agustus" -> 8
        "September" -> 9
        "Oktober" -> 10
        "November" -> 11
        "Desember" -> 12
        else -> null
    }
}
