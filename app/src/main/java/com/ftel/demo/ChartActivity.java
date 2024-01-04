package com.ftel.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.BarChart;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private BarChart barChart;
    private RadioButton radioButton2_4GHz, radioButton5GHz;
    private WifiManager wifiManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int temp = 0;
    private boolean booleanCondition = false;
    private ArrayList<ScanResult> scanResults = new ArrayList<>();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM, HH:mm:ss.SSS");

    private boolean sortCondition = true;

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
        radioButton2_4GHz = findViewById(R.id.radio_2_4GHz);
        radioButton5GHz = findViewById(R.id.radio_5GHz);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        radioButton2_4GHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "2.4GHz", Toast.LENGTH_SHORT).show();
                sortCondition = true;
                //startAndShowWifiResults(sortCondition);
            }
        });

        radioButton5GHz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "5GHz", Toast.LENGTH_SHORT).show();
                sortCondition = false;
                //startAndShowWifiResults(sortCondition);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAndShowWifiResults(sortCondition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void startAndShowWifiResults(boolean is2_4GHz) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    List<ScanResult> filteredScanResults = getScanWifiResults(is2_4GHz);

                    ArrayList<BarEntry> data = filteredScanResults.stream()
                            .map(result -> new BarEntry(filteredScanResults.indexOf(result),
                                    (float) result.level,
                                    TextUtils.isEmpty(result.SSID) ? result.BSSID : result.SSID))
                            .collect(Collectors.toCollection(ArrayList::new));

                    BarDataSet barDataSet = new BarDataSet(data, "Count: " + temp + " - Last Scan: " + dateFormatter.format(LocalDateTime.now()));
                    temp++;
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                    BarData barData = new BarData(barDataSet);

                    barChart.setData(barData);
                    barChart.invalidate();

                    setupChart(filteredScanResults);
                    booleanCondition = true;
                }

                handler.postDelayed(this, 5000);
            }
        }, 0);
    }


    private List<ScanResult> getScanWifiResults(boolean is2_4GHz) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return new ArrayList<>();
        }

        return wifiManager.getScanResults().stream()
                .filter(result -> result.level > -80)
                .filter(item -> is2_4GHz ?
                        wifiFrequencyToChannel(item.frequency) >= 1 && wifiFrequencyToChannel(item.frequency) <= 14 :
                        wifiFrequencyToChannel(item.frequency) >= 36 && wifiFrequencyToChannel(item.frequency) <= 165)
                .collect(Collectors.toList());
    }

    private void setupChart(List<ScanResult> filteredScanResults) {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startAndShowWifiResults(false);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}