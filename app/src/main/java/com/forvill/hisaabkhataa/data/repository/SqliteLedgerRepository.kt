package com.forvill.hisaabkhataa.data.repository

import android.content.Context
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
import com.forvill.hisaabkhataa.data.local.LedgerSQLiteHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SqliteLedgerRepository(context: Context) : LedgerRepository {
    private val appContext = context.applicationContext
    private val helper = LedgerSQLiteHelper(appContext)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

    override fun getOnboardingPages(): List<OnboardingPage> = SampleData.onboardingPages
    override fun getAppSettings(): AppSettings = helper.loadAppSettings()
    override fun saveAppSettings(settings: AppSettings): AppSettings = helper.saveAppSettings(settings)
    override fun getBusinessProfile(): BusinessProfile = helper.loadBusinessProfile()
    override fun saveBusinessProfile(profile: BusinessProfile): BusinessProfile = helper.saveBusinessProfile(profile)
    override fun getParties(): List<Party> = helper.loadParties()

    override fun addLedgerEntry(partyId: String, draft: AddEntryDraft): List<Party> {
        val parties = getParties()
        val amount = draft.amount.toIntOrNull() ?: return parties
        val now = System.currentTimeMillis()
        val updated = parties.map { party ->
            if (party.id != partyId) {
                party
            } else {
                val entry = LedgerEntry(
                    id = "${partyId}-$now",
                    title = if (draft.type == TransactionType.Udhaar) "Nayi udhaar entry" else "Payment receive hua",
                    note = draft.note.ifBlank { "Quick entry from mobile" },
                    amount = amount,
                    type = draft.type,
                    time = "Just now",
                    createdAtMillis = now,
                    dueDateLabel = draft.dueDateLabel.ifBlank { null },
                    dueDateValue = draft.dueDateValue.ifBlank { null },
                )
                val nextBalance = if (draft.type == TransactionType.Udhaar) party.balance + amount else party.balance - amount
                party.copy(balance = nextBalance, lastActivity = "Just now", entries = listOf(entry) + party.entries)
            }
        }
        helper.replaceAll(updated)
        helper.enqueueSyncOperation(
            operationType = if (draft.type == TransactionType.Udhaar) "ADD_UDHAAR" else "ADD_PAYMENT",
            entityId = partyId,
            payloadPreview = "amount=$amount, note=${draft.note.ifBlank { "-" }}, due=${draft.dueDateValue.ifBlank { "-" }}",
        )
        return updated
    }

    override fun createParty(parties: List<Party>, draft: PartyDraft): List<Party> {
        if (draft.name.isBlank()) return parties
        val newParty = Party(
            id = draft.name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-') + "-" + System.currentTimeMillis(),
            name = draft.name.trim(),
            phone = draft.phone.trim(),
            balance = 0,
            lastActivity = "Just now",
            creditLimit = draft.creditLimit.toIntOrNull() ?: 0,
            tags = draft.tags.split(',').map { it.trim() }.filter { it.isNotBlank() },
            entries = emptyList(),
        )
        val updated = listOf(newParty) + parties
        helper.replaceAll(updated)
        helper.enqueueSyncOperation("CREATE_PARTY", newParty.id, "name=${newParty.name}, phone=${newParty.phone.ifBlank { "-" }}")
        return updated
    }

    override fun updateParty(parties: List<Party>, partyId: String, draft: PartyDraft): List<Party> {
        val updated = parties.map { party ->
            if (party.id != partyId) party else party.copy(
                name = draft.name.ifBlank { party.name },
                phone = draft.phone.ifBlank { party.phone },
                creditLimit = draft.creditLimit.toIntOrNull() ?: party.creditLimit,
                tags = draft.tags.split(',').map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { party.tags },
            )
        }
        helper.replaceAll(updated)
        val party = updated.firstOrNull { it.id == partyId }
        if (party != null) {
            helper.enqueueSyncOperation("UPDATE_PARTY", party.id, "name=${party.name}, limit=${party.creditLimit}")
        }
        return updated
    }

    override fun buildReminders(parties: List<Party>): List<ReminderItem> {
        return parties.mapNotNull { party ->
            if (party.balance <= 0) return@mapNotNull null
            val latestDue = party.entries.firstOrNull { it.type == TransactionType.Udhaar && !it.dueDateValue.isNullOrBlank() }
            ReminderItem(
                id = "reminder-${party.id}",
                partyName = party.name,
                dueAmount = party.balance,
                dueLabel = buildDueLabel(latestDue?.dueDateValue),
                channel = if (party.phone.isNotBlank()) "WhatsApp" else "Call",
            )
        }.sortedByDescending { it.dueAmount }.take(8)
    }

    override fun buildReports(parties: List<Party>, filter: ReportFilter): List<ReportCard> {
        val filteredEntries = filterEntries(parties, filter)
        val filteredParties = parties.filter { party -> filteredEntries.any { it.first == party.id } }
        val paymentTotal = filteredEntries.filter { it.second.type == TransactionType.Payment }.sumOf { it.second.amount }
        val udhaarTotal = filteredEntries.filter { it.second.type == TransactionType.Udhaar }.sumOf { it.second.amount }
        val outstandingDue = filteredParties.filter { it.balance > 0 }.sumOf { it.balance }
        val overdueCount = buildReminders(filteredParties).count { it.dueLabel.contains("Overdue", true) }
        return listOf(
            ReportCard("Collection in range", "Rs. $paymentTotal", describeRange(filter)),
            ReportCard("Udhaar added", "Rs. $udhaarTotal", "Total credit entries in selected range"),
            ReportCard("Outstanding due", "Rs. $outstandingDue", "Current pending balance for affected parties"),
            ReportCard("Overdue parties", overdueCount.toString(), "Dues needing recovery follow-up"),
        )
    }

    override fun exportReports(
        businessProfile: BusinessProfile,
        parties: List<Party>,
        filter: ReportFilter,
    ): ReportExport {
        val filteredEntries = filterEntries(parties, filter)
        val exportDir = File(appContext.getExternalFilesDir(null) ?: appContext.filesDir, "exports").apply { mkdirs() }
        val fileName = "ledger-report-${System.currentTimeMillis()}.csv"
        val exportFile = File(exportDir, fileName)
        val lines = buildList {
            add("business_name,owner_name,business_type,city,range")
            add(csvRow(businessProfile.businessName, businessProfile.ownerName, businessProfile.businessType, businessProfile.city, describeRange(filter)))
            add("")
            add("party_name,phone,entry_type,amount,note,created_on,due_date")
            filteredEntries.forEach { (_, entry, party) ->
                add(
                    csvRow(
                        party.name,
                        party.phone,
                        entry.type.name,
                        entry.amount.toString(),
                        entry.note,
                        displayDate(entry.createdAtMillis),
                        entry.dueDateValue ?: entry.dueDateLabel.orEmpty(),
                    ),
                )
            }
        }
        exportFile.writeText(lines.joinToString(separator = "\n"))
        return ReportExport(
            filePath = exportFile.absolutePath,
            fileName = fileName,
            lineCount = filteredEntries.size,
            generatedAtLabel = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date()),
        )
    }

    override fun getSyncQueueItems(): List<SyncQueueItem> = helper.loadSyncQueue()

    override fun retrySyncQueue(): List<SyncQueueItem> = helper.retrySyncQueue()

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

    override fun getExpenses(): List<TransactionExpense> = helper.loadExpenses()

    override fun saveExpenses(expenses: List<TransactionExpense>): List<TransactionExpense> {
        helper.saveExpenses(expenses)
        return getExpenses()
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

    private fun filterEntries(parties: List<Party>, filter: ReportFilter): List<Triple<String, LedgerEntry, Party>> {
        val range = resolveRange(filter)
        return parties.flatMap { party ->
            party.entries.map { entry -> Triple(party.id, entry, party) }
        }.filter { (_, entry, _) ->
            when {
                range == null -> true
                entry.createdAtMillis <= 0L -> false
                else -> entry.createdAtMillis in range.first..range.second
            }
        }
    }

    private fun resolveRange(filter: ReportFilter): Pair<Long, Long>? {
        val todayStart = truncate(Date()).time
        val todayEnd = endOfDay(Date()).time
        return when (filter.rangeKey) {
            "today" -> todayStart to todayEnd
            "7d" -> truncate(Date(todayStart - 6L * DAY_MS)).time to todayEnd
            "30d" -> truncate(Date(todayStart - 29L * DAY_MS)).time to todayEnd
            "custom" -> {
                val start = parseDate(filter.startDate)?.let { truncate(it).time } ?: return null
                val end = parseDate(filter.endDate)?.let { endOfDay(it).time } ?: return null
                start to end
            }
            else -> null
        }
    }

    private fun buildDueLabel(dueDateValue: String?): String {
        if (dueDateValue.isNullOrBlank()) return "Follow up needed"
        return try {
            val dueDate = dateFormat.parse(dueDateValue) ?: return "Follow up needed"
            val today = truncate(Date())
            val diffDays = ((dueDate.time - today.time) / DAY_MS).toInt()
            when {
                diffDays < 0 -> "Overdue by ${-diffDays} day(s)"
                diffDays == 0 -> "Due today"
                diffDays == 1 -> "Due tomorrow"
                else -> "Due in $diffDays days"
            }
        } catch (_: Throwable) {
            "Follow up needed"
        }
    }

    private fun describeRange(filter: ReportFilter): String {
        return when (filter.rangeKey) {
            "today" -> "Transactions captured today"
            "7d" -> "Transactions from the last 7 days"
            "30d" -> "Transactions from the last 30 days"
            "custom" -> if (filter.startDate.isNotBlank() && filter.endDate.isNotBlank()) {
                "Transactions from ${filter.startDate} to ${filter.endDate}"
            } else {
                "Enter start and end date in YYYY-MM-DD"
            }
            else -> "All locally stored ledger transactions"
        }
    }

    private fun displayDate(timestamp: Long): String {
        if (timestamp <= 0L) return "Not available"
        return displayDateFormat.format(Date(timestamp))
    }

    private fun parseDate(value: String): Date? {
        return runCatching { dateFormat.parse(value) }.getOrNull()
    }

    private fun truncate(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    private fun endOfDay(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
    }

    private fun csvRow(vararg values: String): String {
        return values.joinToString(",") { value -> "\"${value.replace("\"", "\"\"")}\"" }
    }

    companion object {
        private const val DAY_MS = 24L * 60L * 60L * 1000L
    }
}
