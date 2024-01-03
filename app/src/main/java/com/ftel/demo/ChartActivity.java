package com.ftel.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private BarChart barChart;

    WifiManager wifiManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String[] channels = new String[]{
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"
    };
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM, hh:mm:sss.z");
    private int temp = 1;
    private Boolean temp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chart);
        barChart = findViewById(R.id.barchart);
        barChart.setOnChartValueSelectedListener(this);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startWifiScanning();
        }
    }

    private void startWifiScanning() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                temp2 = wifiManager.startScan();
                scanWifiAndShowResults();
                handler.postDelayed(this, 5000);
            }
        }, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startWifiScanning();
        }
    }

    private void scanWifiAndShowResults() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<ScanResult> scanResults = wifiManager.getScanResults();

        List<ScanResult> filteredScanResults = new ArrayList<>();
        for (ScanResult result : scanResults) {
            if (result.level > -80) {
                filteredScanResults.add(result);
            }
        }

        ArrayList<BarEntry> data = new ArrayList<>();

        for (int i = 0; i < filteredScanResults.size(); i++) {
            String barName;
            if (TextUtils.isEmpty(filteredScanResults.get(i).SSID)) {
                barName = filteredScanResults.get(i).BSSID;
            } else {
                barName = filteredScanResults.get(i).SSID;
            }

            data.add(new BarEntry(i, Float.parseFloat(filteredScanResults.get(i).level + ""), barName));
        }
        BarDataSet barDataSet = new BarDataSet(data, "Count: "+temp+" - Start Scan? "+temp2+" - Last Scan: "+dateFormatter.format(new Date()));
        temp++;
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData();
        barData.clearValues();
        barData = new BarData(barDataSet);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setInverted(true);

        barChart.getAxisRight().setEnabled(false);

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < filteredScanResults.size()) {
                    return (filteredScanResults.get(index).SSID.length() >= 1 ? filteredScanResults.get(index).SSID : filteredScanResults.get(index).BSSID) + " (" + wifiFrequencyToChannel(filteredScanResults.get(index).frequency) + ")";
                }
                return "N/A";
            }
        });

        /*barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < channels.length) {
                    return channels[index];
                }
                return "N/A";
            }
        });*/



        barChart.setData(barData);
        barChart.invalidate();
    }

    private int wifiFrequencyToChannel(int frequency) {
        if (frequency >= 2412 && frequency <= 2484) {
            return (frequency - 2412) / 5 + 1;
        } else if (frequency >= 5170 && frequency <= 5825) {
            return (frequency - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}