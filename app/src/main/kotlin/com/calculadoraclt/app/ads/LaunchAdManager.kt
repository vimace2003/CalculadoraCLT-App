package com.calculadoraclt.app.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Date

private const val VALIDADE_ANUNCIO_MS = 4 * 60 * 60 * 1000L
private const val INTERVALO_MINIMO_ENTRE_EXIBICOES_MS = 5 * 60 * 1000L

/**
 * Mostra um anúncio Interstitial (tela cheia) ao abrir o app / voltar do background,
 * sem nenhuma tela de escolha antes — o anúncio aparece direto e só pode ser fechado
 * quando a própria rede libera o botão de fechar (alguns segundos após o início).
 *
 * Diferente do Rewarded Interstitial (formato usado antes), o Interstitial padrão não tem
 * exigência de tela de introdução com opção de pular — essa exigência do AdMob é específica
 * de formatos "recompensados". Ainda assim, respeita um intervalo mínimo entre exibições
 * ([INTERVALO_MINIMO_ENTRE_EXIBICOES_MS]) para não repetir o anúncio a cada troca rápida de
 * app, o que contaria como frequência abusiva pelas políticas de conteúdo do Google Play.
 */
class LaunchAdManager(
    private val application: Application,
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var interstitialAd: InterstitialAd? = null
    private var loadTime: Long = 0L
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null
    private var ultimaExibicaoEm: Long = 0L

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun loadAd() {
        if (isLoadingAd || isAdAvailable()) return
        isLoadingAd = true
        InterstitialAd.load(
            application,
            AdIds.INTERSTITIAL,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoadingAd = false
                }
            },
        )
    }

    private fun isAdAvailable(): Boolean {
        interstitialAd ?: return false
        return Date().time - loadTime < VALIDADE_ANUNCIO_MS
    }

    private fun mostrarAnuncio() {
        val activity = currentActivity ?: return
        val ad = interstitialAd ?: return

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                ultimaExibicaoEm = Date().time
            }
        }
        ad.show(activity)
    }

    override fun onStart(owner: LifecycleOwner) {
        if (isShowingAd) return
        val passouIntervaloMinimo = Date().time - ultimaExibicaoEm >= INTERVALO_MINIMO_ENTRE_EXIBICOES_MS
        if (isAdAvailable() && passouIntervaloMinimo) {
            mostrarAnuncio()
        } else {
            loadAd()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityResumed(activity: Activity) = Unit
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity === activity) currentActivity = null
    }
}
