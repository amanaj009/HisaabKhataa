package com.forvill.hisaabkhataa.data.repository

import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.AppSettings
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.ExpenseSummary
import com.forvill.hisaabkhataa.core.model.ExpenseType
import com.forvill.hisaabkhataa.core.model.LedgerApiPayload
import com.forvill.hisaabkhataa.core.model.LedgerEntry
import com.forvill.hisaabkhataa.core.model.OnboardingPage
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.PartyDraft
import com.forvill.hisaabkhataa.core.model.ReminderItem
import com.forvill.hisaabkhataa.core.model.ReportCard
import com.forvill.hisaabkhataa.core.model.ReportExport
import com.forvill.hisaabkhataa.core.model.ReportFilter
import com.forvill.hisaabkhataa.core.model.SyncQueueItem
import com.forvill.hisaabkhataa.core.model.TransactionExpense
import com.forvill.hisaabkhataa.core.model.TransactionType
import java.io.File

class FakeLedgerRepository : LedgerRepository {
    private var settings = AppSettings()
    private var profile = SampleData.defaultBusiness
    private var parties = SampleData.parties
    private val syncQueue = mutableListOf<SyncQueueItem>()

    override fun getOnboardingPages(): List<OnboardingPage> = SampleData.onboardingPages
    override fun getAppSettings(): AppSettings = settings
    override fun saveAppSettings(settings: AppSettings): AppSettings {
        this.settings = settings
        return this.settings
    }

    override fun getBusinessProfile(): BusinessProfile = profile
    override fun saveBusinessProfile(profile: BusinessProfile): BusinessProfile {
        this.profile = profile
        return this.profile
    }

    override fun getParties(): List<Party> = parties

    override fun addLedgerEntry(partyId: String, draft: AddEntryDraft): List<Party> {
        val amount = draft.amount.toIntOrNull() ?: return parties
        val now = System.currentTimeMillis()
        parties = parties.map { party ->
            if (party.id != partyId) {
                party
            } else {
                val newEntry = LedgerEntry(
                    id = "${partyId}-${party.entries.size + 1}",
                    title = if (draft.type == TransactionType.Udhaar) "New udhaar entry" else "Payment received",
                    note = draft.note.ifBlank { "Quick entry from mobile" },
                    amount = amount,
                    type = draft.type,
                    time = "Just now",
                    createdAtMillis = now,
                    dueDateLabel = draft.dueDateLabel.ifBlank { null },
                    dueDateValue = draft.dueDateValue.ifBlank { null },
                )
                val newBalance = if (draft.type == TransactionType.Udhaar) {
                    party.balance + amount
                } else {
                    party.balance - amount
                }
                party.copy(balance = newBalance, lastActivity = "Just now", entries = listOf(newEntry) + party.entries)
            }
        }
        syncQueue.add(
            0,
            SyncQueueItem(
                id = "queue-$now",
                operationType = if (draft.type == TransactionType.Udhaar) "ADD_UDHAAR" else "ADD_PAYMENT",
                entityId = partyId,
                payloadPreview = "amount=$amount",
                status = "pending",
                retryCount = 0,
                lastAttemptLabel = "Not tried yet",
                nextRetryLabel = "Whenever remote sync is enabled",
                lastError = null,
            ),
        )
        return parties
    }

    override fun createParty(parties: List<Party>, draft: PartyDraft): List<Party> {
        if (draft.name.isBlank()) return this.parties
        val nextParty = Party(
            id = draft.name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-') + "-" + (this.parties.size + 1),
            name = draft.name.trim(),
            phone = draft.phone.trim(),
            balance = 0,
            lastActivity = "Just now",
            creditLimit = draft.creditLimit.toIntOrNull() ?: 0,
            tags = draft.tags.split(',').map { it.trim() }.filter { it.isNotBlank() },
            entries = emptyList(),
        )
        this.parties = listOf(nextParty) + this.parties
        return this.parties
    }

