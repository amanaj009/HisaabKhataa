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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.LedgerApiPayload
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.PartyDraft
import com.forvill.hisaabkhataa.core.model.ReminderItem
import com.forvill.hisaabkhataa.core.model.TransactionType

@Composable
fun HomeScreen(
    strings: AppStrings,
    profile: BusinessProfile,
    parties: List<Party>,
    onAddEntry: (TransactionType) -> Unit,
    onOpenParty: (String) -> Unit,
    onOpenReminders: () -> Unit,
) {
    val dueParties = parties.filter { it.balance > 0 }
    val totalDue = dueParties.sumOf { it.balance }
    val advanceParties = parties.filter { it.balance < 0 }

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
                    text = "${strings.homeGreetingPrefix}, ${profile.ownerName}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${profile.businessName} - ${profile.city}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Today's collection", "Rs. 12,450", Icons.Outlined.AccountBalanceWallet, Modifier.fillMaxWidth())
                SummaryCard("Pending parties", dueParties.size.toString(), Icons.Outlined.Groups, Modifier.fillMaxWidth())
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Total due", "Rs. $totalDue", Icons.Outlined.Notifications, Modifier.fillMaxWidth(), onClick = onOpenReminders)
                SummaryCard("Advance parties", advanceParties.size.toString(), Icons.Outlined.AccountBalanceWallet, Modifier.fillMaxWidth())
            }
        }
        item {
            ActionBand(strings = strings, onAddEntry = onAddEntry, onOpenReminders = onOpenReminders)
        }
        item {
            Text(
                text = strings.homeImportantCustomers,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
        items(parties.sortedByDescending { kotlin.math.abs(it.balance) }) { party ->
            CustomerStrip(
                party = party,
                onClick = { onOpenParty(party.id) },
            )
        }
    }
}

@Composable
fun PartyListScreen(
    strings: AppStrings,
    parties: List<Party>,
    onSelectParty: (String) -> Unit,
    onAddEntry: (TransactionType) -> Unit,
    onAddParty: () -> Unit,
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
                Text(strings.partyListTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onAddParty) { Text(strings.addParty) }
                    Button(onClick = { onAddEntry(TransactionType.Udhaar) }) { Text(strings.newEntry) }
                }
            }
        }
        items(parties.sortedBy { it.name }) { party ->
            CustomerStrip(party = party, onClick = { onSelectParty(party.id) })
        }
    }
}

