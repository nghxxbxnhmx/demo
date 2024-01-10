package com.ftel.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/*import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;*/

public class ScanWifiActivity extends AppCompatActivity {
    private TextView textView;
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanwifi);

        textView = findViewById(R.id.textView);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            scanWifiAndShowResults();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanWifiAndShowResults();
        } else {

        }
    }
    private void scanWifiAndShowResults() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ArrayList<ScanResult> scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<DataInfo> data = new ArrayList<>();
        for(ScanResult i : scanResults) {
            DataInfo wifiInfo = new DataInfo();
            wifiInfo.ssid = i.SSID;
            wifiInfo.signalLevel = i.level;
            data.add(wifiInfo);
        }
        textView.setTextSize(18f);
        textView.setText(gson.toJson(data));
        //textView.setText(gson.toJson(scanResults));
    }

    public static class DataInfo {
        String ssid;
        long signalLevel;
    }
}