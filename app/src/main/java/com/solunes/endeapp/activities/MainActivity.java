package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.Historico;
import com.solunes.endeapp.models.MedEntreLineas;
import com.solunes.endeapp.models.PrintObsData;
import com.solunes.endeapp.models.User;
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
    public static final String KEY_RATE = "update_rate";
    public static final String KEY_RATE_MONTH = "update_rate_month";
    private static final String KEY_DOWNLOAD = "download";
    private static final String KEY_WAS_UPLOAD = "was_upload";
    private static final String KEY_SEND = "send";
    private static final String KEY_ENDPOINT_GESTION = "endpoint_gestion";
    private static final String KEY_ENDPOINT_MONTH = "endpoint_month";
    private static final String KEY_ENDPOINT_REMESA = "endpoint_remesa";
    private static final String KEY_ENDPOINT_TPL = "endpoint_tpl";

    private boolean isRate;
    private boolean wasDownload;

    private TextView labelReady;
    private TextView labelReadyRuta;

    private TextView textDownload;
    private TextView textSend;
    private TextView textTarifa;

    private TextView statePerformed;
    private TextView stateMissing;
    private TextView statePrinted;
    private TextView statePostponed;

    private CardView cardRate;

    private int nroRemesa;
    private int nroArea;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DBAdapter adapter = new DBAdapter(this);
        user = adapter.getUser(UserPreferences.getInt(this, LoginActivity.KEY_LOGIN_ID));
        labelReady = (TextView) findViewById(R.id.label_info);
        labelReadyRuta = (TextView) findViewById(R.id.label_info_ruta);

        textDownload = (TextView) findViewById(R.id.text_date_download);
        textSend = (TextView) findViewById(R.id.text_date_send);
        textTarifa = (TextView) findViewById(R.id.text_date_tarifa);

        stateMissing = (TextView) findViewById(R.id.state_missing);
        statePerformed = (TextView) findViewById(R.id.state_performed);
        statePrinted = (TextView) findViewById(R.id.state_printed);
        statePostponed = (TextView) findViewById(R.id.state_postponed);
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
                UserPreferences.putInt(this, LoginActivity.KEY_LOGIN_ID, 0);
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void downloadRoutes(final View view) {
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        if (dbAdapter.getSizeData() > 0) {
            if (!UserPreferences.getBoolean(this, KEY_WAS_UPLOAD)) {
                Snackbar.make(view, "No se han subido los datos", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        dbAdapter.deleteAllData();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando....");
        progressDialog.setCancelable(false);
        final Calendar calendar = Calendar.getInstance();
        final String gestion = calendar.get(Calendar.YEAR) + "";
        final String month = (calendar.get(Calendar.MONTH) + 1) + "";
        String remesa = calendar.get(Calendar.DAY_OF_MONTH) + "";
        if (remesa.length() == 1) {
            remesa = "0" + remesa;
        }
        String url = "http://ende.solunes.com/api/descarga/" + gestion + "/" + month + "/" + remesa + "/" + user.getRutaCod();
        final String finalRemesa = remesa;
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

                        UserPreferences.putString(MainActivity.this, KEY_ENDPOINT_GESTION, gestion);
                        UserPreferences.putString(MainActivity.this, KEY_ENDPOINT_MONTH, month);
                        UserPreferences.putString(MainActivity.this, KEY_ENDPOINT_REMESA, finalRemesa);
                    }
                };
                new Thread(runSaveData).start();
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
                progressDialog.setOnDismissListener(null);
                progressDialog.dismiss();
                Snackbar.make(view, "Error al descargar los datos", Snackbar.LENGTH_SHORT).show();
            }
        }).execute();
        progressDialog.show();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                updateStates();
                String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
                textDownload.setText(humanDate);
                Snackbar.make(view, "Datos descargados", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void sendReading(final View view) {
        if (!wasDownload) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
//        DBAdapter dbAdapter = new DBAdapter(this);
//        if (dbAdapter.getSizeData() == dbAdapter.getCountSave()) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Runnable runSaveData = new Runnable() {

            @Override
            public void run() {
                Hashtable<String, String> params = prepareDataToPost();
                new PostRequest(params, null, "http://ende.solunes.com/api/subida", new CallbackAPI() {
                    @Override
                    public void onSuccess(String result, int statusCode) {
                        Log.e(TAG, "onSuccess: " + result);
                        String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
                        textSend.setText(humanDate);
                        progressDialog.dismiss();

                        Snackbar.make(view, "Datos enviados", Snackbar.LENGTH_SHORT).show();
                        UserPreferences.putLong(getApplicationContext(), KEY_SEND, Calendar.getInstance().getTimeInMillis());
                        UserPreferences.putBoolean(getApplicationContext(), KEY_WAS_UPLOAD, true);
                    }

                    @Override
                    public void onFailed(String reason, int statusCode) {
                        Log.e(TAG, "onFailed: " + reason);
                        progressDialog.dismiss();
                        Snackbar.make(view, "Error al enviar datos", Snackbar.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        };
        new Thread(runSaveData).start();
//        } else {
//            Snackbar.make(view, "No se han completado las lecturas", Snackbar.LENGTH_SHORT).show();
//        }
    }

    public void updateRate(final View view) {
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
                Snackbar.make(view, "Se ha actualizado los parametros fijos", Snackbar.LENGTH_SHORT).show();
                isRate = true;
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
                progressDialog.dismiss();
                Snackbar.make(view, "Error al descargar los parametros", Snackbar.LENGTH_SHORT).show();
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

            values.put(DataModel.Columns.id.name(), object.getInt(DataModel.Columns.id.name()));
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
            values.put(DataModel.Columns.TlxCtgTap.name(), object.getInt(DataModel.Columns.TlxCtgTap.name()));
            values.put(DataModel.Columns.TlxCtgAseo.name(), object.getInt(DataModel.Columns.TlxCtgAseo.name()));
            values.put(DataModel.Columns.TlxNroMed.name(), object.getString(DataModel.Columns.TlxNroMed.name()));
            values.put(DataModel.Columns.TlxNroDig.name(), object.getInt(DataModel.Columns.TlxNroDig.name()));
            values.put(DataModel.Columns.TlxFacMul.name(), object.getDouble(DataModel.Columns.TlxFacMul.name()));
            values.put(DataModel.Columns.TlxFecAnt.name(), object.getString(DataModel.Columns.TlxFecAnt.name()));
            values.put(DataModel.Columns.TlxFecLec.name(), object.getString(DataModel.Columns.TlxFecLec.name()));
//            values.put(DataModel.Columns.TlxHorLec.name(), object.getString(DataModel.Columns.TlxHorLec.name()));
            values.put(DataModel.Columns.TlxUltInd.name(), object.getInt(DataModel.Columns.TlxUltInd.name()));
            values.put(DataModel.Columns.TlxConPro.name(), object.getInt(DataModel.Columns.TlxConPro.name()));
            values.put(DataModel.Columns.TlxTipLec.name(), object.getInt(DataModel.Columns.TlxTipLec.name()));
            values.put(DataModel.Columns.TlxSgl.name(), object.getString(DataModel.Columns.TlxSgl.name()));
            values.put(DataModel.Columns.TlxTipDem.name(), object.getInt(DataModel.Columns.TlxTipDem.name()));
            values.put(DataModel.Columns.TlxOrdSeq.name(), object.getInt(DataModel.Columns.TlxOrdSeq.name()));
            values.put(DataModel.Columns.TlxLeePot.name(), object.getInt(DataModel.Columns.TlxLeePot.name()));
            values.put(DataModel.Columns.TlxCotaseo.name(), object.getInt(DataModel.Columns.TlxCotaseo.name()));
            values.put(DataModel.Columns.TlxTap.name(), object.getDouble(DataModel.Columns.TlxTap.name()));
            values.put(DataModel.Columns.TlxPotCon.name(), object.getInt(DataModel.Columns.TlxPotCon.name()));
            values.put(DataModel.Columns.TlxPotFac.name(), object.getInt(DataModel.Columns.TlxPotFac.name()));
            values.put(DataModel.Columns.TlxCliNit.name(), object.getDouble(DataModel.Columns.TlxCliNit.name()));
            values.put(DataModel.Columns.TlxFecCor.name(), object.getString(DataModel.Columns.TlxFecCor.name()));
            values.put(DataModel.Columns.TlxFecVto.name(), object.getString(DataModel.Columns.TlxFecVto.name()));
            values.put(DataModel.Columns.TlxFecproEmi.name(), object.getString(DataModel.Columns.TlxFecproEmi.name()));
            values.put(DataModel.Columns.TlxFecproMed.name(), object.getString(DataModel.Columns.TlxFecproMed.name()));
            values.put(DataModel.Columns.TlxTope.name(), object.getInt(DataModel.Columns.TlxTope.name()));
            values.put(DataModel.Columns.TlxLeyTag.name(), object.getInt(DataModel.Columns.TlxLeyTag.name()));
            values.put(DataModel.Columns.TlxTpoTap.name(), object.getInt(DataModel.Columns.TlxTpoTap.name()));
            values.put(DataModel.Columns.TlxKwhAdi.name(), object.getInt(DataModel.Columns.TlxKwhAdi.name()));
            values.put(DataModel.Columns.TlxImpAvi.name(), object.getInt(DataModel.Columns.TlxImpAvi.name()));
            values.put(DataModel.Columns.TlxCarFac.name(), object.getInt(DataModel.Columns.TlxCarFac.name()));
            values.put(DataModel.Columns.TlxDeuEneC.name(), object.getInt(DataModel.Columns.TlxDeuEneC.name()));
            values.put(DataModel.Columns.TlxDeuEneI.name(), object.getDouble(DataModel.Columns.TlxDeuEneI.name()));
            values.put(DataModel.Columns.TlxDeuAseC.name(), object.getInt(DataModel.Columns.TlxDeuAseC.name()));
            values.put(DataModel.Columns.TlxDeuAseI.name(), object.getDouble(DataModel.Columns.TlxDeuAseI.name()));
            values.put(DataModel.Columns.TlxFecEmi.name(), object.getString(DataModel.Columns.TlxFecEmi.name()));
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
            values.put(DataModel.Columns.TlxEntEne.name(), object.getInt(DataModel.Columns.TlxEntEne.name()));
            values.put(DataModel.Columns.TlxDecEne.name(), object.getInt(DataModel.Columns.TlxDecEne.name()));
            values.put(DataModel.Columns.TlxEntPot.name(), object.getInt(DataModel.Columns.TlxEntPot.name()));
            values.put(DataModel.Columns.TlxDecPot.name(), object.getInt(DataModel.Columns.TlxDecPot.name()));
            values.put(DataModel.Columns.TlxPotTag.name(), object.getInt(DataModel.Columns.TlxPotTag.name()));
            values.put(DataModel.Columns.TlxPreAnt1.name(), object.getString(DataModel.Columns.TlxPreAnt1.name()));
            values.put(DataModel.Columns.TlxPreAnt2.name(), object.getString(DataModel.Columns.TlxPreAnt2.name()));
            values.put(DataModel.Columns.TlxPreAnt3.name(), object.getString(DataModel.Columns.TlxPreAnt3.name()));
            values.put(DataModel.Columns.TlxPreAnt4.name(), object.getString(DataModel.Columns.TlxPreAnt4.name()));
            values.put(DataModel.Columns.TlxDebAuto.name(), object.getString(DataModel.Columns.TlxDebAuto.name()));

            values.put(DataModel.Columns.TlxRecordatorio.name(), object.getString(DataModel.Columns.TlxRecordatorio.name()));
            values.put(DataModel.Columns.estado_lectura.name(), 0);
            values.put(DataModel.Columns.enviado.name(), 0);
//            values.put(DataModel.Columns.TlxDignidad.name(), object.getInt(DataModel.Columns.TlxDignidad.name()));
            dbAdapter.saveObject(DBHelper.DATA_TABLE, values);

            JSONObject historico = object.getJSONObject("historico");
            ContentValues valuesH = new ContentValues();
            valuesH.put(Historico.Columns.id.name(), historico.getInt(Historico.Columns.id.name()));
            valuesH.put(Historico.Columns.general_id.name(), historico.getInt(Historico.Columns.general_id.name()));
            valuesH.put(Historico.Columns.ConMes01.name(), historico.getString(Historico.Columns.ConMes01.name()));
            valuesH.put(Historico.Columns.ConMes02.name(), historico.getString(Historico.Columns.ConMes02.name()));
            valuesH.put(Historico.Columns.ConMes03.name(), historico.getString(Historico.Columns.ConMes03.name()));
            valuesH.put(Historico.Columns.ConMes04.name(), historico.getString(Historico.Columns.ConMes04.name()));
            valuesH.put(Historico.Columns.ConMes05.name(), historico.getString(Historico.Columns.ConMes05.name()));
            valuesH.put(Historico.Columns.ConMes06.name(), historico.getString(Historico.Columns.ConMes06.name()));
            valuesH.put(Historico.Columns.ConMes07.name(), historico.getString(Historico.Columns.ConMes07.name()));
            valuesH.put(Historico.Columns.ConMes08.name(), historico.getString(Historico.Columns.ConMes08.name()));
            valuesH.put(Historico.Columns.ConMes09.name(), historico.getString(Historico.Columns.ConMes09.name()));
            valuesH.put(Historico.Columns.ConMes10.name(), historico.getString(Historico.Columns.ConMes10.name()));
            valuesH.put(Historico.Columns.ConMes11.name(), historico.getString(Historico.Columns.ConMes11.name()));
            valuesH.put(Historico.Columns.ConMes12.name(), historico.getString(Historico.Columns.ConMes12.name()));
            valuesH.put(Historico.Columns.ConKwh01.name(), historico.getInt(Historico.Columns.ConKwh01.name()));
            valuesH.put(Historico.Columns.ConKwh02.name(), historico.getInt(Historico.Columns.ConKwh02.name()));
            valuesH.put(Historico.Columns.ConKwh03.name(), historico.getInt(Historico.Columns.ConKwh03.name()));
            valuesH.put(Historico.Columns.ConKwh04.name(), historico.getInt(Historico.Columns.ConKwh04.name()));
            valuesH.put(Historico.Columns.ConKwh05.name(), historico.getInt(Historico.Columns.ConKwh05.name()));
            valuesH.put(Historico.Columns.ConKwh06.name(), historico.getInt(Historico.Columns.ConKwh06.name()));
            valuesH.put(Historico.Columns.ConKwh07.name(), historico.getInt(Historico.Columns.ConKwh07.name()));
            valuesH.put(Historico.Columns.ConKwh08.name(), historico.getInt(Historico.Columns.ConKwh08.name()));
            valuesH.put(Historico.Columns.ConKwh09.name(), historico.getInt(Historico.Columns.ConKwh09.name()));
            valuesH.put(Historico.Columns.ConKwh10.name(), historico.getInt(Historico.Columns.ConKwh10.name()));
            valuesH.put(Historico.Columns.ConKwh11.name(), historico.getInt(Historico.Columns.ConKwh11.name()));
            valuesH.put(Historico.Columns.ConKwh12.name(), historico.getInt(Historico.Columns.ConKwh12.name()));
            dbAdapter.saveObject(DBHelper.HISTORICO_TABLE, valuesH);
        }
        dbAdapter.close();
    }

    private Hashtable<String, String> prepareDataToPost() {
        Hashtable<String, String> params = new Hashtable<>();

        DBAdapter dbAdapter = new DBAdapter(this);
        ArrayList<DataModel> allData = dbAdapter.getAllData();

        params.put("gestion", UserPreferences.getString(getApplicationContext(), KEY_ENDPOINT_GESTION));
        params.put("mes", UserPreferences.getString(getApplicationContext(), KEY_ENDPOINT_MONTH));
        params.put("remesa", UserPreferences.getString(getApplicationContext(), KEY_ENDPOINT_REMESA));
        params.put("RutaCod", String.valueOf(user.getRutaCod()));

        ArrayList<PrintObsData> printObsDataArrayList = dbAdapter.getPrintObsData();

        for (DataModel dataModel : allData) {
            String json = DataModel.getJsonToSend(dataModel, dbAdapter.getObsByCli(dataModel.getId()), printObsDataArrayList);
            Log.e(TAG, "prepareDataToPost json: "+json );
            params.put("" + (dataModel.getTlxCli()), json);
        }

        try {
            ArrayList<MedEntreLineas> entreLineasList = dbAdapter.getMedEntreLineas();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < entreLineasList.size(); i++) {
                MedEntreLineas entreLineas = entreLineasList.get(i);
                jsonArray.put(i, entreLineas.toJson());
            }
            Log.e(TAG, "prepareDataToPost: mel: "+ jsonArray.toString());
            params.put("med_entre_lineas", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbAdapter.close();
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
        if (sizeData != 0) {
            int countSave = dbAdapter.getCountSave();
            int countPrinted = dbAdapter.getCountPrinted();
            int countPostponed = dbAdapter.getCountPostponed();
            statePerformed.setText(String.valueOf(countSave));
            stateMissing.setText(String.valueOf(sizeData - countSave));
            statePrinted.setText(String.valueOf(countPrinted));
            statePostponed.setText(String.valueOf(countPostponed));
            DataModel data = dbAdapter.getData(1);
            nroRemesa = data.getTlxRem();
            nroArea = data.getTlxAre();
            labelReadyRuta.setText(String.valueOf(data.getTlxRutA()));

        }
        dbAdapter.close();

        String dateFromstring = StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT_1, Calendar.getInstance().getTime());
        labelReady.setText(dateFromstring);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStates();
    }

    public static final String KEY_FILTER = "filter";
    public static final int KEY_READY = 1;
    public static final int KEY_MISSING = 2;
    public static final int KEY_PRINT = 3;
    public static final int KEY_POSTPONED = 4;

    public void startReading(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class));
    }

    public void statusReady(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_READY));
    }

    public void statusMissing(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_MISSING));
    }

    public void statusPrint(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_PRINT));
    }

    public void StatusPostponed(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_POSTPONED));
    }

    public void analytics(View view) {
        startActivity(new Intent(MainActivity.this, AnalyticsActivity.class));
    }

    public void newMedidor(final View view) {
        if (!wasDownload) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder newMedidor = new AlertDialog.Builder(this);
        newMedidor.setTitle("Nuevo Medidor");
        View viewInside = LayoutInflater.from(this).inflate(R.layout.layout_new_medidor, null);
        final EditText nroMed = (EditText) viewInside.findViewById(R.id.new_med_number);
        final EditText lecMed = (EditText) viewInside.findViewById(R.id.new_med_lectura);
        newMedidor.setView(viewInside);
        newMedidor.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (nroMed.getText().toString().isEmpty() || lecMed.getText().toString().isEmpty()) {
                    Snackbar.make(view, "Ambos campos son necesarios", Snackbar.LENGTH_SHORT).show();
                } else {
                    DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                    boolean valid = dbAdapter.validNewMedidor(Integer.parseInt(nroMed.getText().toString()));
                    if (valid) {
                        ContentValues cv = new ContentValues();
                        cv.put(MedEntreLineas.Columns.MelAre.name(), nroArea);
                        cv.put(MedEntreLineas.Columns.MelRem.name(), nroRemesa);
                        cv.put(MedEntreLineas.Columns.MelMed.name(), Integer.parseInt(nroMed.getText().toString()));
                        cv.put(MedEntreLineas.Columns.MelLec.name(), Integer.parseInt(lecMed.getText().toString()));
                        dbAdapter.saveObject(DBHelper.MED_ENTRE_LINEAS_TABLE, cv);
                        dbAdapter.close();
                        Log.e(TAG, "onClick: " + nroMed.getText().toString());
                        Log.e(TAG, "onClick: " + lecMed.getText().toString());
                        Snackbar.make(view, "Nuevo medidor para la ruta", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "El número de medidor ya existe", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
        newMedidor.setNegativeButton("Cancelar", null);
        newMedidor.show();
    }
}
