package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.models.FacturaDosificacion;
import com.solunes.endeapp.models.ItemFacturacion;
import com.solunes.endeapp.models.LimitesMaximos;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.PrintObs;
import com.solunes.endeapp.models.Tarifa;
import com.solunes.endeapp.models.TarifaAseo;
import com.solunes.endeapp.models.TarifaTap;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.GetRequest;
import com.solunes.endeapp.networking.PostRequest;
import com.solunes.endeapp.networking.Token;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.Urls;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * Esta activity controla el login del usuario
 */
public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    public static final String KEY_DOMAIN = "key_domain";
    public static final String KEY_PRINT_MANE = "key_print_name";

    private User user;

    private EditText editDomain;
    private TextView nroDomain;

    private EditText editPrintName;
    private TextView printName;
    private String url;

    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        editDomain = (EditText) findViewById(R.id.edit_domain);
        TextView textUsername = (TextView) findViewById(R.id.text_username);
        Button btnSaveDomain = (Button) findViewById(R.id.btn_save_domain);
        Button btnFixParams = (Button) findViewById(R.id.btn_fix_params);
        nroDomain = (TextView) findViewById(R.id.label_nro_domain);
        url = UserPreferences.getString(getApplicationContext(), KEY_DOMAIN);
        if (url != null) {
            nroDomain.setText("Url: " + url);
            editDomain.setText(url);
        }

        // obtener usuario
        int id_user = getIntent().getExtras().getInt("id_user");
        DBAdapter dbAdapter = new DBAdapter(this);
        user = dbAdapter.getUser(id_user);
        dbAdapter.close();
        textUsername.setText(user.getLecNom());
        btnSaveDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editDomain.getText().toString().isEmpty()) {
                    // guardar url del servidor
                    url = editDomain.getText().toString();
                    UserPreferences.putString(getApplicationContext(), KEY_DOMAIN, url);
                    nroDomain.setText("Url: " + url);
                }
            }
        });
        btnFixParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // mensaje de descarga de parametros fijos
                builder = new AlertDialog.Builder(AdminActivity.this);
                progressDialog = new ProgressDialog(AdminActivity.this);
                builder.setTitle("Administrador");
                builder.setPositiveButton("Aceptar", null);
                progressDialog.setMessage("Descargando....");
                progressDialog.setCancelable(false);
                if (url == null) {
                    Snackbar.make(view, "No hay url del servidor", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                // previamente a la consulta del servidor, se obtiene un Token
                Token.getToken(getApplicationContext(), user, new Token.CallbackToken() {
                    @Override
                    public void onSuccessToken() {
                        parametrosRequest();
                    }

                    @Override
                    public void onFailToken() {
                        progressDialog.dismiss();
                    }
                });
                progressDialog.show();
            }
        });

        // se muestra el nombre de la impresora
        printName = (TextView) findViewById(R.id.label_print_name);
        String string = UserPreferences.getString(getApplicationContext(), KEY_PRINT_MANE);
        if (string != null) {
            printName.setText("Impresora: " + string);
        }
        editPrintName = (EditText) findViewById(R.id.edit_print_name);
        Button buttonPrintName = (Button) findViewById(R.id.btn_print_name);
        buttonPrintName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // se guarda el nombre de la impresora si no hay
                String printname = editPrintName.getText().toString();
                if (!printname.isEmpty()) {
                    UserPreferences.putString(getApplicationContext(), KEY_PRINT_MANE, printname);
                    printName.setText("Impresora: " + printname);
                }
            }
        });
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
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo para procesar lo obtenido de los parametros fijos
     *
     * @param context contexto de la aplicacion para hacer consultas a la base de datos
     * @param result  es una cadena json que contiene de los datos de parametros fijos
     * @throws JSONException excepcion si el string result no tiene formato json
     */
    public static void processResultFixParams(Context context, String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);

        // guarda tarifas
        JSONArray tarifas = jsonObject.getJSONArray("tarifas");
        DBAdapter dbAdapter = new DBAdapter(context);
        dbAdapter.clearTables();
        for (int i = 0; i < tarifas.length(); i++) {
            JSONObject object = tarifas.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(Tarifa.Columns.id.name(), object.getInt(Tarifa.Columns.id.name()));
            values.put(Tarifa.Columns.categoria_tarifa_id.name(), object.getInt(Tarifa.Columns.categoria_tarifa_id.name()));
            values.put(Tarifa.Columns.item_facturacion_id.name(), object.getInt(Tarifa.Columns.item_facturacion_id.name()));
            try {
                values.put(Tarifa.Columns.kwh_desde.name(), object.getInt(Tarifa.Columns.kwh_desde.name()));
            } catch (JSONException e) {
                values.put(Tarifa.Columns.kwh_desde.name(), 0);
            }
            try {
                values.put(Tarifa.Columns.kwh_hasta.name(), object.getInt(Tarifa.Columns.kwh_hasta.name()));
            } catch (JSONException e) {
                values.put(Tarifa.Columns.kwh_hasta.name(), 0);
            }
            values.put(Tarifa.Columns.importe.name(), object.getDouble(Tarifa.Columns.importe.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.TARIFA_TABLE, values);
        }

        // guarda usuarios
        JSONArray usuarios = jsonObject.getJSONArray("usuarios");
        for (int i = 0; i < usuarios.length(); i++) {
            JSONObject object = usuarios.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(User.Columns.LecId.name(), object.getInt(User.Columns.LecId.name()));
            values.put(User.Columns.LecNom.name(), object.getString(User.Columns.LecNom.name()));
            values.put(User.Columns.LecCod.name(), object.getString(User.Columns.LecCod.name()));
            values.put(User.Columns.LecPas.name(), object.getString(User.Columns.LecPas.name()));
            values.put(User.Columns.LecNiv.name(), object.getInt(User.Columns.LecNiv.name()));
            values.put(User.Columns.LecAsi.name(), object.getInt(User.Columns.LecAsi.name()));
            values.put(User.Columns.LecAct.name(), object.getInt(User.Columns.LecAct.name()));
            values.put(User.Columns.AreaCod.name(), object.getInt(User.Columns.AreaCod.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.USER_TABLE, values);
        }

        // guarda observaciones
        JSONArray observaciones = jsonObject.getJSONArray("observaciones");
        for (int i = 0; i < observaciones.length(); i++) {
            JSONObject object = observaciones.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(Obs.Columns.id.name(), object.getInt(Obs.Columns.id.name()));
            values.put(Obs.Columns.ObsDes.name(), object.getString(Obs.Columns.ObsDes.name()));
            values.put(Obs.Columns.ObsTip.name(), object.getInt(Obs.Columns.ObsTip.name()));
            values.put(Obs.Columns.ObsLec.name(), object.getInt(Obs.Columns.ObsLec.name()));
            values.put(Obs.Columns.ObsFac.name(), object.getInt(Obs.Columns.ObsFac.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.OBS_TABLE, values);
        }

        // guarda parametros
        JSONArray parametros = jsonObject.getJSONArray("parametros");
        for (int i = 0; i < parametros.length(); i++) {
            JSONObject object = parametros.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(Parametro.Columns.id.name(), object.getInt(Parametro.Columns.id.name()));
            values.put(Parametro.Columns.codigo.name(), object.getString(Parametro.Columns.codigo.name()));
            try {
                values.put(Parametro.Columns.valor.name(), object.getInt(Parametro.Columns.valor.name()));
            } catch (JSONException e) {
                values.put(Parametro.Columns.valor.name(), 0);
            }
            try {
                values.put(Parametro.Columns.texto.name(), object.getString(Parametro.Columns.texto.name()));
            } catch (JSONException e) {
                values.put(Parametro.Columns.texto.name(), "");
            }
            // guardar values
            dbAdapter.saveObject(DBHelper.PARAMETRO_TABLE, values);
        }

        // guarda items de facturacion
        JSONArray itemFacturacion = jsonObject.getJSONArray("item_facturacion");
        for (int i = 0; i < itemFacturacion.length(); i++) {
            JSONObject object = itemFacturacion.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(ItemFacturacion.Columns.id.name(), object.getInt(ItemFacturacion.Columns.id.name()));
            values.put(ItemFacturacion.Columns.codigo.name(), object.getInt(ItemFacturacion.Columns.codigo.name()));
            values.put(ItemFacturacion.Columns.concepto.name(), object.getInt(ItemFacturacion.Columns.concepto.name()));
            values.put(ItemFacturacion.Columns.descripcion.name(), object.getString(ItemFacturacion.Columns.descripcion.name()));
            values.put(ItemFacturacion.Columns.estado.name(), object.getInt(ItemFacturacion.Columns.estado.name()));
            values.put(ItemFacturacion.Columns.credito_fiscal.name(), object.getInt(ItemFacturacion.Columns.credito_fiscal.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.ITEM_FACTURACION_TABLE, values);
        }

        // guarda observaciones de impresion
        JSONArray observacionesImp = jsonObject.getJSONArray("observaciones_imp");
        for (int i = 0; i < observacionesImp.length(); i++) {
            JSONObject object = observacionesImp.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(PrintObs.Columns.id.name(), object.getInt(PrintObs.Columns.id.name()));
            values.put(PrintObs.Columns.ObiDes.name(), object.getString(PrintObs.Columns.ObiDes.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.PRINT_OBS_TABLE, values);
        }

        //  guarda la dosificacion de facturas
        JSONArray facturaDosificacion = jsonObject.getJSONArray("factura_dosificacion");
        for (int i = 0; i < facturaDosificacion.length(); i++) {
            JSONObject object = facturaDosificacion.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(FacturaDosificacion.Columns.id.name(), object.getInt(FacturaDosificacion.Columns.id.name()));
            values.put(FacturaDosificacion.Columns.area_id.name(), object.getInt(FacturaDosificacion.Columns.area_id.name()));
            values.put(FacturaDosificacion.Columns.numero.name(), object.getInt(FacturaDosificacion.Columns.numero.name()));
            values.put(FacturaDosificacion.Columns.comprobante.name(), object.getInt(FacturaDosificacion.Columns.comprobante.name()));
            values.put(FacturaDosificacion.Columns.numero_autorizacion.name(), object.getInt(FacturaDosificacion.Columns.numero_autorizacion.name()));
            values.put(FacturaDosificacion.Columns.numero_factura.name(), object.getInt(FacturaDosificacion.Columns.numero_factura.name()));
            values.put(FacturaDosificacion.Columns.estado.name(), object.getInt(FacturaDosificacion.Columns.estado.name()));
            values.put(FacturaDosificacion.Columns.fecha_inicial.name(), object.getString(FacturaDosificacion.Columns.fecha_inicial.name()));
            values.put(FacturaDosificacion.Columns.fecha_limite_emision.name(), object.getString(FacturaDosificacion.Columns.fecha_limite_emision.name()));
            values.put(FacturaDosificacion.Columns.llave_dosificacion.name(), object.getString(FacturaDosificacion.Columns.llave_dosificacion.name()));
            values.put(FacturaDosificacion.Columns.leyenda1.name(), object.getString(FacturaDosificacion.Columns.leyenda1.name()));
            values.put(FacturaDosificacion.Columns.leyenda1.name(), object.getString(FacturaDosificacion.Columns.leyenda1.name()));
            values.put(FacturaDosificacion.Columns.actividad_economica.name(), object.getString(FacturaDosificacion.Columns.actividad_economica.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.FACTURA_DOSIFICACION_TABLE, values);
        }

        // guarda las tarifas de alumbrado publico
        JSONArray tarifaTap = jsonObject.getJSONArray("tarifas-tap");
        for (int i = 0; i < tarifaTap.length(); i++) {
            JSONObject object = tarifaTap.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(TarifaTap.Columns.id.name(), object.getInt(TarifaTap.Columns.id.name()));
            values.put(TarifaTap.Columns.area_id.name(), object.getInt(TarifaTap.Columns.area_id.name()));
            values.put(TarifaTap.Columns.categoria_tarifa_id.name(), object.getInt(TarifaTap.Columns.categoria_tarifa_id.name()));
            values.put(TarifaTap.Columns.anio.name(), object.getInt(TarifaTap.Columns.anio.name()));
            values.put(TarifaTap.Columns.mes.name(), object.getInt(TarifaTap.Columns.mes.name()));
            values.put(TarifaTap.Columns.valor.name(), object.getDouble(TarifaTap.Columns.valor.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.TARIFA_TAP_TABLE, values);
        }

        // guarda las tarifas de aseo
        JSONArray tarifaTas = jsonObject.getJSONArray("tarifas-aseo");
        for (int i = 0; i < tarifaTas.length(); i++) {
            JSONObject object = tarifaTas.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(TarifaAseo.Columns.id.name(), object.getInt(TarifaAseo.Columns.id.name()));
            values.put(TarifaAseo.Columns.categoria_tarifa_id.name(), object.getInt(TarifaAseo.Columns.categoria_tarifa_id.name()));
            values.put(TarifaAseo.Columns.anio.name(), object.getInt(TarifaAseo.Columns.anio.name()));
            values.put(TarifaAseo.Columns.mes.name(), object.getInt(TarifaAseo.Columns.mes.name()));
            values.put(TarifaAseo.Columns.kwh_desde.name(), object.getInt(TarifaAseo.Columns.kwh_desde.name()));
            values.put(TarifaAseo.Columns.kwh_hasta.name(), object.getInt(TarifaAseo.Columns.kwh_hasta.name()));
            values.put(TarifaAseo.Columns.importe.name(), object.getDouble(TarifaAseo.Columns.importe.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.TARIFA_ASEO_TABLE, values);
        }

        // guarda los limites maximos
        JSONArray limitesMaximos = jsonObject.getJSONArray("limites_maximos");
        for (int i = 0; i < limitesMaximos.length(); i++) {
            JSONObject object = limitesMaximos.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(LimitesMaximos.Columns.id.name(), object.getInt(LimitesMaximos.Columns.id.name()));
            values.put(LimitesMaximos.Columns.categoria_tarifa_id.name(), object.getInt(LimitesMaximos.Columns.categoria_tarifa_id.name()));
            values.put(LimitesMaximos.Columns.max_kwh.name(), object.getInt(LimitesMaximos.Columns.max_kwh.name()));
            values.put(LimitesMaximos.Columns.max_bs.name(), object.getInt(LimitesMaximos.Columns.max_bs.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.LIMITES_MAXIMOS_TABLE, values);
        }
    }

    /**
     * Este metodo hace la consulta el servidor para obtener los paratros fijos
     */
    private void parametrosRequest() {
        new GetRequest(getApplicationContext(), Urls.urlParametros(getApplicationContext()), new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                try {
                    processResultFixParams(getApplicationContext(), result);
                    progressDialog.dismiss();
                    builder.setMessage("Los datos de tarifas, observaciones y usuarios han sido descargados");
                    builder.show();
                    UserPreferences.putLong(getApplicationContext(), MainActivity.KEY_RATE, Calendar.getInstance().getTimeInMillis());
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    UserPreferences.putInt(getApplicationContext(), MainActivity.KEY_RATE_MONTH, month);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                builder.setMessage(reason);
                builder.show();
                progressDialog.dismiss();
            }
        }).execute();
    }
}
