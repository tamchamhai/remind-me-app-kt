package com.example.remind_me.feature.topic_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Whatshot
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

@Composable
fun TopicListHeader(
    title: String = "RemindMe",
    streak: Int = 0,
    onPreviewLockScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer)
                    .clickable { onPreviewLockScreen() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockClock,
                    contentDescription = strings.previewLockScreenNow,
                    tint = colors.onPrimaryContainer
                )
            }
        }
    }
}
