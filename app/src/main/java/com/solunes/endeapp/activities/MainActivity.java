package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.GetRequest;
import com.solunes.endeapp.networking.PostRequest;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String KEY_RATE = "update_rate";
    private static final String KEY_RATE_MONTH = "update_rate_month";
    private static final String KEY_DOWNLOAD = "download";
    private static final String KEY_WAS_UPLOAD = "was_upload";
    private static final String KEY_SEND = "send";

    private boolean isRate;
    private boolean wasDownload;

    private TextView textDownload;
    private TextView textSend;
    private TextView textTarifa;

    private TextView stateTotal;
    private TextView statePerformed;
    private TextView stateMissing;
    private TextView stateAverage;

    private CardView cardRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textDownload = (TextView) findViewById(R.id.text_date_download);
        textSend = (TextView) findViewById(R.id.text_date_send);
        textTarifa = (TextView) findViewById(R.id.text_date_tarifa);

        stateAverage = (TextView) findViewById(R.id.state_average);
        stateMissing = (TextView) findViewById(R.id.state_missing);
        statePerformed = (TextView) findViewById(R.id.state_performed);
        stateTotal = (TextView) findViewById(R.id.state_total);

        Calendar calendar = Calendar.getInstance();
        long dateDownload = UserPreferences.getLong(this, KEY_DOWNLOAD);
        if (dateDownload > 0) {
            calendar.setTimeInMillis(dateDownload);
            textDownload.setText(StringUtils.getHumanDate(calendar.getTime()));
            wasDownload = true;
        }
        long dateSend = UserPreferences.getLong(this, KEY_SEND);
        if (dateSend > 0) {
            calendar.setTimeInMillis(dateSend);
            textSend.setText(StringUtils.getHumanDate(calendar.getTime()));
        }
        long dateRate = UserPreferences.getLong(this, KEY_RATE);
        if (dateRate > 0) {
            calendar.setTimeInMillis(dateRate);
            textTarifa.setText(StringUtils.getHumanDate(calendar.getTime()));
            isRate = true;
        }

        cardRate = (CardView) findViewById(R.id.card_rate);

        validDay();
        updateStates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                UserPreferences.putBoolean(this, LoginActivity.KEY_LOGIN, false);
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startReading(View view) {
        if (!wasDownload || !isRate) {
            Toast.makeText(MainActivity.this, "No se han descargado las rutas o tarifas", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class));
    }

    public void downloadRoutes(final View view) {
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        if (dbAdapter.getSizeData() > 0) {
            if (!UserPreferences.getBoolean(this, KEY_WAS_UPLOAD)) {
                Toast.makeText(MainActivity.this, "No se han subido los datos", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        dbAdapter.deleteAllData();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando....");
        progressDialog.setCancelable(false);
        final Calendar calendar = Calendar.getInstance();
        String remesa = calendar.get(Calendar.DAY_OF_MONTH) + "";
        if (remesa.length() == 1) {
            remesa = "0" + remesa;
        }
        String url = "http://ende.solunes.com/api/descarga/" + remesa + "/12345";
        new GetRequest(url, new CallbackAPI() {
            @Override
            public void onSuccess(final String result, int statusCode) {
                Runnable runSaveData = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            processResponse(result);
                        } catch (JSONException e) {
                            Log.e(TAG, "onSuccess: ", e);
                        }
                        progressDialog.dismiss();
                        wasDownload = true;
                        UserPreferences.putBoolean(getApplicationContext(), KEY_WAS_UPLOAD, false);
                        UserPreferences.putInt(getApplicationContext(), ReadingActivity.KEY_LAST_PAGER_PSOTION, 0);
                        UserPreferences.putLong(MainActivity.this, KEY_DOWNLOAD, Calendar.getInstance().getTimeInMillis());
                    }
                };
                new Thread(runSaveData).start();
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
                progressDialog.setOnDismissListener(null);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error al descargar los datos", Toast.LENGTH_SHORT).show();
            }
        }).execute();
        progressDialog.show();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                updateStates();
                String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
                textDownload.setText(humanDate);
                Toast.makeText(MainActivity.this, "Datos descargados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendReading(final View view) {
        if (!wasDownload) {
            Toast.makeText(MainActivity.this, "No se han descargado las rutas", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Hashtable<String, String> params = prepareDataToPost();
        new PostRequest(params, null, "http://ende.solunes.com/api/subida", new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                Log.e(TAG, "onSuccess: " + result);
                String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
                textSend.setText(humanDate);
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this, "Datos enviados", Toast.LENGTH_SHORT).show();
                UserPreferences.putLong(getApplicationContext(), KEY_SEND, Calendar.getInstance().getTimeInMillis());
                UserPreferences.putBoolean(getApplicationContext(), KEY_WAS_UPLOAD, true);
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error al enviar datos", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public void updateRate(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando....");
        progressDialog.setCancelable(false);
        String url = "http://ende.solunes.com/api/parametros-fijos";
        new GetRequest(url, new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                try {
                    AdminActivity.processResultFixParams(getApplicationContext(), result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
                textTarifa.setText(humanDate);
                UserPreferences.putLong(getApplicationContext(), KEY_RATE, Calendar.getInstance().getTimeInMillis());
                int month = Calendar.getInstance().get(Calendar.MONTH);
                UserPreferences.putInt(getApplicationContext(), KEY_RATE_MONTH, month);
                cardRate.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
                Toast.makeText(MainActivity.this, "Se ha actualizado la estructura tarifaria", Toast.LENGTH_SHORT).show();
                isRate = true;
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error al descargar los parametros", Toast.LENGTH_SHORT).show();
            }
        }).execute();
        progressDialog.show();
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

            dbAdapter.saveObject(DBAdapter.TABLE_DATA, values);
        }
        dbAdapter.close();
    }

    private Hashtable<String, String> prepareDataToPost() {
        Hashtable<String, String> params = new Hashtable<>();

        DBAdapter dbAdapter = new DBAdapter(this);
        ArrayList<DataModel> allData = dbAdapter.getAllData();

        params.put("TlxRem", String.valueOf(allData.get(0).getTlxRem()));
        params.put("TlxAre", String.valueOf(allData.get(0).getTlxAre()));
        params.put("TlxRutA", String.valueOf(allData.get(0).getTlxRutA()));
        for (DataModel dataModel : allData) {
            String json = dataModel.getJsonToSend(dataModel);
            params.put("" + (dataModel.getTlxCli()), json);
        }
        return params;
    }

    public void validDay() {
        Calendar calendar = Calendar.getInstance();
        int monthRate = UserPreferences.getInt(getApplicationContext(), KEY_RATE_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        if (monthRate < currentMonth) {
            isRate = false;
            cardRate.setBackgroundTintList(getResources().getColorStateList(R.color.color_tint));
        } else if (currentMonth == 0) {
            isRate = false;
            cardRate.setBackgroundTintList(getResources().getColorStateList(R.color.color_tint));
        }
    }

    private void updateStates() {
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        int sizeData = dbAdapter.getSizeData();
        stateTotal.setText(String.valueOf(sizeData));
        int countSave = dbAdapter.getCountSave();
        dbAdapter.close();
        statePerformed.setText(String.valueOf(countSave));
        stateMissing.setText(String.valueOf(sizeData - countSave));
        stateAverage.setText("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStates();
    }
}
