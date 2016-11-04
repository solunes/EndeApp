package com.solunes.endeapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.solunes.endeapp.R;
import com.solunes.endeapp.control_code.ControlCode;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.DataObs;
import com.solunes.endeapp.models.Historico;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.PrintObs;
import com.solunes.endeapp.models.PrintObsData;
import com.solunes.endeapp.utils.GenLecturas;
import com.solunes.endeapp.utils.PrintGenerator;
import com.solunes.endeapp.utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jhonlimaster on 01-12-15.
 */
public class DataFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "DataFragment";
    public static final String KEY_ID_DATA = "id_data";
    private OnFragmentListener onFragmentListener;

    private EditText inputReading;
    private EditText inputPotenciaReading;
    private EditText inputObsCode;
    private Button buttonConfirm;
    private Button buttonObs;
    private TextView labelObs;
    private TextView estadoMedidor;
    private EditText inputRemenber;
    private EditText kwInst;
    private EditText reactiva;
    private EditText kwalto;
    private EditText kwmedio;
    private EditText kwBajo;
    private EditText demBajo;
    private EditText demMedio;
    private EditText demAlto;
    private View inputFechaAlto;
    private View inputFechaMedio;
    private View inputFechaBajo;
    private View inputHoraAlto;
    private View inputHoraMedio;
    private View inputHoraBajo;

    private TextView labelFechaAlto;
    private TextView labelFechaMedio;
    private TextView labelFechaBajo;
    private TextView labelHoraAlto;
    private TextView labelHoraMedio;
    private TextView labelHoraBajo;
    private EditText inputPreNue1;
    private EditText inputPreNue2;

    private ArrayList<String> printTitles;
    private ArrayList<Double> printValues;
    private ArrayList<PrintObs> listPrintObs;

    private double importeTotalFactura;
    private double importeMesCancelar;

    private DataModel dataModel;

    private int positionPrintObs;
    private int positionFecha;
    private int positionHora;

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

    public static DataFragment newInstance(int idData) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID_DATA, idData);
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
        dataModel = dbAdapter.getData(arguments.getInt(KEY_ID_DATA));
        Log.e(TAG, "onCreateView: " + dataModel.getTlxUltInd() + " " +
                "\n " + dataModel.getTlxNom() +
                "\n tiplec: " + dataModel.getTlxTipLec() +
                "\n kwhdev: " + dataModel.getTlxKwhDev() +
                "\n kwhadi: " + dataModel.getTlxKwhAdi() +
                "\n leytag: " + dataModel.getTlxLeyTag() +
                "\n pottag: " + dataModel.getTlxPotTag() +
                "\n dignidad: " + dataModel.getTlxDignidad() +
                "\n deudas aseo: " + dataModel.getTlxDeuAseC() +
                "\n deudas energia: " + dataModel.getTlxDeuEneC() +
                "\n " + dataModel.getTlxNom());
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
            if (dataModel.getTlxPotLei() > 0) {
                inputPotenciaReading.setText(String.valueOf(dataModel.getTlxPotLei()));
                inputPotenciaReading.setEnabled(false);
            }
        }
        inputPreNue1 = (EditText) view.findViewById(R.id.input_pre_nue1);
        inputPreNue2 = (EditText) view.findViewById(R.id.input_pre_nue2);
        if (dataModel.getTlxPreNue1() != null) {
            inputPreNue1.setText(dataModel.getTlxPreNue1());
            inputPreNue1.setEnabled(false);
            inputPreNue2.setEnabled(false);
        }
        if (dataModel.getTlxPreNue2() != null) {
            inputPreNue2.setText(dataModel.getTlxPreNue2());
        }

        View layoutPrecinto = view.findViewById(R.id.layout_precinto_nuevo);
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
        layoutGranDemanda(view);
    }

    private void actionButtons() {
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (dataModel.getEstadoLectura() == estados_lectura.Impreso.ordinal()) {
                    rePrint();
                } else {
                    // Precintos
                    if (dataModel.getTlxPotTag() == 1) {
                        String sPreNue1 = inputPreNue1.getText().toString();
                        String sPreNue2 = inputPreNue2.getText().toString();
                        if (!sPreNue1.isEmpty()) {
                            dataModel.setTlxPreNue1(sPreNue1);
                            dataModel.setTlxPreNue2(sPreNue2);
                        } else {
                            Snackbar.make(view, "Ingrese un precinto", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (dataModel.getTlxTipDem() == 3) {
                        methodGranDemanda();
                    }

                    if (inputReading.getText().toString().isEmpty()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("Advertencia");
                        dialog.setMessage("La lectura va ser 0 de consumo.\n¿Esta seguro?");
                        dialog.setNegativeButton("Cancelar", null);
                        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                inputReading.setText("0");
                                methodPequeñaMedianaDemanda(view);
                            }
                        });
                        dialog.show();
                    } else {
                        methodPequeñaMedianaDemanda(view);
                    }
                }
            }
        });

        buttonObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Selecionar una observacion");
                DBAdapter dbAdapter = new DBAdapter(getContext());
                Cursor cursor = dbAdapter.getObs();
                final String[] stringObs = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    stringObs[i] = Obs.fromCursor(cursor).getObsDes();
                }
                cursor.close();
                dbAdapter.close();
                alertDialog.setSingleChoiceItems(stringObs, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        dialogInterface.dismiss();
                        DBAdapter dbAdapter = new DBAdapter(getContext());
                        Obs obs = Obs.fromCursor(dbAdapter.getObs(stringObs[position]));
                        dbAdapter.close();
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
                dbAdapter.close();
            }
        });
    }

    private void methodGranDemanda() {
        if (!kwInst.getText().toString().isEmpty()) {
            dataModel.setTlxKwInst(Integer.parseInt(kwInst.getText().toString()));
        }
        if (!reactiva.getText().toString().isEmpty()) {
            dataModel.setTlxReactiva(Integer.parseInt(reactiva.getText().toString()));
        }
        if (!kwalto.getText().toString().isEmpty()) {
            dataModel.setTlxKwhAlto(Integer.parseInt(kwalto.getText().toString()));
        }
        if (!kwmedio.getText().toString().isEmpty()) {
            dataModel.setTlxKwhMedio(Integer.parseInt(kwmedio.getText().toString()));
        }
        if (!kwBajo.getText().toString().isEmpty()) {
            dataModel.setTlxKwhBajo(Integer.parseInt(kwBajo.getText().toString()));
        }
        if (!demBajo.getText().toString().isEmpty()) {
            dataModel.setTlxDemBajo(Integer.parseInt(demBajo.getText().toString()));
        }
        if (!demMedio.getText().toString().isEmpty()) {
            dataModel.setTlxDemMedio(Integer.parseInt(demMedio.getText().toString()));
        }
        if (!demAlto.getText().toString().isEmpty()) {
            dataModel.setTlxDemAlto(Integer.parseInt(demAlto.getText().toString()));
        }
        dataModel.setTlxFechaAlto(labelFechaAlto.getText().toString());
        dataModel.setTlxFechaMedio(labelFechaMedio.getText().toString());
        dataModel.setTlxFechaBajo(labelFechaBajo.getText().toString());
        dataModel.setTlxHoraAlto(labelHoraAlto.getText().toString());
        dataModel.setTlxHoraMedio(labelHoraMedio.getText().toString());
        dataModel.setTlxHoraBajo(labelHoraBajo.getText().toString());
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

        if (dataModel.getTlxTipDem() == 2) {
            if (inputPotenciaReading.getText().toString().isEmpty()) {
                Snackbar.make(view, "Ingresar indice de potencia", Snackbar.LENGTH_SHORT).show();
                return;
            }
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
            if (nuevaLectura > dataModel.getTlxTope()) {
                Snackbar.make(view, "La lectura no puede tener mas de " + dataModel.getTlxNroDig() + " digitos", Snackbar.LENGTH_SHORT).show();
                return;
            }
            nuevaLectura = correccionDeDigitos(nuevaLectura, dataModel.getTlxDecEne());
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
        dbAdapter.close();
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
                contentValues.put(PrintObsData.Columns.general_id.name(), dataModel.getId());
                contentValues.put(PrintObsData.Columns.observacion_imp_id.name(), printObs.getId());
                dbAdapter.saveObject(DBHelper.PRINT_OBS_DATA_TABLE, contentValues);

                printTitles.add("Importe por cargo fijo");
                printValues.add(dataModel.getTlxCarFij());
                printTitles.add("Importe por energía");
                printValues.add(dataModel.getTlxImpEn());
                double importePotencia = 0;
                if (dataModel.getTlxTipDem() == 2) {
                    printTitles.add("Importe por potencia");
                    printValues.add(dataModel.getTlxImpPot());
                }
                double importeConsumo = GenLecturas.round(dataModel.getTlxCarFij() + dataModel.getTlxImpEn() + importePotencia);
                printTitles.add("Importe por consumo");
                printValues.add(importeConsumo);
                if (dataModel.getTlxDignidad() == 1) {
                    printTitles.add("Beneficiado por tarifa dignidad con");
                    printValues.add(dataModel.getTlxDesTdi());
                }
                if (dataModel.getTlxLey1886() == 1) {
                    printTitles.add("Menos descuento Ley 1886");
                    printValues.add(dataModel.getTlxLey1886());
                }
                double totalConsumo = GenLecturas.totalConsumo(importeConsumo, dataModel.getTlxDesTdi());
                printTitles.add("Importe total por consumo");
                printValues.add(totalConsumo);
                printTitles.add("Importe total por el suministro");
                printValues.add(dataModel.getTlxImpFac());

                importeTotalFactura = GenLecturas.totalFacturar(dataModel.getTlxConsFacturado(),
                        dataModel.getTlxLey1886(),
                        dataModel.getTlxDesTdi(),
                        importeConsumo);
                // TODO: 20-10-16 algo falta aqui
                importeMesCancelar = importeTotalFactura + 0;

                sendPrint();
            }
        });
        dbAdapter.close();
        reprintDialog.show();
    }

    private void hidingViews(Obs obs) {
        buttonObs.setEnabled(false);
        inputObsCode.setEnabled(false);
        inputReading.setEnabled(false);
        if (obs.getObsFac() == 1 && dataModel.getTlxTipLec() != 4) {
            dataModel.setEstadoLectura(estados_lectura.Impreso.ordinal());
            estadoMedidor.setText(estados_lectura.Impreso.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
            buttonConfirm.setText(R.string.re_print);
            inputPotenciaReading.setEnabled(false);
            inputPreNue1.setEnabled(false);
            inputPreNue2.setEnabled(false);
        } else {
            buttonConfirm.setEnabled(false);
            dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
            estadoMedidor.setText(estados_lectura.Postergado.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
        }
        if (dataModel.getTlxTipDem() == 3) {
            dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
            estadoMedidor.setText(estados_lectura.Postergado.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));

            kwInst.setEnabled(false);
            reactiva.setEnabled(false);
            kwalto.setEnabled(false);
            kwmedio.setEnabled(false);
            kwBajo.setEnabled(false);
            demBajo.setEnabled(false);
            demMedio.setEnabled(false);
            demAlto.setEnabled(false);
            inputFechaAlto.setEnabled(false);
            inputFechaMedio.setEnabled(false);
            inputFechaBajo.setEnabled(false);
            inputHoraAlto.setEnabled(false);
            inputHoraMedio.setEnabled(false);
            inputHoraBajo.setEnabled(false);
        }

        if (dataModel.getTlxImpAvi() == 1) {
            dataModel.setEstadoLectura(estados_lectura.Impreso.ordinal());
        } else if (dataModel.getTlxImpAvi() == 2) {
            dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
        }
    }

    private void printFactura(View view, Obs obs) {
        if (dataModel.getTlxTipDem() == 3) {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (dataModel.getTlxTipLec() == 4) {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (obs.getObsFac() == 1) {
            if (dataModel.getTlxImpAvi() == 1) {
                sendPrint();
            } else {
                Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
            }
        } else {
            DBAdapter dbAdapter = new DBAdapter(getContext());
            ContentValues values = new ContentValues();
            values.put(DataModel.Columns.TlxImpAvi.name(), 0);
            dbAdapter.updateData(dataModel.getTlxCli(), values);
            dbAdapter.close();
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
        }
    }

    private void sendPrint() {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        String[] leyenda = dbAdapter.getLeyenda();
        Historico historico = dbAdapter.getHistorico(dataModel.getId());
        dbAdapter.close();
        onFragmentListener.onPrinting(PrintGenerator.creator(
                dataModel,
                printTitles,
                printValues,
                historico,
                importeTotalFactura,
                importeMesCancelar,
                leyenda));
        printValues = new ArrayList<>();
        printTitles = new ArrayList<>();
        importeMesCancelar = 0;
        importeTotalFactura = 0;
    }

    private void saveLectura(Obs obs) {
        DBAdapter dbAdapter = new DBAdapter(getContext());

        ContentValues cv = new ContentValues();
        Calendar calendar = Calendar.getInstance();

        cv.put(DataModel.Columns.TlxHorLec.name(), StringUtils.getHumanHour(calendar.getTime()));
        cv.put(DataModel.Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());
        cv.put(DataModel.Columns.TlxImpEn.name(), dataModel.getTlxImpEn());
        cv.put(DataModel.Columns.TlxConsumo.name(), dataModel.getTlxConsumo());
        cv.put(DataModel.Columns.TlxConsFacturado.name(), dataModel.getTlxConsFacturado());
        cv.put(DataModel.Columns.TlxImpTot.name(), dataModel.getTlxImpTot());
        cv.put(DataModel.Columns.TlxImpPot.name(), dataModel.getTlxImpPot());

        cv.put(DataModel.Columns.TlxDesTdi.name(), dataModel.getTlxDesTdi());
        cv.put(DataModel.Columns.TlxLey1886.name(), dataModel.getTlxLey1886());
        cv.put(DataModel.Columns.TlxPotLei.name(), dataModel.getTlxPotLei());
        cv.put(DataModel.Columns.TlxUltObs.name(), dataModel.getTlxUltObs());
        cv.put(DataModel.Columns.TlxPreNue1.name(), dataModel.getTlxPreNue1());
        cv.put(DataModel.Columns.TlxPreNue2.name(), dataModel.getTlxPreNue2());
        cv.put(DataModel.Columns.TlxPreNue3.name(), dataModel.getTlxPreNue3());
        cv.put(DataModel.Columns.TlxPreNue4.name(), dataModel.getTlxPreNue4());

        cv.put(DataModel.Columns.TlxKwInst.name(), dataModel.getTlxKwInst());
        cv.put(DataModel.Columns.TlxReactiva.name(), dataModel.getTlxReactiva());
        cv.put(DataModel.Columns.TlxKwhAlto.name(), dataModel.getTlxKwhAlto());
        cv.put(DataModel.Columns.TlxKwhMedio.name(), dataModel.getTlxKwhMedio());
        cv.put(DataModel.Columns.TlxKwhBajo.name(), dataModel.getTlxKwhBajo());
        cv.put(DataModel.Columns.TlxDemAlto.name(), dataModel.getTlxDemAlto());
        cv.put(DataModel.Columns.TlxHoraAlto.name(), dataModel.getTlxHoraAlto());
        cv.put(DataModel.Columns.TlxFechaAlto.name(), dataModel.getTlxFechaAlto());
        cv.put(DataModel.Columns.TlxDemMedio.name(), dataModel.getTlxDemMedio());
        cv.put(DataModel.Columns.TlxHoraMedio.name(), dataModel.getTlxHoraMedio());
        cv.put(DataModel.Columns.TlxFechaMedio.name(), dataModel.getTlxFechaMedio());
        cv.put(DataModel.Columns.TlxDemBajo.name(), dataModel.getTlxDemBajo());
        cv.put(DataModel.Columns.TlxHoraBajo.name(), dataModel.getTlxHoraBajo());
        cv.put(DataModel.Columns.TlxFechaBajo.name(), dataModel.getTlxFechaBajo());

        cv.put(DataModel.Columns.TlxImpFac.name(), dataModel.getTlxImpFac());
        cv.put(DataModel.Columns.TlxCarFij.name(), dataModel.getTlxCarFij());
        cv.put(DataModel.Columns.TlxImpTap.name(), dataModel.getTlxImpTap());
        cv.put(DataModel.Columns.TlxImpAse.name(), dataModel.getTlxImpAse());
        cv.put(DataModel.Columns.TlxKwhDev.name(), dataModel.getTlxKwhDev());
        cv.put(DataModel.Columns.TlxRecordatorio.name(), dataModel.getTlxRecordatorio());
        cv.put(DataModel.Columns.estado_lectura.name(), dataModel.getEstadoLectura());

        cv.put(DataModel.Columns.TlxCodCon.name(), dataModel.getTlxCodCon());

        dbAdapter.updateData(dataModel.getTlxCli(), cv);

        cv = new ContentValues();
        cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
        cv.put(DataObs.Columns.observacion_id.name(), obs.getId());
        dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);

        int conPro = dataModel.getTlxConPro();
        if (dataModel.getTlxNvaLec() > (conPro + conPro * 0.2)) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
            cv.put(DataObs.Columns.observacion_id.name(), 80);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        } else if (dataModel.getTlxNvaLec() < (conPro * 0.8)) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
            cv.put(DataObs.Columns.observacion_id.name(), 81);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        }

        dbAdapter.close();
        onFragmentListener.onTabListener();
    }

    private void calculo(int lectura) {
        lectura = (int) (lectura * dataModel.getTlxFacMul());

        DBAdapter dbAdapter = new DBAdapter(getContext());
        dataModel.setTlxCarFij(dbAdapter.getCargoFijo(dataModel.getTlxCtg()));
        printTitles.add("Importe por cargo fijo");
        printValues.add(dataModel.getTlxCarFij());

        double importeEnergia = GenLecturas.importeEnergia(getContext(), lectura, dataModel.getTlxCtg());
        dataModel.setTlxImpEn(importeEnergia);
        printTitles.add("Importe por energía");
        printValues.add(dataModel.getTlxImpEn());

        double importePotencia = 0;
        if (dataModel.getTlxTipDem() == 2) {
            int potenciaLeida = Integer.valueOf(inputPotenciaReading.getText().toString());
            potenciaLeida = correccionDeDigitos(potenciaLeida, dataModel.getTlxDecPot());
            dataModel.setTlxPotLei(potenciaLeida);
            int potMax = Math.max(potenciaLeida, dataModel.getTlxPotFac());

            importePotencia = potMax * dbAdapter.getCargoPotencia(dataModel.getTlxCtg());

            dataModel.setTlxImpPot(importePotencia);
            printTitles.add("Importe por potencia");
            printValues.add(dataModel.getTlxImpPot());
        }
        double importeConsumo = GenLecturas.round(dbAdapter.getCargoFijo(dataModel.getTlxCtg()) + importeEnergia + importePotencia);
        printTitles.add("Importe por consumo");
        printValues.add(importeConsumo);

        dbAdapter.close();
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
        // TODO: 20-10-16 algo falta aqui
        importeMesCancelar = importeTotalFactura + 0;
        dataModel.setTlxImpTot(importeMesCancelar + dataModel.getTlxDeuEneI() + dataModel.getTlxDeuAseI());

        dataModel.setTlxCodCon(getControlCode(dataModel));
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
            buttonObs.setEnabled(false);
            inputObsCode.setEnabled(false);
            inputReading.setEnabled(false);
            buttonConfirm.setEnabled(false);
            estadoMedidor.setText(estados_lectura.Postergado.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
        } else {
            estadoMedidor.setText(estados_lectura.Pendiente.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPendiente));
        }
        DBAdapter dbAdapter = new DBAdapter(getContext());
        Obs obs = dbAdapter.getObsByCli(dataModel.getId());
        if (obs != null) {
            inputObsCode.setText(String.valueOf(obs.getId()));
            labelObs.setText(obs.getObsDes());
        }
        if (dataModel.getTlxTipDem() == 2 && dataModel.getTlxPotLei() > 0) {
            inputPotenciaReading.setText(String.valueOf(dataModel.getTlxPotLei()));
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        switch (positionFecha) {
            case 1:
                labelFechaBajo.setText(year + "/" + month + "/" + day);
                break;
            case 2:
                labelFechaMedio.setText(year + "/" + month + "/" + day);
                break;
            case 3:
                labelFechaAlto.setText(year + "/" + month + "/" + day);
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        switch (positionHora) {
            case 1:
                labelHoraBajo.setText(hour + ":" + minute);
                break;
            case 2:
                labelHoraMedio.setText(hour + ":" + minute);
                break;
            case 3:
                labelHoraAlto.setText(hour + ":" + minute);
                break;
        }
    }

    public interface OnFragmentListener {
        void onTabListener();

        void onPrinting(String srcToPrint);
    }

    private enum estados_lectura {
        Pendiente, Impreso, Postergado
    }

    private void layoutGranDemanda(View view) {
        View layoutGranDemanda = view.findViewById(R.id.layout_gran_demanda);
        if (dataModel.getTlxTipDem() == 3) {
            layoutGranDemanda.setVisibility(View.VISIBLE);
            kwInst = (EditText) view.findViewById(R.id.input_gd_kwinst);
            reactiva = (EditText) view.findViewById(R.id.input_gd_reactiva);
            kwalto = (EditText) view.findViewById(R.id.input_gd_kwhalto);
            kwmedio = (EditText) view.findViewById(R.id.input_gd_kwhmedio);
            kwBajo = (EditText) view.findViewById(R.id.input_gd_kwhbajo);
            demBajo = (EditText) view.findViewById(R.id.input_gd_dembajo);
            demMedio = (EditText) view.findViewById(R.id.input_gd_demmedio);
            demAlto = (EditText) view.findViewById(R.id.input_gd_demalto);
            labelFechaAlto = (TextView) view.findViewById(R.id.label_gd_fechaalto);
            labelFechaMedio = (TextView) view.findViewById(R.id.label_gd_fechamedio);
            labelFechaBajo = (TextView) view.findViewById(R.id.label_gd_fechabajo);
            labelHoraAlto = (TextView) view.findViewById(R.id.label_gd_horaalto);
            labelHoraMedio = (TextView) view.findViewById(R.id.label_gd_horamedio);
            labelHoraBajo = (TextView) view.findViewById(R.id.label_gd_horabajo);
            inputFechaAlto = view.findViewById(R.id.input_gd_fechaalto);
            inputFechaMedio = view.findViewById(R.id.input_gd_fechamedio);
            inputFechaBajo = view.findViewById(R.id.input_gd_fechabajo);
            inputHoraAlto = view.findViewById(R.id.input_gd_horaalto);
            inputHoraMedio = view.findViewById(R.id.input_gd_horamedio);
            inputHoraBajo = view.findViewById(R.id.input_gd_horabajo);

            if (dataModel.getEstadoLectura() != 0) {
                kwInst.setText(String.valueOf(dataModel.getTlxKwInst()));
                reactiva.setText(String.valueOf(dataModel.getTlxReactiva()));
                kwalto.setText(String.valueOf(dataModel.getTlxKwhAlto()));
                kwmedio.setText(String.valueOf(dataModel.getTlxKwhMedio()));
                kwBajo.setText(String.valueOf(dataModel.getTlxKwhBajo()));
                demAlto.setText(String.valueOf(dataModel.getTlxDemAlto()));
                demMedio.setText(String.valueOf(dataModel.getTlxDemMedio()));
                demBajo.setText(String.valueOf(dataModel.getTlxDemBajo()));
                labelFechaAlto.setText(dataModel.getTlxFechaAlto());
                labelFechaMedio.setText(dataModel.getTlxFechaMedio());
                labelFechaBajo.setText(dataModel.getTlxFechaBajo());
                labelHoraAlto.setText(dataModel.getTlxHoraAlto());
                labelHoraMedio.setText(dataModel.getTlxHoraMedio());
                labelHoraBajo.setText(dataModel.getTlxHoraBajo());
                kwInst.setEnabled(false);
                reactiva.setEnabled(false);
                kwalto.setEnabled(false);
                kwmedio.setEnabled(false);
                kwBajo.setEnabled(false);
                demAlto.setEnabled(false);
                demMedio.setEnabled(false);
                demBajo.setEnabled(false);
                inputFechaAlto.setEnabled(false);
                inputFechaMedio.setEnabled(false);
                inputFechaBajo.setEnabled(false);
                inputHoraAlto.setEnabled(false);
                inputHoraMedio.setEnabled(false);
                inputHoraBajo.setEnabled(false);
            }

            inputFechaAlto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    positionFecha = 3;
                    new DatePickerDialog(getContext(), DataFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            inputFechaMedio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    positionFecha = 2;
                    new DatePickerDialog(getContext(), DataFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            inputFechaBajo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    positionFecha = 1;
                    new DatePickerDialog(getContext(), DataFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            inputHoraAlto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    new TimePickerDialog(getContext(), DataFragment.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
                    positionHora = 3;
                }
            });
            inputHoraMedio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    new TimePickerDialog(getContext(), DataFragment.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
                    positionHora = 2;
                }
            });
            inputHoraBajo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    new TimePickerDialog(getContext(), DataFragment.this, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true).show();
                    positionHora = 1;
                }
            });
        }
    }

    private int correccionDeDigitos(int nuevaLectura, int decimales) {
        if (decimales == 0) {
            return (nuevaLectura);
        }
        String strlectura = String.valueOf(nuevaLectura);
        String res;
        if (strlectura.length() == decimales) {
            res = strlectura;
            Double as = Double.parseDouble("0." + res);
            return (roundDouble(as));
        } else if (strlectura.length() < decimales) {
            int lendif = decimales - strlectura.length();
            res = "";
            for (int i = 0; i < lendif; i++) {
                res += "0";
            }
            res += strlectura;
            Double as = Double.parseDouble("0." + res);
            return (roundDouble(as));
        } else {
            res = strlectura.substring(strlectura.length() - decimales, strlectura.length());
            Double as = Double.parseDouble("0." + res);
            int newInt = roundDouble(as);
            int intLecttura = Integer.parseInt(strlectura.substring(0, strlectura.length() - decimales));
            return (intLecttura + newInt);
        }
    }

    private static int roundDouble(double as) {
        long factor = (long) Math.pow(10, 0);
        as = as * factor;
        long tmp = Math.round(as);
        int newInt = (int) ((double) tmp / factor);
        return newInt;
    }

    private String getControlCode(DataModel dataModel) {
        ControlCode controlCode = new ControlCode();
        DBAdapter dbAdapter = new DBAdapter(getContext());
        String llaveDosificacion = dbAdapter.getLlaveDosificacion(dataModel.getTlxAre());
        String generateControlCode = controlCode.generate(dataModel.getTlxNroAut(),
                dataModel.getTlxFacNro(),
                String.valueOf(dataModel.getTlxCliNit()),
                dataModel.getTlxFecEmi().replace("-", ""),
                String.valueOf((int) dataModel.getTlxImpFac()),
                llaveDosificacion);
        return generateControlCode;

//        int count=0;
//        int fiveDigitsVerhoeffCount=0;
//        int stringDKeyCount=0;
//        int sumProductCount=0;
//        int base64SINCount=0;
//        int ccCount=0;
//
//        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                count+=1;
//                //reemplaza "|" por "/-/" por no ser compatible con el metodo split
//                line = line.replace("|", "/-/");
//                String[] ary = line.split("/-/");
//                //genera codigo de control
//                String cc = controlCode.generate(ary[0], ary[1], ary[2], ary[3].replace("/", ""), ary[4], ary[5]);
//                System.out.println(cc);
//
//                //controla errores
//                if(!ary[6].equals(controlCode.getFiveDigitsVerhoeff()))fiveDigitsVerhoeffCount+=1;
//                if(!ary[7].equals(controlCode.getStringDKey()))stringDKeyCount+=1;
//                if(!ary[8].equals(String.valueOf(controlCode.getSumProduct())))sumProductCount+=1;
//                if(!ary[9].equals(String.valueOf(controlCode.getBase64SIN())))base64SINCount+=1;
//                if(!ary[10].equals(cc))ccCount+=1;
//            }
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//
//        System.out.println("Error 5 digitos Verhoeff: " + fiveDigitsVerhoeffCount);
//        System.out.println("Error Cadena de dosificación: " + stringDKeyCount);
//        System.out.println("Error Suma Producto: " + sumProductCount);
//        System.out.println("Error Base64: " + base64SINCount);
//        System.out.println("Error codigo de control: " + ccCount);
//        System.out.println("---------------------------------------------");
//        System.out.println("Total Registros testeados: " + count);
    }
}
