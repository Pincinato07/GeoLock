package com.example.geolockapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GeoLockService extends Service {

    private static final String CHANNEL_ID = "GeoLockChannel";
    private static final int NOTIFICATION_ID = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundServiceWithNotification();
        iniciarMonitoramento();
    }

    private void startForegroundServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "GeoLock Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("GeoLockApp Monitorando")
                .setContentText("Seu dispositivo está sendo monitorado.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void iniciarMonitoramento() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest request = LocationRequest.create();
        request.setInterval(3000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    SharedPreferences preferences = getSharedPreferences("GeoPrefs", MODE_PRIVATE);

                    float lat1 = preferences.getFloat("lat1", 0f);
                    float lon1 = preferences.getFloat("lon1", 0f);
                    float lat2 = preferences.getFloat("lat2", 0f);
                    float lon2 = preferences.getFloat("lon2", 0f);

                    if (lat1 == 0 || lat2 == 0) return;

                    double userLat = location.getLatitude();
                    double userLon = location.getLongitude();

                    double dx = lon2 - lon1;
                    double dy = lat2 - lat1;
                    double dxU = userLon - lon1;
                    double dyU = userLat - lat1;

                    double cross = (dx * dyU) - (dy * dxU);

                    if (Math.abs(cross) > 0.0001) {
                        registrarBloqueio();
                        abrirTelaAlerta();
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private void registrarBloqueio() {
        String dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = getSharedPreferences("GeoAlerta", MODE_PRIVATE);
        String historico = prefs.getString("historico", "");
        historico += dataHora + "\n";
        prefs.edit().putString("historico", historico).apply();
    }

    private void abrirTelaAlerta() {
        Intent intent = new Intent(this, AlertaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
