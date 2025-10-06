package com.example.shoppinglist.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shoppinglist.components.ItemInput
import com.example.shoppinglist.components.SearchInput
import com.example.shoppinglist.components.ShoppingList

@Composable
fun HomeScreen(navController: NavController) {
    var newItemText by rememberSaveable { mutableStateOf("") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val shoppingItems = remember { mutableStateListOf<String>() }

    val filteredItems by remember(searchQuery, shoppingItems) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                shoppingItems
            } else {
                shoppingItems.filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Input item baru
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

        // Input pencarian
        SearchInput(
            query = searchQuery,
            onQueryChange = {
                val it = ""
                searchQuery = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Daftar belanja
        ShoppingList(items = filteredItems)
    }
}