    override fun updateParty(parties: List<Party>, partyId: String, draft: PartyDraft): List<Party> {
        this.parties = this.parties.map { party ->
            if (party.id != partyId) party else party.copy(
                name = draft.name.ifBlank { party.name },
                phone = draft.phone.ifBlank { party.phone },
                creditLimit = draft.creditLimit.toIntOrNull() ?: party.creditLimit,
                tags = draft.tags.split(',').map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { party.tags },
            )
        }
        return this.parties
    }

    override fun buildReminders(parties: List<Party>): List<ReminderItem> {
        return parties
            .filter { it.balance > 0 }
            .sortedByDescending { it.balance }
            .take(6)
            .mapIndexed { index, party ->
                ReminderItem(
                    id = "reminder-${party.id}",
                    partyName = party.name,
                    dueAmount = party.balance,
                    dueLabel = if (index % 2 == 0) "Due today" else "Follow up needed",
                    channel = if (party.phone.isNotBlank()) "WhatsApp" else "Call",
                )
            }
    }

    override fun buildReports(parties: List<Party>, filter: ReportFilter): List<ReportCard> {
        val totalDue = parties.filter { it.balance > 0 }.sumOf { it.balance }
        val totalAdvance = parties.filter { it.balance < 0 }.sumOf { -it.balance }
        val activeLedgers = parties.count { it.entries.isNotEmpty() }
        val todayCollection = parties.flatMap { it.entries }.filter { it.type == TransactionType.Payment }.sumOf { it.amount }
        return listOf(
            ReportCard("Today's collection", "Rs. $todayCollection", "Payment entries recorded in ledger"),
            ReportCard("Outstanding due", "Rs. $totalDue", "Pending balances across customers"),
            ReportCard("Advance balance", "Rs. $totalAdvance", "Amount already received in advance"),
            ReportCard("Active ledgers", activeLedgers.toString(), "Customers with transaction history"),
        )
    }

    override fun exportReports(businessProfile: BusinessProfile, parties: List<Party>, filter: ReportFilter): ReportExport {
        val file = File(System.getProperty("java.io.tmpdir"), "fake-ledger-report.csv")
        file.writeText("business_name,owner_name\n${businessProfile.businessName},${businessProfile.ownerName}\n")
        return ReportExport(file.absolutePath, file.name, parties.sumOf { it.entries.size }, "Now")
    }

    override fun getSyncQueueItems(): List<SyncQueueItem> = syncQueue

    override fun retrySyncQueue(): List<SyncQueueItem> {
        repeat(syncQueue.size) { index ->
            syncQueue[index] = syncQueue[index].copy(
                status = "deferred",
                retryCount = syncQueue[index].retryCount + 1,
                lastAttemptLabel = "Now",
                nextRetryLabel = "Later",
                lastError = "Local-only mode active. Remote sync disabled for now.",
            )
        }
        return syncQueue
    }

    override fun buildLedgerPayload(
        businessProfile: BusinessProfile,
        selectedLanguage: String,
        partyId: String,
        draft: AddEntryDraft,
    ): LedgerApiPayload? {
        val amount = draft.amount.toIntOrNull() ?: return null
        return LedgerApiPayload(
            businessName = businessProfile.businessName,
            selectedLanguage = selectedLanguage,
            partyId = partyId,
            transactionType = draft.type.name.lowercase(),
            amount = amount,
            note = draft.note.trim(),
            dueDateLabel = draft.dueDateLabel.ifBlank { null },
            dueDateValue = draft.dueDateValue.ifBlank { null },
        )
    }

    private var expenses = mutableListOf<TransactionExpense>()

    override fun getExpenses(): List<TransactionExpense> = expenses

    override fun saveExpenses(expenses: List<TransactionExpense>): List<TransactionExpense> {
        this.expenses = expenses.toMutableList()
        return this.expenses
    }

    override fun buildExpenseSummary(expenses: List<TransactionExpense>): ExpenseSummary {
        val totalIncome = expenses.filter { it.type == ExpenseType.Income }.sumOf { it.amount }
        val totalExpense = expenses.filter { it.type == ExpenseType.Expense }.sumOf { it.amount }
        val categorySpending = expenses.filter { it.type == ExpenseType.Expense }
            .groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
        
        return ExpenseSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netSavings = totalIncome - totalExpense,
            categoryWiseSpending = categorySpending
        )
    }
}
