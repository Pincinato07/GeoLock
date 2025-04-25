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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txtUltimoAlerta, txtLocalizacao;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences preferences;

    private static final String PREFS_NAME = "GeoPrefs";
    private static final String KEY_LIMITE = "limite_latitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        txtUltimoAlerta = findViewById(R.id.txt_ultimo_alerta);
        txtLocalizacao = findViewById(R.id.txt_localizacao);
        Button btnPegarLocalizacao = findViewById(R.id.btn_pegar_localizacao);
        Button btnCalibrarLimite = findViewById(R.id.btn_calibrar_limite);
        Button btnHistorico = findViewById(R.id.btn_ver_historico);

        // Exibir último alerta salvo
        String ultimo = preferences.getString("ultimo_alerta", "Nenhum");
        txtUltimoAlerta.setText("Último alerta: " + ultimo);

        btnPegarLocalizacao.setOnClickListener(v -> obterLocalizacaoAtual(false));
        btnCalibrarLimite.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalibrarActivity.class);
            startActivity(intent);
        });
        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoricoActivity.class);
            startActivity(intent);
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        iniciarMonitoramento();
        Intent serviceIntent = new Intent(this, GeoLockService.class);
        startService(serviceIntent);

        Button btnConfiguracoes = findViewById(R.id.btn_configuracoes);
        btnConfiguracoes.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);
        });


    }

    private void obterLocalizacaoAtual(boolean calibrar) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                txtLocalizacao.setText("Localização atual: " + lat + ", " + lon);

                if (calibrar) {
                    preferences.edit().putFloat(KEY_LIMITE, (float) lat).apply();
                    Toast.makeText(this, "✔️ Limite calibrado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void iniciarMonitoramento() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location location = result.getLastLocation();
                if (location != null) {

                    float lat1 = preferences.getFloat("lat1", 0f);
                    float lon1 = preferences.getFloat("lon1", 0f);
                    float lat2 = preferences.getFloat("lat2", 0f);
                    float lon2 = preferences.getFloat("lon2", 0f);

                    if (lat1 == 0 || lat2 == 0) return; // Ainda não calibrado

                    double userLat = location.getLatitude();
                    double userLon = location.getLongitude();

                    // Vetores para cálculo vetorial
                    double dx = lon2 - lon1;
                    double dy = lat2 - lat1;
                    double dxU = userLon - lon1;
                    double dyU = userLat - lat1;

                    // Produto vetorial para saber se cruzou a linha
                    double cross = (dx * dyU) - (dy * dxU);

                    if (Math.abs(cross) > 0.0001) {
                        registrarBloqueio();
                        abrirTelaAlerta();
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(request, locationCallback, null);
    }

    private void abrirTelaAlerta() {
        Intent intent = new Intent(this, AlertaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void registrarBloqueio() {
        String dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        SharedPreferences prefs = getSharedPreferences("GeoAlerta", MODE_PRIVATE);
        String historico = prefs.getString("historico", "");
        historico += dataHora + "\n";

        prefs.edit().putString("historico", historico).apply();

        preferences.edit().putString("ultimo_alerta", dataHora).apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarMonitoramento();
        } else {
            Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
        }
    }
}
