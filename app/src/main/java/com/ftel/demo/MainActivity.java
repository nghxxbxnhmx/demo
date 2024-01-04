package com.ftel.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button pingButton, testButton, chartButton, wifiInfoButton, tracertButton, pageLoadTimerButton, nsLookupButton, speedTestButton, portScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        pingButton = findViewById(R.id.pingButton);
        testButton = findViewById(R.id.testButton);
        chartButton = findViewById(R.id.chartButton);
        wifiInfoButton = findViewById(R.id.wifiInfoButton);
        tracertButton = findViewById(R.id.tracertButton);
        pageLoadTimerButton = findViewById(R.id.pageLoadTimerButton);
        nsLookupButton = findViewById(R.id.nsLookupButton);
        speedTestButton = findViewById(R.id.speedTestButton);
        portScanButton = findViewById(R.id.portScanButton);

        applyGradient(pingButton, "#1DB954", "#191414");
        applyGradient(testButton, "#1DB954", "#191414");
        applyGradient(chartButton, "#1DB954", "#191414");
        applyGradient(wifiInfoButton, "#1DB954", "#191414");
        applyGradient(tracertButton, "#1DB954", "#191414");
        applyGradient(pageLoadTimerButton, "#1DB954", "#191414");
        applyGradient(nsLookupButton, "#1DB954", "#191414");
        applyGradient(speedTestButton, "#1DB954", "#191414");
        applyGradient(portScanButton, "#1DB954", "#191414");

        pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PingActivity.class));
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanWifiActivity.class));
            }
        });

        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChartActivity.class));
            }
        });

        wifiInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WifiInfoActivity.class));
            }
        });

        tracertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TracertActivity.class));
            }
        });

        pageLoadTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PageLoadTimerActivity.class));
            }
        });

        speedTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SpeedTestActivity.class));
            }
        });

        nsLookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NsLookupActivity.class));
            }
        });

        nsLookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NsLookupActivity.class));
            }
        });

        portScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PortScanActivity.class));
            }
        });

    }
    private void applyGradient(Button button, String startColor, String endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(startColor), Color.parseColor(endColor)});
        gradientDrawable.setCornerRadius(8f);
        button.setBackground(gradientDrawable);
    }
}