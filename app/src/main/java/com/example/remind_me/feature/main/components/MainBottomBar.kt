package com.example.remind_me.feature.main.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.remind_me.core.theme.RemindMeTheme

@Composable
fun MainBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = colors.primary,
        selectedTextColor = colors.primary,
        indicatorColor = colors.primaryContainer,
        unselectedIconColor = colors.textSecondary,
        unselectedTextColor = colors.textSecondary
    )

    NavigationBar(
        containerColor = colors.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = strings.tabOverview) },
            label = { Text(strings.tabOverview) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = strings.tabTopics) },
            label = { Text(strings.tabTopics) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Quiz, contentDescription = strings.tabPractice) },
            label = { Text(strings.tabPractice) },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = itemColors
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = strings.tabSettings) },
            label = { Text(strings.tabSettings) },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = itemColors
        )
    }
}

