package com.example.geolockapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Button btnAtivar, btnDesativar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        btnAtivar = findViewById(R.id.btn_ativar);
        btnDesativar = findViewById(R.id.btn_desativar);

        btnAtivar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GeoLockService.class);
            startService(intent);
            Toast.makeText(this, "Monitoramento ativado", Toast.LENGTH_SHORT).show();
        });

        btnDesativar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GeoLockService.class);
            stopService(intent);
            Toast.makeText(this, "Monitoramento desativado", Toast.LENGTH_SHORT).show();
        });
    }
}
