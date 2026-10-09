package com.forvill.hisaabkhataa.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.core.model.AppSettings
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.LedgerEntry
import com.forvill.hisaabkhataa.core.model.ExpenseType
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.SyncQueueItem
import com.forvill.hisaabkhataa.core.model.TransactionExpense
import com.forvill.hisaabkhataa.core.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LedgerSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
        seedSampleData(db)
        seedBusinessProfile(db)
        seedAppSettings(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            createTables(db)
            addColumnIfMissing(db, "ledger_entries", "created_at", "INTEGER NOT NULL DEFAULT 0")
            seedBusinessProfile(db)
            seedAppSettings(db)
        }
        if (oldVersion < 3) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS expenses (
                    id TEXT PRIMARY KEY,
                    amount INTEGER NOT NULL,
                    note TEXT NOT NULL,
                    category_id TEXT NOT NULL,
                    type TEXT NOT NULL,
                    timestamp INTEGER NOT NULL,
                    merchant TEXT,
                    is_auto_generated INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent(),
            )
        }
    }

    fun loadParties(): List<Party> {
        val db = readableDatabase
        val partyCursor = db.rawQuery("SELECT * FROM parties ORDER BY name ASC", null)
        val parties = mutableListOf<Party>()
        partyCursor.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
                parties += Party(
                    id = id,
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                    balance = cursor.getInt(cursor.getColumnIndexOrThrow("balance")),
                    lastActivity = cursor.getString(cursor.getColumnIndexOrThrow("last_activity")),
                    creditLimit = cursor.getInt(cursor.getColumnIndexOrThrow("credit_limit")),
                    tags = cursor.getString(cursor.getColumnIndexOrThrow("tags_csv")).split(',').map { it.trim() }.filter { it.isNotBlank() },
                    entries = loadEntriesForParty(db, id),
                )
            }
        }
        return parties
    }

    fun replaceAll(parties: List<Party>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("ledger_entries", null, null)
            db.delete("parties", null, null)
            parties.forEach { party ->
                db.insert("parties", null, ContentValues().apply {
                    put("id", party.id)
                    put("name", party.name)
                    put("phone", party.phone)
                    put("balance", party.balance)
                    put("last_activity", party.lastActivity)
                    put("credit_limit", party.creditLimit)
                    put("tags_csv", party.tags.joinToString(", "))
                })
                party.entries.forEach { entry ->
                    db.insert("ledger_entries", null, entryValues(party.id, entry))
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun loadBusinessProfile(): BusinessProfile {
        val cursor = readableDatabase.rawQuery("SELECT * FROM business_profile WHERE id = 1", null)
        cursor.use {
            if (it.moveToFirst()) {
                return BusinessProfile(
                    ownerName = it.getString(it.getColumnIndexOrThrow("owner_name")),
                    businessName = it.getString(it.getColumnIndexOrThrow("business_name")),
                    businessType = it.getString(it.getColumnIndexOrThrow("business_type")),
                    city = it.getString(it.getColumnIndexOrThrow("city")),
                )
            }
        }
        return SampleData.defaultBusiness
    }

    fun saveBusinessProfile(profile: BusinessProfile): BusinessProfile {
        writableDatabase.insertWithOnConflict(
            "business_profile",
            null,
            ContentValues().apply {
                put("id", 1)
                put("owner_name", profile.ownerName)
                put("business_name", profile.businessName)
                put("business_type", profile.businessType)
                put("city", profile.city)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
        return loadBusinessProfile()
    }

    fun loadAppSettings(): AppSettings {
        val cursor = readableDatabase.rawQuery("SELECT * FROM app_settings WHERE id = 1", null)
        cursor.use {
            if (it.moveToFirst()) {
                return AppSettings(
                    selectedLanguage = it.getString(it.getColumnIndexOrThrow("selected_language")),
                    smartRemindersEnabled = it.getInt(it.getColumnIndexOrThrow("smart_reminders_enabled")) == 1,
                )
            }
        }
        return AppSettings()
    }

    fun saveAppSettings(settings: AppSettings): AppSettings {
        writableDatabase.insertWithOnConflict(
            "app_settings",
            null,
            ContentValues().apply {
                put("id", 1)
                put("selected_language", settings.selectedLanguage)
                put("smart_reminders_enabled", if (settings.smartRemindersEnabled) 1 else 0)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
        return loadAppSettings()
    }

    fun enqueueSyncOperation(
        operationType: String,
        entityId: String,
        payloadPreview: String,
    ) {
        writableDatabase.insert(
            "sync_queue",
            null,
            ContentValues().apply {
                put("id", "${operationType.lowercase(Locale.US)}-${System.currentTimeMillis()}")
                put("operation_type", operationType)
                put("entity_id", entityId)
                put("payload_preview", payloadPreview)
                put("status", "pending")
                put("retry_count", 0)
                put("last_attempt_at", 0L)
                put("next_retry_at", System.currentTimeMillis())
                put("last_error", "")
                put("created_at", System.currentTimeMillis())
            },
        )
    }

    fun loadSyncQueue(): List<SyncQueueItem> {
        val cursor = readableDatabase.rawQuery("SELECT * FROM sync_queue ORDER BY created_at DESC", null)
        val items = mutableListOf<SyncQueueItem>()
        cursor.use {
            while (it.moveToNext()) {
                items += SyncQueueItem(
                    id = it.getString(it.getColumnIndexOrThrow("id")),
                    operationType = it.getString(it.getColumnIndexOrThrow("operation_type")),
                    entityId = it.getString(it.getColumnIndexOrThrow("entity_id")),
                    payloadPreview = it.getString(it.getColumnIndexOrThrow("payload_preview")),
                    status = it.getString(it.getColumnIndexOrThrow("status")),
                    retryCount = it.getInt(it.getColumnIndexOrThrow("retry_count")),
                    lastAttemptLabel = formatTimestamp(it.getLong(it.getColumnIndexOrThrow("last_attempt_at"))),
                    nextRetryLabel = formatTimestamp(it.getLong(it.getColumnIndexOrThrow("next_retry_at"))),
                    lastError = it.getStringOrNull("last_error")?.ifBlank { null },
                )
            }
        }
        return items
    }

    fun retrySyncQueue(): List<SyncQueueItem> {
        val db = writableDatabase
        val now = System.currentTimeMillis()
        val cursor = db.rawQuery("SELECT id, retry_count FROM sync_queue WHERE status IN ('pending', 'deferred')", null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow("id"))
                val retryCount = it.getInt(it.getColumnIndexOrThrow("retry_count")) + 1
                val backoffMinutes = when {
                    retryCount <= 1 -> 5L
                    retryCount == 2 -> 15L
                    retryCount == 3 -> 30L
                    else -> 60L
                }
                db.update(
                    "sync_queue",
                    ContentValues().apply {
                        put("status", "deferred")
                        put("retry_count", retryCount)
                        put("last_attempt_at", now)
                        put("next_retry_at", now + backoffMinutes * 60_000L)
                        put("last_error", "Local-only mode active. Remote sync disabled for now.")
                    },
                    "id = ?",
                    arrayOf(id),
                )
            }
        }
        return loadSyncQueue()
    }

    fun loadExpenses(): List<TransactionExpense> {
        val cursor = readableDatabase.rawQuery("SELECT * FROM expenses ORDER BY timestamp DESC", null)
        val expenses = mutableListOf<TransactionExpense>()
        cursor.use {
            while (it.moveToNext()) {
                expenses += TransactionExpense(
                    id = it.getString(it.getColumnIndexOrThrow("id")),
                    amount = it.getInt(it.getColumnIndexOrThrow("amount")),
                    note = it.getString(it.getColumnIndexOrThrow("note")),
                    categoryId = it.getString(it.getColumnIndexOrThrow("category_id")),
                    type = if (it.getString(it.getColumnIndexOrThrow("type")).equals("income", true)) ExpenseType.Income else ExpenseType.Expense,
                    timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp")),
                    merchant = it.getStringOrNull("merchant"),
                    isAutoGenerated = it.getInt(it.getColumnIndexOrThrow("is_auto_generated")) == 1,
                )
            }
        }
        return expenses
    }

    fun saveExpenses(expenses: List<TransactionExpense>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            expenses.forEach { expense ->
                db.insertWithOnConflict(
                    "expenses",
                    null,
                    ContentValues().apply {
                        put("id", expense.id)
                        put("amount", expense.amount)
                        put("note", expense.note)
                        put("category_id", expense.categoryId)
                        put("type", expense.type.name)
                        put("timestamp", expense.timestamp)
                        put("merchant", expense.merchant)
                        put("is_auto_generated", if (expense.isAutoGenerated) 1 else 0)
                    },
                    SQLiteDatabase.CONFLICT_REPLACE,
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun createTables(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS parties (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                balance INTEGER NOT NULL,
                last_activity TEXT NOT NULL,
                credit_limit INTEGER NOT NULL,
                tags_csv TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS ledger_entries (
                id TEXT PRIMARY KEY,
                party_id TEXT NOT NULL,
                title TEXT NOT NULL,
                note TEXT NOT NULL,
                amount INTEGER NOT NULL,
                type TEXT NOT NULL,
                time_label TEXT NOT NULL,
                created_at INTEGER NOT NULL DEFAULT 0,
                due_date_label TEXT,
                due_date_value TEXT
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS business_profile (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                owner_name TEXT NOT NULL,
                business_name TEXT NOT NULL,
                business_type TEXT NOT NULL,
                city TEXT NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS app_settings (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                selected_language TEXT NOT NULL,
                smart_reminders_enabled INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sync_queue (
                id TEXT PRIMARY KEY,
                operation_type TEXT NOT NULL,
                entity_id TEXT NOT NULL,
                payload_preview TEXT NOT NULL,
                status TEXT NOT NULL,
                retry_count INTEGER NOT NULL,
                last_attempt_at INTEGER NOT NULL,
                next_retry_at INTEGER NOT NULL,
                last_error TEXT,
                created_at INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS expenses (
                id TEXT PRIMARY KEY,
                amount INTEGER NOT NULL,
                note TEXT NOT NULL,
                category_id TEXT NOT NULL,
                type TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                merchant TEXT,
                is_auto_generated INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent(),
        )
    }

    private fun loadEntriesForParty(db: SQLiteDatabase, partyId: String): List<LedgerEntry> {
        val cursor = db.rawQuery("SELECT * FROM ledger_entries WHERE party_id = ? ORDER BY created_at DESC, id DESC", arrayOf(partyId))
        val entries = mutableListOf<LedgerEntry>()
        cursor.use {
            while (it.moveToNext()) {
                entries += LedgerEntry(
                    id = it.getString(it.getColumnIndexOrThrow("id")),
                    title = it.getString(it.getColumnIndexOrThrow("title")),
                    note = it.getString(it.getColumnIndexOrThrow("note")),
                    amount = it.getInt(it.getColumnIndexOrThrow("amount")),
                    type = if (it.getString(it.getColumnIndexOrThrow("type")).equals("payment", true)) TransactionType.Payment else TransactionType.Udhaar,
                    time = it.getString(it.getColumnIndexOrThrow("time_label")),
                    createdAtMillis = it.getLong(it.getColumnIndexOrThrow("created_at")),
                    dueDateLabel = it.getStringOrNull("due_date_label"),
                    dueDateValue = it.getStringOrNull("due_date_value"),
                )
            }
        }
        return entries
    }

    private fun seedSampleData(db: SQLiteDatabase) {
        val countCursor = db.rawQuery("SELECT COUNT(*) FROM parties", null)
        countCursor.use {
            if (it.moveToFirst() && it.getInt(0) > 0) return
        }
        SampleData.parties.forEach { party ->
            db.insert("parties", null, ContentValues().apply {
                put("id", party.id)
                put("name", party.name)
                put("phone", party.phone)
                put("balance", party.balance)
                put("last_activity", party.lastActivity)
                put("credit_limit", party.creditLimit)
                put("tags_csv", party.tags.joinToString(", "))
            })
            party.entries.forEach { entry ->
                db.insert("ledger_entries", null, entryValues(party.id, entry))
            }
        }
    }

    private fun seedBusinessProfile(db: SQLiteDatabase) {
        db.insertWithOnConflict(
            "business_profile",
            null,
            ContentValues().apply {
                put("id", 1)
                put("owner_name", SampleData.defaultBusiness.ownerName)
                put("business_name", SampleData.defaultBusiness.businessName)
                put("business_type", SampleData.defaultBusiness.businessType)
                put("city", SampleData.defaultBusiness.city)
            },
            SQLiteDatabase.CONFLICT_IGNORE,
        )
    }

    private fun seedAppSettings(db: SQLiteDatabase) {
        db.insertWithOnConflict(
            "app_settings",
            null,
            ContentValues().apply {
                put("id", 1)
                put("selected_language", "English")
                put("smart_reminders_enabled", 1)
            },
            SQLiteDatabase.CONFLICT_IGNORE,
        )
    }

    private fun addColumnIfMissing(db: SQLiteDatabase, tableName: String, columnName: String, definition: String) {
        val columns = db.rawQuery("PRAGMA table_info($tableName)", null)
        columns.use {
            while (it.moveToNext()) {
                if (it.getString(it.getColumnIndexOrThrow("name")) == columnName) {
                    return
                }
            }
        }
        db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $definition")
    }

    private fun entryValues(partyId: String, entry: LedgerEntry): ContentValues {
        return ContentValues().apply {
            put("id", entry.id)
            put("party_id", partyId)
            put("title", entry.title)
            put("note", entry.note)
            put("amount", entry.amount)
            put("type", entry.type.name)
            put("time_label", entry.time)
            put("created_at", entry.createdAtMillis)
            put("due_date_label", entry.dueDateLabel)
            put("due_date_value", entry.dueDateValue)
        }
    }

    private fun formatTimestamp(value: Long): String {
        if (value <= 0L) return "Not tried yet"
        return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date(value))
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val index = getColumnIndex(columnName)
        if (index < 0 || isNull(index)) return null
        return getString(index)
    }

    companion object {
        private const val DB_NAME = "hisaab_khataa.sqlite"
        private const val DB_VERSION = 3
    }
}
