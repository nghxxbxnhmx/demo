package com.ftel.demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;

import java.util.ArrayList;
import java.util.List;

public class NsLookupActivity extends AppCompatActivity {

    private Button startButton;
    private EditText domainEditText;
    private EditText dnsServerEditText;
    private ListView resultListView;
    private List<String> resultList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nslookup);

        startButton = findViewById(R.id.startButton);
        domainEditText = findViewById(R.id.domainEditText);
        dnsServerEditText = findViewById(R.id.dnsServerEditText);
        resultListView = findViewById(R.id.resultListView);

        resultList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultList);
        resultListView.setAdapter(adapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domainName = domainEditText.getText().toString();
                String dnsServer = dnsServerEditText.getText().toString();

                if (domainName.isEmpty()) {
                    Toast.makeText(NsLookupActivity.this, "Please enter a domain name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dnsServer.isEmpty()) {
                    dnsServer = "8.8.8.8"; // Default DNS server (Google DNS)
                }

                new DnsLookupTask().execute(domainName, dnsServer);
            }
        });
    }

    private class DnsLookupTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            String domainName = params[0];
            String dnsServer = params[1];

            try {
                // Perform DNS query
                Resolver resolver = new SimpleResolver(dnsServer);
                Lookup lookup = new Lookup(domainName, Type.ANY);
                lookup.setResolver(resolver);

                Record[] records = lookup.run();

                List<String> result = new ArrayList<>();
                if (lookup.getResult() == Lookup.SUCCESSFUL) {
                    for (Record record : records) {
                        result.add(record.toString());
                    }
                } else {
                    result.add("DNS query failed");
                }

                return result;
            } catch (Exception e) {
                Log.d("NsLookupActivity", "DNS query exception: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {
            resultList.clear();
            if (result != null) {
                resultList.addAll(result);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
