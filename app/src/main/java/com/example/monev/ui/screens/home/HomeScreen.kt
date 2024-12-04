package com.example.monev.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.monev.R
import com.example.monev.ui.components.home.CardHome
import com.example.monev.ui.theme.poppins


@Composable
fun HomeScreen(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme

    // List bulan dan jumlah
    val months = listOf(
        "Januari" to "Rp. 100.000",
        "Februari" to "Rp. 150.000",
        "Maret" to "Rp. 200.000",
        "April" to "Rp. 250.000",
        "Mei" to "Rp. 300.000",
        "Juni" to "Rp. 350.000",
        "Juli" to "Rp. 400.000",
        "Agustus" to "Rp. 450.000",
        "September" to "Rp. 500.000",
        "Oktober" to "Rp. 550.000",
        "November" to "Rp. 600.000",
        "Desember" to "Rp. 650.000"
    )

    // Menggunakan LazyListState untuk LazyColumn
    val lazyListState = rememberLazyListState()

    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(start = 30.dp, end = 30.dp) // Hapus paddingTop
            ) {
                // LazyColumn untuk scroll
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState // Menggunakan LazyListState
                ) {
                    item {
                        // Menyesuaikan padding top berdasarkan scroll position
                        val paddingTop = if (lazyListState.firstVisibleItemIndex > 0) 0.dp else 40.dp

                        // Bagian atas (header dengan teks)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = paddingTop), // Apply paddingTop
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                        modifier = Modifier.padding(0.dp)
                            ) {
                                Text(
                                    text = "Andi",
                                    color = colorScheme.primary,
                                    fontSize = 30.sp,
                                    fontFamily = poppins
                                )
                                Text(
                                    text = "Selamat Datang",
                                    color = colorScheme.tertiary,
                                    fontFamily = poppins,
                                    fontSize = 25.sp
                                )
                            }

                            // icon bundar
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                // Ganti Icon dengan gambar dari drawable
                                Image(
                                    painter = painterResource(id = R.drawable.guide),
                                    contentDescription = "Menu Icon",
                                    modifier = Modifier.size(24.dp) // Ukuran gambar
                                )
                            }
                        }
                    }

                    item {
                        // box yang kamu ingin tampilkan di atas
                        Box(
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth()
                                .background(Color.Red)
                        ) {

                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(50.dp)) // spacer
                    }

                    item {
                        // Judul History
                        Text(
                            text = "History Bulan Ini",
                            fontFamily = poppins,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Menampilkan semua card dari list
                    items(months) { (month, amount) ->
                        CardHome(month = month, amount = amount, navController = navController)
                    }
                }
            }
        }
    )
}
