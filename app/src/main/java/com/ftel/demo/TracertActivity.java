package com.ftel.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class TracertActivity extends AppCompatActivity {
    private EditText editTextIpAddress;
    private Button buttonTracert;
    private TextView textViewResult;
    private ArrayList<String> response = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracert);

        textViewResult = findViewById(R.id.textViewResult);

        editTextIpAddress = findViewById(R.id.editTextIPAddress);
        buttonTracert = findViewById(R.id.buttonTracert);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        buttonTracert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });
    }

}