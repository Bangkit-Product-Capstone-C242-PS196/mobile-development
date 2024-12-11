import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.monev.ui.navigation.Destinations

@Composable
fun MyBottomBar(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val currentDestination = navController.currentBackStackEntryAsState().value

    NavigationBar(
        containerColor = colorScheme.surfaceVariant,
        tonalElevation = 8.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .shadow(8.dp)
    ) {
        val navigationItems = listOf(
            NavigationItem(
                route = Destinations.HomeScreen.route,
                icon = Icons.Default.Home,
                label = "Beranda"
            ),
            NavigationItem(
                route = Destinations.ChatbotScreen.route,
                icon = Icons.Default.Face,
                label = "Bot"
            ),
            NavigationItem(
                route = Destinations.SettingScreen.route,
                icon = Icons.Default.Settings,
                label = "Pengaturan"
            )
        )

        navigationItems.forEach { item ->
            val isSelected = currentDestination?.destination?.route == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.scale(if (isSelected) 1.2f else 1f)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colorScheme.onSurfaceVariant,
                    selectedTextColor = colorScheme.primary,
                    unselectedIconColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    unselectedTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .animateContentSize()
            )
        }
    }
}

// Helper data class for navigation items
data class NavigationItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)