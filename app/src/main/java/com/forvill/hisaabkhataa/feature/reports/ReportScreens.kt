package com.forvill.hisaabkhataa.feature.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun ReportsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Reports", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("A merchant-friendly summary of collection, pending balances, and business trend.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(SampleData.reports.chunked(2)) { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { report ->
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(22.dp)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(report.title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(report.value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(report.caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Column(modifier = Modifier.weight(1f)) {}
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(24.dp)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("What we can add next", fontWeight = FontWeight.Bold)
                    Text("1. PDF report export", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("2. Date-range filtering", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("3. Separate cashbook and khata reports", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
