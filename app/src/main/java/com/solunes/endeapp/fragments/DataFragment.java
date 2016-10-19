package com.solunes.endeapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.solunes.endeapp.models.PrintObs;
import com.solunes.endeapp.models.PrintObsData;
import com.solunes.endeapp.utils.GenLecturas;
import com.solunes.endeapp.utils.PrintGenerator;
import com.solunes.endeapp.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jhonlimaster on 01-12-15.
 */
public class DataFragment extends Fragment {
    private static final String TAG = "DataFragment";
    public static final String KEY_POSITION = "position";
    private OnFragmentListener onFragmentListener;

    private View rootView;
    private EditText inputReading;
    private EditText inputPotenciaReading;
    private EditText inputObsCode;
    private Button buttonConfirm;
    private Button buttonObs;
    private TextView labelObs;
    private TextView estadoMedidor;
    private EditText inputRemenber;

    private View layoutPrecinto;
    private View layoutGranDemanda;

    private ArrayList<String> printTitles;
    private ArrayList<Double> printValues;
    private ArrayList<PrintObs> listPrintObs;

    private double importeTotalFactura;
    private double importeMesCancelar;

    private DataModel dataModel;

    private int positionPrintObs;

    public DataFragment() {
        printTitles = new ArrayList<>();
        printValues = new ArrayList<>();
        listPrintObs = new ArrayList<>();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        Bundle arguments = getArguments();
        DBAdapter dbAdapter = new DBAdapter(getContext());
        Cursor cursor = dbAdapter.getPrintObs();
        while (cursor.moveToNext()) {
            listPrintObs.add(PrintObs.fromCursor(cursor));
        }
        dataModel = dbAdapter.getData(arguments.getInt(KEY_POSITION));
        Log.e(TAG, "onCreateView: " + dataModel.getTlxUltInd());
        dbAdapter.close();
        setupUI(view, dataModel);
        rootView = view.findViewById(R.id.data_layout);
        actionButtons(view);
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

        labelObs = (TextView) view.findViewById(R.id.label_obs);
        inputReading = (EditText) view.findViewById(R.id.input_reading);
        inputReading.setSelected(false);
        buttonConfirm = (Button) view.findViewById(R.id.button_confirm);
        buttonObs = (Button) view.findViewById(R.id.button_obs);
        inputObsCode = (EditText) view.findViewById(R.id.obs_code);
        estadoMedidor = (TextView) view.findViewById(R.id.estado_client);
        estadoMedidor.setText(DataFragment.estados_lectura.values()[data.getEstadoLectura()].name());
        estadoMedidor.setTextColor(getResources().getColor(R.color.colorPendiente));
        inputRemenber = (EditText) view.findViewById(R.id.input_remenber);
        if (data.getTlxRecordatorio() != null) {
            inputRemenber.setText(data.getTlxRecordatorio());
        }
        inputRemenber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                dataModel.setTlxRecordatorio(inputRemenber.getText().toString());
                DBAdapter dbAdapter = new DBAdapter(getContext());
                ContentValues values = new ContentValues();
                values.put(DataModel.Columns.TlxRecordatorio.name(), inputRemenber.getText().toString());
                dbAdapter.updateData(data.getTlxCli(), values);
                dbAdapter.close();
            }
        });

        inputPotenciaReading = (EditText) view.findViewById(R.id.input_potencia_reading);
        if (dataModel.getTlxTipDem() == 2) {
            inputPotenciaReading.setVisibility(View.VISIBLE);
        }
        layoutPrecinto = view.findViewById(R.id.layout_precinto_nuevo);
        if (dataModel.getTlxPotTag() == 1) {
            layoutPrecinto.setVisibility(View.VISIBLE);
            TextView preAnt1 = (TextView) view.findViewById(R.id.label_pre_ant1);
            preAnt1.setText(dataModel.getTlxPreAnt1());
            TextView preAnt2 = (TextView) view.findViewById(R.id.label_pre_ant2);
            preAnt2.setText(dataModel.getTlxPreAnt2());
            TextView preAnt3 = (TextView) view.findViewById(R.id.label_pre_ant3);
            preAnt3.setText(dataModel.getTlxPreAnt3());
            TextView preAnt4 = (TextView) view.findViewById(R.id.label_pre_ant4);
            preAnt4.setText(dataModel.getTlxPreAnt4());
        }
        layoutGranDemanda = view.findViewById(R.id.layout_gran_demanda);
        if (dataModel.getTlxTipDem() == 3) {
            layoutGranDemanda.setVisibility(View.VISIBLE);
        }
    }

    private void actionButtons(final View viewRoot) {
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (dataModel.getTlxPotTag() == 1) {
                    String inputPreNue1 = ((EditText) viewRoot.findViewById(R.id.input_pre_nue1)).getText().toString();
                    String inputPreNue2 = ((EditText) viewRoot.findViewById(R.id.input_pre_nue2)).getText().toString();
                    String inputPreNue3 = ((EditText) viewRoot.findViewById(R.id.input_pre_nue3)).getText().toString();
                    String inputPreNue4 = ((EditText) viewRoot.findViewById(R.id.input_pre_nue4)).getText().toString();
                    if (!inputPreNue1.isEmpty() && !inputPreNue2.isEmpty() && !inputPreNue3.isEmpty() && !inputPreNue4.isEmpty()) {
                        dataModel.setTlxPreNue1(inputPreNue1);
                        dataModel.setTlxPreNue2(inputPreNue2);
                        dataModel.setTlxPreNue3(inputPreNue3);
                        dataModel.setTlxPreNue4(inputPreNue4);
                    } else {
                        Snackbar.make(view, "Todos los campos de precintos son requeridos", Snackbar.LENGTH_SHORT).show();
                    }
                }

                if (dataModel.getTlxTipDem() == 3) {
                    methodGranDemanda(viewRoot);
                    return;
                }

                if (dataModel.getEstadoLectura() == estados_lectura.Impreso.ordinal()) {
                    rePrint();
                } else {
                    methodPequeñaMedianaDemanda(view);
                }
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
                        inputObsCode.setText(String.valueOf(obs.getId()));
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

    private void methodGranDemanda(View view) {
        EditText kwInst = (EditText) view.findViewById(R.id.input_gd_kwinst);
        boolean valid = true;
        if (kwInst.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText reactiva = (EditText) view.findViewById(R.id.input_gd_reactiva);
        if (reactiva.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText kwalto = (EditText) view.findViewById(R.id.input_gd_kwhalto);
        if (kwalto.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText kwmedio = (EditText) view.findViewById(R.id.input_gd_kwhmedio);
        if (kwmedio.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText kwBajo = (EditText) view.findViewById(R.id.input_gd_kwhbajo);
        if (kwBajo.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText demBajo = (EditText) view.findViewById(R.id.input_gd_dembajo);
        if (demBajo.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText demMedio = (EditText) view.findViewById(R.id.input_gd_demmedio);
        if (demMedio.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText demAlto = (EditText) view.findViewById(R.id.input_gd_demalto);
        if (demAlto.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText fechaAlto = (EditText) view.findViewById(R.id.input_gd_fechaalto);
        if (fechaAlto.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText fechaMedio = (EditText) view.findViewById(R.id.input_gd_fechamedio);
        if (fechaMedio.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText fechaBajo = (EditText) view.findViewById(R.id.input_gd_fechabajo);
        if (fechaBajo.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText horaAlto = (EditText) view.findViewById(R.id.input_gd_horaalto);
        if (horaAlto.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText horaMedio = (EditText) view.findViewById(R.id.input_gd_horamedio);
        if (horaMedio.getText().toString().isEmpty()) {
            valid = false;
        }
        EditText horaBajo = (EditText) view.findViewById(R.id.input_gd_horabajo);
        if (horaBajo.getText().toString().isEmpty()) {
            valid = false;
        }

        if (valid) {
            dataModel.setTlxKwInst(Integer.parseInt(kwInst.getText().toString()));
            dataModel.setTlxReactiva(Integer.parseInt(reactiva.getText().toString()));
            dataModel.setTlxKwhAlto(Integer.parseInt(kwalto.getText().toString()));
            dataModel.setTlxKwhMedio(Integer.parseInt(kwmedio.getText().toString()));
            dataModel.setTlxKwhBajo(Integer.parseInt(kwBajo.getText().toString()));
            dataModel.setTlxDemBajo(Integer.parseInt(demBajo.getText().toString()));
            dataModel.setTlxDemMedio(Integer.parseInt(demMedio.getText().toString()));
            dataModel.setTlxDemAlto(Integer.parseInt(demAlto.getText().toString()));
            dataModel.setTlxFechaAlto(Integer.parseInt(fechaAlto.getText().toString()));
            dataModel.setTlxFechaMedio(Integer.parseInt(fechaMedio.getText().toString()));
            dataModel.setTlxFechaBajo(Integer.parseInt(fechaBajo.getText().toString()));
            dataModel.setTlxHoraAlto(Integer.parseInt(horaAlto.getText().toString()));
            dataModel.setTlxHoraMedio(Integer.parseInt(horaMedio.getText().toString()));
            dataModel.setTlxHoraBajo(Integer.parseInt(horaBajo.getText().toString()));
        } else {
            Snackbar.make(view, "Todos los campos de gran demanda son requeridos", Snackbar.LENGTH_SHORT).show();
        }

    }

    private void methodPequeñaMedianaDemanda(final View view) {
        int obsCod = 104;
        if (!inputObsCode.getText().toString().isEmpty()) {
            obsCod = Integer.parseInt(inputObsCode.getText().toString());
        }
        DBAdapter dbAdapter = new DBAdapter(getContext());
        final Obs obs = Obs.fromCursor(dbAdapter.getObs(obsCod));

        String input = inputReading.getText().toString();
        if (input.length() >= String.valueOf(Integer.MAX_VALUE).length()) {
            Snackbar.make(view, "La lectura no puede tener mas de " + dataModel.getTlxNroDig() + " digitos", Snackbar.LENGTH_SHORT).show();
        }

        int lecturaKwh;
        boolean giro = false;
        if (obs.getObsLec() == 3) {
            lecturaKwh = dataModel.getTlxConPro();
            dataModel.setTlxNvaLec(dataModel.getTlxUltInd());
            dataModel.setTlxKwhDev(lecturaKwh);
        } else if (input.isEmpty()) {
            Snackbar.make(view, "Ingresar un indice", Snackbar.LENGTH_SHORT).show();
            return;
        } else {
            int nuevaLectura = Integer.parseInt(input);
            Log.e(TAG, "onClick: " + nuevaLectura + " - " + dataModel.getTlxTope());
            if (nuevaLectura > dataModel.getTlxTope()) {
                Snackbar.make(view, "La lectura no puede tener mas de " + dataModel.getTlxNroDig() + " digitos", Snackbar.LENGTH_SHORT).show();
                return;
            }
            nuevaLectura = correcionDeDigitos(nuevaLectura, dataModel.getTlxDecEne());
            if (nuevaLectura < dataModel.getTlxUltInd()) {
                giro = true;
            }
            dataModel.setTlxNvaLec(nuevaLectura);
            lecturaKwh = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), nuevaLectura, dataModel.getTlxNroDig());
        }

        dataModel.setTlxConsumo(lecturaKwh);
        if (dataModel.getTlxKwhDev() > 0 && obs.getObsLec() != 3) {
            lecturaKwh = lecturaKwh - dataModel.getTlxKwhDev();
            if (lecturaKwh > 0) {
                dataModel.setTlxKwhDev(0);
            } else {
                dataModel.setTlxKwhDev(Math.abs(lecturaKwh));
                lecturaKwh = 0;
            }
        }
        lecturaKwh = lecturaKwh + dataModel.getTlxKwhAdi();
        dataModel.setTlxConsFacturado(lecturaKwh);
        dataModel.setTlxKwhAdi(0);

        int conPro = dataModel.getTlxConPro();

        boolean isAlert = false;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alerta!");
        String message = "Se ha detectado:";
        if (dataModel.getTlxConsumo() > (conPro + conPro * dbAdapter.getConsumoElevado())) {
            message = message + "\n- Consumo elevado";
            isAlert = true;
        } else if (dataModel.getTlxConsumo() < (conPro * dbAdapter.getConsumoBajo())) {
            message = message + "\n- Consumo bajo";
            isAlert = true;
        }
        if (giro) {
            message = message + "\n- Giro de medidor";
            isAlert = true;
        }
        builder.setMessage(message);
        builder.setNegativeButton("Cancelar", null);
        final int finalLecturaKwh = lecturaKwh;
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                calculo(finalLecturaKwh);
                hidingViews(obs);
                saveLectura(obs);
                printFactura(view, obs);
            }
        });
        if (isAlert) {
            builder.show();
        } else {
            calculo(finalLecturaKwh);
            hidingViews(obs);
            saveLectura(obs);
            printFactura(view, obs);
        }
    }

    private void rePrint() {
        final DBAdapter dbAdapter = new DBAdapter(getContext());
        String[] itemsPrintObs = new String[listPrintObs.size()];
        for (int i = 0; i < listPrintObs.size(); i++) {
            itemsPrintObs[i] = listPrintObs.get(i).getObiDes();
        }
        AlertDialog.Builder reprintDialog = new AlertDialog.Builder(getContext());
        reprintDialog.setTitle("Selecionar una observacion");
        reprintDialog.setSingleChoiceItems(itemsPrintObs, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                positionPrintObs = position;
            }
        });
        reprintDialog.setNegativeButton("Cancelar", null);
        reprintDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrintObs printObs = listPrintObs.get(positionPrintObs);
                ContentValues contentValues = new ContentValues();
                contentValues.put(PrintObsData.Columns.OigRem.name(), dataModel.getTlxRem());
                contentValues.put(PrintObsData.Columns.OigAre.name(), dataModel.getTlxAre());
                contentValues.put(PrintObsData.Columns.OigCli.name(), dataModel.getTlxCli());
                contentValues.put(PrintObsData.Columns.OigObs.name(), printObs.getId());
                dbAdapter.saveObject(DBHelper.PRINT_OBS_DATA_TABLE, contentValues);
                sendPrint();
            }
        });
        reprintDialog.show();
    }

    private void hidingViews(Obs obs) {
        if (obs.getObsFac() == 1) {
            dataModel.setEstadoLectura(estados_lectura.Impreso.ordinal());
            estadoMedidor.setText(estados_lectura.Impreso.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
            buttonConfirm.setText(R.string.re_print);
            buttonObs.setEnabled(false);
            inputObsCode.setEnabled(false);
            inputReading.setEnabled(false);
        } else {
            dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
            estadoMedidor.setText(estados_lectura.Postergado.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
        }
    }

    private void printFactura(View view, Obs obs) {
        if (obs.getObsFac() == 1) {
            sendPrint();
        } else {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void sendPrint() {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        String[] leyenda = dbAdapter.getLeyenda();
        onFragmentListener.onPrinting(PrintGenerator.creator(dataModel, printTitles, printValues, importeTotalFactura, importeMesCancelar, leyenda));
    }

    private void saveLectura(Obs obs) {
        DBAdapter dbAdapter = new DBAdapter(getContext());

        ContentValues cv = new ContentValues();
        Calendar calendar = Calendar.getInstance();

        cv.put(DataModel.Columns.TlxHorLec.name(), StringUtils.getHumanHour(calendar.getTime()));
//        cv.put(DataModel.Columns.TlxFecEmi.name(), StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT, calendar.getTime()));
        cv.put(DataModel.Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());
        cv.put(DataModel.Columns.TlxImpEn.name(), dataModel.getTlxImpEn());
        cv.put(DataModel.Columns.TlxConsumo.name(), dataModel.getTlxConsumo());
        cv.put(DataModel.Columns.TlxConsFacturado.name(), dataModel.getTlxConsFacturado());
        cv.put(DataModel.Columns.TlxImpTot.name(), dataModel.getTlxImpTot());
        cv.put(DataModel.Columns.TlxImpFac.name(), dataModel.getTlxImpFac());
        cv.put(DataModel.Columns.TlxCarFij.name(), dataModel.getTlxCarFij());
        cv.put(DataModel.Columns.TlxImpTap.name(), dataModel.getTlxImpTap());
        cv.put(DataModel.Columns.TlxImpAse.name(), dataModel.getTlxImpAse());
        cv.put(DataModel.Columns.TlxKwhDev.name(), dataModel.getTlxKwhDev());
        cv.put(DataModel.Columns.estado_lectura.name(), dataModel.getEstadoLectura());

        dbAdapter.updateData(dataModel.getTlxCli(), cv);

        cv = new ContentValues();
        cv.put(DataObs.Columns.ObgRem.name(), dataModel.getTlxRem());
        cv.put(DataObs.Columns.ObgAre.name(), dataModel.getTlxAre());
        cv.put(DataObs.Columns.ObgCli.name(), dataModel.getTlxCli());
        cv.put(DataObs.Columns.ObgObs.name(), obs.getId());
        dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        Log.e(TAG, "saveLectura: save obs " + obs.getId());

        int conPro = dataModel.getTlxConPro();
        if (dataModel.getTlxNvaLec() > (conPro + conPro * 0.2)) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.ObgRem.name(), dataModel.getTlxRem());
            cv.put(DataObs.Columns.ObgAre.name(), dataModel.getTlxAre());
            cv.put(DataObs.Columns.ObgCli.name(), dataModel.getTlxCli());
            cv.put(DataObs.Columns.ObgObs.name(), 80);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
            Log.e(TAG, "saveLectura: consumo elevado");
        } else if (dataModel.getTlxNvaLec() < (conPro * 0.8)) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.ObgRem.name(), dataModel.getTlxRem());
            cv.put(DataObs.Columns.ObgAre.name(), dataModel.getTlxAre());
            cv.put(DataObs.Columns.ObgCli.name(), dataModel.getTlxCli());
            cv.put(DataObs.Columns.ObgObs.name(), 81);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
            Log.e(TAG, "saveLectura: consumo bajo");
        }

        dbAdapter.close();
        onFragmentListener.onTabListener();
    }

    private void calculo(int lectura) {
//        labelEnergiaFacturada.setText("Energia facturada: " + lectura);
        // multiplicador de energia
        lectura = (int) (lectura * dataModel.getTlxFacMul());

        DBAdapter dbAdapter = new DBAdapter(getContext());
        dataModel.setTlxCarFij(dbAdapter.getCargoFijo(dataModel.getTlxCtg()));
        printTitles.add("Importe por cargo fijo");
        printValues.add(dataModel.getTlxCarFij());
        dbAdapter.close();

        double importeEnergia = GenLecturas.importeEnergia(getContext(), lectura, dataModel.getTlxCtg());
        dataModel.setTlxImpEn(importeEnergia);
        printTitles.add("Importe por energía");
        printValues.add(dataModel.getTlxImpEn());
//        labelImporteConsumo.setText("importe por consumo: " + importeEnergia);
        double importePotencia = 0;
        if (dataModel.getTlxTipDem() == 2) {
            importePotencia = Double.valueOf(inputPotenciaReading.getText().toString());
            dataModel.setTlxImpPot(importePotencia);
            printTitles.add("Importe por potencia");
            printValues.add(dataModel.getTlxImpPot());
        }
        double importeConsumo = GenLecturas.round(dbAdapter.getCargoFijo(dataModel.getTlxCtg()) + importeEnergia + importePotencia);
        printTitles.add("Importe por consumo");
        printValues.add(importeConsumo);

        double tarifaDignidad = 0;
        if (dataModel.getTlxDignidad() == 1) {
            tarifaDignidad = GenLecturas.tarifaDignidad(lectura, importeConsumo);
            dataModel.setTlxDesTdi(tarifaDignidad);
            printTitles.add("Beneficiado por tarifa dignidad con");
            printValues.add(dataModel.getTlxDesTdi());
        }

        double ley1886 = 0;
        if (dataModel.getTlxLey1886() == 1) {
            ley1886 = GenLecturas.ley1886(getContext(), lectura, dataModel.getTlxCtg());
            dataModel.setTlxLey1886(ley1886);
            printTitles.add("Menos descuento Ley 1886");
            printValues.add(dataModel.getTlxLey1886());
        }

        double totalConsumo = GenLecturas.totalConsumo(importeConsumo, tarifaDignidad);
        printTitles.add("Importe total por consumo");
        printValues.add(totalConsumo);
//        labelTotalConsumo.setText("Importe total por consumo: " + totalConsumo);

        double totalSuministro = GenLecturas.totalSuministro(totalConsumo, ley1886);
//        labelTotalSuministro.setText("Importe total por el suminstro: " + totalSuministro);
        dataModel.setTlxImpFac(totalSuministro);
        printTitles.add("Importe total por el suministro");
        printValues.add(dataModel.getTlxImpFac());

        double totalSuministroTap = GenLecturas.totalSuministroTap(lectura);
        dataModel.setTlxImpTap(totalSuministroTap);

        double totalSuministroAseo = GenLecturas.totalSuministroAseo(lectura);
        dataModel.setTlxImpAse(totalSuministroAseo);

        importeTotalFactura = GenLecturas.totalFacturar(totalSuministro, totalSuministroTap, totalSuministroAseo);
//        dataModel.setTlxImpTot(GenLecturas.totalFacturar(totalSuministro, totalSuministroTap, totalSuministroAseo));
        importeMesCancelar = importeTotalFactura + 0;
        dataModel.setTlxImpTot(importeMesCancelar + dataModel.getTlxDeuEneI() + dataModel.getTlxDeuAseI());
    }

    private void validSaved() {
        if (dataModel.getEstadoLectura() == 1) {
            estadoMedidor.setText(estados_lectura.Impreso.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
            buttonConfirm.setText(R.string.re_print);
            inputReading.setEnabled(false);
            buttonObs.setEnabled(false);
            inputObsCode.setEnabled(false);
            inputReading.setEnabled(false);
            inputReading.setText(String.valueOf(dataModel.getTlxNvaLec()));
        } else if (dataModel.getEstadoLectura() == 2) {
            estadoMedidor.setText(estados_lectura.Postergado.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
        } else {
            estadoMedidor.setText(estados_lectura.Pendiente.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPendiente));
        }
    }

    public void printResponse(String response) {
        Snackbar.make(rootView, response, Snackbar.LENGTH_LONG).show();
    }

    public interface OnFragmentListener {
        void onTabListener();

        void onPrinting(String srcToPrint);
    }

    private enum estados_lectura {
        Pendiente, Impreso, Postergado
    }

    private int correcionDeDigitos(int nuevaLectura, int decEne) {
        if (decEne == 0) {
            return nuevaLectura;
        }
        String strlectura = String.valueOf(nuevaLectura);
        String res = strlectura.substring(strlectura.length() - decEne, strlectura.length());
        int intDec = Integer.parseInt(res);
        Double as = Double.parseDouble("0." + intDec);

        long factor = (long) Math.pow(10, 0);
        as = as * factor;
        long tmp = Math.round(as);
        int newInt = (int) ((double) tmp / factor);
        int intLecttura = Integer.parseInt(strlectura.substring(0, strlectura.length() - decEne));
        return intLecttura + newInt;
    }
}
