package com.forvill.hisaabkhataa.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.forvill.hisaabkhataa.core.i18n.AppLanguage
import com.forvill.hisaabkhataa.core.i18n.languageFromName
import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.TransactionType
import com.forvill.hisaabkhataa.data.repository.FakeLedgerRepository
import com.forvill.hisaabkhataa.data.repository.LedgerRepository

class AppState(
    private val repository: LedgerRepository = FakeLedgerRepository(),
) {
    var selectedLanguage by mutableStateOf("English")
    var mobileNumber by mutableStateOf("")
    var otpCode by mutableStateOf("1234")
    var businessProfile by mutableStateOf(repository.getBusinessProfile())
    var selectedPartyId by mutableStateOf(repository.getParties().first().id)
    var addEntryDraft by mutableStateOf(AddEntryDraft())

    private val _parties = mutableStateListOf<Party>().apply {
        addAll(repository.getParties())
    }
    val parties: List<Party> get() = _parties
    val onboardingPages = repository.getOnboardingPages()

    fun appLanguage(): AppLanguage = languageFromName(selectedLanguage)

    fun updateProfile(profile: BusinessProfile) {
        businessProfile = profile
    }

    fun selectParty(partyId: String) {
        selectedPartyId = partyId
    }

    fun setEntryType(type: TransactionType) {
        addEntryDraft = addEntryDraft.copy(type = type)
    }

    fun updateEntryAmount(amount: String) {
        addEntryDraft = addEntryDraft.copy(amount = amount.filter { it.isDigit() })
    }

    fun updateEntryNote(note: String) {
        addEntryDraft = addEntryDraft.copy(note = note)
    }

    fun updateEntryDueDateLabel(label: String) {
        addEntryDraft = addEntryDraft.copy(dueDateLabel = label)
    }

    fun saveEntry() {
        val updated = repository.addLedgerEntry(selectedPartyId, addEntryDraft)
        _parties.clear()
        _parties.addAll(updated)
        addEntryDraft = AddEntryDraft()
    }

    fun resetEntryDraft(type: TransactionType = TransactionType.Udhaar) {
        addEntryDraft = AddEntryDraft(type = type)
    }

    fun selectedParty(): Party = parties.first { it.id == selectedPartyId }
}
