package com.ftel.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ftel.demo.utils.IpConfigHelper;
import com.google.gson.Gson;


public class WifiInfoActivity extends AppCompatActivity implements PhoneStateCallback {

    private TextView textView;
    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;
    private Context applicationContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiinfo);

        textView = findViewById(R.id.textView);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mPhoneStateListener = new PhoneStateListener();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        mPhoneStateListener.setCallback(this);

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
        String macAddress = IpConfigHelper.getMacAddress(applicationContext).toUpperCase();
        String ssid = IpConfigHelper.getSSID(applicationContext);
        String bssid = IpConfigHelper.getBSSID(applicationContext).toUpperCase();
        String snr = "";
        textView.setTextSize(18f);
        textView.setText("IP Address: " + ipAddress + "\n"
                + "Subnet Mask: " + subnetMask + "\n"
                + "Default Gateway: " + defaultGateway + "\n"
                + "Physical Address: " + macAddress + "\n"
                + "Wifi Address: " + bssid + "\n"
                + "Wifi Name: " + ssid + "\n"
        );
    }

    @Override
    public void onSignalStrengthChanged(int snr, int lteSignalStrength) {
        String existingText = textView.getText().toString();
        String newText = existingText + "\nSNR: " + snr;
        textView.setText(newText);
    }
}