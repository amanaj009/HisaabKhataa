package com.forvill.hisaabkhataa.data.repository

import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.LedgerEntry
import com.forvill.hisaabkhataa.core.model.OnboardingPage
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.TransactionType

class FakeLedgerRepository : LedgerRepository {
    override fun getOnboardingPages(): List<OnboardingPage> = SampleData.onboardingPages

    override fun getBusinessProfile(): BusinessProfile = SampleData.defaultBusiness

    override fun getParties(): List<Party> = SampleData.parties

    override fun addLedgerEntry(partyId: String, draft: AddEntryDraft): List<Party> {
        val amount = draft.amount.toIntOrNull() ?: return getParties()
        return getParties().map { party ->
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
                    dueDateLabel = draft.dueDateLabel.ifBlank { null },
                )
                val newBalance = if (draft.type == TransactionType.Udhaar) {
                    party.balance + amount
                } else {
                    party.balance - amount
                }
                party.copy(
                    balance = newBalance,
                    lastActivity = "Just now",
                    entries = listOf(newEntry) + party.entries,
                )
            }
        }
    }
}
