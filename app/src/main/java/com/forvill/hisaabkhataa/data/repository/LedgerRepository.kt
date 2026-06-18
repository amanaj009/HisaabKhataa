package com.forvill.hisaabkhataa.data.repository

import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.OnboardingPage
import com.forvill.hisaabkhataa.core.model.Party

interface LedgerRepository {
    fun getOnboardingPages(): List<OnboardingPage>
    fun getBusinessProfile(): BusinessProfile
    fun getParties(): List<Party>
    fun addLedgerEntry(partyId: String, draft: AddEntryDraft): List<Party>
}
