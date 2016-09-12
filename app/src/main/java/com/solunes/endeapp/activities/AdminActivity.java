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
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Tarifa;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.GetRequest;
import com.solunes.endeapp.utils.UserPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity {

    private static final String TAG = "AdminActivity";
    public static final String KEY_TPL = "key_tpl";

    private EditText editTpl;

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
                            processResultFixParams(getApplicationContext(),result);
                            progressDialog.dismiss();
                            builder.setMessage("Los datos de tarifas, observaciones y usuarios han sido descargados");
                            builder.show();
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
            values.put(Tarifa.Columns.kwh_desde.name(), object.getInt(Tarifa.Columns.kwh_desde.name()));
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
            values.put(User.Columns.id.name(), object.getInt(User.Columns.id.name()));
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
            values.put(Obs.Columns.ObsCod.name(), object.getInt(Obs.Columns.ObsCod.name()));
            values.put(Obs.Columns.ObsDes.name(), object.getString(Obs.Columns.ObsDes.name()));
            values.put(Obs.Columns.ObsTip.name(), object.getInt(Obs.Columns.ObsTip.name()));
            values.put(Obs.Columns.ObsLec.name(), object.getInt(Obs.Columns.ObsLec.name()));
            values.put(Obs.Columns.ObsFac.name(), object.getInt(Obs.Columns.ObsFac.name()));
            // guardar values
            dbAdapter.saveObject(DBHelper.OBS_TABLE, values);
        }
     }
}
