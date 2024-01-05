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
import android.widget.RadioGroup;
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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

    private RadioGroup radioGroup;
    private WifiManager wifiManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int temp = 0;
    private List<ScanResult> filteredScanResults = new ArrayList<>();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM, HH:mm:ss");

    private long DELAY_WIFI_MILIS = 8000;

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
        radioGroup = findViewById(R.id.radioGroup);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                handler.removeCallbacksAndMessages(null);
                temp = 0;
                startAndShowWifiResults(checkedId == radioButton2_4GHz.getId());

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAndShowWifiResults(true);
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
                    setDataChart(is2_4GHz);
                    setupChart(filteredScanResults, is2_4GHz);
                }
                handler.postDelayed(this, DELAY_WIFI_MILIS);
            }
        }, 0);
    }

    private List<ScanResult> getScanWifiResults(boolean is2_4GHz) {
        if (temp != 0 && wifiManager.startScan()) {
            temp++;
        } else if (temp == 0) {
            temp++;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return new ArrayList<>();
        }
        return wifiManager.getScanResults().stream()
                .filter(result -> result.level > -100)
                .filter(item -> is2_4GHz ?
                        wifiFrequencyToChannel(item.frequency) >= 1 && wifiFrequencyToChannel(item.frequency) <= 14 :
                        wifiFrequencyToChannel(item.frequency) >= 36 && wifiFrequencyToChannel(item.frequency) <= 165)
                .collect(Collectors.toList());
    }

    private void setupChart(List<ScanResult> filteredScanResults, boolean is2_4GHz) {
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(filteredScanResults.size());

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setInverted(true);
        yAxis.setAxisMinimum(-100);
        yAxis.setAxisMaximum(0);
        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getBarData().setHighlightEnabled(false);

        // Đặt màu trong suốt và opacity
        barChart.getBarData().setDrawValues(false); // Tắt hiển thị giá trị trên cột
        barChart.getBarData().setBarWidth(0.8f); // Đặt độ rộng của cột


        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int channel = Math.round(value);

                if (is2_4GHz) {
                    if (channel >= 1 && channel <= 14) {
                        return String.valueOf(channel);
                    }
                } else {
                    if (channel >= 36 && channel <= 165) {
                        return String.valueOf(channel);
                    }
                }
                return "";
            }
        });
    }

    private void setDataChart(boolean is2_4GHz) {
        filteredScanResults = getScanWifiResults(is2_4GHz);
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<BarEntry> yValues = new ArrayList<>();
        filteredScanResults.forEach(i -> {
            String label = TextUtils.isEmpty(i.SSID) ? i.BSSID : i.SSID;

            BarEntry barEntry = new BarEntry(Float.valueOf(wifiFrequencyToChannel(i.frequency)), i.level);

            xValues.add(label);
            yValues.add(barEntry);
        });


        BarDataSet barDataSet = new BarDataSet(yValues, "Count: " + (temp) + " - Last Scan: " + dateFormatter.format(LocalDateTime.now()));

        //barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setColor(Color.parseColor("#40FF0000")); // Màu đỏ trong suốt (#40 là mức độ trong suốt)
        barDataSet.setDrawValues(false); // Tắt hiển thị giá trị trên cột

        // Đặt border cho cột
        barDataSet.setBarBorderWidth(2f);
        barDataSet.setBarBorderColor(Color.BLACK);

        // Đặt dữ liệu cho BarChart
        BarData barData = new BarData(barDataSet);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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