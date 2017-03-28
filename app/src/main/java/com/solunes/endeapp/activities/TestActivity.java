package com.solunes.endeapp.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.fragments.DataFragment;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.DataObs;
import com.solunes.endeapp.models.DetalleFactura;
import com.solunes.endeapp.models.Historico;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Resultados;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.GetRequest;
import com.solunes.endeapp.networking.PostRequest;
import com.solunes.endeapp.networking.Token;
import com.solunes.endeapp.utils.GenLecturas;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import static com.solunes.endeapp.activities.MainActivity.prepareDataToPost;
import static com.solunes.endeapp.activities.MainActivity.processResponse;
import static com.solunes.endeapp.activities.MainActivity.stringNull;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    private DBAdapter dbAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        dbAdapter = new DBAdapter(getApplicationContext());
        user = new User();
        user.setLecCod("user1");
        Button testDownload = (Button) findViewById(R.id.btn_test_download);
        testDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });
        Button testRun = (Button) findViewById(R.id.btn_test_run);
        testRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startTest();
            }
        });

        Button testSend = (Button) findViewById(R.id.btn_test_send);
        testSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRes();
            }
        });
    }

    private void inicio(DataModel dataModel, int lecturaEnergia, int lecturaPotencia, int obsCod) {
        Cursor dbAdapterObs = dbAdapter.getObs(obsCod);
        final Obs obs = Obs.fromCursor(dbAdapterObs);
        dbAdapterObs.close();

        // obtener tipo de lectura
        int tipoLectura = dataModel.getTlxTipLec();
        if (obs.getId() != 104) {
            tipoLectura = obs.getObsLec();
        }

        if (obs.getObsInd() == 1) {
            lecturaEnergia = dataModel.getTlxUltInd();
        } else if (obs.getObsInd() == 2) {
            lecturaEnergia = 0;
        }

        methodPequeñaMedianaDemanda(dataModel, lecturaEnergia, lecturaPotencia, tipoLectura, obs);
    }

    private void methodPequeñaMedianaDemanda(DataModel dataModel, int lecturaEnergia, int lecturaPotencia, int tipoLectura, final Obs obs) {

        int lecturaKwh;
        // obtener lectura de energia y verificar digitos
        int nuevaLectura = lecturaEnergia;
        if (nuevaLectura > dataModel.getTlxTope()) {
            return;
        }
        nuevaLectura = DataFragment.correccionDeDigitos(nuevaLectura, dataModel.getTlxDecEne());

        // correccion si es que el consumo estimado tiene un indice mayor a cero, se vuelve una lectura normal
        if (tipoLectura == 9 && lecturaEnergia == 0) {
            tipoLectura = 3;
        }

        // Calcular la lectura en Kwh segun el tipo de lectura
        if (tipoLectura == 3) {
            lecturaKwh = dataModel.getTlxConPro();
        } else {
            if (nuevaLectura < dataModel.getTlxUltInd()) {
                if ((dataModel.getTlxUltInd() - nuevaLectura) <= 10) {
                    nuevaLectura = dataModel.getTlxUltInd();
                }
            }
            lecturaKwh = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), nuevaLectura, dataModel.getTlxNroDig());
        }


        // Verificacion si el estado de cliente es cortado o suspendido y se introduce el mismo indice al anterior, se posterga
        if (dataModel.getTlxEstCli() == 3 || dataModel.getTlxEstCli() == 5) {
            if (dataModel.getTlxUltInd() == nuevaLectura) {
                tipoLectura = 5;
            }
        }

        // si hay alerta y el tipo de lectura no es postergada
        confirmarLectura(dataModel, tipoLectura, nuevaLectura, lecturaPotencia, lecturaKwh, obs);
        dbAdapter.close();
    }

    private void confirmarLectura(DataModel dataModel, int tipoLectura, int nuevaLectura, int lecturaPotencia, int finalLecturaKwh, Obs obs) {
        boolean isCalculo = DataFragment.calculo(getApplicationContext(),
                dataModel,
                tipoLectura,
                nuevaLectura,
                finalLecturaKwh,
                lecturaPotencia,
                obs);

        if (isCalculo) {
            DataFragment.saveDataModel(getApplicationContext(), dataModel, obs);
            Log.e(TAG, "calculo: " + dataModel.getId());
        } else {
            Log.e(TAG, "calculo: Error " + dataModel.getId());
        }
    }

    public void download() {
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
                                    response = processResponse(getApplicationContext(), result);
                                } catch (JSONException e) {
                                    Log.e(TAG, "onSuccess: ", e);
                                }
                                Log.e(TAG, "run: res: " + response);
                            }
                        };
                        new Thread(runSaveData).start();
                    }

                    @Override
                    public void onFailed(String reason, int statusCode) {
                        Log.e(TAG, "onFailed: " + reason);
                    }
                }).execute();
            }

            @Override
            public void onFailToken() {
                Log.e(TAG, "onFailToken: ");
            }
        });
    }

    private void sendRes() {
        Hashtable<String, String> params = prepareDataToPost(getApplicationContext(), user);
        new PostRequest(getApplicationContext(), params, null, Urls.urlSubida(getApplicationContext()), new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                Log.e(TAG, "onSuccess: " + result);
                Toast.makeText(TestActivity.this, "Ressultados enviados", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
                Toast.makeText(TestActivity.this, reason, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void startTest() {
        new AsyncTask<Boolean, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Boolean... booleen) {
//                ArrayList<Integer> integers = new ArrayList<>();
//                integers.add(143859);
//                integers.add(976005);
//                for (int id : integers) {
//                    DataModel data = dbAdapter.getData(id);
//                    Resultados dataRes = dbAdapter.getDataRes(data.getId());
//                    inicio(data, dataRes.getLectura(), dataRes.getLecturaPotencia(), dataRes.getObservacion());
//                }

                for (DataModel dataModel : dbAdapter.getAllData()) {
                    Resultados dataRes = dbAdapter.getDataRes(dataModel.getId());
                    inicio(dataModel, dataRes.getLectura(), dataRes.getLecturaPotencia(), dataRes.getObservacion());
                }
                Log.e(TAG, "doInBackground: finish");

                return Boolean.TRUE;
            }

        }.execute();
    }
}
