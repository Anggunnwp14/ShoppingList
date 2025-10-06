package com.example.shoppinglist.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shoppinglist.components.ItemInput
import com.example.shoppinglist.components.SearchInput
import com.example.shoppinglist.components.ShoppingList
import com.example.shoppinglist.components.Title
import com.example.shoppinglist.ui.theme.HomeScreen
import com.example.shoppinglist.ui.theme.ProfileScreen
import com.example.shoppinglist.ui.theme.SettingScreen
import com.example.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListTheme {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val currentScreen = when (currentRoute) {
                    Screen.Home.route -> Screen.Home
                    Screen.Profile.route -> Screen.Profile
                    Screen.Setting.route -> Screen.Setting
                    else -> Screen.Home
                }

                val bottomBarItems = listOf(Screen.Home, Screen.Profile)

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text("Menu Aplikasi", modifier = Modifier.padding(16.dp))
                            Divider()
                            NavigationDrawerItem(
                                label = { Text(Screen.Setting.title) },
                                selected = currentRoute == Screen.Setting.route,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate(Screen.Setting.route)
                                },
                                icon = {
                                    Icon(Screen.Setting.icon, contentDescription = Screen.Setting.title)
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(currentScreen.title) },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            NavigationBar {
                                bottomBarItems.forEach { screen ->
                                    NavigationBarItem(
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(screen.icon, contentDescription = screen.title)
                                        },
                                        label = { Text(screen.title) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color(0xFF81C784),
                                            selectedTextColor = Color(0xFF81C784),
                                            indicatorColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                            unselectedIconColor = Color.Gray,
                                            unselectedTextColor = Color.Gray
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(
                                Screen.Home.route,
                                enterTransition = { fadeIn(animationSpec = tween(300)) },
                                exitTransition = { fadeOut(animationSpec = tween(300)) }
                            ) {
                                HomeScreen(navController)
                            }
                            composable(
                                Screen.Profile.route,
                                enterTransition = { fadeIn(animationSpec = tween(300)) },
                                exitTransition = { fadeOut(animationSpec = tween(300)) }
                            ) {
                                ProfileScreen()
                            }
                            composable(
                                Screen.Setting.route,
                                enterTransition = { fadeIn(animationSpec = tween(300)) },
                                exitTransition = { fadeOut(animationSpec = tween(300)) }
                            ) {
                                SettingScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShoppingListApp() {
    var newItemText by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val shoppingItems = remember { mutableStateListOf<String>() }

    val filteredItems by remember(searchQuery, shoppingItems) {
        derivedStateOf {
            if (searchQuery.isBlank()) shoppingItems
            else shoppingItems.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(horizontal = 16.dp)
    ) {
        Title()
        ItemInput(
            text = newItemText,
            onTextChange = {
                val it = ""
                newItemText = it
            },
            onAddItem = {
                if (newItemText.isNotBlank()) {
                    shoppingItems.add(newItemText)
                    newItemText = ""
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SearchInput(query = searchQuery, onQueryChange = { searchQuery = it })
        Spacer(modifier = Modifier.height(16.dp))
        ShoppingList(items = filteredItems)
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListAppPreview() {
    ShoppingListTheme {
        ShoppingListApp()
    }
}

// 🧭 Navigasi antar screen
sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Shopping List", Icons.Default.Home)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Setting : Screen("setting", "Settings", Icons.Default.Settings)
}
