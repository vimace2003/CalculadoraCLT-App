package com.calculadoraclt.app.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.calculadoraclt.app.navigation.DecimoTerceiroRoute
import com.calculadoraclt.app.navigation.FeriasRoute
import com.calculadoraclt.app.navigation.FgtsRoute
import com.calculadoraclt.app.navigation.FolhaPagamentoRoute
import com.calculadoraclt.app.navigation.HorasExtrasRoute
import com.calculadoraclt.app.navigation.ReajustesRoute
import com.calculadoraclt.app.navigation.RescisaoRoute
import com.calculadoraclt.app.navigation.SalarioLiquidoRoute
import com.calculadoraclt.app.navigation.SalarioPorHoraRoute
import com.calculadoraclt.app.navigation.SeguroDesempregoRoute
import com.calculadoraclt.app.navigation.TempoEntreDatasRoute

data class CalculatorItem(
    val route: Any,
    val titulo: String,
    val icone: ImageVector,
    val disponivel: Boolean,
)

val calculatorCatalog: List<CalculatorItem> = listOf(
    CalculatorItem(RescisaoRoute, "Rescisão", Icons.Filled.Description, disponivel = true),
    CalculatorItem(SalarioLiquidoRoute, "Salário Líquido", Icons.Filled.AccountBalanceWallet, disponivel = true),
    CalculatorItem(FolhaPagamentoRoute, "Folha de Pagamento", Icons.AutoMirrored.Filled.ReceiptLong, disponivel = true),
    CalculatorItem(FeriasRoute, "Férias", Icons.Filled.BeachAccess, disponivel = true),
    CalculatorItem(HorasExtrasRoute, "Horas Extras", Icons.Filled.Timer, disponivel = true),
    CalculatorItem(SeguroDesempregoRoute, "Seguro-Desemprego", Icons.Filled.Shield, disponivel = true),
    CalculatorItem(FgtsRoute, "FGTS", Icons.Filled.Savings, disponivel = true),
    CalculatorItem(DecimoTerceiroRoute, "13º Salário", Icons.Filled.CardGiftcard, disponivel = true),
    CalculatorItem(TempoEntreDatasRoute, "Tempo entre Datas", Icons.Filled.CalendarMonth, disponivel = true),
    CalculatorItem(ReajustesRoute, "Reajustes e Dissídios", Icons.AutoMirrored.Filled.TrendingUp, disponivel = true),
    CalculatorItem(SalarioPorHoraRoute, "Salário por Hora", Icons.Filled.Schedule, disponivel = true),
)
