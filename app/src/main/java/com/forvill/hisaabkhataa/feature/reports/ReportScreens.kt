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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.forvill.hisaabkhataa.core.i18n.AppStrings
import com.forvill.hisaabkhataa.core.model.ReportCard
import com.forvill.hisaabkhataa.core.model.ReportExport
import com.forvill.hisaabkhataa.core.model.ReportFilter

@Composable
fun ReportsScreen(
    strings: AppStrings,
    reports: List<ReportCard>,
    filter: ReportFilter,
    lastExport: ReportExport?,
    onRangeKeyChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onExport: () -> Unit,
) {
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
                Text("Date-range report aur local CSV export dono isi screen se handle honge.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Filter range", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = filter.rangeKey == "today", onClick = { onRangeKeyChange("today") }, label = { Text("Today") })
                        FilterChip(selected = filter.rangeKey == "7d", onClick = { onRangeKeyChange("7d") }, label = { Text("7 days") })
                        FilterChip(selected = filter.rangeKey == "30d", onClick = { onRangeKeyChange("30d") }, label = { Text("30 days") })
                        FilterChip(selected = filter.rangeKey == "all", onClick = { onRangeKeyChange("all") }, label = { Text("All") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = filter.rangeKey == "custom", onClick = { onRangeKeyChange("custom") }, label = { Text("Custom") })
                    }
                    if (filter.rangeKey == "custom") {
                        OutlinedTextField(
                            value = filter.startDate,
                            onValueChange = onStartDateChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Start date (YYYY-MM-DD)") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = filter.endDate,
                            onValueChange = onEndDateChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("End date (YYYY-MM-DD)") },
                            singleLine = true,
                        )
                    }
                    Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) {
                        Text("Export local CSV")
                    }
                }
            }
        }
        if (lastExport != null) {
            item {
                Card(shape = RoundedCornerShape(22.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Latest export", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(lastExport.fileName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Rows: ${lastExport.lineCount}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Generated: ${lastExport.generatedAtLabel}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(lastExport.filePath, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        items(reports) { report ->
            Card(shape = RoundedCornerShape(22.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(report.title, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(report.value.replace("â‚¹", "Rs. "), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(report.caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
