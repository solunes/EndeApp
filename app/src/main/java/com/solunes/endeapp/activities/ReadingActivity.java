package com.solunes.endeapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import java.util.ArrayList;
import java.util.Set;

public class ReadingActivity extends AppCompatActivity implements DataFragment.OnFragmentListener {

    private static final String TAG = "ReadingActivity";

    public static final String KEY_LAST_PAGER_PSOTION = "last_pager_position";

    private ZebraPrinter printer;
    private Connection connection;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter adapter;

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
        adapter = new PagerAdapter(getSupportFragmentManager(), dbAdapter.getSizeData());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        ArrayList<DataModel> allData = dbAdapter.getAllData();
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tabAt = tabLayout.getTabAt(i);
            View inflate = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabText = (TextView) inflate.findViewById(R.id.textview_custom_tab);
            tabText.setText(String.valueOf(i + 1));
            if (allData.get(i).getTlxNvaLec() > 0) {
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
        Log.e(TAG, "onCreate: " + pagerPosition);
        viewPager.setCurrentItem(pagerPosition);
    }

    @Override
    public void onTabListener() {
        Log.e(TAG, "onTabListener: " + viewPager.getCurrentItem());
        View customView = tabLayout.getTabAt(viewPager.getCurrentItem()).getCustomView();
        TextView textTab = (TextView) customView.findViewById(R.id.textview_custom_tab);
        textTab.setTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public void onSetItem(int pos) {
        viewPager.setCurrentItem(pos);
    }

    @Override
    public void onPrinting(String srcToPrint) {
        Log.e(TAG, "onPrinting: connection " + connection.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

    private void sendTestLabel() {
        try {
            ZebraPrinterLinkOs linkOsPrinter = ZebraPrinterFactory.createLinkOsPrinter(printer);

            PrinterStatus printerStatus = (linkOsPrinter != null) ? linkOsPrinter.getCurrentStatus() : printer.getCurrentStatus();

            if (printerStatus.isReadyToPrint) {
                byte[] configLabel = getConfigLabel();
                Log.e("TAG", "sendTestLabel: " + configLabel);
                connection.write(configLabel);
                Log.e(TAG, "sending data");
            } else if (printerStatus.isHeadOpen) {
                Log.e(TAG, "printer head open");
            } else if (printerStatus.isPaused) {
                Log.e(TAG, "printer is paused");
            } else if (printerStatus.isPaperOut) {
                Log.e(TAG, "printer media out");
            }
            sleeper(1500);
            if (connection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) connection).getFriendlyName();
                Log.e(TAG, friendlyName);
                sleeper(500);
            }
        } catch (ConnectionException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            disconnect();
        }
    }

    private byte[] getConfigLabel() {
        byte[] configLabel = null;
        try {
            PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();
            Log.e(TAG, "printerLanguage: " + printerLanguage);
            SGD.SET("device.languages", "ZPL", connection);
            Log.e(TAG, "printerLanguages: " + printerLanguage);
            String cpclConfigLabel = "! 0 200 200 170 1\r\n" +
                    "ENCODING UTF-8\r\n" +
                    "! U1 SETLP 7 0 26\r\n" +
//                    "! U1 SETLF 12\r\n" +
//                    "T 7 0 10 10 A°°°°°°A\r\n" +
//                    "RIGHT 782\r\n" +
//                    "T 7 0 10 30 n° categóría\r\n" +

                    "CENTER\r\n" +
                    "T 7 0 10 70 13\r\n" +

//                    "! U1 SETLF 18\r\n"+
//                    "LEFT\r\n"+
//                    "T 7 0 45 132 Fecha emision: \r\n" +
//                    "CENTER\r\n"+
//                    "T 7 0 50 132 LA PAZ 22 DE SEPTIEMBRE DE 2016 \r\n" +
//
//                    "! U1 SETLF 16\r\n"+
//                    "LEFT\r\n"+
//                    "T 7 0 45 152 Nombre: Alex Jhonny Cruz Mamani \r\n" +

//                    "SETLP 7 0 14\r\n"+
//                    "LEFT\r\n"+
//                    "T 7 0 45 172 NIT/CI:\r\n" +
//                    "T 7 0 280 172 Nro CLIENTE:\r\n" +
//                    "T 7 0 575 172 Nro MEDIDOR:\r\n" +
//                    "RIGHT 100\r\n"+
//                    "T 7 0 150 172 3216549870\r\n" +
//                    "T 7 0 445 172 654321-5-5\r\n" +
//                    "T 7 0 720 172 654987\r\n" +
//
//                    "LEFT\r\n"+
//                    "T 7 0 45 192 DIRECCION: Av. Invavi #84 Viacha\r\n" +
//
//                    "T 7 0 45 212 CIUDAD/LOCALIDAD: LA PAZ\r\n" +
//                    "T 7 0 450 212 ACTIVIDAD: VIVIENDA\r\n" +
//
//                    "T 7 0 45 232 REMESA/RUTA: 16/13020\r\n" +
//                    "T 7 0 450 232 CARTA FACTURA:  \r\n" +
//
//                    "T 7 0 45 252 MES DE LA FACTURA: \r\n" +
//                    "T 7 0 245 252 AGOSTO-2016\r\n" +
//                    "T 7 0 430 252 CATEGORIA: D2-PD-BT\r\n" +
//
//                    "T 7 0 45 272 FECHA DE LECTURA:\r\n" +
//                    "T 7 0 250 272 ANTERIOR:\r\n" +
//                    "T 7 0 530 272 ACTUAL:\r\n" +
//                    "RIGHT 100\r\n"+
//                    "T 7 0 375 272 25-JUL-16\r\n" +
//                    "RIGHT 782\r\n"+
//                    "T 7 0 720 272 26-AGO-16\r\n" +
//
//                    "LEFT\r\n"+
//                    "T 7 0 45 292 LECTURA MEDIDOR:\r\n" +
//                    "T 7 0 250 292 ANTERIOR:\r\n" +
//                    "T 7 0 530 292 ACTUAL:\r\n" +
//                    "RIGHT 100\r\n"+
//                    "T 7 0 375 292 2516\r\n" +
//                    "RIGHT 782\r\n"+
//                    "T 7 0 720 292 2616\r\n" +
//
//                    "LEFT\r\n"+
//                    "T 7 0 45 312 TIPO LECTURA:  LECTURA NORMAL\r\n" +
//
//                    "T 7 0 45 332 ENERGIA CONSUMIDO EN (30) DIAS\r\n" +
//                    "RIGHT 782\r\n"+
//                    "T 7 0 720 332 232 kWh\r\n" +
//
//                    "LEFT\r\n"+
//                    "T 7 0 45 352 TOTAL ENERGIA A FACTURAR:\r\n" +
//                    "RIGHT 782\r\n"+
//                    "T 7 0 720 352 323 kWh\r\n" +

//                    "T 7 0 0 10 0\r\n" +
//                    "T 7 0 100 10 100\r\n" +
//                    "T 7 0 200 10 200\r\n" +
//                    "T 7 0 300 10 300\r\n" +
//                    "T 7 0 400 10 400\r\n" +
//                    "T 7 0 500 10 500\r\n" +
//                    "T 7 0 600 10 600\r\n" +
//                    "T 7 0 700 10 700\r\n" +
//                    "T 7 0 0 30 0\r\n" +
//                    "T 7 0 100 30 100\r\n" +
//                    "T 7 0 200 30 200\r\n" +
//                    "T 7 0 300 30 300\r\n" +
//                    "T 7 0 400 30 400\r\n" +
//                    "T 7 0 500 30 500\r\n" +
//                    "T 7 0 600 30 600\r\n" +
//                    "T 7 0 700 30 700\r\n" +
//                    "FORM\r\n"+
                    "PRINT\r\n";

            configLabel = cpclConfigLabel.getBytes();

        } catch (ConnectionException e) {
            Log.e(TAG, "getConfigLabel exception: ", e);
        }
        return configLabel;
    }

    private void sleeper(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
