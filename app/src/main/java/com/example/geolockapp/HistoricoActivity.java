package com.example.geolockapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HistoricoActivity extends AppCompatActivity {

    private TextView txtHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        txtHistorico = findViewById(R.id.txt_historico);
        Button btnVoltar = findViewById(R.id.btn_voltar_main);

        SharedPreferences prefs = getSharedPreferences("GeoAlerta", MODE_PRIVATE);
        String historico = prefs.getString("historico", "Nenhum bloqueio registrado.");

        txtHistorico.setText(historico);

        btnVoltar.setOnClickListener(v -> finish());
    }
}
