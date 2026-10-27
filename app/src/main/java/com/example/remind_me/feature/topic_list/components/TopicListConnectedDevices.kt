package com.example.remind_me.feature.topic_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
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
fun TopicListConnectedDevices(
    isServiceEnabled: Boolean = false,
    hasOverlayPermission: Boolean = true,
    triggerEveryNUnlocks: Int = 1,
    onToggleService: (Boolean) -> Unit = {},
    onRequestOverlayPermission: () -> Unit = {},
    onPreviewLockScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isServiceEnabled) colors.primaryContainer else colors.surfaceMuted
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockPerson,
                            contentDescription = strings.lockScreenReminderCardTitle,
                            tint = if (isServiceEnabled) colors.onPrimaryContainer else colors.textSecondary
                        )
                    }
                    Column {
                        Text(
                            text = strings.lockScreenReminderCardTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isServiceEnabled) {
                                strings.formatServiceStatusEnabled(triggerEveryNUnlocks)
                            } else {
                                strings.serviceStatusDisabledDesc
                            },
                            fontSize = 13.sp,
                            color = if (isServiceEnabled) colors.primary else colors.textSecondary
                        )
                    }
                }
                Switch(
                    checked = isServiceEnabled,
                    onCheckedChange = onToggleService,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.primary
                    )
                )
            }

            if (!hasOverlayPermission) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.warningContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFFF57C00),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.overlayPermissionWarning,
                                fontSize = 12.sp,
                                color = colors.warningText,
                                lineHeight = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onRequestOverlayPermission) {
                            Text(
                                text = strings.grantPermissionButton,
                                fontWeight = FontWeight.Bold,
                                color = colors.streakText
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onPreviewLockScreen,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = if (colors.isDark) Color(0xFF00252A) else Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.previewLockScreenNow,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
