package com.ftel.demo;

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

import com.ftel.demo.adapters.PingAdapter;
import com.ftel.demo.dto.PingResult;
import com.ftel.demo.utils.PingHelper;

import java.util.ArrayList;

public class PingActivity extends AppCompatActivity {
    private EditText editTextIpAddress;
    private Button buttonPing, buttonCancel;
    private RecyclerView recyclerView;
    private TextView avgResponseTimeTextView;

    private PingAdapter pingAdapter = new PingAdapter();
    private ArrayList<String> response = new ArrayList<>();
    private ArrayList<PingResult> pingResults = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isPingRunning = false; // Added variable to control the loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        recyclerView = findViewById(R.id.recyclerViewResult);
        recyclerView.setAdapter(pingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editTextIpAddress = findViewById(R.id.editTextIPAddress);
        buttonPing = findViewById(R.id.buttonPing);
        buttonCancel = findViewById(R.id.buttonCancel);
        avgResponseTimeTextView = findViewById(R.id.avgResponseTimeTextView);

        buttonPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPing.setEnabled(false);
                buttonCancel.setEnabled(true);
                response.clear();
                pingAdapter.clearData();
                startPingLoop(editTextIpAddress.getText().toString().trim());
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPing.setEnabled(true);
                buttonCancel.setEnabled(false);
                stopPingLoop();
            }
        });
    }

    private void startPingLoop(final String ipAddress) {
        isPingRunning = true;
        handler.postDelayed(pingTaskRunnable(ipAddress), 0);
    }

    private void stopPingLoop() {
        isPingRunning = false;
    }

    private Runnable pingTaskRunnable(final String ipAddress) {
        return new Runnable() {
            @Override
            public void run() {
                if (isPingRunning) {
                    new PingTask().execute(ipAddress);
                    handler.postDelayed(pingTaskRunnable(ipAddress), 1000);
                }
            }
        };
    }

    public class PingTask extends AsyncTask<String, Void, PingResult> {
        @Override
        protected PingResult doInBackground(String... params) {
            String ipAddress = params.length > 0 ? params[0] : "";
            PingHelper pingHelper = new PingHelper(ipAddress);
            int ttl = 59;
            int timeout = 10000;
            int packetSize = 32;
            PingResult result = pingHelper.sendPing(ttl, timeout, packetSize);
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