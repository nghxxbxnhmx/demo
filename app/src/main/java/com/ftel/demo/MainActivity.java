package com.ftel.demo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftel.demo.adapter.PingAdapter;
import com.ftel.demo.dto.PingResult;
import com.ftel.demo.util.PingUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editTextIpAddress;
    private Button buttonPing;
    private RecyclerView recyclerView;
    private TextView avgResponseTimeTextView;
    private Button buttonActivity_2;
    private Button buttonActivity_Chart;

    private PingAdapter pingAdapter = new PingAdapter();
    private ArrayList<String> response = new ArrayList<>();
    private ArrayList<PingResult> pingResults = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fix order of initialization for recyclerView
        recyclerView = findViewById(R.id.recyclerViewResult);
        recyclerView.setAdapter(pingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextIpAddress = findViewById(R.id.editTextIPAddress);
        buttonPing = findViewById(R.id.buttonPing);
        avgResponseTimeTextView = findViewById(R.id.avgResponseTimeTextView);
        buttonActivity_2 = findViewById(R.id.activity_2Button);
        buttonActivity_Chart= findViewById(R.id.activityChartButton);

        buttonPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAddress = editTextIpAddress.getText().toString().trim();
                handler.postDelayed(pingTaskRunnable(ipAddress), 0);
            }
        });

        buttonActivity_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity_2.class));
            }
        });

        buttonActivity_Chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Activity_Chart.class));
            }
        });
    }

    private Runnable pingTaskRunnable(final String ipAddress) {
        return new Runnable() {
            @Override
            public void run() {
                new PingTask().execute(ipAddress);
            }
        };
    }

    public class PingTask extends AsyncTask<String, Void, PingResult> {
        @Override
        protected PingResult doInBackground(String... params) {
            String ipAddress = params.length > 0 ? params[0] : "";
            PingUtil pingUtil = new PingUtil(ipAddress);
            int ttl = 59;
            int timeout = 10000;
            int packetSize = 32;
            PingResult result = pingUtil.sendPing(ttl, timeout, packetSize);
            pingResults.add(result);
            return result;
        }

        @Override
        protected void onPostExecute(PingResult pingResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String resultString = String.format(
                            "Ping #%d\nStatus: %s - IpAddress: %s\nResponse Time: %dms",
                            response.size() + 1,
                            pingResult.getReplyStatus(),
                            pingResult.getIpAddress(),
                            pingResult.getResponseTime()
                    );

                    response.add(0, resultString);

                    double avgResponseTime = pingResults.stream().mapToDouble(PingResult::getResponseTime).average().orElse(0);
                    avgResponseTimeTextView.setText("Avg: " + String.format("%.2f", avgResponseTime));

                    pingAdapter.clearData();
                    pingAdapter.setData(response);

                    handler.postDelayed(() -> pingTaskRunnable(editTextIpAddress.getText().toString().trim()), 1000);
                }
            });
        }
    }
}