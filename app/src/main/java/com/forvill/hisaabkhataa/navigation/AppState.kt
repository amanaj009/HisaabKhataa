package com.forvill.hisaabkhataa.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.forvill.hisaabkhataa.core.i18n.AppLanguage
import com.forvill.hisaabkhataa.core.i18n.languageFromName
import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.AppSettings
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.LedgerApiPayload
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.PartyDraft
import com.forvill.hisaabkhataa.core.model.ReminderItem
import com.forvill.hisaabkhataa.core.model.ReportCard
import com.forvill.hisaabkhataa.core.model.ReportExport
import com.forvill.hisaabkhataa.core.model.ReportFilter
import com.forvill.hisaabkhataa.core.model.SyncQueueItem
import com.forvill.hisaabkhataa.core.model.TransactionType
import com.forvill.hisaabkhataa.data.repository.LedgerRepository

class AppState(
    private val repository: LedgerRepository,
) {
    var settings by mutableStateOf(repository.getAppSettings())
    var selectedLanguage by mutableStateOf(settings.selectedLanguage)
    var mobileNumber by mutableStateOf("")
    var otpCode by mutableStateOf("1234")
    var businessProfile by mutableStateOf(repository.getBusinessProfile())
    var selectedPartyId by mutableStateOf(repository.getParties().first().id)
    var addEntryDraft by mutableStateOf(AddEntryDraft())
    var partyDraft by mutableStateOf(PartyDraft())
    var editingPartyId by mutableStateOf<String?>(null)
    var reportFilter by mutableStateOf(ReportFilter())
    var lastReportExport by mutableStateOf<ReportExport?>(null)

    private val _parties = mutableStateListOf<Party>().apply {
        addAll(repository.getParties())
    }
    private val _syncQueue = mutableStateListOf<SyncQueueItem>().apply {
        addAll(repository.getSyncQueueItems())
    }

    val parties: List<Party> get() = _parties
    val onboardingPages = repository.getOnboardingPages()
    val reminders: List<ReminderItem> get() = if (settings.smartRemindersEnabled) repository.buildReminders(parties) else emptyList()
    val reports: List<ReportCard> get() = repository.buildReports(parties, reportFilter)
    val syncQueue: List<SyncQueueItem> get() = _syncQueue
    val latestLedgerPayload: LedgerApiPayload?
        get() = repository.buildLedgerPayload(businessProfile, selectedLanguage, selectedPartyId, addEntryDraft)

    fun appLanguage(): AppLanguage = languageFromName(selectedLanguage)

    fun updateProfile(profile: BusinessProfile) {
        businessProfile = repository.saveBusinessProfile(profile)
    }

    fun updateSelectedLanguage(language: String) {
        settings = repository.saveAppSettings(settings.copy(selectedLanguage = language))
        selectedLanguage = settings.selectedLanguage
    }

    fun updateReminderPreference(enabled: Boolean) {
        settings = repository.saveAppSettings(settings.copy(smartRemindersEnabled = enabled))
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

    fun updateEntryDueDateValue(value: String) {
        addEntryDraft = addEntryDraft.copy(dueDateValue = value)
    }

    fun saveEntry() {
        val updated = repository.addLedgerEntry(selectedPartyId, addEntryDraft)
        _parties.clear()
        _parties.addAll(updated)
        refreshSyncQueue()
        addEntryDraft = AddEntryDraft()
    }

    fun updatePartyName(value: String) {
        partyDraft = partyDraft.copy(name = value)
    }

    fun updatePartyPhone(value: String) {
        partyDraft = partyDraft.copy(phone = value)
    }

    fun updatePartyCreditLimit(value: String) {
        partyDraft = partyDraft.copy(creditLimit = value.filter { it.isDigit() })
    }

    fun updatePartyTags(value: String) {
        partyDraft = partyDraft.copy(tags = value)
    }

    fun startCreateParty() {
        editingPartyId = null
        partyDraft = PartyDraft()
    }

    fun startEditSelectedParty() {
        val party = selectedParty()
        editingPartyId = party.id
        partyDraft = PartyDraft(
            name = party.name,
            phone = party.phone,
            creditLimit = party.creditLimit.toString(),
            tags = party.tags.joinToString(", "),
        )
    }

    fun savePartyDraft() {
        val updated = if (editingPartyId == null) {
            repository.createParty(parties, partyDraft)
        } else {
            repository.updateParty(parties, editingPartyId.orEmpty(), partyDraft)
        }
        _parties.clear()
        _parties.addAll(updated)
        refreshSyncQueue()
        if (editingPartyId == null && updated.isNotEmpty()) {
            selectedPartyId = updated.first().id
        }
        partyDraft = PartyDraft()
        editingPartyId = null
    }

    fun updateReportRange(rangeKey: String) {
        reportFilter = reportFilter.copy(rangeKey = rangeKey)
    }

    fun updateReportStartDate(value: String) {
        reportFilter = reportFilter.copy(startDate = value)
    }

    fun updateReportEndDate(value: String) {
        reportFilter = reportFilter.copy(endDate = value)
    }

    fun exportReports() {
        lastReportExport = repository.exportReports(businessProfile, parties, reportFilter)
    }

    fun retrySyncQueue() {
        _syncQueue.clear()
        _syncQueue.addAll(repository.retrySyncQueue())
    }

    fun resetEntryDraft(type: TransactionType = TransactionType.Udhaar) {
        addEntryDraft = AddEntryDraft(type = type)
    }

    fun selectedParty(): Party = parties.first { it.id == selectedPartyId }

    private fun refreshSyncQueue() {
        _syncQueue.clear()
        _syncQueue.addAll(repository.getSyncQueueItems())
    }
}
