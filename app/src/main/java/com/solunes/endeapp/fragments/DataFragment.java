package com.solunes.endeapp.fragments;

import android.animation.LayoutTransition;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.DataObs;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.utils.GenLecturas;
import com.solunes.endeapp.utils.StringUtils;

import java.util.Calendar;

/**
 * Created by jhonlimaster on 01-12-15.
 */
public class DataFragment extends Fragment implements SearchView.OnQueryTextListener {
    private static final String TAG = "DataFragment";
    private static final String KEY_POSITION = "position";
    private OnFragmentListener onFragmentListener;

    private EditText inputReading;
    private EditText inputObsCode;
    private Button buttonConfirm;
    private Button buttonObs;
    private TextView labelEnergiaFacturada;
    private TextView labelImporteConsumo;
    private TextView labelTotalConsumo;
    private TextView labelTotalSuministro;
    private TextView labelTotalFacturar;
    private TextView labelObs;
    private TextView estadoMedidor;
    private SearchView searchView;
    private MenuItem searchItem;

    private DataModel dataModel;

    public DataFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentListener = (OnFragmentListener) context;
    }

    public static DataFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        DataFragment dataFragment = new DataFragment();
        dataFragment.setArguments(bundle);
        return dataFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Cliente o Medidor");
        searchView.setLayoutTransition(new LayoutTransition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        Bundle arguments = getArguments();
        DBAdapter dbAdapter = new DBAdapter(getContext());
        dataModel = dbAdapter.getData(arguments.getInt(KEY_POSITION));
//        Log.e(TAG, "onCreateView: tipo de lectura " + dataModel.getTlxTipLec() + " ---- ultimo tipo " + dataModel.getTlxUltTipL());
        dbAdapter.close();
        setupUI(view, dataModel);
        actionButtons();
        validSaved();
        return view;
    }

    public void setupUI(View view, final DataModel data) {
        TextView nameData = (TextView) view.findViewById(R.id.data_name);
        nameData.setText(data.getTlxNom());
        TextView dataClient = (TextView) view.findViewById(R.id.data_client);
        dataClient.setText("N° Cliente: " + data.getTlxCli());
        TextView adressCliente = (TextView) view.findViewById(R.id.adress_client);
        adressCliente.setText(data.getTlxDir());
        TextView categoryCliente = (TextView) view.findViewById(R.id.category_client);
        categoryCliente.setText("Categoria: " + data.getTlxSgl());
        TextView medidorCliente = (TextView) view.findViewById(R.id.medidor_client);
        medidorCliente.setText("N°: " + data.getTlxNroMed());
        TextView digitosCliente = (TextView) view.findViewById(R.id.digitos_client);
        digitosCliente.setText("Digitos: " + data.getTlxNroDig());
        TextView ordenCliente = (TextView) view.findViewById(R.id.orden_client);
        ordenCliente.setText("Orden: " + data.getTlxOrdTpl());

        labelEnergiaFacturada = (TextView) view.findViewById(R.id.label_energia_facturada);
        labelImporteConsumo = (TextView) view.findViewById(R.id.label_importe_consumo);
        labelTotalConsumo = (TextView) view.findViewById(R.id.label_total_consumo);
        labelTotalSuministro = (TextView) view.findViewById(R.id.label_total_suministro);
        labelTotalFacturar = (TextView) view.findViewById(R.id.label_total_facturar);
        labelObs = (TextView) view.findViewById(R.id.label_obs);
        inputReading = (EditText) view.findViewById(R.id.input_reading);
        inputReading.setSelected(false);
        buttonConfirm = (Button) view.findViewById(R.id.button_confirm);
        buttonObs = (Button) view.findViewById(R.id.button_obs);
        inputObsCode = (EditText) view.findViewById(R.id.obs_code);
        estadoMedidor = (TextView) view.findViewById(R.id.estado_client);
        estadoMedidor.setText(DataFragment.estados_lectura.values()[data.getEstadoLectura()].name());
        estadoMedidor.setTextColor(getResources().getColor(R.color.colorPendiente));
    }

    private void actionButtons() {
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int obsCod = 104;
                if (!inputObsCode.getText().toString().isEmpty()) {
                    obsCod = Integer.parseInt(inputObsCode.getText().toString());
                }
                DBAdapter dbAdapter = new DBAdapter(getContext());
                Obs obs = Obs.fromCursor(dbAdapter.getObs(obsCod));

                String input = inputReading.getText().toString();

                int lecturaKwh;
                if (obs.getObsLec() == 3) {
                    lecturaKwh = dataModel.getTlxConPro();
                    dataModel.setTlxNvaLec(dataModel.getTlxUltInd());
                    dataModel.setTlxKwhDev(lecturaKwh);
                } else if (input.isEmpty()) {
                    Snackbar.make(view, "Ingresar un indice", Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    int nuevaLectura = Integer.parseInt(input);
                    dataModel.setTlxNvaLec(nuevaLectura);
                    lecturaKwh = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), nuevaLectura, dataModel.getTlxNroDig());
                }

                // lecturaKwh ajustada
                if (dataModel.getTlxUltTipL() == 3) {
                    lecturaKwh = lecturaKwh - dataModel.getTlxKwhDev();
                    if (lecturaKwh > 0) {
                        dataModel.setTlxKwhDev(0);
                    } else {
                        dataModel.setTlxKwhDev(lecturaKwh);
                        lecturaKwh = 0;
                    }
                }
                if (dataModel.getTlxKwhDev() > 0) {
                    lecturaKwh = lecturaKwh - dataModel.getTlxKwhDev();
                    if (lecturaKwh > 0) {
                        dataModel.setTlxKwhDev(0);
                    } else {
                        dataModel.setTlxKwhDev(lecturaKwh);
                        lecturaKwh = 0;
                    }
                }
                lecturaKwh = lecturaKwh + dataModel.getTlxKwhAdi();
                Log.e(TAG, "lectura Kwh: " + lecturaKwh);
                dataModel.setTlxKwhAdi(0);
                calculo(lecturaKwh);
                if (obs.getObsFac() == 1) {
                    dataModel.setEstadoLectura(estados_lectura.Impreso.ordinal());
                    estadoMedidor.setText(estados_lectura.Impreso.name());
                    estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
                    buttonConfirm.setEnabled(false);
                    buttonObs.setEnabled(false);
                    inputObsCode.setEnabled(false);
                    inputReading.setEnabled(false);
                } else {
                    dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
                    estadoMedidor.setText(estados_lectura.Postergado.name());
                    estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
                }
                saveLectura(obs);
                printFactura(view, obs);
            }
        });
        buttonObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Selecionar una observacion");
                final DBAdapter dbAdapter = new DBAdapter(getContext());
                Cursor cursor = dbAdapter.getObs();
                final String[] stringObs = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    stringObs[i] = Obs.fromCursor(cursor).getObsDes();
                }
                cursor.close();
                alertDialog.setSingleChoiceItems(stringObs, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        dialogInterface.dismiss();
                        Obs obs = Obs.fromCursor(dbAdapter.getObs(stringObs[position]));
                        labelObs.setText(obs.getObsDes());
                        inputObsCode.setText(String.valueOf(obs.getObsCod()));
                    }
                });
                alertDialog.show();
            }
        });
        inputObsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    labelObs.setText("Ninguno");
                    return;
                }
                DBAdapter dbAdapter = new DBAdapter(getContext());
                int idOBs = Integer.parseInt(editable.toString());
                Cursor cursor = dbAdapter.getObs(idOBs);
                if (cursor.getCount() > 0) {
                    Obs obs = Obs.fromCursor(cursor);
                    labelObs.setText(obs.getObsDes());
                } else {
                    labelObs.setText("Codigo incorrecto");
                }
            }
        });
    }

    private void printFactura(View view, Obs obs) {
        if (obs.getObsFac() == 1) {
            Snackbar.make(view, "Imprimiendo...", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveLectura(Obs obs) {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        ContentValues cv = new ContentValues();
        Calendar calendar = Calendar.getInstance();

        cv.put(DataModel.Columns.TlxHorLec.name(), StringUtils.getHumanHour(calendar.getTime()));
        cv.put(DataModel.Columns.TlxFecEmi.name(), StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT, calendar.getTime()));
        cv.put(DataModel.Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());
        cv.put(DataModel.Columns.TlxImpEn.name(), dataModel.getTlxImpEn());
        cv.put(DataModel.Columns.TlxConsumo.name(), dataModel.getTlxConsumo());
        cv.put(DataModel.Columns.TlxConsFacturado.name(), dataModel.getTlxConsFacturado());
        cv.put(DataModel.Columns.TlxImpTot.name(), dataModel.getTlxImpTot());
        cv.put(DataModel.Columns.estado_lectura.name(), dataModel.getEstadoLectura());

        dbAdapter.updateData(dataModel.getTlxCli(), cv);

        cv = new ContentValues();
        cv.put(DataObs.Columns.ObsRem.name(), dataModel.getTlxRem());
        cv.put(DataObs.Columns.ObsAre.name(), dataModel.getTlxAre());
        cv.put(DataObs.Columns.ObsCli.name(), dataModel.getTlxCli());
        cv.put(DataObs.Columns.ObsCod.name(), obs.getObsCod());
        dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        Log.e(TAG, "saveLectura: save obs " + obs.getObsCod());

        int conPro = dataModel.getTlxConPro();
        if (dataModel.getTlxNvaLec() > (conPro + conPro * 0.2)) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.ObsRem.name(), dataModel.getTlxRem());
            cv.put(DataObs.Columns.ObsAre.name(), dataModel.getTlxAre());
            cv.put(DataObs.Columns.ObsCli.name(), dataModel.getTlxCli());
            cv.put(DataObs.Columns.ObsCod.name(), 80);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
            Log.e(TAG, "saveLectura: consumo elevado");
        } else if (dataModel.getTlxNvaLec() < (conPro * 0.8)) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.ObsRem.name(), dataModel.getTlxRem());
            cv.put(DataObs.Columns.ObsAre.name(), dataModel.getTlxAre());
            cv.put(DataObs.Columns.ObsCli.name(), dataModel.getTlxCli());
            cv.put(DataObs.Columns.ObsCod.name(), 81);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
            Log.e(TAG, "saveLectura: consumo bajo");
        }

        dbAdapter.close();
        onFragmentListener.onTabListener();
    }

    private void calculo(int lectura) {
        labelEnergiaFacturada.setText("Energia facturada: " + lectura);

        double importeConsumo = GenLecturas.importeConsumo(getContext(), lectura);
        labelImporteConsumo.setText("importe por consumo: " + importeConsumo);
        dataModel.setTlxImpEn(importeConsumo);

        double tarifaDignidad = GenLecturas.tarifaDignidad(lectura, importeConsumo);
        dataModel.setTlxDesTdi(tarifaDignidad);

        double ley1886 = GenLecturas.ley1886(getContext(), lectura);
        dataModel.setTlxLey1886(ley1886);

        double totalConsumo = GenLecturas.totalConsumo(importeConsumo, tarifaDignidad, ley1886);
        labelTotalConsumo.setText("Importe total por consumo: " + totalConsumo);
        dataModel.setTlxConsumo(totalConsumo);

        double totalSuministro = GenLecturas.totalSuministro(totalConsumo);
        labelTotalSuministro.setText("Importe total por el suminstro: " + totalSuministro);
        dataModel.setTlxConsFacturado(totalSuministro);
        dataModel.setTlxImpFac(totalSuministro);

        double totalSuministroTap = GenLecturas.totalSuministroTap(lectura);
        dataModel.setTlxImpTap(totalSuministro + totalSuministroTap);

        double totalSuministroAseo = GenLecturas.totalSuministroAseo(lectura);
        dataModel.setTlxImpAse(totalSuministroAseo);

        dataModel.setTlxImpTot(GenLecturas.totalFacturar(totalSuministro, totalSuministroTap, totalSuministroAseo));
        labelTotalFacturar.setText("Importe total a facturar: " + dataModel.getTlxImpTot());
    }

    private void validSaved() {
        if (dataModel.getTlxFecEmi() != null) {
            inputReading.setText(String.valueOf(dataModel.getTlxNvaLec()));
//            int lecturaNormal = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), dataModel.getTlxNvaLec(), dataModel.getTlxNroDig());
//            labelEnergiaFacturada.setText("Energia facturada: " + lecturaNormal);
//            labelImporteConsumo.setText("importe por consumo: " + dataModel.getTlxImpEn());
//            labelTotalConsumo.setText("Importe total por consumo: " + dataModel.getTlxConsumo());
//            labelTotalSuministro.setText("Importe total por el suminstro: " + dataModel.getTlxConsFacturado());
//            labelTotalFacturar.setText("Importe total a facturar: " + dataModel.getTlxImpTot());
            if (dataModel.getEstadoLectura() == 1) {
                estadoMedidor.setText(estados_lectura.Impreso.name());
                estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
                buttonConfirm.setEnabled(false);
                inputReading.setEnabled(false);
                buttonObs.setEnabled(false);
                inputObsCode.setEnabled(false);
            } else if (dataModel.getEstadoLectura() == 2) {
                estadoMedidor.setText(estados_lectura.Postergado.name());
                estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
            } else {
                estadoMedidor.setText(estados_lectura.Pendiente.name());
                estadoMedidor.setTextColor(getResources().getColor(R.color.colorPendiente));
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String filter = !TextUtils.isEmpty(query) ? query : null;
        DBAdapter dbAdapter = new DBAdapter(getContext());
        Cursor cursor = dbAdapter.searchClienteMedidor(filter);
        if (cursor.getCount() > 0) {
            DataModel dataModel = DataModel.fromCursor(cursor);
            onFragmentListener.onSetItem(dataModel.get_id() - 1);
        } else {
            Snackbar.make(buttonConfirm, "no hay conincidencias", Snackbar.LENGTH_SHORT).show();
        }
        cursor.close();
        dbAdapter.close();
        Log.e(TAG, "onQueryTextSubmit: " + filter);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public interface OnFragmentListener {
        void onTabListener();

        void onSetItem(int pos);
    }

    private enum estados_lectura {
        Pendiente, Impreso, Postergado
    }
}
