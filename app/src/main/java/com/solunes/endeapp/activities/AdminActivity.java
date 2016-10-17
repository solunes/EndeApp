package com.solunes.endeapp.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.models.ItemFacturacion;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.PrintObs;
import com.solunes.endeapp.models.Tarifa;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.GetRequest;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    public static final String KEY_TPL = "key_tpl";

    private EditText editTpl;
    private TextView nroTpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        editTpl = (EditText) findViewById(R.id.edit_tpl);
        TextView textUsername = (TextView) findViewById(R.id.text_username);
        Button btnSaveTpl = (Button) findViewById(R.id.btn_save_tpl);
        Button btnFixParams = (Button) findViewById(R.id.btn_fix_params);
        nroTpl = (TextView) findViewById(R.id.label_nro_tpl);
        int nro = UserPreferences.getInt(getApplicationContext(), KEY_TPL);
        if (nro > 0) {
            nroTpl.setText("Nro. TPL: " + nro);
            editTpl.setText(nro + "");
        }

        int id_user = getIntent().getExtras().getInt("id_user");
        DBAdapter dbAdapter = new DBAdapter(this);
        User user = dbAdapter.getUser(id_user);
        dbAdapter.close();
        textUsername.setText(user.getLecNom());
        btnSaveTpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTpl.getText().toString().isEmpty()) {
                    UserPreferences.putInt(getApplicationContext(), KEY_TPL, Integer.parseInt(editTpl.getText().toString()));
                    nroTpl.setText("Nro. TPL: " + editTpl.getText().toString());
                }
            }
        });
        btnFixParams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                final ProgressDialog progressDialog = new ProgressDialog(AdminActivity.this);
                builder.setTitle("Administrador");
                builder.setPositiveButton("Aceptar", null);
                progressDialog.setMessage("Descargando....");
                progressDialog.setCancelable(false);
                new GetRequest("http://ende.solunes.com/api/parametros-fijos", new CallbackAPI() {
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
                        builder.setMessage("Error en descarga");
                        builder.show();
                        progressDialog.dismiss();
                    }
                }).execute();
                progressDialog.show();
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

    public static void processResultFixParams(Context context, String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
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

        JSONArray usuarios = jsonObject.getJSONArray("usuarios");
        for (int i = 0; i < usuarios.length(); i++) {
            JSONObject object = usuarios.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(User.Columns.LecId.name(), object.getInt(User.Columns.LecId.name()));
//            values.put(User.Columns.LecNro.name(), object.getInt(User.Columns.LecNro.name()));
            values.put(User.Columns.LecNom.name(), object.getString(User.Columns.LecNom.name()));
            values.put(User.Columns.LecCod.name(), object.getString(User.Columns.LecCod.name()));
            values.put(User.Columns.LecPas.name(), object.getString(User.Columns.LecPas.name()));
            values.put(User.Columns.LecNiv.name(), object.getInt(User.Columns.LecNiv.name()));
//            values.put(User.Columns.LecAsi.name(), object.getInt(User.Columns.LecAsi.name()));
//            values.put(User.Columns.LecAct.name(), object.getInt(User.Columns.LecAct.name()));
//            values.put(User.Columns.AreaCod.name(), object.getInt(User.Columns.AreaCod.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.USER_TABLE, values);
        }

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

        JSONArray parametros = jsonObject.getJSONArray("parametros");
        for (int i = 0; i < parametros.length(); i++) {
            JSONObject object = parametros.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(Parametro.Columns.id.name(), object.getInt(Parametro.Columns.id.name()));
            try {
                values.put(Parametro.Columns.valor.name(), object.getString(Parametro.Columns.valor.name()));
            } catch (JSONException e) {
                values.put(Parametro.Columns.valor.name(), 0);
            }
            try {
                values.put(Parametro.Columns.texto.name(), object.getInt(Parametro.Columns.texto.name()));
            } catch (JSONException e) {
                values.put(Parametro.Columns.texto.name(), "");
            }
            // guardar values
            dbAdapter.saveObject(DBHelper.PARAMETRO_TABLE, values);
        }

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
        JSONArray observacionesImp = jsonObject.getJSONArray("observaciones_imp");
        for (int i = 0; i < observacionesImp.length(); i++) {
            JSONObject object = observacionesImp.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put(PrintObs.Columns.id.name(), object.getInt(PrintObs.Columns.id.name()));
            values.put(PrintObs.Columns.ObiDes.name(), object.getString(PrintObs.Columns.ObiDes.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.PRINT_OBS_TABLE, values);
        }
    }
}
