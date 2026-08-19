package com.calculadoraclt.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ResultLine(
    val label: String,
    val value: String,
    val destaque: Boolean = false,
)

@Composable
fun ResultSummaryCard(
    titulo: String,
    linhas: List<ResultLine>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            linhas.forEachIndexed { index, linha ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = linha.label,
                        style = if (linha.destaque) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = linha.value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (linha.destaque) FontWeight.Bold else FontWeight.Normal,
                    )
                }
                if (index != linhas.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
