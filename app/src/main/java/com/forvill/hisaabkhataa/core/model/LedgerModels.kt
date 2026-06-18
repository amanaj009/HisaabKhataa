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
    val dueDateLabel: String? = null,
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

data class AddEntryDraft(
    val amount: String = "",
    val note: String = "",
    val dueDateLabel: String = "",
    val type: TransactionType = TransactionType.Udhaar,
)
