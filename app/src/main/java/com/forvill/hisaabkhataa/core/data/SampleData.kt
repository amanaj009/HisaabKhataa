package com.forvill.hisaabkhataa.core.data

import android.graphics.Color
import com.forvill.hisaabkhataa.core.model.BusinessProfile
import com.forvill.hisaabkhataa.core.model.LedgerEntry
import com.forvill.hisaabkhataa.core.model.OnboardingPage
import com.forvill.hisaabkhataa.core.model.Party
import com.forvill.hisaabkhataa.core.model.ReminderItem
import com.forvill.hisaabkhataa.core.model.ReportCard
import com.forvill.hisaabkhataa.core.model.TransactionType
import java.util.Calendar

object SampleData {
    // Standard colors for the app
    val Clay = Color.parseColor("#B14944")
    val Moss = Color.parseColor("#4A6D5D")
    val Gold = Color.parseColor("#A68044")

    // Central sample dataset keeps UI work moving before API and persistence layers are added.
    val onboardingPages = listOf(
        OnboardingPage(
            title = "Track udhaar instantly",
            description = "Customer khata in a few taps with a clear red-green balance view.",
            accent = Clay,
        ),
        OnboardingPage(
            title = "Collect faster",
            description = "Friendly reminders and one-tap follow-up nudges for due customers.",
            accent = Moss,
        ),
        OnboardingPage(
            title = "See business pulse",
            description = "Daily cash, credit movement, and top pending parties in one glance.",
            accent = Gold,
        ),
    )

    val defaultBusiness = BusinessProfile(
        ownerName = "Aman Gupta",
        businessName = "Forvill Kirana Store",
        businessType = "Grocery & daily essentials",
        city = "Lucknow",
    )

    val parties = listOf(
        Party(
            id = "sharma-traders",
            name = "Sharma Traders",
            phone = "+91 98765 10234",
            balance = 4500,
            lastActivity = "2 hours ago",
            creditLimit = 12000,
            tags = listOf("Wholesale", "Regular"),
            entries = listOf(
                LedgerEntry("st-1", "Atta bags", "10 x 5kg", 3200, TransactionType.Udhaar, "Today, 10:15 AM", createdAtMillis = sampleTimestamp(0, 10, 15)),
                LedgerEntry("st-2", "UPI collection", "Partial payment", 1500, TransactionType.Payment, "Yesterday, 7:42 PM", createdAtMillis = sampleTimestamp(-1, 19, 42)),
                LedgerEntry("st-3", "Oil cartons", "3 x 15L", 2800, TransactionType.Udhaar, "Yesterday, 11:20 AM", createdAtMillis = sampleTimestamp(-1, 11, 20)),
            ),
        ),
        Party(
            id = "aman-dairy",
            name = "Aman Dairy",
            phone = "+91 99887 34122",
            balance = -2000,
            lastActivity = "Today",
            creditLimit = 5000,
            tags = listOf("Milk supplier"),
            entries = listOf(
                LedgerEntry("ad-1", "Morning milk bill", "14 packets", 2000, TransactionType.Payment, "Today, 6:20 AM", createdAtMillis = sampleTimestamp(0, 6, 20)),
                LedgerEntry("ad-2", "Previous due", "Cleared balance", 1800, TransactionType.Payment, "Yesterday, 8:00 PM", createdAtMillis = sampleTimestamp(-1, 20, 0)),
            ),
        ),
        Party(
            id = "rohit-hardware",
            name = "Rohit Hardware",
            phone = "+91 87654 21980",
            balance = 980,
            lastActivity = "1 day ago",
            creditLimit = 7000,
            tags = listOf("Occasional"),
            entries = listOf(
                LedgerEntry("rh-1", "Repair material", "Hooks and tape", 980, TransactionType.Udhaar, "Yesterday, 1:05 PM", createdAtMillis = sampleTimestamp(-1, 13, 5)),
            ),
        ),
    )

    val reminders = listOf(
        ReminderItem("r1", "Sharma Traders", 4500, "Due today", "WhatsApp"),
        ReminderItem("r2", "Rohit Hardware", 980, "Due in 2 days", "SMS"),
        ReminderItem("r3", "Kiran Cosmetics", 3100, "Overdue by 3 days", "Call"),
    )

    val reports = listOf(
        ReportCard("Today's collection", "₹12,450", "Cash + online received"),
        ReportCard("Outstanding due", "₹38,920", "Across 42 active parties"),
        ReportCard("This week sales", "₹1.82L", "11% better than last week"),
        ReportCard("Recovery rate", "74%", "Reminder flow is working well"),
    )
    private fun sampleTimestamp(dayOffset: Int, hour: Int, minute: Int): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, dayOffset)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
