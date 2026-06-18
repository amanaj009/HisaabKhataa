package com.forvill.hisaabkhataa.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.Party

@Composable
fun HomeScreen(
    profile: BusinessProfile,
    parties: List<Party>,
    onAddEntry: () -> Unit,
    onOpenParty: (String) -> Unit,
    onOpenReminders: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Namaste, ${profile.ownerName}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${profile.businessName} • ${profile.city}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Today's collection", "₹12,450", Icons.Outlined.AccountBalanceWallet, Modifier.weight(1f))
                SummaryCard("Pending parties", "38", Icons.Outlined.Groups, Modifier.weight(1f))
            }
        }
        item {
            SummaryCard(
                title = "Reminders ready",
                value = "14 follow-ups",
                icon = Icons.Outlined.Notifications,
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenReminders,
            )
        }
        item {
            ActionBand(onAddEntry = onAddEntry, onOpenReminders = onOpenReminders)
        }
        item {
            Text(
                text = "Important customers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
        items(parties) { party ->
            CustomerStrip(
                party = party,
                onClick = { onOpenParty(party.id) },
            )
        }
    }
}

@Composable
fun PartyListScreen(
    parties: List<Party>,
    onSelectParty: (String) -> Unit,
    onAddEntry: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Party list", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Button(onClick = onAddEntry) { Text("New entry") }
            }
        }
        items(parties) { party ->
            CustomerStrip(party = party, onClick = { onSelectParty(party.id) })
        }
    }
}

@Composable
fun PartyDetailScreen(
    party: Party,
    onAddEntry: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(party.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(party.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (party.balance >= 0) "Due balance ₹${party.balance}" else "Advance balance ₹${-party.balance}",
                        color = if (party.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Credit limit ₹${party.creditLimit} • ${party.tags.joinToString()}")
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onAddEntry, modifier = Modifier.weight(1f)) { Text("Add udhaar") }
                Button(onClick = onAddEntry, modifier = Modifier.weight(1f)) { Text("Add payment") }
            }
        }
        item {
            Text("Ledger history", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        items(party.entries) { entry ->
            Card(shape = RoundedCornerShape(22.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(entry.title, fontWeight = FontWeight.SemiBold)
                        Text("${entry.note} • ${entry.time}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = if (entry.type.name == "Udhaar") "+₹${entry.amount}" else "-₹${entry.amount}",
                        color = if (entry.type.name == "Udhaar") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun AddEntryScreen(
    party: Party,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Add new entry", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Create a quick udhaar or payment record for ${party.name}.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Recommended fields", fontWeight = FontWeight.SemiBold)
                Text("1. Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("2. Type: Udhaar / Payment", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("3. Note or sold items", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("4. Optional due date / reminder", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text("Back to ledger")
        }
    }
}

@Composable
fun ReminderScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Payment reminders", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        items(SampleData.reminders) { reminder ->
            Card(shape = RoundedCornerShape(22.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(reminder.partyName, fontWeight = FontWeight.SemiBold)
                        Text("${reminder.dueLabel} • ${reminder.channel}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("₹${reminder.dueAmount}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ActionBand(
    onAddEntry: () -> Unit,
    onOpenReminders: () -> Unit,
) {
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Daily work shortcuts", fontWeight = FontWeight.Bold)
                Text("Create khata entries or send payment nudges faster.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAddEntry) { Text("New entry") }
                OutlinedButton(onClick = onOpenReminders) { Text("Remind") }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Card(modifier = clickableModifier, shape = RoundedCornerShape(24.dp)) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                    .padding(10.dp),
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Column {
                Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CustomerStrip(
    party: Party,
    onClick: () -> Unit,
) {
    Card(shape = RoundedCornerShape(22.dp), modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(party.name, fontWeight = FontWeight.SemiBold)
                Text("Last activity ${party.lastActivity}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = if (party.balance >= 0) "+₹${party.balance} due" else "₹${-party.balance} advance",
                color = if (party.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
