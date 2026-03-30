package com.skpizza.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AboutActivity extends AppCompatActivity {

    private AdView adView; // 🔥 AdMob banner (optional)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // 🔙 Action bar setup
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("About");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView tvAbout = findViewById(R.id.tv_about);

        // ✅ Get app version (Android 13+ safe)
        String version = "1.0";
        try {
            PackageInfo pInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pInfo = getPackageManager().getPackageInfo(
                        getPackageName(),
                        PackageManager.PackageInfoFlags.of(0)
                );
            } else {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            }
            version = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ About text
        String aboutText =
                "🍕 SK PIZZA\n\n" +
                "Fresh, fast & flavourful food delivered to your door.\n\n" +
                "Version: " + version + "\n\n" +
                "📍 Gujrat, Pakistan\n\n" +
                "© 2026 SK Pizza. All rights reserved.";

        tvAbout.setText(aboutText);

        // =========================
        // 🔥 AdMob Banner (optional)
        // =========================
        adView = findViewById(R.id.adViewAbout);

        if (adView != null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
    }

    // 🔙 Back button (toolbar)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
