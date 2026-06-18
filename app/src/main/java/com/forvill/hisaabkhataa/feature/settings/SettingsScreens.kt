package com.forvill.hisaabkhataa.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.model.AppSettings
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.SyncQueueItem

@Composable
fun SettingsScreen(
    strings: AppStrings,
    profile: BusinessProfile,
    settings: AppSettings,
    syncQueue: List<SyncQueueItem>,
    onProfileChange: (BusinessProfile) -> Unit,
    onLanguageChange: (String) -> Unit,
    onReminderToggle: (Boolean) -> Unit,
    onRetrySync: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
) {
    val pendingCount = syncQueue.count { it.status.equals("pending", true) }
    val deferredCount = syncQueue.count { it.status.equals("deferred", true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(strings.settingsTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("App abhi fully local mode me kaam karti hai. Google login ya cloud dependency use nahi ho rahi.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Business profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = profile.ownerName,
                        onValueChange = { onProfileChange(profile.copy(ownerName = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Owner name") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = profile.businessName,
                        onValueChange = { onProfileChange(profile.copy(businessName = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Business name") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = profile.businessType,
                        onValueChange = { onProfileChange(profile.copy(businessType = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Business type") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = profile.city,
                        onValueChange = { onProfileChange(profile.copy(city = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("City") },
                        singleLine = true,
                    )
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Language", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = settings.selectedLanguage == "English",
                            onClick = { onLanguageChange("English") },
                            label = { Text("English") },
                        )
                        FilterChip(
                            selected = settings.selectedLanguage == "Hindi",
                            onClick = { onLanguageChange("Hindi") },
                            label = { Text("Hindi") },
                        )
                    }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.75f)) {
                        Text("Smart payment reminders", fontWeight = FontWeight.SemiBold)
                        Text("Due parties ke liye reminder list dikhani hai ya nahi, yahin se control hoga.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = settings.smartRemindersEnabled, onCheckedChange = onReminderToggle)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Future sync queue", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Pending: $pendingCount | Deferred: $deferredCount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Har local change queue me save ho raha hai. Jab future me backend add hoga tab isi queue se retry/sync chal sakti hai.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = onRetrySync, modifier = Modifier.fillMaxWidth()) {
                        Text("Retry queued changes")
                    }
                }
            }
        }
        if (syncQueue.isNotEmpty()) {
            item {
                Text("Latest queued changes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            items(syncQueue.take(5)) { item ->
                Card(shape = RoundedCornerShape(22.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${item.operationType} - ${item.entityId}", fontWeight = FontWeight.SemiBold)
                        Text(item.payloadPreview, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Status: ${item.status} | Retries: ${item.retryCount}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Last try: ${item.lastAttemptLabel}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Next retry: ${item.nextRetryLabel}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!item.lastError.isNullOrBlank()) {
                            Text(item.lastError, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        item {
            SettingsRow(
                title = strings.privacyPolicyTitle,
                subtitle = "How user, transaction, report, and reminder data stays on device in this build",
                onClick = onOpenPrivacyPolicy,
            )
        }
    }
}

@Composable
fun PrivacyPolicyScreen(strings: AppStrings) {
    val sections = listOf(
        "Information we collect" to "Hisaab Khataa stores merchant profile details, customer names, mobile numbers, ledger balances, transaction notes, reminder preferences, report summaries, and local sync queue metadata on the device.",
        "How we use information" to "This information is used to record udhaar and jama entries, display party balances, create reminders, generate reports, and prepare future sync payloads without sending them anywhere in this local-only build.",
        "Data sharing" to "This version does not connect to Google login, cloud backup, or third-party analytics. Data remains local to the device unless the merchant manually exports a report file.",
        "Security and retention" to "Production builds should protect ledger data using secure device storage, screen lock guidance, app-level protections, and clear retention/deletion flows when backup or account systems are introduced.",
        "User choices" to "Users can update business profile details, switch app language, disable reminder suggestions, and export local reports. A full delete/reset option can be added in the next version if needed.",
        "Contact" to "Forvill can publish an official support email, business address, and issue-resolution process here before Play Store release.",
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(strings.privacyPolicyTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(strings.privacyPolicySubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(sections) { (title, description) ->
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
) {
    val rowModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Card(shape = RoundedCornerShape(22.dp), modifier = rowModifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.82f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null)
        }
    }
}
