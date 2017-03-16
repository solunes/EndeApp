package com.solunes.endeapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.solunes.endeapp.BuildConfig;
import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.utils.UserPreferences;

/**
 * En esta actividad se muesta el login de inicio de sesion
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_LOGIN_ID = "user_id";

    private EditText user;
    private EditText pass;
    private TextInputLayout inputLayoutUser;
    private TextInputLayout inputLayoutPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // si ya hay un usuario logueado, pasa director a la vista principal
        if (UserPreferences.getBoolean(this, KEY_LOGIN)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        versionName();

        inputLayoutUser = (TextInputLayout) findViewById(R.id.input_user);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.input_pass);
        user = (EditText) findViewById(R.id.edit_user);
        pass = (EditText) findViewById(R.id.edit_pass);
        Button buttonSign = (Button) findViewById(R.id.btn_signup);
        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validacion de campos
                boolean valid = true;
                if (user.getText().toString().isEmpty()) {
                    inputLayoutUser.setError("Campo requerido!!!");
                    valid = false;
                } else {
                    inputLayoutUser.setError(null);
                }
                if (pass.getText().toString().isEmpty()) {
                    inputLayoutPass.setError("Campo requerido!!!");
                    valid = false;
                } else {
                    inputLayoutPass.setError(null);
                }
                if (valid) {
                    // obtiene un usuario de la base de datos
                    DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                    Cursor cursor = dbAdapter.checkUser(getResources().getString(R.string.seed), user.getText().toString(), pass.getText().toString());
                    // hay usuario
                    if (cursor.getCount() > 0) {
                        User user = User.fromCursor(cursor);
                        // si el es un administrador, va a la vista de administrador
                        if (user.getLecNiv() == 1) {
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.putExtra("id_user", user.getLecId());
                            startActivity(intent);
                        } else {
                            // si se hace un cambio de usuario se eliminan los datos del anterior usuario
                            if (user.getLecId() != UserPreferences.getInt(LoginActivity.this, KEY_LOGIN_ID)) {
                                UserPreferences.putLong(LoginActivity.this, MainActivity.KEY_SEND, 0);
                                UserPreferences.putInt(LoginActivity.this, LoginActivity.KEY_LOGIN_ID, 0);
                                UserPreferences.putLong(LoginActivity.this, MainActivity.KEY_DOWNLOAD, 0);
                                UserPreferences.putBoolean(LoginActivity.this, MainActivity.KEY_WAS_UPLOAD, false);
                                dbAdapter.clearTablesNoUser();
                            }
                            // se ingresa a la vista principal
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id_user", user.getLecId());
                            startActivity(intent);
                            UserPreferences.putBoolean(LoginActivity.this, KEY_LOGIN, true);
                            UserPreferences.putInt(LoginActivity.this, KEY_LOGIN_ID, user.getLecId());
                        }
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                    dbAdapter.close();
                }
            }
        });
    }

    private void versionName() {
        TextView textView = (TextView) findViewById(R.id.label_version_name);
        textView.setText("v" + BuildConfig.VERSION_NAME);
    }
}
