package com.skpizza.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.LoadAdError;

public class IntroActivity extends AppCompatActivity {

    private AppOpenAd appOpenAd;
    private boolean isAdShowing = false;
    private boolean isAdLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 🔥 Load App Open Ad
        loadAppOpenAd();

        // ⏱ Splash delay (2 sec max)
        new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                showAdIfAvailable();
            }
        }.start();
    }

    // =========================
    // 🔥 Load App Open Ad
    // =========================
    private void loadAppOpenAd() {
        AdRequest request = new AdRequest.Builder().build();

        AppOpenAd.load(
                this,
                "ca-app-pub-XXXXXXXXXXXXXXXX/OOOOOOOOOO", // 🔥 REPLACE
                request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isAdLoaded = true;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        isAdLoaded = false;
                        goToMain();
                    }
                });
    }

    // =========================
    // 🚀 Show Ad if ready
    // =========================
    private void showAdIfAvailable() {
        if (isAdLoaded && appOpenAd != null && !isAdShowing) {

            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isAdShowing = false;
                    goToMain();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    isAdShowing = false;
                    goToMain();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isAdShowing = true;
                }
            });

            appOpenAd.show(this);

        } else {
            goToMain();
        }
    }

    // =========================
    // ➡️ Go to MainActivity
    // =========================
    private void goToMain() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }
}