@Composable
fun PartyDetailScreen(
    strings: AppStrings,
    party: Party,
    onAddEntry: (TransactionType) -> Unit,
    onEditParty: () -> Unit,
) {
    val totalUdhaar = party.entries.filter { it.type == TransactionType.Udhaar }.sumOf { it.amount }
    val totalPayment = party.entries.filter { it.type == TransactionType.Payment }.sumOf { it.amount }

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
                        text = if (party.balance >= 0) "Due balance Rs. ${party.balance}" else "Advance balance Rs. ${-party.balance}",
                        color = if (party.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Credit limit Rs. ${party.creditLimit} - ${party.tags.joinToString()}")
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryStat("Total udhaar", "Rs. $totalUdhaar", Modifier.fillMaxWidth())
                SummaryStat("Total payment", "Rs. $totalPayment", Modifier.fillMaxWidth())
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onEditParty, modifier = Modifier.fillMaxWidth()) { Text(strings.editParty) }
                OutlinedButton(onClick = { onAddEntry(TransactionType.Udhaar) }, modifier = Modifier.fillMaxWidth()) { Text(strings.addUdhaar) }
                Button(onClick = { onAddEntry(TransactionType.Payment) }, modifier = Modifier.fillMaxWidth()) { Text(strings.addPayment) }
            }
        }
        item {
            Text(strings.ledgerHistory, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
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
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth(0.7f)) {
                        Text(entry.title, fontWeight = FontWeight.SemiBold)
                        Text("${entry.note} - ${entry.time}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!entry.dueDateLabel.isNullOrBlank()) {
                            Text("Due: ${entry.dueDateLabel}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        text = if (entry.type == TransactionType.Udhaar) "+Rs. ${entry.amount}" else "-Rs. ${entry.amount}",
                        color = if (entry.type == TransactionType.Udhaar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun AddEntryScreen(
    strings: AppStrings,
    party: Party,
    draft: AddEntryDraft,
    apiPayload: LedgerApiPayload?,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onDueDateChange: (String) -> Unit,
    onDueDateValueChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(strings.addEntryTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("${strings.addEntrySubtitlePrefix} ${party.name}.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = draft.type == TransactionType.Udhaar,
                onClick = { onTypeChange(TransactionType.Udhaar) },
                label = { Text(strings.addUdhaar) },
            )
            FilterChip(
                selected = draft.type == TransactionType.Payment,
                onClick = { onTypeChange(TransactionType.Payment) },
                label = { Text(strings.addPayment) },
            )
        }
        OutlinedTextField(
            value = draft.amount,
            onValueChange = onAmountChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Amount") },
            prefix = { Text("Rs. ") },
            singleLine = true,
        )
        OutlinedTextField(
            value = draft.note,
            onValueChange = onNoteChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Note or sold items") },
            minLines = 3,
        )
        OutlinedTextField(
            value = draft.dueDateLabel,
            onValueChange = onDueDateChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Due date or reminder label") },
            placeholder = { Text("Example: Tomorrow evening") },
            singleLine = true,
        )
        OutlinedTextField(
            value = draft.dueDateValue,
            onValueChange = onDueDateValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Due date (YYYY-MM-DD)") },
            placeholder = { Text("Example: 2026-06-25") },
            singleLine = true,
        )
        Card(shape = RoundedCornerShape(22.dp)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preview", fontWeight = FontWeight.SemiBold)
                Text("Party: ${party.name}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Type: ${if (draft.type == TransactionType.Udhaar) strings.addUdhaar else strings.addPayment}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Amount: Rs. ${draft.amount.ifBlank { "0" }}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (apiPayload != null) {
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(strings.apiPayloadTitle, fontWeight = FontWeight.SemiBold)
                    Text("party_id: ${apiPayload.partyId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("type: ${apiPayload.transactionType}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("amount: ${apiPayload.amount}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("language: ${apiPayload.selectedLanguage}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("due_date: ${apiPayload.dueDateValue ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = draft.amount.isNotBlank() && draft.note.isNotBlank(),
        ) {
            Text(strings.addEntrySave)
        }
    }
}

@Composable
fun PartyEditorScreen(
    strings: AppStrings,
    draft: PartyDraft,
    isEditMode: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onCreditLimitChange: (String) -> Unit,
    onTagsChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = if (isEditMode) strings.editParty else strings.addParty,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        OutlinedTextField(
            value = draft.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Party name") },
            singleLine = true,
        )
        OutlinedTextField(
            value = draft.phone,
            onValueChange = onPhoneChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mobile number") },
            singleLine = true,
        )
        OutlinedTextField(
            value = draft.creditLimit,
            onValueChange = onCreditLimitChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Credit limit") },
            prefix = { Text("Rs. ") },
            singleLine = true,
        )
        OutlinedTextField(
            value = draft.tags,
            onValueChange = onTagsChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tags") },
            placeholder = { Text("Wholesale, Regular") },
        )
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = draft.name.isNotBlank(),
        ) {
            Text(strings.saveParty)
        }
    }
}

@Composable
fun ReminderScreen(strings: AppStrings, reminders: List<ReminderItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(strings.remindersTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        items(reminders) { reminder ->
            Card(shape = RoundedCornerShape(22.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(reminder.partyName, fontWeight = FontWeight.SemiBold)
                        Text("${reminder.dueLabel} - ${reminder.channel}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("Rs. ${reminder.dueAmount}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ActionBand(
    strings: AppStrings,
    onAddEntry: (TransactionType) -> Unit,
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth(0.64f)) {
                Text(strings.homeShortcutsTitle, fontWeight = FontWeight.Bold)
                Text(strings.homeShortcutsSubtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onAddEntry(TransactionType.Udhaar) }) { Text(strings.newEntry) }
                OutlinedButton(onClick = onOpenReminders) { Text(strings.remind) }
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
private fun SummaryStat(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold)
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
            Column(modifier = Modifier.fillMaxWidth(0.68f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(party.name, fontWeight = FontWeight.SemiBold)
                Text("Last activity ${party.lastActivity}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    party.tags.take(2).forEach { tag ->
                        Text(tag, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (party.balance >= 0) "+Rs. ${party.balance}" else "Rs. ${-party.balance}",
                    color = if (party.balance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (party.balance >= 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(if (party.balance >= 0) "Due" else "Advance", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
