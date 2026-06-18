package com.forvill.hisaabkhataa.data.repository

import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.AppSettings
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.LedgerApiPayload
import com.forvill.hisaabkhataa.core.model.OnboardingPage
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.PartyDraft
import com.forvill.hisaabkhataa.core.model.ReminderItem
import com.forvill.hisaabkhataa.core.model.ReportCard
import com.forvill.hisaabkhataa.core.model.ReportExport
import com.forvill.hisaabkhataa.core.model.ReportFilter
import com.forvill.hisaabkhataa.core.model.SyncQueueItem

interface LedgerRepository {
    fun getOnboardingPages(): List<OnboardingPage>
    fun getAppSettings(): AppSettings
    fun saveAppSettings(settings: AppSettings): AppSettings
    fun getBusinessProfile(): BusinessProfile
    fun saveBusinessProfile(profile: BusinessProfile): BusinessProfile
    fun getParties(): List<Party>
    fun addLedgerEntry(partyId: String, draft: AddEntryDraft): List<Party>
    fun createParty(parties: List<Party>, draft: PartyDraft): List<Party>
    fun updateParty(parties: List<Party>, partyId: String, draft: PartyDraft): List<Party>
    fun buildReminders(parties: List<Party>): List<ReminderItem>
    fun buildReports(parties: List<Party>, filter: ReportFilter): List<ReportCard>
    fun exportReports(businessProfile: BusinessProfile, parties: List<Party>, filter: ReportFilter): ReportExport
    fun getSyncQueueItems(): List<SyncQueueItem>
    fun retrySyncQueue(): List<SyncQueueItem>
    fun buildLedgerPayload(
        businessProfile: BusinessProfile,
        selectedLanguage: String,
        partyId: String,
        draft: AddEntryDraft,
    ): LedgerApiPayload?
}
