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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.model.BusinessProfile

@Composable
fun SettingsScreen(
    strings: AppStrings,
    profile: BusinessProfile,
    onOpenPrivacyPolicy: () -> Unit,
) {
    val settingsItems = listOf(
        "Business profile" to "${profile.businessName} - ${profile.city}",
        "Language" to "English / Hindi",
        "Backup & restore" to "Manual export support can be added next",
        "Notification preferences" to "Payment reminders, collections, recovery nudges",
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(strings.settingsTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        items(settingsItems) { (title, subtitle) ->
            SettingsRow(title = title, subtitle = subtitle)
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
                    Column {
                        Text("Smart payment reminders", fontWeight = FontWeight.SemiBold)
                        Text("Auto-suggest follow-ups for dues", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = true, onCheckedChange = {})
                }
            }
        }
        item {
            SettingsRow(
                title = strings.privacyPolicyTitle,
                subtitle = "How user, transaction, and reminder data is handled",
                onClick = onOpenPrivacyPolicy,
            )
        }
    }
}

@Composable
fun PrivacyPolicyScreen(strings: AppStrings) {
    val sections = listOf(
        "Information we collect" to "Hisaab Khataa can store merchant profile details, customer names, mobile numbers, ledger balances, transaction notes, reminder preferences, and report summaries. In the current prototype, this data is shown as sample local content only.",
        "How we use information" to "This information is used to help merchants record udhaar and jama entries, display party balances, create reminders, and generate business insights such as collection totals and pending dues.",
        "Data sharing" to "We do not present any third-party selling or trading of merchant data in this prototype. If payment, backup, analytics, or messaging integrations are added later, they should be clearly disclosed in a production privacy policy before release.",
        "Security and retention" to "Production builds should protect account and ledger data using secure transport, access control, and device-safe storage. Data retention periods should match operational, regulatory, and user-requested deletion requirements.",
        "User choices" to "Users should be able to update business profile details, manage reminder preferences, change language, and request deletion or export of their account data when backend services are added.",
        "Contact" to "Forvill can publish an official support email, business address, and response process here before Play Store release.",
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
