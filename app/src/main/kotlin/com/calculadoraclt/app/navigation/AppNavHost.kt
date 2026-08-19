package com.calculadoraclt.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.calculadoraclt.app.feature.decimoterceiro.DecimoTerceiroScreen
import com.calculadoraclt.app.feature.fgts.FgtsScreen
import com.calculadoraclt.app.feature.ferias.FeriasScreen
import com.calculadoraclt.app.feature.folhapagamento.FolhaPagamentoScreen
import com.calculadoraclt.app.feature.home.HomeScreen
import com.calculadoraclt.app.feature.horasextras.HorasExtrasScreen
import com.calculadoraclt.app.feature.reajustes.ReajustesScreen
import com.calculadoraclt.app.feature.rescisao.RescisaoScreen
import com.calculadoraclt.app.feature.salarioliquido.SalarioLiquidoScreen
import com.calculadoraclt.app.feature.salarioporhora.SalarioPorHoraScreen
import com.calculadoraclt.app.feature.segurodesemprego.SeguroDesempregoScreen
import com.calculadoraclt.app.feature.tempoentredatas.TempoEntreDatasScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(onNavigateTo = { navController.navigate(it) })
        }
        composable<TempoEntreDatasRoute> {
            TempoEntreDatasScreen(onNavigateBack = navController::popBackStack)
        }
        composable<SalarioPorHoraRoute> {
            SalarioPorHoraScreen(onNavigateBack = navController::popBackStack)
        }
        composable<FgtsRoute> {
            FgtsScreen(onNavigateBack = navController::popBackStack)
        }
        composable<ReajustesRoute> {
            ReajustesScreen(onNavigateBack = navController::popBackStack)
        }
        composable<HorasExtrasRoute> {
            HorasExtrasScreen(onNavigateBack = navController::popBackStack)
        }
        composable<SalarioLiquidoRoute> {
            SalarioLiquidoScreen(onNavigateBack = navController::popBackStack)
        }
        composable<DecimoTerceiroRoute> {
            DecimoTerceiroScreen(onNavigateBack = navController::popBackStack)
        }
        composable<FeriasRoute> {
            FeriasScreen(onNavigateBack = navController::popBackStack)
        }
        composable<SeguroDesempregoRoute> {
            SeguroDesempregoScreen(onNavigateBack = navController::popBackStack)
        }
        composable<FolhaPagamentoRoute> {
            FolhaPagamentoScreen(onNavigateBack = navController::popBackStack)
        }
        composable<RescisaoRoute> {
            RescisaoScreen(onNavigateBack = navController::popBackStack)
        }
    }
}
