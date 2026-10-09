package com.forvill.hisaabkhataa.data.repository

import android.content.Context
import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.AppSettings
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.LedgerApiPayload
import com.forvill.hisaabkhataa.core.model.ExpenseSummary
import com.forvill.hisaabkhataa.core.model.OnboardingPage
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.PartyDraft
import com.forvill.hisaabkhataa.core.model.ReminderItem
import com.forvill.hisaabkhataa.core.model.ReportCard
import com.forvill.hisaabkhataa.core.model.ReportExport
import com.forvill.hisaabkhataa.core.model.ReportFilter
import com.forvill.hisaabkhataa.core.model.SyncQueueItem
import com.forvill.hisaabkhataa.core.model.TransactionExpense

class ProductionLedgerRepository(
    context: Context,
    private val localRepository: LedgerRepository = SqliteLedgerRepository(context),
) : LedgerRepository {
    override fun getOnboardingPages(): List<OnboardingPage> = localRepository.getOnboardingPages()
    override fun getAppSettings(): AppSettings = localRepository.getAppSettings()
    override fun saveAppSettings(settings: AppSettings): AppSettings = localRepository.saveAppSettings(settings)
    override fun getBusinessProfile(): BusinessProfile = localRepository.getBusinessProfile()
    override fun saveBusinessProfile(profile: BusinessProfile): BusinessProfile = localRepository.saveBusinessProfile(profile)
    override fun getParties(): List<Party> = localRepository.getParties()
    override fun addLedgerEntry(partyId: String, draft: AddEntryDraft): List<Party> = localRepository.addLedgerEntry(partyId, draft)
    override fun createParty(parties: List<Party>, draft: PartyDraft): List<Party> = localRepository.createParty(parties, draft)
    override fun updateParty(parties: List<Party>, partyId: String, draft: PartyDraft): List<Party> = localRepository.updateParty(parties, partyId, draft)
    override fun buildReminders(parties: List<Party>): List<ReminderItem> = localRepository.buildReminders(parties)
    override fun buildReports(parties: List<Party>, filter: ReportFilter): List<ReportCard> = localRepository.buildReports(parties, filter)
    override fun exportReports(businessProfile: BusinessProfile, parties: List<Party>, filter: ReportFilter): ReportExport {
        return localRepository.exportReports(businessProfile, parties, filter)
    }

    override fun getSyncQueueItems(): List<SyncQueueItem> = localRepository.getSyncQueueItems()
    override fun retrySyncQueue(): List<SyncQueueItem> = localRepository.retrySyncQueue()
    override fun buildLedgerPayload(
        businessProfile: BusinessProfile,
        selectedLanguage: String,
        partyId: String,
        draft: AddEntryDraft,
    ): LedgerApiPayload? = localRepository.buildLedgerPayload(businessProfile, selectedLanguage, partyId, draft)

    override fun getExpenses(): List<TransactionExpense> = localRepository.getExpenses()

    override fun saveExpenses(expenses: List<TransactionExpense>): List<TransactionExpense> {
        return localRepository.saveExpenses(expenses)
    }

    override fun buildExpenseSummary(expenses: List<TransactionExpense>): ExpenseSummary {
        return localRepository.buildExpenseSummary(expenses)
    }
}
