package com.ftel.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ftel.demo.utils.IpConfigHelper;
import com.google.gson.Gson;


public class WifiInfoActivity extends AppCompatActivity {

    private TextView textView;
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;

    private Context applicationContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiinfo);

        textView = findViewById(R.id.textView);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            displayWifiInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayWifiInfo();
        } else {
            textView.setText("PERMISSION_GRANTED denied!");
        }
    }

    private void displayWifiInfo() {
        applicationContext = (Context) getApplicationContext();

        String ipAddress = IpConfigHelper.getIPAddress(true);
        String subnetMask = IpConfigHelper.getSubnetMask();
        String defaultGateway = IpConfigHelper.getDefaultGateway();
        String macAddress = IpConfigHelper.getMacAddress(applicationContext);
        String ssid = IpConfigHelper.getSSID(applicationContext);
        String bssid = IpConfigHelper.getBSSID(applicationContext);

        textView.setText("IP Address: " + ipAddress + "\n"
                + "Subnet Mask: " + subnetMask + "\n"
                + "Default Gateway: " + defaultGateway + "\n"
                + "Physical Address: " + macAddress + "\n"
                + "\n"
                + "Wifi Address: " + bssid + "\n"
                + "Wifi Name: " + ssid);
        Log.i("RESULT: ", new Gson().toJson(IpConfigHelper.getWifiInfo(applicationContext)));
    }
}