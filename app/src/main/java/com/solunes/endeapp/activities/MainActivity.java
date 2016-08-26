package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
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
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.GetRequest;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        if (dateDownload != null) {
            textDownload.setText(dateDownload);
            isDownload = true;
        }
        String dateSend = UserPreferences.getString(this, KEY_SEND);
        if (dateSend != null) {
            textSend.setText(dateSend);
        }
        String dateRate = UserPreferences.getString(this, KEY_RATE);
        if (dateRate != null) {
            textTarifa.setText(dateRate);
            isRate = true;
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
        new GetRequest("http://ende.solunes.com/api/descarga/25/12345", new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                Log.e(TAG, "onSuccess: " + result.length());
                try {
                    processResponse(result);
                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess: ", e);
                }
                progressDialog.dismiss();
                String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
                textDownload.setText(humanDate);
                UserPreferences.putString(MainActivity.this, KEY_DOWNLOAD, humanDate);
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
            }
        }).execute();
        progressDialog.show();


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

    private void processResponse(String result) throws JSONException {
        JSONArray results = new JSONArray(result);
        DBAdapter dbAdapter = new DBAdapter(this);
        for (int i = 0; i < results.length(); i++) {
            JSONObject object = results.getJSONObject(i);
            ContentValues values = new ContentValues();

            values.put(DataModel.Columns.TlxRem.name(), object.getInt(DataModel.Columns.TlxRem.name()));
            values.put(DataModel.Columns.TlxAre.name(), object.getInt(DataModel.Columns.TlxAre.name()));
            values.put(DataModel.Columns.TlxRutO.name(), object.getInt(DataModel.Columns.TlxRutO.name()));
            values.put(DataModel.Columns.TlxRutA.name(), object.getInt(DataModel.Columns.TlxRutA.name()));
            values.put(DataModel.Columns.TlxAno.name(), object.getInt(DataModel.Columns.TlxAno.name()));
            values.put(DataModel.Columns.TlxMes.name(), object.getInt(DataModel.Columns.TlxMes.name()));
            values.put(DataModel.Columns.TlxCli.name(), object.getDouble(DataModel.Columns.TlxCli.name()));
            values.put(DataModel.Columns.TlxOrdTpl.name(), object.getInt(DataModel.Columns.TlxOrdTpl.name()));
            values.put(DataModel.Columns.TlxNom.name(), object.getString(DataModel.Columns.TlxNom.name()));
            values.put(DataModel.Columns.TlxDir.name(), object.getString(DataModel.Columns.TlxDir.name()));
            values.put(DataModel.Columns.TlxCtaAnt.name(), object.getString(DataModel.Columns.TlxCtaAnt.name()));
            values.put(DataModel.Columns.TlxCtg.name(), object.getString(DataModel.Columns.TlxCtg.name()));
            values.put(DataModel.Columns.TlxNroMed.name(), object.getString(DataModel.Columns.TlxNroMed.name()));
            values.put(DataModel.Columns.TlxNroDig.name(), object.getInt(DataModel.Columns.TlxNroDig.name()));
            values.put(DataModel.Columns.TlxFacMul.name(), object.getDouble(DataModel.Columns.TlxFacMul.name()));
            values.put(DataModel.Columns.TlxFecAnt.name(), object.getString(DataModel.Columns.TlxFecAnt.name()));
            values.put(DataModel.Columns.TlxFecLec.name(), object.getString(DataModel.Columns.TlxFecLec.name()));
            values.put(DataModel.Columns.TlxHorLec.name(), object.getString(DataModel.Columns.TlxHorLec.name()));
            values.put(DataModel.Columns.TlxUltInd.name(), object.getInt(DataModel.Columns.TlxUltInd.name()));
            values.put(DataModel.Columns.TlxConPro.name(), object.getInt(DataModel.Columns.TlxConPro.name()));
//            values.put(DataModel.Columns.TlxNvaLec.name(), object.getInt(DataModel.Columns.TlxNvaLec.name()));
            values.put(DataModel.Columns.TlxTipLec.name(), object.getInt(DataModel.Columns.TlxTipLec.name()));
            values.put(DataModel.Columns.TlxSgl.name(), object.getString(DataModel.Columns.TlxSgl.name()));
            values.put(DataModel.Columns.TlxOrdSeq.name(), object.getInt(DataModel.Columns.TlxOrdSeq.name()));
//            values.put(DataModel.Columns.TlxImpFac.name(), object.getInt(DataModel.Columns.TlxImpFac.name()));
//            values.put(DataModel.Columns.TlxImpTap.name(), object.getDouble(DataModel.Columns.TlxImpTap.name()));
//            values.put(DataModel.Columns.TlxImpAse.name(), object.getDouble(DataModel.Columns.TlxImpAse.name()));
//            values.put(DataModel.Columns.TlxCarFij.name(), object.getDouble(DataModel.Columns.TlxCarFij.name()));
//            values.put(DataModel.Columns.TlxImpEn.name(), object.getDouble(DataModel.Columns.TlxImpEn.name()));
//            values.put(DataModel.Columns.TlxImpPot.name(), object.getDouble(DataModel.Columns.TlxImpPot.name()));
//            values.put(DataModel.Columns.TlxDesTdi.name(), object.getDouble(DataModel.Columns.TlxDesTdi.name()));
//            values.put(DataModel.Columns.TlxLey1886.name(), object.getDouble(DataModel.Columns.TlxLey1886.name()));
            values.put(DataModel.Columns.TlxLeePot.name(), object.getInt(DataModel.Columns.TlxLeePot.name()));
            values.put(DataModel.Columns.TlxCotaseo.name(), object.getInt(DataModel.Columns.TlxCotaseo.name()));
            values.put(DataModel.Columns.TlxTap.name(), object.getDouble(DataModel.Columns.TlxTap.name()));
            values.put(DataModel.Columns.TlxPotCon.name(), object.getInt(DataModel.Columns.TlxPotCon.name()));
            values.put(DataModel.Columns.TlxPotFac.name(), object.getInt(DataModel.Columns.TlxPotFac.name()));
            values.put(DataModel.Columns.TlxCliNit.name(), object.getDouble(DataModel.Columns.TlxCliNit.name()));
//            values.put(DataModel.Columns.TlxFecCor.name(), object.getDouble(DataModel.Columns.TlxFecCor.name()));
//            values.put(DataModel.Columns.TlxFecVto.name(), object.getDouble(DataModel.Columns.TlxFecVto.name()));
//            values.put(DataModel.Columns.TlxFecproEmi.name(), object.getDouble(DataModel.Columns.TlxFecproEmi.name()));
//            values.put(DataModel.Columns.TlxFecproMed.name(), object.getDouble(DataModel.Columns.TlxFecproMed.name()));
//            values.put(DataModel.Columns.TlxTope.name(), object.getDouble(DataModel.Columns.TlxTope.name()));
            values.put(DataModel.Columns.TlxLeyTag.name(), object.getInt(DataModel.Columns.TlxLeyTag.name()));
            values.put(DataModel.Columns.TlxTpoTap.name(), object.getInt(DataModel.Columns.TlxTpoTap.name()));
//            values.put(DataModel.Columns.TlxImpTot.name(), object.getDouble(DataModel.Columns.TlxImpTot.name()));
            values.put(DataModel.Columns.TlxKwhAdi.name(), object.getInt(DataModel.Columns.TlxKwhAdi.name()));
            values.put(DataModel.Columns.TlxImpAvi.name(), object.getInt(DataModel.Columns.TlxImpAvi.name()));
            values.put(DataModel.Columns.TlxCarFac.name(), object.getInt(DataModel.Columns.TlxCarFac.name()));
            values.put(DataModel.Columns.TlxDeuEneC.name(), object.getInt(DataModel.Columns.TlxDeuEneC.name()));
            values.put(DataModel.Columns.TlxDeuEneI.name(), object.getDouble(DataModel.Columns.TlxDeuEneI.name()));
            values.put(DataModel.Columns.TlxDeuAseC.name(), object.getInt(DataModel.Columns.TlxDeuAseC.name()));
            values.put(DataModel.Columns.TlxDeuAseI.name(), object.getDouble(DataModel.Columns.TlxDeuAseI.name()));
//            values.put(DataModel.Columns.TlxFecEmi.name(), object.getDouble(DataModel.Columns.TlxFecEmi.name()));
            values.put(DataModel.Columns.TlxUltPag.name(), object.getString(DataModel.Columns.TlxUltPag.name()));
            values.put(DataModel.Columns.TlxEstado.name(), object.getInt(DataModel.Columns.TlxEstado.name()));
            values.put(DataModel.Columns.TlxUltObs.name(), object.getString(DataModel.Columns.TlxUltObs.name()));
            values.put(DataModel.Columns.TlxActivi.name(), object.getString(DataModel.Columns.TlxActivi.name()));
            values.put(DataModel.Columns.TlxCiudad.name(), object.getString(DataModel.Columns.TlxCiudad.name()));
            values.put(DataModel.Columns.TlxFacNro.name(), object.getDouble(DataModel.Columns.TlxFacNro.name()));
            values.put(DataModel.Columns.TlxNroAut.name(), object.getDouble(DataModel.Columns.TlxNroAut.name()));
            values.put(DataModel.Columns.TlxCodCon.name(), object.getString(DataModel.Columns.TlxCodCon.name()));
            values.put(DataModel.Columns.TlxFecLim.name(), object.getString(DataModel.Columns.TlxFecLim.name()));
            values.put(DataModel.Columns.TlxKwhDev.name(), object.getInt(DataModel.Columns.TlxKwhDev.name()));
            values.put(DataModel.Columns.TlxUltTipL.name(), object.getInt(DataModel.Columns.TlxUltTipL.name()));
            values.put(DataModel.Columns.TlxCliNew.name(), object.getInt(DataModel.Columns.TlxCliNew.name()));
//            values.put(DataModel.Columns.TlxEntEne.name(), object.getInt(DataModel.Columns.TlxEntEne.name()));
//            values.put(DataModel.Columns.TlxEntPot.name(), object.getInt(DataModel.Columns.TlxEntPot.name()));
//            values.put(DataModel.Columns.TlxPotFacM.name(), object.getInt(DataModel.Columns.TlxPotFacM.name()));
//            values.put(DataModel.Columns.TlxPerCo3.name(), object.getDouble(DataModel.Columns.TlxPerCo3.name()));
//            values.put(DataModel.Columns.TlxPerHr3.name(), object.getDouble(DataModel.Columns.TlxPerHr3.name()));
//            values.put(DataModel.Columns.TlxPerCo2.name(), object.getDouble(DataModel.Columns.TlxPerCo2.name()));
//            values.put(DataModel.Columns.TlxPerHr2.name(), object.getDouble(DataModel.Columns.TlxPerHr2.name()));
//            values.put(DataModel.Columns.TlxPerCo1.name(), object.getDouble(DataModel.Columns.TlxPerCo1.name()));
//            values.put(DataModel.Columns.TlxPerHr1.name(), object.getDouble(DataModel.Columns.TlxPerHr1.name()));
//            values.put(DataModel.Columns.TlxConsumo.name(), object.getDouble(DataModel.Columns.TlxConsumo.name()));
//            values.put(DataModel.Columns.TlxPerdidas.name(), object.getDouble(DataModel.Columns.TlxPerdidas.name()));
//            values.put(DataModel.Columns.TlxConsFacturado.name(), object.getDouble(DataModel.Columns.TlxConsFacturado.name()));
            values.put(DataModel.Columns.TlxDebAuto.name(), object.getString(DataModel.Columns.TlxDebAuto.name()));

            dbAdapter.saveDataObject(values);
        }
        dbAdapter.close();
    }
}
