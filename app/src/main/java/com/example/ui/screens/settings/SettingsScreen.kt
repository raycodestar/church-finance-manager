package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateToChurchProfile: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToActivityHistory: () -> Unit,
    onNavigateToRecentlyDeleted: () -> Unit,
    onNavigateToSupabaseSql: () -> Unit,
    onSignOut: () -> Unit
) {
    val churchProfile by viewModel.churchProfile.collectAsState()
    val adminProfile by viewModel.adminProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings & Administration",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Manage church profile, financial categories, and system settings",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Church & Admin Card Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = churchProfile?.name ?: "Church Finance Manager",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Administrator: ${adminProfile?.fullName ?: "Church Administrator"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Currency: ${churchProfile?.defaultCurrency ?: "UGX"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Settings Menu Items
        SettingsMenuItem(
            title = "Church Profile Settings",
            subtitle = "Update church name, address, phone, email, and currency",
            icon = Icons.Default.AccountBalance,
            onClick = onNavigateToChurchProfile
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsMenuItem(
            title = "Financial Categories",
            subtitle = "Custom income and expense categories",
            icon = Icons.Default.Category,
            onClick = onNavigateToCategories
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsMenuItem(
            title = "Activity History & Audit Logs",
            subtitle = "View full history of financial entries and edits",
            icon = Icons.Default.History,
            onClick = onNavigateToActivityHistory
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsMenuItem(
            title = "Recently Deleted (Trash Bin)",
            subtitle = "Restore soft-deleted gatherings and transactions",
            icon = Icons.Default.DeleteSweep,
            onClick = onNavigateToRecentlyDeleted
        )

        Spacer(modifier = Modifier.height(10.dp))

        SettingsMenuItem(
            title = "Supabase PostgreSQL DDL & RLS",
            subtitle = "View database schema, policies, and cloud backend setup",
            icon = Icons.Default.Storage,
            onClick = onNavigateToSupabaseSql
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Lock, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out Administrator")
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SettingsMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}
