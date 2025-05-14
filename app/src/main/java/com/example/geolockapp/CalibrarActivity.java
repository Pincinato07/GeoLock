package com.example.geolockapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class CalibrarActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences preferences;

    private static final String PREFS_NAME = "GeoPrefs";
    private static final String KEY_LAT1 = "lat1";
    private static final String KEY_LON1 = "lon1";
    private static final String KEY_LAT2 = "lat2";
    private static final String KEY_LON2 = "lon2";

    private double lat1 = 0, lon1 = 0, lat2 = 0, lon2 = 0;

    private TextView txtStatus;
    private Button btnPonto1, btnPonto2, btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        txtStatus = findViewById(R.id.txt_status);
        btnPonto1 = findViewById(R.id.btn_local1);
        btnPonto2 = findViewById(R.id.btn_local2);
        btnSalvar = findViewById(R.id.btn_salvar_calibragem);

        btnPonto1.setOnClickListener(v -> capturarLocal(1));
        btnPonto2.setOnClickListener(v -> capturarLocal(2));
        btnSalvar.setOnClickListener(v -> {
            if (lat1 != 0 && lat2 != 0) {
                preferences.edit()
                        .putFloat(KEY_LAT1, (float) lat1)
                        .putFloat(KEY_LON1, (float) lon1)
                        .putFloat(KEY_LAT2, (float) lat2)
                        .putFloat(KEY_LON2, (float) lon2)
                        .apply();

                startActivity(new Intent(this, ConfirmacaoActivity.class));
            } else {
                Toast.makeText(this, "Capture os dois pontos antes de salvar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void capturarLocal(int ponto) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                if (ponto == 1) {
                    lat1 = location.getLatitude();
                    lon1 = location.getLongitude();
                    txtStatus.setText("📍 Localização 1 definida.");
                } else {
                    lat2 = location.getLatitude();
                    lon2 = location.getLongitude();
                    txtStatus.setText("📍 Localização 2 definida.");
                }
            } else {
                Toast.makeText(this, "Erro ao obter localização.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
