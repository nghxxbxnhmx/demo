package com.ftel.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class PageLoadTimerActivity extends AppCompatActivity {
    private EditText urlEditText;
    private Button pageLoadTimerButton;
    private TextView pageLoadTimerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_load_timer);

        urlEditText = findViewById(R.id.urlEditText);
        pageLoadTimerButton = findViewById(R.id.pageLoadTimerButton);
        pageLoadTimerTextView = findViewById(R.id.pageLoadTimerTextView);

        pageLoadTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageLoadTimerButton.setText("Please wait, processing.");
                pageLoadTimerButton.setEnabled(false);
                new WebsiteLoadTimeTask().execute(String.valueOf(urlEditText.getText()).toString());
            }
        });
    }

    private class WebsiteLoadTimeTask extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... params) {
            String urlString = params.length > 0 ? params[0] : "";
            return pageLoadTimer(urlString);
        }

        @Override
        protected void onPostExecute(Long elapsedTime) {
            pageLoadTimerTextView.setText("Time taken to load website: " + elapsedTime + " milliseconds");

            pageLoadTimerButton.setText("Load");
            pageLoadTimerButton.setEnabled(true);
        }
    }

    public Long pageLoadTimer(String host) {
        long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(host)
                .build();
        try {
            Response response = client.newCall(request).execute();
            int responseCode = response.code();
            Log.i("WebsiteLoadTimeTask", "Response Code: " + responseCode);
        } catch (IOException e) {
            Log.e("WebsiteLoadTimeTask", "Error during HTTP request", e);
        }

        return System.currentTimeMillis() - startTime;
    }
}