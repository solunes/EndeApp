package com.solunes.endeapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.adapters.PagerAdapter;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.fragments.DataFragment;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.MedEntreLineas;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.networking.CallbackAPI;
import com.solunes.endeapp.networking.PostRequest;
import com.solunes.endeapp.utils.StringUtils;
import com.solunes.endeapp.utils.Urls;
import com.solunes.endeapp.utils.UserPreferences;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Set;

import static com.solunes.endeapp.activities.MainActivity.KEY_ENDPOINT_GESTION;
import static com.solunes.endeapp.activities.MainActivity.KEY_ENDPOINT_MONTH;
import static com.solunes.endeapp.activities.MainActivity.KEY_ENDPOINT_REMESA;
import static com.solunes.endeapp.activities.MainActivity.KEY_SEND;
import static com.solunes.endeapp.activities.MainActivity.KEY_WAS_UPLOAD;

public class ReadingActivity extends AppCompatActivity implements DataFragment.OnFragmentListener, SearchView.OnQueryTextListener {

    private static final String TAG = "ReadingActivity";

    public static final String KEY_LAST_PAGER_PSOTION = "last_pager_position";
    public static final String KEY_LAST_DATA_SAVED = "last_data_saved";

    private ZebraPrinter printer;
    private Connection connection;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter adapter;

    private SearchView searchView;
    private MenuItem searchItem;

    private RadioGroup radioGroup;
    private RadioButton radioCli;

    private Snackbar snackbar;

    private int currentState = -1;
    private ArrayList<DataModel> datas;
    private User user;

