package com.forvill.hisaabkhataa.data.network

import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.Party

// This contract is intentionally simple so a future PHP/JSON API can slot in without changing UI screens.
interface LedgerApiService {
    suspend fun fetchBusinessProfile(): BusinessProfile
    suspend fun fetchParties(): List<Party>
    suspend fun postLedgerEntry(partyId: String, draft: AddEntryDraft): ApiResult<Unit>
}

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String) : ApiResult<Nothing>
}
