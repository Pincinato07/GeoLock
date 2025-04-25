package com.example.geolockapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlertaActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextView txtTimer;
    private Handler handler = new Handler();
    private long startTime;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int segundos = (int) (millis / 1000);
            int minutos = segundos / 60;
            segundos = segundos % 60;
            txtTimer.setText(String.format(Locale.getDefault(), "Tempo fora da zona: %d:%02d", minutos, segundos));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);

        txtTimer = findViewById(R.id.txt_timer);
        Button btnDesligar = findViewById(R.id.btn_desligar_alarme);

        tocarAlarme();
        iniciarTimer();
        salvarHorario();

        btnDesligar.setOnClickListener(v -> {
            pararAlarme();
            finishAffinity(); // Fecha todas as activities abertas
        });
    }

    private void tocarAlarme() {
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();
    }

    private void pararAlarme() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        handler.removeCallbacks(timerRunnable);
    }

    private void iniciarTimer() {
        startTime = System.currentTimeMillis();
        handler.post(timerRunnable);
    }

    private void salvarHorario() {
        String dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        getSharedPreferences("GeoAlerta", MODE_PRIVATE)
                .edit()
                .putString("ultimo_alerta", dataHora)
                .apply();
    }

    @Override
    public void onBackPressed() {
        // Impede o botão voltar
        super.onBackPressed();
    }
}
