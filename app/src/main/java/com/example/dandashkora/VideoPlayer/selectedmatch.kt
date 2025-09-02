package com.example.dandashkora.VideoPlayer



import android.annotation.SuppressLint

import android.content.pm.ActivityInfo

import android.net.Uri

import android.os.Bundle

import android.view.View

import android.view.ViewGroup

import android.widget.ImageButton

import androidx.activity.enableEdgeToEdge

import androidx.annotation.OptIn

import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.WindowCompat

import androidx.media3.common.MediaItem

import androidx.media3.common.util.UnstableApi

import androidx.media3.exoplayer.ExoPlayer

import androidx.media3.ui.AspectRatioFrameLayout

import androidx.media3.ui.PlayerView

import com.example.dandashkora.R

import com.google.android.gms.ads.AdRequest

import com.google.android.gms.ads.LoadAdError

import com.google.android.gms.ads.MobileAds

import com.google.android.gms.ads.RequestConfiguration

import com.google.android.gms.ads.interstitial.InterstitialAd

import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback





class selectedmatch : AppCompatActivity() {



    private var player: ExoPlayer? = null

    private lateinit var playerView: PlayerView

    private lateinit var btnPlayPause: ImageButton

    private lateinit var btnFullscreen: ImageButton

    private lateinit var btnQuality: ImageButton



    private var isFullscreen = false

    private var matchServers: HashMap<String, HashMap<String, String>> = hashMapOf()

    private var currentServer: String = ""

    private var currentUrl: String? = null



// AdMob

    private var mInterstitialAd: InterstitialAd? = null

    private var adShown = false // To ensure the ad shows only once



    @OptIn(UnstableApi::class)

    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_selectedmatch)



        playerView = findViewById(R.id.playerView)

        btnPlayPause = findViewById(R.id.btnPlayPause)

        btnFullscreen = findViewById(R.id.btnFullscreen)

        btnQuality = findViewById(R.id.btnQuality)



// Get servers map from intent safely

        matchServers = (intent.getSerializableExtra("MATCH_SERVERS") as? HashMap<String, HashMap<String, String>>)

            ?: hashMapOf()



        if (matchServers.isNotEmpty()) {

            currentServer = matchServers.keys.first()

            currentUrl = matchServers[currentServer]?.values?.firstOrNull()

        }



// Initialize AdMob

        MobileAds.initialize(this) {}



// Set test device

        val testDeviceIds = listOf("aee17b89-8f77-43d9-b426-10ca1ad1abe1")

        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()

        MobileAds.setRequestConfiguration(configuration)



        loadInterstitialAd() // Load the ad before any video



        btnFullscreen.setOnClickListener { toggleFullscreen() }

        btnPlayPause.setOnClickListener { togglePlayPause() }

        btnQuality.setOnClickListener { showServerDialog() }

    }



    private fun loadInterstitialAd() {

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(

            this,

            "ca-app-pub-3760228049158676/8878512156", // Your Ad Unit ID

            adRequest,

            object : InterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(adError: LoadAdError) {

                    mInterstitialAd = null

// If ad fails → play video immediately

                    playVideoAfterAd()

                }



                override fun onAdLoaded(interstitialAd: InterstitialAd) {

                    mInterstitialAd = interstitialAd

                    showInterstitialAd()

                }

            }

        )

    }



    private fun showInterstitialAd() {

        if (!adShown && mInterstitialAd != null) {

            adShown = true

            mInterstitialAd?.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {

                    mInterstitialAd = null

                    playVideoAfterAd()

                }



                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {

                    playVideoAfterAd()

                }

            }

            mInterstitialAd?.show(this)

        } else {

            playVideoAfterAd()

        }

    }



    private fun playVideoAfterAd() {

// Video only starts after ad is dismissed or failed

        currentUrl?.let { initializePlayer(it, autoPlay = true) }

    }



    private fun initializePlayer(url: String, autoPlay: Boolean = true) {

        player = ExoPlayer.Builder(this).build()

        playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(url))

        player?.setMediaItem(mediaItem)

        player?.prepare()

        player?.playWhenReady = autoPlay

    }



    private fun togglePlayPause() {

        player?.let {

            if (it.isPlaying) {

                it.pause()

                btnPlayPause.setImageResource(R.drawable.play_ico)

            } else {

                it.play()

                btnPlayPause.setImageResource(R.drawable.pasue_ico)

            }

        }

    }



    @UnstableApi

    private fun toggleFullscreen() {

        if (isFullscreen) exitFullscreen() else enterFullscreen()

    }



    private fun showServerDialog() {

        val servers = matchServers.keys.toTypedArray()

        if (servers.isEmpty()) return

        AlertDialog.Builder(this)

            .setTitle("Select Server")

            .setItems(servers) { _, index ->

                currentServer = servers[index]

                showQualityDialog()

            }

            .show()

    }



    private fun showQualityDialog() {

        val qualities = matchServers[currentServer]?.keys?.toTypedArray() ?: return

        AlertDialog.Builder(this)

            .setTitle("Select Quality")

            .setItems(qualities) { _, index ->

                val selectedQuality = qualities[index]

                val url = matchServers[currentServer]?.get(selectedQuality)

                if (!url.isNullOrEmpty()) {

                    currentUrl = url

                    player?.setMediaItem(MediaItem.fromUri(Uri.parse(currentUrl)), true)

                    player?.prepare()

                    player?.playWhenReady = true

                }

            }

            .show()

    }



    @UnstableApi

    private fun enterFullscreen() {

        window.decorView.systemUiVisibility =

            (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)



        val params = playerView.layoutParams

        params.width = ViewGroup.LayoutParams.MATCH_PARENT

        params.height = ViewGroup.LayoutParams.MATCH_PARENT

        playerView.layoutParams = params

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL



        btnFullscreen.setImageResource(R.drawable.exitfullscreen_ico)

        isFullscreen = true

    }



    @UnstableApi

    private fun exitFullscreen() {

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE



        val params = playerView.layoutParams

        params.width = ViewGroup.LayoutParams.MATCH_PARENT

        params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        playerView.layoutParams = params

        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT



        btnFullscreen.setImageResource(R.drawable.fullscreen_ico2)

        isFullscreen = false

    }



    override fun onStop() {

        super.onStop()

        player?.release()

        player = null

    }

}