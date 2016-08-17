package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.solunes.endeapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private boolean isDownload;
    private boolean isSend;
    private boolean isRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void startReading(View view) {
        if (!isDownload) {
            Toast.makeText(MainActivity.this, "No se han descargado las rutas", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class));
    }

    public void downloadRoutes(final View view) {
        if (!isRate) {
            Toast.makeText(MainActivity.this, "No hay estructura tarifaria", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando....");
        progressDialog.setCancelable(false);
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                view.setEnabled(false);
                isDownload = true;
            }
        });
        progressDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 500);
    }

    public void sendReading(final View view) {
        if (!isDownload) {
            Toast.makeText(MainActivity.this, "No se han descargado las rutas", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando....");
        progressDialog.setCancelable(false);
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                view.setEnabled(false);
                isSend = true;
            }
        });
        progressDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 500);

    }

    public void updateRate(View view) {
        isRate = true;
        Toast.makeText(MainActivity.this, "Se ha actualizado la estructura tarifaria", Toast.LENGTH_SHORT).show();
    }
}
