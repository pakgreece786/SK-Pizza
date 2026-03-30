package com.skpizza.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ✅ AdMob Init
        MobileAds.initialize(this, initializationStatus -> {});

        // ✅ Banner Ad
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // ✅ Load Interstitial
        loadInterstitialAd();

        // ✅ WebView Setup
        webView = findViewById(R.id.webview);
        WebSettings ws = webView.getSettings();

        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);

        webView.setWebChromeClient(new WebChromeClient());

        // 🔥 CONNECT HTML → ANDROID
        webView.addJavascriptInterface(new JSBridge(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                String url = request.getUrl().toString();

                // WhatsApp / Call
                if (url.startsWith("https://wa.me/") || url.startsWith("tel:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    handleClick();
                    return true;
                }

                // Play Store
                if (url.contains("play.google.com")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    handleClick();
                    return true;
                }

                // Count normal clicks
                handleClick();

                return false;
            }
        });

        webView.loadUrl("file:///android_asset/index.html");
    }

    // =========================
    // CLICK HANDLER (GLOBAL)
    // =========================
    private void handleClick() {
        clickCount++;

        if (clickCount % 3 == 0) {
            showInterstitialAd();
        }
    }

    // =========================
    // JS BRIDGE (FROM HTML)
    // =========================
    public class JSBridge {
        @JavascriptInterface
        public void notifyClick() {
            runOnUiThread(() -> handleClick());
        }
    }

    // =========================
    // INTERSTITIAL ADS
    // =========================
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712", // TEST ID
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd ad) {
                        interstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError error) {
                        interstitialAd = null;
                    }
                });
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(MainActivity.this);
            loadInterstitialAd(); // preload next
        }
    }

    // =========================
    // BACK BUTTON (WITH EXIT POPUP)
    // =========================
    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }

        // 🔥 EXIT CONFIRM DIALOG
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    showInterstitialAd();

                    // small delay before exit
                    webView.postDelayed(() -> finish(), 500);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
