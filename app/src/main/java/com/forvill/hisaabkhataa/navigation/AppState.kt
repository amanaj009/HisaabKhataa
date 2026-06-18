package com.forvill.hisaabkhataa.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.Party

class AppState {
    var selectedLanguage by mutableStateOf("English")
    var mobileNumber by mutableStateOf("")
    var otpCode by mutableStateOf("1234")
    var businessProfile by mutableStateOf(SampleData.defaultBusiness)
    var selectedPartyId by mutableStateOf(SampleData.parties.first().id)

    val parties: List<Party> = SampleData.parties

    fun updateProfile(profile: BusinessProfile) {
        businessProfile = profile
    }

    fun selectParty(partyId: String) {
        selectedPartyId = partyId
    }

    fun selectedParty(): Party = parties.first { it.id == selectedPartyId }
}
