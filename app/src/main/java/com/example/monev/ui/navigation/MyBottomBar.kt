import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.monev.ui.navigation.Destinations
import androidx.compose.runtime.remember as remember1

@Composable
fun MyBottomBar(
    navController: NavController
) {
    val selectedRoute = remember1 { mutableStateOf(Destinations.HomeScreen.route) }

    NavigationBar(
        containerColor = Color(0xFFF3F4F9),
        tonalElevation = 8.dp
    ) {
        // Home Item
        NavigationBarItem(
            selected = selectedRoute.value == Destinations.HomeScreen.route,
            onClick = {
                selectedRoute.value = Destinations.HomeScreen.route
                navController.navigate(Destinations.HomeScreen.route) {
                    popUpTo(Destinations.HomeScreen.route) { inclusive = true }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Black,
                unselectedTextColor = Color.Black
            )
        )

        // Settings Item
        NavigationBarItem(
            selected = selectedRoute.value == Destinations.SettingScreen.route,
            onClick = {
                selectedRoute.value = Destinations.SettingScreen.route
                navController.navigate(Destinations.SettingScreen.route) {
                    popUpTo(Destinations.SettingScreen.route) { inclusive = true }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            label = {
                Text(
                    text = "Settings",
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Black,
                unselectedTextColor = Color.Black
            )
        )
    }
}