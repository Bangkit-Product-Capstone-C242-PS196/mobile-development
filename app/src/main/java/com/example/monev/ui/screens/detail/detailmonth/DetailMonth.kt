package com.example.monev.ui.screens.detail.detailmonth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun DetailMonthScreen(month: String?, amount: String?) {
    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Log untuk melihat data yang diterima
                Log.d("DetailMonth", "Received: Month = $month, Amount = $amount")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Detail untuk bulan $month",
                        fontSize = 30.sp
                    )
                    Text(
                        text = "Jumlah: $amount",
                        fontSize = 25.sp
                    )
                }
            }
        }
    )
}
