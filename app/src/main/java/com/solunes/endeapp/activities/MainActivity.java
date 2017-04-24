package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import com.solunes.endeapp.models.DetalleFactura;
import com.solunes.endeapp.models.Historico;
import com.solunes.endeapp.models.MedEntreLineas;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.Resultados;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.GetRequest;
import com.solunes.endeapp.networking.PostRequest;
import com.solunes.endeapp.networking.Token;
import com.solunes.endeapp.utils.Encrypt;
import com.solunes.endeapp.utils.FileUtils;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.Urls;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Esta es la actividad principal de la aplicacion
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String KEY_RATE = "update_rate";
    public static final String KEY_RATE_MONTH = "update_rate_month";
    public static final String KEY_DOWNLOAD = "download";
    public static final String KEY_WAS_UPLOAD = "was_upload";
    public static final String KEY_SEND = "send";

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

    Handler handler = new Handler();
    Runnable handlerTask;
    private int interval;

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
        cardRate = (CardView) findViewById(R.id.card_rate);

        View layoutSend = findViewById(R.id.layout_send_reading);
        if (user.getLecNiv() == 3) {
            layoutSend.setEnabled(false);
        }

        for (File f : getExternalFilesDirs(null)) {
            Log.e(TAG, "onCreate: " + f);
        }

        Log.e(TAG, "onCreate: " + getExternalFilesDir(null));

        // se verifican las fechas de las accciones como descarga, envio y parametros fijos
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

        validDay();
        updateStates();

        interval = (int) adapter.getParametroValor(Parametro.Values.tiempo_envio.name());
        handlerTask = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: enviado: mins " + interval);
                sendEveryMinute();
                handler.postDelayed(handlerTask, interval * 1000 * 60);
            }
        };
        if (user.getLecNiv() != 3) {
            handlerTask.run();
        }
        adapter.close();
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
                // opcion del menu para cerrar sesion
                UserPreferences.putBoolean(this, LoginActivity.KEY_LOGIN, false);
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * metodo para descargar las lecturas del usuario actual
     *
     * @param view vista para mostrar mensajes al usuario
     */
    public void downloadRoutes(final View view) {
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        if (dbAdapter.getSizeData() > 0) {
            if (!UserPreferences.getBoolean(this, KEY_WAS_UPLOAD)) {
                Snackbar.make(view, "No se han subido los datos", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando....");
        progressDialog.setCancelable(false);

        final String url = Urls.urlDescarga(getApplicationContext()) + user.getLecCod();
        Token.getToken(getApplicationContext(), user, new Token.CallbackToken() {
            @Override
            public void onSuccessToken() {
                new GetRequest(getApplicationContext(), url, new CallbackAPI() {
                    @Override
                    public void onSuccess(final String result, int statusCode) {
                        Runnable runSaveData = new Runnable() {

                            @Override
                            public void run() {
                                boolean response = false;
                                try {
                                    response = processResponse(getApplicationContext(),result);
                                } catch (JSONException e) {
                                    Log.e(TAG, "onSuccess: ", e);
                                }
                                if (response) {
                                    wasDownload = true;
                                    UserPreferences.putBoolean(getApplicationContext(), KEY_WAS_UPLOAD, false);
                                    UserPreferences.putInt(getApplicationContext(), ReadingActivity.KEY_LAST_PAGER_PSOTION, 0);
                                    UserPreferences.putLong(MainActivity.this, KEY_DOWNLOAD, Calendar.getInstance().getTimeInMillis());
                                } else {
                                    Snackbar.make(view, "No hay datos en la descarga", Snackbar.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        };
                        new Thread(runSaveData).start();
                    }

                    @Override
                    public void onFailed(String reason, int statusCode) {
                        Log.e(TAG, "onFailed: " + reason);
                        progressDialog.setOnDismissListener(null);
                        progressDialog.dismiss();
                        Snackbar.make(view, reason, Snackbar.LENGTH_SHORT).show();
                    }
                }).execute();
            }

            @Override
            public void onFailToken() {
                progressDialog.setOnDismissListener(null);
                progressDialog.dismiss();
            }
        });

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

    /**
     * metodo para mostrar un dialogo de espera mientras se esta conectando al servicor
     *
     * @param view
     */
    public void sendReading(final View view) {
        if (!wasDownload) {
            Snackbar.make(view, "No se han descargado las lecturas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Runnable runSaveData = new Runnable() {

            @Override
            public void run() {
                sendPostRequest(view, progressDialog);
            }
        };
        new Thread(runSaveData).start();
    }

    /**
     * metodo para enviar las lecturas el servidor
     *
     * @param view
     * @param progressDialog
     */
    public void sendPostRequest(final View view, final ProgressDialog progressDialog) {
        Hashtable<String, String> params = prepareDataToPost(getApplicationContext(), user);
//                Token.getToken(getApplicationContext(), user);
        new PostRequest(getApplicationContext(), params, null, Urls.urlSubida(getApplicationContext()), new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                Log.e(TAG, "onSuccess: " + result);

                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray uploaded_array = jsonObject.getJSONArray("uploaded_array");
                    for (int i = 0; i < uploaded_array.length(); i++) {
                        ContentValues values = new ContentValues();
                        values.put(DataModel.Columns.enviado.name(), DataModel.EstadoEnviado.enviado.ordinal());
                        dbAdapter.updateData(uploaded_array.getInt(i), values);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                Snackbar.make(view, reason, Snackbar.LENGTH_SHORT).show();
            }
        }).execute();
    }

    /**
     * Metodo para actualizar los parametros fijos, muestra un dialog de espera durante el progreso
     *
     * @param view
     */

    public void updateRate(final View view) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Descargando....");
        progressDialog.setCancelable(false);

        Token.getToken(getApplicationContext(), user, new Token.CallbackToken() {
            @Override
            public void onSuccessToken() {
                new GetRequest(getApplicationContext(), Urls.urlParametros(getApplicationContext()), new CallbackAPI() {
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
                        Snackbar.make(view, reason, Snackbar.LENGTH_SHORT).show();
                    }
                }).execute();
            }

            @Override
            public void onFailToken() {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
    }

    /**
     * Metodo para procesar la respuesta del servidor en la descarga de lecturas, luego se guardan
     * los datos en la base de datos
     *
     * @param result un string json con los datos de la descarga
     * @return retorna true si se hay datos para guardar
     * @throws JSONException lanza una excepcion si el formato del string result no es json
     */
    public static boolean processResponse(Context context,String result) throws JSONException {
        JSONArray results = new JSONArray(result);
        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.beforeDownloadData();
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
            values.put(DataModel.Columns.TlxDav.name(), object.getInt(DataModel.Columns.TlxDav.name()));
            values.put(DataModel.Columns.TlxEstCli.name(), object.getInt(DataModel.Columns.TlxEstCli.name()));
            values.put(DataModel.Columns.TlxOrdTpl.name(), object.getInt(DataModel.Columns.TlxOrdTpl.name()));
            values.put(DataModel.Columns.TlxPromAseo.name(), object.getInt(DataModel.Columns.TlxPromAseo.name()));
            values.put(DataModel.Columns.TlxDivAseo.name(), object.getInt(DataModel.Columns.TlxDivAseo.name()));
            values.put(DataModel.Columns.TlxNom.name(), object.getString(DataModel.Columns.TlxNom.name()).trim());
            values.put(DataModel.Columns.TlxDir.name(), object.getString(DataModel.Columns.TlxDir.name()).trim());
            values.put(DataModel.Columns.TlxCtaAnt.name(), object.getString(DataModel.Columns.TlxCtaAnt.name()).trim());
            values.put(DataModel.Columns.TlxCtg.name(), object.getString(DataModel.Columns.TlxCtg.name()).trim());
            values.put(DataModel.Columns.TlxCtgTap.name(), object.getInt(DataModel.Columns.TlxCtgTap.name()));
            values.put(DataModel.Columns.TlxCtgAseo.name(), object.getInt(DataModel.Columns.TlxCtgAseo.name()));
            values.put(DataModel.Columns.TlxNroMed.name(), object.getString(DataModel.Columns.TlxNroMed.name()).trim());
            values.put(DataModel.Columns.TlxNroDig.name(), object.getInt(DataModel.Columns.TlxNroDig.name()));
            values.put(DataModel.Columns.TlxFacMul.name(), object.getDouble(DataModel.Columns.TlxFacMul.name()));
            values.put(DataModel.Columns.TlxFecAnt.name(), object.getString(DataModel.Columns.TlxFecAnt.name()).trim());
            values.put(DataModel.Columns.TlxFecLec.name(), object.getString(DataModel.Columns.TlxFecLec.name()).trim());
            values.put(DataModel.Columns.TlxUltInd.name(), object.getInt(DataModel.Columns.TlxUltInd.name()));
            values.put(DataModel.Columns.TlxConPro.name(), object.getInt(DataModel.Columns.TlxConPro.name()));
            values.put(DataModel.Columns.TlxTipLec.name(), object.getInt(DataModel.Columns.TlxTipLec.name()));
            values.put(DataModel.Columns.TlxSgl.name(), object.getString(DataModel.Columns.TlxSgl.name()).trim());
            values.put(DataModel.Columns.TlxTipDem.name(), object.getInt(DataModel.Columns.TlxTipDem.name()));
            values.put(DataModel.Columns.TlxOrdSeq.name(), object.getInt(DataModel.Columns.TlxOrdSeq.name()));
            values.put(DataModel.Columns.TlxLeePot.name(), object.getInt(DataModel.Columns.TlxLeePot.name()));
            values.put(DataModel.Columns.TlxCotaseo.name(), object.getInt(DataModel.Columns.TlxCotaseo.name()));
            values.put(DataModel.Columns.TlxTap.name(), object.getDouble(DataModel.Columns.TlxTap.name()));
            values.put(DataModel.Columns.TlxPotCon.name(), object.getInt(DataModel.Columns.TlxPotCon.name()));
            values.put(DataModel.Columns.TlxPotFac.name(), object.getInt(DataModel.Columns.TlxPotFac.name()));
            values.put(DataModel.Columns.TlxCliNit.name(), object.getString(DataModel.Columns.TlxCliNit.name()));
            values.put(DataModel.Columns.TlxFecCor.name(), object.getString(DataModel.Columns.TlxFecCor.name()).trim());
            values.put(DataModel.Columns.TlxFecVto.name(), object.getString(DataModel.Columns.TlxFecVto.name()).trim());
            values.put(DataModel.Columns.TlxFecproEmi.name(), object.getString(DataModel.Columns.TlxFecproEmi.name()).trim());
            values.put(DataModel.Columns.TlxFecproMed.name(), object.getString(DataModel.Columns.TlxFecproMed.name()).trim());
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
            values.put(DataModel.Columns.TlxFecEmi.name(), object.getString(DataModel.Columns.TlxFecEmi.name()).trim());
            values.put(DataModel.Columns.TlxUltPag.name(), object.getString(DataModel.Columns.TlxUltPag.name()).trim());
            values.put(DataModel.Columns.TlxEstado.name(), object.getInt(DataModel.Columns.TlxEstado.name()));
            values.put(DataModel.Columns.TlxUltObs.name(), object.getString(DataModel.Columns.TlxUltObs.name()).trim());
            values.put(DataModel.Columns.TlxActivi.name(), object.getString(DataModel.Columns.TlxActivi.name()).trim());
            values.put(DataModel.Columns.TlxCiudad.name(), object.getString(DataModel.Columns.TlxCiudad.name()).trim());
            values.put(DataModel.Columns.TlxFacNro.name(), object.getString(DataModel.Columns.TlxFacNro.name()).trim());
            values.put(DataModel.Columns.TlxNroAut.name(), object.getString(DataModel.Columns.TlxNroAut.name()).trim());
            values.put(DataModel.Columns.TlxCodCon.name(), object.getString(DataModel.Columns.TlxCodCon.name()).trim());
            values.put(DataModel.Columns.TlxFecLim.name(), object.getString(DataModel.Columns.TlxFecLim.name()).trim());
            values.put(DataModel.Columns.TlxKwhDev.name(), object.getInt(DataModel.Columns.TlxKwhDev.name()));
            values.put(DataModel.Columns.TlxUltTipL.name(), object.getInt(DataModel.Columns.TlxUltTipL.name()));
            values.put(DataModel.Columns.TlxCliNew.name(), object.getInt(DataModel.Columns.TlxCliNew.name()));
            values.put(DataModel.Columns.TlxEntEne.name(), object.getInt(DataModel.Columns.TlxEntEne.name()));
            values.put(DataModel.Columns.TlxDecEne.name(), object.getInt(DataModel.Columns.TlxDecEne.name()));
            values.put(DataModel.Columns.TlxEntPot.name(), object.getInt(DataModel.Columns.TlxEntPot.name()));
            values.put(DataModel.Columns.TlxDecPot.name(), object.getInt(DataModel.Columns.TlxDecPot.name()));
            values.put(DataModel.Columns.TlxDemPot.name(), object.getString(DataModel.Columns.TlxDemPot.name()).trim());
            values.put(DataModel.Columns.TlxPotTag.name(), object.getInt(DataModel.Columns.TlxPotTag.name()));
            values.put(DataModel.Columns.TlxKwhAdi2.name(), object.getInt(DataModel.Columns.TlxKwhAdi.name()));
            values.put(DataModel.Columns.TlxKwhDev2.name(), object.getInt(DataModel.Columns.TlxKwhDev.name()));
            values.putAll(stringNull(DataModel.Columns.TlxPreAnt1.name(), object.getString(DataModel.Columns.TlxPreAnt1.name()).trim()));
            values.putAll(stringNull(DataModel.Columns.TlxPreAnt2.name(), object.getString(DataModel.Columns.TlxPreAnt2.name()).trim()));
            values.putAll(stringNull(DataModel.Columns.TlxPreAnt3.name(), object.getString(DataModel.Columns.TlxPreAnt3.name()).trim()));
            values.putAll(stringNull(DataModel.Columns.TlxPreAnt4.name(), object.getString(DataModel.Columns.TlxPreAnt4.name()).trim()));
            values.putAll(stringNull(DataModel.Columns.TlxDebAuto.name(), object.getString(DataModel.Columns.TlxDebAuto.name()).trim()));
            values.putAll(stringNull(DataModel.Columns.TlxRecordatorio.name(), object.getString(DataModel.Columns.TlxRecordatorio.name()).trim()));
            values.put(DataModel.Columns.estado_lectura.name(), 0);
            values.put(DataModel.Columns.enviado.name(), 0);
            values.put(DataModel.Columns.TlxDignidad.name(), object.getInt(DataModel.Columns.TlxDignidad.name()));
            dbAdapter.saveObject(DBHelper.DATA_TABLE, values);

            try {
                JSONObject historico = object.getJSONObject("historico");
                Log.e(TAG, "processResponse: " + historico.toString());
                ContentValues valuesH = new ContentValues();
                valuesH.put(Historico.Columns.id.name(), historico.getInt(Historico.Columns.id.name()));
                valuesH.put(Historico.Columns.general_id.name(), historico.getInt(Historico.Columns.general_id.name()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes01.name(), historico.getString(Historico.Columns.ConMes01.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes02.name(), historico.getString(Historico.Columns.ConMes02.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes03.name(), historico.getString(Historico.Columns.ConMes03.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes04.name(), historico.getString(Historico.Columns.ConMes04.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes05.name(), historico.getString(Historico.Columns.ConMes05.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes06.name(), historico.getString(Historico.Columns.ConMes06.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes07.name(), historico.getString(Historico.Columns.ConMes07.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes08.name(), historico.getString(Historico.Columns.ConMes08.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes09.name(), historico.getString(Historico.Columns.ConMes09.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes10.name(), historico.getString(Historico.Columns.ConMes10.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes11.name(), historico.getString(Historico.Columns.ConMes11.name()).trim()));
                valuesH.putAll(stringNull(Historico.Columns.ConMes12.name(), historico.getString(Historico.Columns.ConMes12.name()).trim()));
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
            } catch (Exception e) {
                Log.e(TAG, "historico nulo", e);
            }

            try {
                JSONObject resultado = object.getJSONObject("resultados");
                ContentValues valuesRes = new ContentValues();
                valuesRes.put(Resultados.Columns.id.name(), resultado.getInt(Resultados.Columns.id.name()));
                valuesRes.put(Resultados.Columns.general_id.name(), resultado.getInt(Resultados.Columns.general_id.name()));
                valuesRes.put(Resultados.Columns.lectura.name(), resultado.getInt(Resultados.Columns.lectura.name()));
                valuesRes.put(Resultados.Columns.lectura_potencia.name(), resultado.getDouble(Resultados.Columns.lectura_potencia.name()));
                valuesRes.put(Resultados.Columns.observacion.name(), resultado.getDouble(Resultados.Columns.observacion.name()));
                dbAdapter.saveObject(DBHelper.RESULTADOS_TABLE, valuesRes);
            } catch (Exception ex){
                Log.e(TAG, "no hay rasultados");
            }

            JSONArray detalleFacturaArray = object.getJSONArray("detalle_factura");
            Log.e(TAG, "processResponse: " + detalleFacturaArray.toString());
            for (int j = 0; j < detalleFacturaArray.length(); j++) {
                JSONObject detalleFactura = detalleFacturaArray.getJSONObject(j);
                ContentValues valuesDF = new ContentValues();
                valuesDF.put(DetalleFactura.Columns.id.name(), detalleFactura.getInt(DetalleFactura.Columns.id.name()));
                valuesDF.put(DetalleFactura.Columns.general_id.name(), detalleFactura.getInt(DetalleFactura.Columns.general_id.name()));
                valuesDF.put(DetalleFactura.Columns.item_facturacion_id.name(), detalleFactura.getInt(DetalleFactura.Columns.item_facturacion_id.name()));
                valuesDF.put(DetalleFactura.Columns.importe.name(), detalleFactura.getDouble(DetalleFactura.Columns.importe.name()));
                valuesDF.put(DetalleFactura.Columns.imp_redondeo.name(), detalleFactura.getDouble(DetalleFactura.Columns.imp_redondeo.name()));
                dbAdapter.saveObject(DBHelper.DETALLE_FACTURA_TABLE, valuesDF);
            }
        }
        dbAdapter.close();
        return results.length() > 0;
    }

    /**
     * Metodo para preparar las lecturas que se van a enviar al servidor,  se envian las lecturas que no
     * se han enviado y que no sean postergadas.
     * Tambien se envian los medidores entre lineas
     *
     * @return retorna un hashtable para enviarlo en el endpoint
     */
    public static Hashtable<String, String> prepareDataToPost(Context context, User user) {
        Hashtable<String, String> params = new Hashtable<>();

        DBAdapter dbAdapter = new DBAdapter(context);
        ArrayList<DataModel> allData = dbAdapter.getAllDataToSend();

        params.put("UsrCod", String.valueOf(user.getLecCod()));

        for (DataModel dataModel : allData) {
            String json = DataModel.getJsonToSend(dataModel,
                    dbAdapter.getDataObsByCli(dataModel.getId()),
                    dbAdapter.getPrintObsData(dataModel.getId()),
                    dbAdapter.getDetalleFactura(dataModel.getId()));
            params.put("" + (dataModel.getTlxCli()), json);
        }

        try {
            ArrayList<MedEntreLineas> entreLineasList = dbAdapter.getMedEntreLineas();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < entreLineasList.size(); i++) {
                MedEntreLineas entreLineas = entreLineasList.get(i);
                jsonArray.put(i, entreLineas.toJson());
            }
            params.put("med_entre_lineas", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbAdapter.close();
        return params;
    }

    /**
     * Metodo para mostrar una alerta en parametros fijos, cuando sea un nuevo mes
     */
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

    /**
     * Metodo para actualizar el panel de estado de las lecturas
     */
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
            DataModel data = dbAdapter.getFirstData();
            nroRemesa = data.getTlxRem();
            nroArea = data.getTlxAre();
            labelReadyRuta.setText(String.valueOf(data.getTlxRutA()));

        }
        dbAdapter.close();

        String dateFromstring = StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT_1, Calendar.getInstance().getTime());
        labelReady.setText(dateFromstring);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(handlerTask);
        Log.e(TAG, "onDestroy: " + handlerTask);
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
            Snackbar.make(view, "No se han descargado las lecturas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class));
    }

    public void statusReady(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las lecturas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_READY));
    }

    public void statusMissing(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las lecturas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_MISSING));
    }

    public void statusPrint(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las lecturas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_PRINT));
    }

    public void StatusPostponed(View view) {
        if (!wasDownload || !isRate) {
            Snackbar.make(view, "No se han descargado las lecturas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(MainActivity.this, ReadingActivity.class).putExtra(KEY_FILTER, KEY_POSTPONED));
    }

    public void analytics(View view) {
        startActivity(new Intent(MainActivity.this, AnalyticsActivity.class));
    }

    private EditText nroMed;
    private EditText lecMed;

    /**
     * Este metodo muestra un cuadro de dialogo donde se agrega un nuevo medidor entre lineas.
     * Tambien verifica que el nuevo medidor no se haya guardado antes, y no estea en las lecturas
     *
     * @param view vista para mostrar mensajes al usuario
     */
    public void newMedidor(final View view) {
        if (!wasDownload) {
            Snackbar.make(view, "No se han descargado las rutas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder newMedidor = new AlertDialog.Builder(this);
        newMedidor.setTitle("Nuevo Medidor");
        View viewInside = LayoutInflater.from(this).inflate(R.layout.layout_new_medidor, null);
        nroMed = (EditText) viewInside.findViewById(R.id.new_med_number);
        lecMed = (EditText) viewInside.findViewById(R.id.new_med_lectura);
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

    public static ContentValues stringNull(String key, String string) {
        ContentValues values = new ContentValues();
        if (string.equalsIgnoreCase("null")) {
            values.putNull(key);
        } else {
            values.put(key, string);
        }
        return values;
    }

    private void sendEveryMinute() {
        Hashtable<String, String> params = prepareDataToPost(getApplicationContext(), user);
        new PostRequest(getApplicationContext(), params, null, Urls.urlSubida(getApplicationContext()), new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                Log.e(TAG, "onSuccess: " + result);
                UserPreferences.putLong(getApplicationContext(), KEY_SEND, Calendar.getInstance().getTimeInMillis());
                UserPreferences.putBoolean(getApplicationContext(), KEY_WAS_UPLOAD, true);
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
            }
        }).execute();
    }
}
