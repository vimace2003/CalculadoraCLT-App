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
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

private const val VALIDADE_ANUNCIO_MS = 4 * 60 * 60 * 1000L

enum class LaunchAdUiState {
    OCULTO,
    MOSTRANDO_INTRODUCAO,
}

/**
 * Mostra um anúncio Rewarded Interstitial ao abrir o app / voltar do background.
 * Esse formato é o indicado pelo AdMob para "assistir por alguns segundos antes de
 * poder pular": a própria rede libera o botão de fechar só depois de um tempo mínimo.
 *
 * Política do AdMob para este formato exige uma tela de introdução com opção de pular
 * ANTES do anúncio começar (ver [LaunchAdUiState.MOSTRANDO_INTRODUCAO] + AdIntroOverlay.kt).
 */
class LaunchAdManager(
    private val application: Application,
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var loadTime: Long = 0L
    private var isLoadingAd = false
    private var isShowingAd = false
    private var currentActivity: Activity? = null

    private val _uiState = MutableStateFlow(LaunchAdUiState.OCULTO)
    val uiState: StateFlow<LaunchAdUiState> = _uiState.asStateFlow()

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun loadAd() {
        if (isLoadingAd || isAdAvailable()) return
        isLoadingAd = true
        RewardedInterstitialAd.load(
            application,
            AdIds.REWARDED_INTERSTITIAL,
            AdRequest.Builder().build(),
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
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
        rewardedInterstitialAd ?: return false
        return Date().time - loadTime < VALIDADE_ANUNCIO_MS
    }

    /** Chamado pela UI quando o usuário decide assistir ao anúncio na tela de introdução. */
    fun onUsuarioAceitouAssistir() {
        val activity = currentActivity
        val ad = rewardedInterstitialAd
        if (activity == null || ad == null) {
            _uiState.value = LaunchAdUiState.OCULTO
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedInterstitialAd = null
                isShowingAd = false
                _uiState.value = LaunchAdUiState.OCULTO
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedInterstitialAd = null
                isShowingAd = false
                _uiState.value = LaunchAdUiState.OCULTO
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        ad.show(activity) { /* recompensa ignorada: não há economia de créditos no app */ }
    }

    /** Chamado pela UI quando o usuário toca em "Pular" na tela de introdução. */
    fun onUsuarioPulou() {
        _uiState.value = LaunchAdUiState.OCULTO
    }

    override fun onStart(owner: LifecycleOwner) {
        if (isShowingAd) return
        if (isAdAvailable()) {
            _uiState.value = LaunchAdUiState.MOSTRANDO_INTRODUCAO
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
