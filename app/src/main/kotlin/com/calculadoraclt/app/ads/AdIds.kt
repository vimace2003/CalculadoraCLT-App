package com.calculadoraclt.app.ads

import com.calculadoraclt.app.BuildConfig

/**
 * Em builds de debug (desenvolvimento, CI, emulador) sempre usa os IDs de TESTE oficiais
 * do Google — nunca os reais, para não gerar tráfego suspeito na conta AdMob de verdade
 * (mesmo sem clicar, carregar anúncios reais repetidamente durante testes pode ser sinalizado
 * como atividade inválida). Os IDs reais só entram no build de release.
 */
object AdIds {
    private const val TESTE_BANNER = "ca-app-pub-3940256099942544/6300978111"
    private const val TESTE_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"

    private const val REAL_BANNER = "ca-app-pub-6996977326182038/8368723372"
    private const val REAL_INTERSTITIAL = "ca-app-pub-6996977326182038/6569235160"

    val BANNER: String = if (BuildConfig.DEBUG) TESTE_BANNER else REAL_BANNER
    val INTERSTITIAL: String = if (BuildConfig.DEBUG) TESTE_INTERSTITIAL else REAL_INTERSTITIAL
}
