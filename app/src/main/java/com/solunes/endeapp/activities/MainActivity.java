package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.UserPreferences;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_RATE = "update_rate";
    private static final String KEY_RATE_MONTH = "update_rate_month";
    private static final String KEY_DOWNLOAD = "download";
    private static final String KEY_SEND = "send";

    private boolean isDownload;
    private boolean isSend;
    private boolean isRate;
    private TextView textDownload;
    private TextView textSend;
    private TextView textTarifa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textDownload = (TextView) findViewById(R.id.text_date_download);
        textSend = (TextView) findViewById(R.id.text_date_send);
        textTarifa = (TextView) findViewById(R.id.text_date_tarifa);

        String dateDownload = UserPreferences.getString(this, KEY_DOWNLOAD);
        if (dateDownload != null){
            textDownload.setText(dateDownload);
        }
        String dateSend = UserPreferences.getString(this, KEY_SEND);
        if (dateSend != null){
            textSend.setText(dateSend);
        }
        String dateRate = UserPreferences.getString(this, KEY_RATE);
        if (dateRate != null){
            textTarifa.setText(dateRate);
        }
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

        String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
        textDownload.setText(humanDate);
        UserPreferences.putString(this, KEY_DOWNLOAD, humanDate);
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

        String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
        textSend.setText(humanDate);
        UserPreferences.putString(this, KEY_SEND, humanDate);
    }

    public void updateRate(View view) {
        isRate = true;
        Toast.makeText(MainActivity.this, "Se ha actualizado la estructura tarifaria", Toast.LENGTH_SHORT).show();
        String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
        textTarifa.setText(humanDate);
        UserPreferences.putString(this, KEY_RATE, humanDate);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        UserPreferences.putInt(this, KEY_RATE_MONTH, month);
    }
}
