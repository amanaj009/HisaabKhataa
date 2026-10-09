package com.forvill.hisaabkhataa.data.network

import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.core.model.AddEntryDraft
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.Party
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class HttpLedgerApiService : LedgerApiService {
    override suspend fun fetchBusinessProfile(): BusinessProfile {
        return SampleData.defaultBusiness
    }

    override suspend fun fetchParties(): List<Party> {
        return SampleData.parties
    }

    override suspend fun postLedgerEntry(partyId: String, draft: AddEntryDraft): ApiResult<Unit> {
        return try {
            val url = URL(ApiConfig.baseUrl + ApiConfig.ledgerSyncPath)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            val json = """
                {
                  "party_id": "${partyId}",
                  "type": "${draft.type.name.lowercase()}",
                  "amount": "${draft.amount}",
                  "note": "${draft.note.replace("\"", "\\\"")}",
                  "due_date_label": "${draft.dueDateLabel.replace("\"", "\\\"")}",
                  "due_date_value": "${draft.dueDateValue.replace("\"", "\\\"")}"
                }
            """.trimIndent()
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(json)
            }
            if (connection.responseCode in 200..299) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error("API sync failed with HTTP ${connection.responseCode}")
            }
        } catch (error: Throwable) {
            ApiResult.Error(error.message ?: "Unknown network error")
        }
    }
}
