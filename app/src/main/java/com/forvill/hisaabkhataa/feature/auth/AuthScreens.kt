package com.forvill.hisaabkhataa.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forvill.hisaabkhataa.core.model.BusinessProfile

@Composable
fun MobileLoginScreen(
    mobileNumber: String,
    onMobileChange: (String) -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text(
            text = "Mobile login",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Use your phone number to access customer ledgers, reminders, and reports.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Card(shape = RoundedCornerShape(28.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = onMobileChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mobile number") },
                    prefix = { Text("+91 ") },
                    singleLine = true,
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onContinue,
                ) {
                    Text("Send OTP")
                }
            }
        }
    }
}

@Composable
fun OtpScreen(
    otpCode: String,
    onOtpChange: (String) -> Unit,
    onVerify: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "OTP verification",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Enter the 4-digit code sent to your mobile. For this prototype, any 4 digits can work.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(4) { index ->
                val value = otpCode.getOrNull(index)?.toString() ?: ""
                OutlinedTextField(
                    value = value,
                    onValueChange = { digit ->
                        val clean = digit.takeLast(1)
                        val chars = otpCode.padEnd(4, ' ').toCharArray()
                        chars[index] = clean.firstOrNull() ?: ' '
                        onOtpChange(chars.concatToString().trim())
                    },
                    modifier = Modifier.width(72.dp),
                    singleLine = true,
                )
            }
        }
        Button(onClick = onVerify, modifier = Modifier.fillMaxWidth()) {
            Text("Verify & continue")
        }
    }
}

@Composable
fun BusinessSetupScreen(
    profile: BusinessProfile,
    onProfileChange: (BusinessProfile) -> Unit,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Business setup",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "This step captures the basic merchant identity we will show across receipts, reminders, and reports.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = profile.ownerName,
            onValueChange = { onProfileChange(profile.copy(ownerName = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Owner name") },
            singleLine = true,
        )
        OutlinedTextField(
            value = profile.businessName,
            onValueChange = { onProfileChange(profile.copy(businessName = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Business name") },
            singleLine = true,
        )
        OutlinedTextField(
            value = profile.businessType,
            onValueChange = { onProfileChange(profile.copy(businessType = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Business type") },
        )
        OutlinedTextField(
            value = profile.city,
            onValueChange = { onProfileChange(profile.copy(city = it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("City") },
            singleLine = true,
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onContinue,
        ) {
            Text("Enter dashboard")
        }
    }
}
