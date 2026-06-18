package com.forvill.hisaabkhataa.feature.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forvill.hisaabkhataa.core.data.SampleData
import com.forvill.hisaabkhataa.core.i18n.AppStrings

@Composable
fun ReportsScreen(strings: AppStrings) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(strings.reportsTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("A merchant-friendly summary of collection, pending balances, and business trend.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(SampleData.reports) { report ->
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(report.title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(report.value.replace("₹", "Rs. "), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(report.caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