    private int interval;
    Handler handler = new Handler();
    Runnable handlerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        DBAdapter dbAdapter = new DBAdapter(this);
        user = dbAdapter.getUser(UserPreferences.getInt(this, LoginActivity.KEY_LOGIN_ID));
        interval = (int) dbAdapter.getParametroValor(Parametro.Values.tiempo_envio.name());
        datas = new ArrayList<>();
        if (getIntent().getExtras() != null) {
            int filter = getIntent().getExtras().getInt(MainActivity.KEY_FILTER);
            currentState = filter;
            switch (filter) {
                case MainActivity.KEY_READY:
                    datas = dbAdapter.getReady();
                    break;
                case MainActivity.KEY_MISSING:
                    datas = dbAdapter.getState(0);
                    break;
                case MainActivity.KEY_PRINT:
                    datas = dbAdapter.getState(1);
                    break;
                case MainActivity.KEY_POSTPONED:
                    datas = dbAdapter.getState(2);
                    break;
            }
        } else {
            datas = dbAdapter.getAllData();
        }
        adapter = new PagerAdapter(getSupportFragmentManager(), datas.size(), datas);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tabAt = tabLayout.getTabAt(i);
            View inflate = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabText = (TextView) inflate.findViewById(R.id.textview_custom_tab);
            tabText.setText(i + 1 + "");
            if (datas.get(i).getEstadoLectura() != DataFragment.estados_lectura.Pendiente.ordinal()) {
                tabText.setTextColor(getResources().getColor(android.R.color.white));
            }
            tabAt.setCustomView(inflate);
        }
        handlerTask = new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: enviado: mins " + interval);
                sendEveryMinute();
                handler.postDelayed(handlerTask, interval * 1000 * 60);
            }
        };
        handlerTask.run();
        dbAdapter.close();

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                doConnection();
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();

        int idData = UserPreferences.getInt(getApplicationContext(), KEY_LAST_PAGER_PSOTION);
        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getId() == idData) {
                viewPager.setCurrentItem(i);
                break;
            }
        }

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioCli = (RadioButton) findViewById(R.id.cli_radio);
    }

    @Override
    public void onTabListener() {
        View customView = tabLayout.getTabAt(viewPager.getCurrentItem()).getCustomView();
        TextView textTab = (TextView) customView.findViewById(R.id.textview_custom_tab);
        textTab.setTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public void onPrinting(final String srcToPrint) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                sendLabelToPrint(srcToPrint);
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    @Override
    public void onAjusteOrden(int idData) {
        DBAdapter dbAdapter = new DBAdapter(this);
        DataModel dataLastSaved = dbAdapter.getLastSaved();
        DataModel dataCurrent = dbAdapter.getData(idData);
        if (dataLastSaved == null) {
            dbAdapter.orderPendents(0, dataCurrent.getTlxOrdTpl());
            dataCurrent.setTlxOrdTpl(1);
        } else {
            dbAdapter.orderPendents(dataLastSaved.getTlxOrdTpl(), dataCurrent.getTlxOrdTpl());
            dataCurrent.setTlxOrdTpl(dataLastSaved.getTlxOrdTpl() + 1);
        }
        ContentValues values = new ContentValues();
        values.put(DataModel.Columns.TlxOrdTpl.name(), dataCurrent.getTlxOrdTpl());
        dbAdapter.updateData(dataCurrent.getId(), values);
        dbAdapter.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search:
                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserPreferences.putInt(this, KEY_LAST_PAGER_PSOTION, datas.get(viewPager.getCurrentItem()).getId());
    }

    private void doConnection() {
        printer = connect();
    }

    private String getMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.e(TAG, "getMacAddress: " + bluetoothAdapter);
        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equalsIgnoreCase(UserPreferences.getString(getApplicationContext(), AdminActivity.KEY_PRINT_MANE))) {
                        Log.e(TAG, "onBluetooth: " + device.getName() + " - " + device.getAddress());
                        return device.getAddress();
                    }
                }
            }
        } else {
            snackbar.setText("Bluetooth apagado");
        }
        return null;
    }

    public ZebraPrinter connect() {
        Log.e(TAG, "Connecting... ");
        snackbar = Snackbar.make(viewPager, "Conectando con la impresora", Snackbar.LENGTH_LONG);
        snackbar.show();
        connection = null;
        String macAddress = getMacAddress();
        connection = new BluetoothConnection(macAddress);
//        SettingsHelper.saveBluetoothAddress(this, getMacAddressFieldText());

        try {
            connection.open();
            Log.e(TAG, "Connected");
            Snackbar.make(viewPager, "Impresora conectada", Snackbar.LENGTH_SHORT).show();
        } catch (ConnectionException e) {
            Log.e(TAG, "Comm Error! Disconnecting");
            sleeper(500);
            disconnect();
        }

        ZebraPrinter printer = null;

        if (connection.isConnected()) {
            try {

                printer = ZebraPrinterFactory.getInstance(connection);
                Log.e(TAG, "Determining Printer Languag");
                String pl = SGD.GET("device.languages", connection);
                Log.e(TAG, "Printer Language " + pl);
            } catch (ConnectionException e) {
                printer = null;
                sleeper(500);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                printer = null;
                sleeper(500);
                disconnect();
            }
        }

        return printer;
    }

    public void disconnect() {
        try {
            Log.e(TAG, "Disconnecting");
            if (connection != null) {
                connection.close();
            }
            Log.e(TAG, "Not Connected");
        } catch (ConnectionException e) {
            Log.e(TAG, "COMM Error! Disconnected");
        }
    }

    private void sendLabelToPrint(String label) {
        if (connection.isConnected()) {
            try {
                ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);
                PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {
                    connection.write(label.getBytes("ISO-8859-1"));
                    Log.e(TAG, "sending data");
                    Snackbar.make(viewPager, "Imprimiendo", Snackbar.LENGTH_LONG).show();
                } else if (printerStatus.isHeadOpen) {
                    Log.e(TAG, "printer head open");
                    Snackbar.make(viewPager, "Cabezal abierto", Snackbar.LENGTH_LONG).show();
                } else if (printerStatus.isPaused) {
                    Log.e(TAG, "printer is paused");
                    Snackbar.make(viewPager, "Impresora pausada", Snackbar.LENGTH_LONG).show();
                } else if (printerStatus.isPaperOut) {
                    Log.e(TAG, "printer media out");
                    Snackbar.make(viewPager, "Impresora sin papel", Snackbar.LENGTH_LONG).show();
                }
                sleeper(1500);
                if (connection instanceof BluetoothConnection) {
                    String friendlyName = ((BluetoothConnection) connection).getFriendlyName();
                    Log.e(TAG, friendlyName);
                    sleeper(500);
                }
            } catch (ConnectionException e) {
                Log.e(TAG, e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "sendLabelToPrint: ", e);
            }
        } else {
            Snackbar.make(viewPager, "Impresora desconectada", Snackbar.LENGTH_LONG).show();
        }
    }

    private void sleeper(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Cliente o Medidor");

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                radioGroup.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                radioGroup.setVisibility(View.GONE);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String filter = !TextUtils.isEmpty(query) ? query : null;
        DBAdapter dbAdapter = new DBAdapter(this);
        boolean isCli = radioCli.isChecked();
        Cursor cursor = dbAdapter.searchClienteMedidor(filter, isCli, currentState);
        if (cursor.getCount() > 0) {
            DataModel dataModel = DataModel.fromCursor(cursor);
            viewPager.setCurrentItem(adapter.getItemPosition(dataModel));
            searchItem.collapseActionView();
            searchView.onActionViewCollapsed();
        } else {
            Snackbar.make(searchView, "no hay conincidencias", Snackbar.LENGTH_SHORT).show();
        }
        cursor.close();
        dbAdapter.close();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void sendEveryMinute() {
        Hashtable<String, String> params = prepareDataToPost();
        new PostRequest(getApplicationContext(), params, null, Urls.urlSubida(getApplicationContext()), new CallbackAPI() {
            @Override
            public void onSuccess(String result, int statusCode) {
                Log.e(TAG, "onSuccess: " + result);
                String humanDate = StringUtils.getHumanDate(Calendar.getInstance().getTime());
                UserPreferences.putLong(getApplicationContext(), KEY_SEND, Calendar.getInstance().getTimeInMillis());
                UserPreferences.putBoolean(getApplicationContext(), KEY_WAS_UPLOAD, true);
            }

            @Override
            public void onFailed(String reason, int statusCode) {
                Log.e(TAG, "onFailed: " + reason);
            }
        }).execute();
    }

    public Hashtable<String, String> prepareDataToPost() {
        Hashtable<String, String> params = new Hashtable<>();

        DBAdapter dbAdapter = new DBAdapter(this);
        ArrayList<DataModel> allData = dbAdapter.getAllDataToSend();

        params.put("gestion", UserPreferences.getString(getApplicationContext(), KEY_ENDPOINT_GESTION));
        params.put("mes", UserPreferences.getString(getApplicationContext(), KEY_ENDPOINT_MONTH));
        params.put("remesa", UserPreferences.getString(getApplicationContext(), KEY_ENDPOINT_REMESA));
        params.put("RutaCod", String.valueOf(user.getRutaCod()));

        for (DataModel dataModel : allData) {
            String json = DataModel.getJsonToSend(dataModel,
                    dbAdapter.getDataObsByCli(dataModel.getId()),
                    dbAdapter.getPrintObsData(dataModel.getId()),
                    dbAdapter.getDetalleFactura(dataModel.getId()));
            Log.e(TAG, "prepareDataToPost json: " + json);
            params.put("" + (dataModel.getTlxCli()), json);
        }

        try {
            ArrayList<MedEntreLineas> entreLineasList = dbAdapter.getMedEntreLineas();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < entreLineasList.size(); i++) {
                MedEntreLineas entreLineas = entreLineasList.get(i);
                jsonArray.put(i, entreLineas.toJson());
            }
            Log.e(TAG, "prepareDataToPost: mel: " + jsonArray.toString());
            params.put("med_entre_lineas", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dbAdapter.close();
        return params;
    }
}
