package com.example.remind_me.feature.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.feature.topic_list.components.TopicListSearchBar

@Composable
fun MainHeader(
    title: String,
    streak: Int,
    isSearchActive: Boolean,
    searchQuery: String,
    onToggleSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Icon Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSearchActive) colors.primary else colors.primaryContainer
                            )
                            .clickable { onToggleSearch() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = strings.searchPlaceholder,
                            tint = if (isSearchActive) {
                                if (colors.isDark) Color(0xFF00252A) else Color.White
                            } else {
                                colors.onPrimaryContainer
                            }
                        )
                    }

                    // Streak Badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = colors.streakContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = strings.streakLabel,
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "$streak",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = colors.streakText
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isSearchActive) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    TopicListSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        placeholder = strings.searchPlaceholder
                    )
                }
            }
        }
    }
}
