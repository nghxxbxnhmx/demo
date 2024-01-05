package com.ftel.demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import fr.bmartel.speedtest.model.SpeedTestMode;

public class SpeedTestActivity extends AppCompatActivity {

    private static final String TAG = "SpeedTest";
    private static final String SPEED_TEST_DOWNLOAD_URI = "https://speedtest-vdc.vinahost.vn/files/10MBvnh.bin";
    private static final String SPEED_TEST_UPLOAD_URI = "http://ipv4.download.thinkbroadband.com";
    private static final String PING_TEST_SERVER = "www.speedtest.net";
    private static final int PING_COUNT = 10;
    private TextView downloadSpeedTextView, uploadSpeedTextView, pingTimeTextView, jitterTextView, serverLocationTextView, ispTextView, publicIpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);

        // Initialize TextViews
        downloadSpeedTextView = findViewById(R.id.downloadSpeedTextView);
        uploadSpeedTextView = findViewById(R.id.uploadSpeedTextView);
        pingTimeTextView = findViewById(R.id.pingTimeTextView);
        jitterTextView = findViewById(R.id.jitterTextView);
        serverLocationTextView = findViewById(R.id.serverLocationTextView);
        ispTextView = findViewById(R.id.ispTextView);
        publicIpTextView = findViewById(R.id.publicIpTextView);

        Button startSpeedTestButton = findViewById(R.id.startSpeedTestButton);
        startSpeedTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SpeedTestTask().execute();
                new PingTestTask().execute();
                new GetInfoTask().execute();
            }
        });
    }

    private class SpeedTestTask extends AsyncTask<Void, String, Void> {
        private double downloadSpeed = 0.0;
        private double uploadSpeed = 0.0;
        @Override
        protected Void doInBackground(Void... voids) {
            SpeedTestSocket speedTestSocket = new SpeedTestSocket();

            // Add a listener to monitor the progress
            speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
                @Override
                public void onCompletion(SpeedTestReport report) {
                    if (speedTestSocket.getSpeedTestMode() == SpeedTestMode.DOWNLOAD) {
                        downloadSpeed = report.getTransferRateBit().doubleValue() / 1e6; // Convert to Mbps
                        publishProgress("download", String.valueOf(downloadSpeed));
                    } else if (speedTestSocket.getSpeedTestMode() == SpeedTestMode.UPLOAD) {
                        uploadSpeed = report.getTransferRateBit().doubleValue() / 1e6; // Convert to Mbps
                        publishProgress("upload", String.valueOf(uploadSpeed));
                    }
                }

                @Override
                public void onError(SpeedTestError speedTestError, String errorMessage) {
                    Log.e(TAG, "Speed test error: " + errorMessage);
                }

                @Override
                public void onProgress(float percent, SpeedTestReport report) {
//                    Log.d(TAG, "Progress: " + percent + "%");
                }
            });

            // Start download test
            speedTestSocket.startDownload(SPEED_TEST_DOWNLOAD_URI);

            // Sleep between tests
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Start upload test
//            speedTestSocket.startUpload(SPEED_TEST_UPLOAD_URI, 1000000);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d(TAG, "onProgressUpdate: Download speed:" + downloadSpeed + ", Upload speed: " + uploadSpeed);
            if (values[0].equals("download")) {
                downloadSpeedTextView.setText("Download Speed: " + downloadSpeed + " Mbps");
            } else if (values[0].equals("upload")) {
                uploadSpeedTextView.setText("Upload Speed: " + uploadSpeed + " Mbps");
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute: Download speed:" + downloadSpeed + ", Upload speed: " + uploadSpeed);
            // Update UI with download and upload speeds
            downloadSpeedTextView.setText("Download Speed: " + downloadSpeed + " Mbps");
            uploadSpeedTextView.setText("Upload Speed: " + uploadSpeed + " Mbps");
        }
    }

    private class PingTestTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            ArrayList<Long> pingTimes = new ArrayList<>();
            long totalTime = 0;

            for (int i = 0; i < PING_COUNT; i++) {
                try {
                    long startTime = System.currentTimeMillis();
                    InetAddress address = InetAddress.getByName(PING_TEST_SERVER);
                    if (address.isReachable(5000)) { // Timeout
                        long endTime = System.currentTimeMillis();
                        long pingTime = endTime - startTime;
                        pingTimes.add(pingTime);
                        totalTime += pingTime;
                    } else {
                        pingTimes.add(-1L);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    pingTimes.add(-1L);
                }
            }

            // Calculate average and jitter
            long averagePing = totalTime / PING_COUNT;
            Collections.sort(pingTimes);
            long jitter = pingTimes.size() > 1 ? Math.abs(pingTimes.get(pingTimes.size() - 1) - pingTimes.get(0)) : 0;

            return "Average Ping: " + averagePing + " ms, Jitter: " + jitter + " ms";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, result);

            // Extract ping and jitter from the result string
            String[] parts = result.split(", ");
            String ping = parts[0].split(": ")[1];
            String jitter = parts[1].split(": ")[1];

            pingTimeTextView.setText("Ping Time: " + ping);
            jitterTextView.setText("Jitter: " + jitter);
        }
    }

    private class GetInfoTask extends AsyncTask<Void, Void, String> {

        private static final String API_URL = "https://ipinfo.io/json";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String city = jsonObject.optString("city");
                    String org = jsonObject.optString("org");
                    String ip = jsonObject.optString("ip");

                    serverLocationTextView.setText("Server Location: " + city);
                    ispTextView.setText("ISP: " + org);
                    publicIpTextView.setText("Public IP: " + ip);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage(), e);
                }
            } else {
                Log.e(TAG, "No response received.");
            }
        }
    }

}