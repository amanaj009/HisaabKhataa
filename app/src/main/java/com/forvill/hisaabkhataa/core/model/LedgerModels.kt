package com.forvill.hisaabkhataa.core.model

enum class TransactionType {
    Udhaar,
    Payment,
}

data class LedgerEntry(
    val id: String,
    val title: String,
    val note: String,
    val amount: Int,
    val type: TransactionType,
    val time: String,
    val createdAtMillis: Long = 0L,
    val dueDateLabel: String? = null,
    val dueDateValue: String? = null,
)

data class Party(
    val id: String,
    val name: String,
    val phone: String,
    val balance: Int,
    val lastActivity: String,
    val creditLimit: Int,
    val tags: List<String>,
    val entries: List<LedgerEntry>,
)

data class PartyDraft(
    val name: String = "",
    val phone: String = "",
    val creditLimit: String = "",
    val tags: String = "",
)

data class ReminderItem(
    val id: String,
    val partyName: String,
    val dueAmount: Int,
    val dueLabel: String,
    val channel: String,
)

data class ReportCard(
    val title: String,
    val value: String,
    val caption: String,
)

data class AppSettings(
    val selectedLanguage: String = "English",
    val smartRemindersEnabled: Boolean = true,
)

data class ReportFilter(
    val rangeKey: String = "all",
    val startDate: String = "",
    val endDate: String = "",
)

data class ReportExport(
    val filePath: String,
    val fileName: String,
    val lineCount: Int,
    val generatedAtLabel: String,
)

data class SyncQueueItem(
    val id: String,
    val operationType: String,
    val entityId: String,
    val payloadPreview: String,
    val status: String,
    val retryCount: Int,
    val lastAttemptLabel: String,
    val nextRetryLabel: String,
    val lastError: String?,
)

data class AddEntryDraft(
    val amount: String = "",
    val note: String = "",
    val dueDateLabel: String = "",
    val dueDateValue: String = "",
    val type: TransactionType = TransactionType.Udhaar,
)

data class LedgerApiPayload(
    val businessName: String,
    val selectedLanguage: String,
    val partyId: String,
    val transactionType: String,
    val amount: Int,
    val note: String,
    val dueDateLabel: String?,
    val dueDateValue: String?,
)
