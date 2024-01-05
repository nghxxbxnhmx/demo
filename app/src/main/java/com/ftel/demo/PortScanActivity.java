package com.ftel.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;


public class PortScanActivity extends AppCompatActivity {

    private EditText addressEditText, startPortEditText, endPortEditText;
    private Button startButton;
    private ListView resultListView;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView progressLabel;
    private boolean isScanning = false;
    private PortScanTask portScanTask;

    private static final int PROGRESS_UPDATE_INTERVAL = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portscan);

        addressEditText = findViewById(R.id.addressEditText);
        startPortEditText = findViewById(R.id.startPortEditText);
        endPortEditText = findViewById(R.id.endPortEditText);
        startButton = findViewById(R.id.startButton);
        resultListView = findViewById(R.id.resultListView);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        progressLabel = findViewById(R.id.progressLabel);

        // Set default values for start and end ports
        startPortEditText.setText("0");
        endPortEditText.setText("1023");


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScanning) {
                    startPortScan();
                } else {
                    stopPortScan();
                }
            }
        });
    }

    private void startPortScan() {
        String address = addressEditText.getText().toString();
        String startPortText = startPortEditText.getText().toString();
        String endPortText = endPortEditText.getText().toString();

        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        int startPort = Integer.parseInt(startPortText);
        int endPort = Integer.parseInt(endPortText);

        if (startPort > endPort) {
            Toast.makeText(this, "Start port must be less than or equal to end port", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setMax(endPort - startPort);
        progressBar.setProgress(0);

        Log.d("PortScan", "Starting port scan for address: " + address + " from port " + startPort + " to " + endPort);

        portScanTask = new PortScanTask(address, startPort, endPort);
        portScanTask.execute();
    }

    private void stopPortScan() {
        if (portScanTask != null) {
            portScanTask.cancel(true);
            portScanTask = null;
        }
        isScanning = false;
        startButton.setText("Start Port Scan");
    }

    private class PortScanTask extends AsyncTask<Void, Integer, Void> {
        private final String address;
        private final int startPort;
        private final int endPort;
        private ArrayAdapter<String> adapter;
        private List<String> openPortStatusList;

        public PortScanTask(String address, int startPort, int endPort) {
            this.address = address;
            this.startPort = startPort;
            this.endPort = endPort;
            this.openPortStatusList = new ArrayList<>();
            this.adapter = new ArrayAdapter<>(PortScanActivity.this, android.R.layout.simple_list_item_1, openPortStatusList);
            resultListView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("PortScan", "onPreExecute - Scan started");
            isScanning = true;
            startButton.setText("Stop Scan");
            progressBar.setMax(100);
            openPortStatusList.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... params) {
            int totalPorts = endPort - startPort + 1;
            int scannedPorts = 0;
            int lastProgress = -1;

            for (int port = startPort; port <= endPort; port++) {
                if (isCancelled()) {
                    return null;
                }

                try {
                    Socket socket = new Socket();
                    Log.d("PortScan", "Try to scan port " + port + "...");
                    socket.connect(new InetSocketAddress(address, port), 100);

                    socket.close();

                    String status = "Open";
                    if (port >= 1 && port <= 1023) {
                        status = "Open but blocked";
                    }

                    Log.d("PortScan", "Port " + port + ": " + status);
                    openPortStatusList.add("Port " + port + ": " + status);
                    publishProgress(scannedPorts, port);

                } catch (IOException e) {
                    Log.d("PortScan", "Port " + port + ": Closed or not reachable");
                }

                scannedPorts++;
                int progress = (int) (((float) scannedPorts / totalPorts) * 100);
                //update status every post scanned
                publishProgress(progress, port);

//                Log.d("PortScanProgress", "ScannedPorts " + scannedPorts + " over " + totalPorts + ": "+ progress +"%");

                //update port every 5%
//                if (progress / 5 > lastProgress / 5 || port == startPort || port == endPort) {
//                    publishProgress(progress, port);
//                    lastProgress = progress;
//                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            int currentPort = values[1]; // Get the current port number

            progressBar.setProgress(progress);
            progressText.setText(progress + "%");
            progressLabel.setText("Scanning port number " + currentPort); // Set the text for current scanning port
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("PortScan", "onPostExecute - Scan finished");
            isScanning = false;
            startButton.setText("Start Port Scan");
            progressBar.setProgress(0);
            progressText.setText("0%");
        }
    }
}