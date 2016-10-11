package com.solunes.endeapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Set;

public class ReadingActivity extends AppCompatActivity implements DataFragment.OnFragmentListener, SearchView.OnQueryTextListener {

    private static final String TAG = "ReadingActivity";

    public static final String KEY_LAST_PAGER_PSOTION = "last_pager_position";

    private ZebraPrinter printer;
    private Connection connection;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter adapter;

    private SearchView searchView;
    private MenuItem searchItem;

    private RadioGroup radioGroup;
    private RadioButton radioCli;

    private int currentState = -1;

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
        ArrayList<DataModel> datas = new ArrayList<>();
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
            tabText.setText(String.valueOf(datas.get(i).get_id()));
            if (datas.get(i).getTlxNvaLec() > 0) {
                tabText.setTextColor(getResources().getColor(android.R.color.white));
            }
            tabAt.setCustomView(inflate);
        }
        dbAdapter.close();

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                doConnection();
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();

        int pagerPosition = UserPreferences.getInt(getApplicationContext(), KEY_LAST_PAGER_PSOTION);
        viewPager.setCurrentItem(pagerPosition);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioCli = (RadioButton) findViewById(R.id.cli_radio);
    }

    @Override
    public void onTabListener() {
        Log.e(TAG, "onTabListener: " + viewPager.getCurrentItem());
        View customView = tabLayout.getTabAt(viewPager.getCurrentItem()).getCustomView();
        TextView textTab = (TextView) customView.findViewById(R.id.textview_custom_tab);
        textTab.setTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public void onPrinting(String srcToPrint) {
        Log.e(TAG, "onPrinting: " + srcToPrint);
        sendLabelToPrint(srcToPrint);
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
        UserPreferences.putInt(this, KEY_LAST_PAGER_PSOTION, viewPager.getCurrentItem());
    }

    private void doConnection() {
        printer = connect();
    }

    private String getMacAddress() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    Log.e(TAG, "onBluetooth: " + device.getName() + " - " + device.getAddress());
                    if (device.getName().equalsIgnoreCase("ende1")) {
                        return device.getAddress();
                    }
                }
            }
        }
        return null;
    }

    public ZebraPrinter connect() {
        Log.e(TAG, "Connecting... ");
        connection = null;
        String macAddress = getMacAddress();
        connection = new BluetoothConnection(macAddress);
//        SettingsHelper.saveBluetoothAddress(this, getMacAddressFieldText());

        try {
            connection.open();
            Log.e(TAG, "Connected");
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
        DataFragment fragment = (DataFragment) adapter.getItem(viewPager.getCurrentItem());
        if (connection.isConnected()) {
            try {
                ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);
                PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {
                    connection.write(label.getBytes());
                    Log.e(TAG, "sending data");
                    fragment.printResponse("Imprimiendo");
                } else if (printerStatus.isHeadOpen) {
                    Log.e(TAG, "printer head open");
                    fragment.printResponse("Cabezal abierto");
                } else if (printerStatus.isPaused) {
                    Log.e(TAG, "printer is paused");
                    fragment.printResponse("Impresora pausada");
                } else if (printerStatus.isPaperOut) {
                    Log.e(TAG, "printer media out");
                    fragment.printResponse("Impresora sin papel");
                }
                sleeper(1500);
                if (connection instanceof BluetoothConnection) {
                    String friendlyName = ((BluetoothConnection) connection).getFriendlyName();
                    Log.e(TAG, friendlyName);
                    sleeper(500);
                }
            } catch (ConnectionException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            fragment.printResponse("Impresora apagada");
        }
    }

    private byte[] getConfigLabel() {
        byte[] configLabel;
        String cpclConfigLabel = "! 0 200 200 170 1\r\n" +
                "ENCODING UTF-8\r\n" +
                "CENTER\r\n" +
                "T 7 0 10 70 13\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 132 Fecha emision: \r\n" +
                "CENTER\r\n" +
                "T 7 0 50 132 LA PAZ 22 DE SEPTIEMBRE DE 2016 \r\n" +

                "LEFT\r\n" +
                "T 7 0 45 152 Nombre: Alex Jhonny Cruz Mamani \r\n" +

                "SETLP 7 0 14\r\n" +
                "LEFT\r\n" +
                "T 7 0 45 172 NIT/CI:\r\n" +
                "T 7 0 280 172 Nro CLIENTE:\r\n" +
                "T 7 0 575 172 Nro MEDIDOR:\r\n" +
                "RIGHT 100\r\n" +
                "T 7 0 150 172 3216549870\r\n" +
                "T 7 0 445 172 654321-5-5\r\n" +
                "T 7 0 720 172 654987\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 192 DIRECCION: Av. Invavi #84 Viacha\r\n" +

                "T 7 0 45 212 CIUDAD/LOCALIDAD: LA PAZ\r\n" +
                "T 7 0 450 212 ACTIVIDAD: VIVIENDA\r\n" +

                "T 7 0 45 232 REMESA/RUTA: 16/13020\r\n" +
                "T 7 0 450 232 CARTA FACTURA:  \r\n" +

                "T 7 0 45 252 MES DE LA FACTURA: \r\n" +
                "T 7 0 245 252 AGOSTO-2016\r\n" +
                "T 7 0 430 252 CATEGORIA: D2-PD-BT\r\n" +

                "T 7 0 45 272 FECHA DE LECTURA:\r\n" +
                "T 7 0 250 272 ANTERIOR:\r\n" +
                "T 7 0 530 272 ACTUAL:\r\n" +
                "RIGHT 100\r\n" +
                "T 7 0 375 272 25-JUL-16\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 272 26-AGO-16\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 292 LECTURA MEDIDOR:\r\n" +
                "T 7 0 250 292 ANTERIOR:\r\n" +
                "T 7 0 530 292 ACTUAL:\r\n" +
                "RIGHT 100\r\n" +
                "T 7 0 375 292 2516\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 292 2616\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 312 TIPO LECTURA:  LECTURA NORMAL\r\n" +

                "T 7 0 45 332 ENERGIA CONSUMIDO EN (30) DIAS\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 332 232 kWh\r\n" +

                "LEFT\r\n" +
                "T 7 0 45 352 TOTAL ENERGIA A FACTURAR:\r\n" +
                "RIGHT 782\r\n" +
                "T 7 0 720 352 323 kWh\r\n" +

//                    "FORM\r\n"+
                "PRINT\r\n";

        configLabel = cpclConfigLabel.getBytes();

        return configLabel;
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
}
