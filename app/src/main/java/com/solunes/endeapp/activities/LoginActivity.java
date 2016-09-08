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
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.utils.UserPreferences;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String KEY_LOGIN = "login";

    private EditText user;
    private EditText pass;
    private TextInputLayout inputLayoutUser;
    private TextInputLayout inputLayoutPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (UserPreferences.getBoolean(this, KEY_LOGIN)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        inputLayoutUser = (TextInputLayout) findViewById(R.id.input_user);
        inputLayoutPass = (TextInputLayout) findViewById(R.id.input_pass);
        user = (EditText) findViewById(R.id.edit_user);
        pass = (EditText) findViewById(R.id.edit_pass);
        Button buttonSign = (Button) findViewById(R.id.btn_signup);
        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                    Cursor cursor = dbAdapter.checkUser(user.getText().toString(), pass.getText().toString());
                    if (cursor.getCount() > 0) {
                        User user = User.fromCursor(cursor);
                        if (user.getLecNiv() == 1){
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            intent.putExtra("id_user", user.getId());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("id_user", user.getId());
                            startActivity(intent);
                            UserPreferences.putBoolean(LoginActivity.this, KEY_LOGIN, true);
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

//        QRGEncoder qrgEncoder = new QRGEncoder("something", null, QRGContents.Type.TEXT, 1000);
//        try {
//            ImageView bg = (ImageView) findViewById(R.id.bg);
//            // Getting QR-Code as Bitmap
//            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
//            // Setting Bitmap to ImageView
//            bg.setImageBitmap(bitmap);
//        } catch (WriterException e) {
//            Log.v(TAG, e.toString());
//        }
    }
}
