package com.example.monev.ui.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.monev.R

// Data class untuk anggota tim
data class TeamMemberData(
    val name: String,
    val linkedin: String,
    val path: String,
    val image: Int
)

@Composable
fun AboutScreen(
    navController: NavController
) {
    // Mendapatkan context dengan benar
    val context = LocalContext.current

    // Daftar anggota tim
    val teamMembers = listOf(
        TeamMemberData(
            name = "Mochamad Rizki Rachman",
            linkedin = "https://www.linkedin.com/in/rizqi-rahcman/?lipi=urn%3Ali%3Apage%3Ad_flagship3_people_connections%3BS1dQdw4ORDWNprd4cnwhUQ%3D%3D",
            path = "Mobile Development",
            image = R.drawable.rahman
        ),
        TeamMemberData(
            name = "Arul Hidayat",
            linkedin = "https://www.linkedin.com/in/arul-hidayat-b71a10308/?lipi=urn%3Ali%3Apage%3Ad_flagship3_people_connections%3BS1dQdw4ORDWNprd4cnwhUQ%3D%3D",
            path = "Machine Learning",
            image = R.drawable.arul
        ),
        TeamMemberData(
            name = "Zamachsyafi Shidqi Athallah",
            linkedin = "https://www.linkedin.com/in/zamachsyafi-shidqi-athallah/?lipi=urn%3Ali%3Apage%3Ad_flagship3_people_connections%3BS1dQdw4ORDWNprd4cnwhUQ%3D%3D",
            path = "Machine Learning",
            image = R.drawable.dika
        ),
        TeamMemberData(
            name = "Fauzan Dwi Eryawan",
            linkedin = "https://www.linkedin.com/in/fauzan-dwi-eryawan-43a095250/?lipi=urn%3Ali%3Apage%3Ad_flagship3_people_connections%3BS1dQdw4ORDWNprd4cnwhUQ%3D%3D",
            path = "Cloud Computing",
            image = R.drawable.dika
        ),
        TeamMemberData(
            name = "Raihan Muhammad Rizki Rahman",
            linkedin = "https://www.linkedin.com/in/raihanmuhammadrr/?lipi=urn%3Ali%3Apage%3Ad_flagship3_people_connections%3BS1dQdw4ORDWNprd4cnwhUQ%3D%3D",
            path = "Cloud Computing",
            image = R.drawable.dika
        ),
        TeamMemberData(
            name = "Adika Akbar Kurniawan",
            linkedin = "https://www.linkedin.com/in/adika-akbar-kurniawan/?lipi=urn%3Ali%3Apage%3Ad_flagship3_people_connections%3BS1dQdw4ORDWNprd4cnwhUQ%3D%3D",
            path = "Mobile Development",
            image = R.drawable.dika
        ),TeamMemberData(
            name = "Ahmad Aziz Fauzi",
            linkedin = "https://www.linkedin.com/in/ahmadazizfauzi/",
            path = "Mobile Development",
            image = R.drawable.aziz
        ),


    )

    // Menambahkan scrollable content jika konten melebihi layar
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gambar utama tanpa ruang terpisah
        Image(
            painter = painterResource(id = R.drawable.monev),
            contentDescription = "Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )

        // Judul
        Text(
            text = "Monev",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
        )

        // Deskripsi
        Text(
            text = "Aplikasi yang bertujuan untuk tunanetra dalam melakukan scan nilai mata uang melalui kamera, yang nanti akan memberikan suara untuk nominal mata uang yang discan, sehingga memudahkan dalam jual beli dan memvalidasi nilai mata uang para tunanetra.",
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        )

        // Tabel Anggota Tim
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Anggota Tim",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mengiterasi daftar anggota tim dan menampilkan masing-masing anggota
            teamMembers.forEach { member ->
                TeamMember(member = member)
            }
        }
    }
}

@Composable
fun TeamMember(member: TeamMemberData) {
    // Mendapatkan context dengan benar
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gambar Anggota Tim
        Image(
            painter = painterResource(id = member.image),
            contentDescription = "Team Member",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Nama, Path, dan Icon LinkedIn
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = member.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )

            Text(
                text = member.path,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // Ikon LinkedIn yang dapat diklik
                Image(
                    painter = painterResource(id = R.drawable.ic_linkedin),
                    contentDescription = "LinkedIn",
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            // Buka link LinkedIn saat ikon diklik
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(member.linkedin))
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}

