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
import com.solunes.endeapp.models.DetalleFactura;
import com.solunes.endeapp.models.Historico;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.PrintObs;
import com.solunes.endeapp.models.PrintObsData;
import com.solunes.endeapp.utils.GenLecturas;
import com.solunes.endeapp.utils.PrintGenerator;
import com.solunes.endeapp.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
                "\n ordtpl: " + dataModel.getTlxOrdTpl());
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
                if (dataModel.getEstadoLectura() == estados_lectura.Leido.ordinal()) {
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
                        methodGranDemanda(view);
                        return;
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
                alertDialog.setTitle("Selecionar una observación");
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

    private void methodGranDemanda(View view) {
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

        String input = inputReading.getText().toString().isEmpty() ? "" : inputReading.getText().toString();
        dataModel.setTlxNvaLec(Integer.parseInt(input));
        int obsCod = 104;
        if (!inputObsCode.getText().toString().isEmpty()) {
            obsCod = Integer.parseInt(inputObsCode.getText().toString());
        }
        dataModel.setTlxImpAvi(0);
        DBAdapter dbAdapter = new DBAdapter(getContext());
        final Obs obs = Obs.fromCursor(dbAdapter.getObs(obsCod));
        hidingViews(obs);
        onFragmentListener.onAjusteOrden(dataModel.getId());
        saveLectura(obs);
        Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
    }

    /**
     * Metodo de verificacion de pequeña y mediana demanda
     *
     * @param view
     */
    private void methodPequeñaMedianaDemanda(final View view) {
        // detectar si una observacion existe
        int obsCod = 104;
        if (!inputObsCode.getText().toString().isEmpty()) {
            obsCod = Integer.parseInt(inputObsCode.getText().toString());
        }
        DBAdapter dbAdapter = new DBAdapter(getContext());
        final Obs obs = Obs.fromCursor(dbAdapter.getObs(obsCod));

        // obtener lectura de energia y verificar digitos
        String input = inputReading.getText().toString();
        if (input.length() >= String.valueOf(Integer.MAX_VALUE).length()) {
            Snackbar.make(view, "La lectura no puede tener mas de " + dataModel.getTlxNroDig() + " digitos", Snackbar.LENGTH_SHORT).show();
        }

        // si es mediana demanda se verifica que tiene potencia
        if (dataModel.getTlxTipDem() == 2) {
            if (inputPotenciaReading.getText().toString().isEmpty()) {
                Snackbar.make(view, "Ingresar indice de potencia", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }

        int tipoLectura = dataModel.getTlxTipLec();
        int nuevaLectura = 0;
        int lecturaKwh;
        boolean giro = false;
        if (obs.getId() != 104) {
            tipoLectura = obs.getObsLec();
        }
        if (tipoLectura == 3) {
            lecturaKwh = dataModel.getTlxConPro();
        } else if (input.isEmpty()) {
            Snackbar.make(view, "Ingresar un indice", Snackbar.LENGTH_SHORT).show();
            return;
        } else {
            nuevaLectura = Integer.parseInt(input);
            if (nuevaLectura > dataModel.getTlxTope()) {
                Snackbar.make(view, "La lectura no puede tener mas de " + dataModel.getTlxNroDig() + " digitos", Snackbar.LENGTH_SHORT).show();
                return;
            }
            nuevaLectura = correccionDeDigitos(nuevaLectura, dataModel.getTlxDecEne());
            if (nuevaLectura < dataModel.getTlxUltInd()) {
                giro = true;
            }
            lecturaKwh = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), nuevaLectura, dataModel.getTlxNroDig());
        }
        int conPro = dataModel.getTlxConPro();

        // Alertas de consumo bajo, consumo elevado y giro de medidor
        boolean isAlert = false;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alerta!");
        String message = "Se ha detectado:";
        if (lecturaKwh > (conPro + conPro * (dbAdapter.getParametroValor(Parametro.Values.consumo_elevado.name()) / 100))) {
            message = message + "\n- Consumo elevado";
            isAlert = true;
        } else if (lecturaKwh < (conPro * (dbAdapter.getParametroValor(Parametro.Values.consumo_bajo.name()) / 100))) {
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
        final int finalNuevaLectura = nuevaLectura;
        final int finalTipoLectura = tipoLectura;
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmarLectura(finalTipoLectura, finalNuevaLectura, finalLecturaKwh, obs, view);
            }
        });
        if (isAlert) {
            builder.show();
        } else {
            confirmarLectura(finalTipoLectura, finalNuevaLectura, finalLecturaKwh, obs, view);

        }
        dbAdapter.close();
    }

    /**
     * Una vez realizada la verificacion se procede con el calculo e impresion
     *
     * @param finalTipoLectura
     * @param finalNuevaLectura
     * @param finalLecturaKwh
     * @param obs
     * @param view
     */
    private void confirmarLectura(int finalTipoLectura, int finalNuevaLectura, int finalLecturaKwh, Obs obs, View view) {
        calculo(finalTipoLectura, finalNuevaLectura, finalLecturaKwh, false, obs);
        hidingViews(obs);
        onFragmentListener.onAjusteOrden(dataModel.getId());
        saveLectura(obs);
        printFactura(view);
    }

    private void rePrint() {
        final DBAdapter dbAdapter = new DBAdapter(getContext());
        String[] itemsPrintObs = new String[listPrintObs.size()];
        for (int i = 0; i < listPrintObs.size(); i++) {
            itemsPrintObs[i] = listPrintObs.get(i).getObiDes();
        }
        AlertDialog.Builder reprintDialog = new AlertDialog.Builder(getContext());
        reprintDialog.setTitle("Selecionar una observación");
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
                // save print obs
                PrintObs printObs = listPrintObs.get(positionPrintObs);
                ContentValues contentValues = new ContentValues();
                contentValues.put(PrintObsData.Columns.general_id.name(), dataModel.getId());
                contentValues.put(PrintObsData.Columns.observacion_imp_id.name(), printObs.getId());
                dbAdapter.saveObject(DBHelper.PRINT_OBS_DATA_TABLE, contentValues);
                ContentValues cvData = new ContentValues();
                cvData.put(DataModel.Columns.enviado.name(), DataModel.EstadoEnviado.no_enviado.ordinal());
                dbAdapter.updateData(dataModel.getId(), cvData);

                calculo(0, 0, 0, true, null);

                sendPrint();
            }
        });
        dbAdapter.close();
        reprintDialog.show();
    }

    private void hidingViews(Obs obs) {
        String impaviString = "";
        if (dataModel.getTlxImpAvi() == 0) {
            impaviString = "No Impreso";
        } else {
            impaviString = "Impreso";
        }
        buttonObs.setEnabled(false);
        inputObsCode.setText(String.valueOf(obs.getId()));
        inputObsCode.setEnabled(false);
        inputReading.setEnabled(false);
        if (dataModel.getTlxTipLec() != 5) {
            dataModel.setEstadoLectura(estados_lectura.Leido.ordinal());
        } else {
            dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
        }
        if (dataModel.getTlxTipDem() == 3) {
            dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
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
        if (dataModel.getEstadoLectura() == 1) {
            estadoMedidor.setText(estados_lectura.Leido.name() + " - " + impaviString);
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
            buttonConfirm.setText(R.string.re_print);
            inputPotenciaReading.setEnabled(false);
            inputPreNue1.setEnabled(false);
            inputPreNue2.setEnabled(false);
        } else {
            buttonConfirm.setEnabled(false);
            estadoMedidor.setText(estados_lectura.Postergado.name() + " - " + impaviString);
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
            buttonConfirm.setEnabled(false);
            buttonConfirm.setText("confirmar");
        }
    }

    private void printFactura(View view) {
        if (dataModel.getTlxTipDem() == 3) {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (dataModel.getTlxImpAvi() == 1) {
            sendPrint();
        } else {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
        }
    }

    private void sendPrint() {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        String[] leyenda = dbAdapter.getLeyenda();
        Historico historico = dbAdapter.getHistorico(dataModel.getId());
        String garantiaString = dbAdapter.getItemDescription(427);
        String aseoTitle = dbAdapter.getItemDescription(171);
        String tapTitle = dbAdapter.getItemDescription(153);
        double carDep = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), 427);
        dbAdapter.close();
        onFragmentListener.onPrinting(PrintGenerator.creator(
                dataModel,
                printTitles,
                printValues,
                historico,
                importeTotalFactura,
                importeMesCancelar,
                garantiaString,
                carDep,
                aseoTitle,
                tapTitle,
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
        cv.put(DataModel.Columns.TlxTipLec.name(), dataModel.getTlxTipLec());
        cv.put(DataModel.Columns.TlxImpAvi.name(), dataModel.getTlxImpAvi());

        dbAdapter.updateData(dataModel.getId(), cv);

        cv = new ContentValues();
        cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
        cv.put(DataObs.Columns.observacion_id.name(), obs.getId());
        dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);

        int conPro = dataModel.getTlxConPro();
        if (dataModel.getTlxNvaLec() > (conPro + conPro * (dbAdapter.getParametroValor(Parametro.Values.consumo_elevado.name()) / 100))) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
            cv.put(DataObs.Columns.observacion_id.name(), 80);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        } else if (dataModel.getTlxNvaLec() < (conPro * (dbAdapter.getParametroValor(Parametro.Values.consumo_bajo.name()) / 100))) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
            cv.put(DataObs.Columns.observacion_id.name(), 81);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        }

        dbAdapter.close();
        onFragmentListener.onTabListener();
    }

    /**
     * Metodo de calculo de pequeña y mediana demanda
     *
     * @param tipoLectura
     * @param nuevaLectura
     * @param lectura
     * @param reprint
     * @param obs
     */
    private void calculo(int tipoLectura, int nuevaLectura, int lectura, boolean reprint, Obs obs) {
        DBAdapter dbAdapter = new DBAdapter(getContext());

        // revisar si no es reimpresion, para no realizar el calculo de nuevo
        if (!reprint) {
            if (obs.getObsFac() == 0) {
                dataModel.setTlxImpAvi(0);
            }

            // correccion para consumo promedio
            if (tipoLectura == 3) {
                dataModel.setTlxNvaLec(dataModel.getTlxUltInd());
                dataModel.setTlxKwhDev(lectura);
            }

            dataModel.setTlxTipLec(tipoLectura);
            dataModel.setTlxNvaLec(nuevaLectura);
            dataModel.setTlxConsumo(lectura);

            // correccion de kwh a devolver sino es consumo promedio o lectura ajustada
            if (dataModel.getTlxKwhDev() > 0 && tipoLectura != 3 && tipoLectura != 6) {
                lectura = lectura - dataModel.getTlxKwhDev();
                if (lectura > 0) {
                    dataModel.setTlxKwhDev(0);
                } else {
                    dataModel.setTlxKwhDev(Math.abs(lectura));
                    lectura = 0;
                }
            }

            // lectura final
            lectura = lectura + dataModel.getTlxKwhAdi();
            dataModel.setTlxConsFacturado(lectura);
            dataModel.setTlxKwhAdi(0);

            // multiplicar la lectura con el multiplicador de energia
            lectura = (int) (lectura * dataModel.getTlxFacMul());

            // obtener cargo fijo de la base de datos para la categoria
            double cargoFijo = dbAdapter.getCargoFijo(dataModel.getTlxCtg());
            // redondeo del cargo fijo
            cargoFijo = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), 1, cargoFijo);
            dataModel.setTlxCarFij(cargoFijo);

            // obtener y calcular el importe de energia por rangos
            double importeEnergia = GenLecturas.importeEnergia(getContext(), lectura, dataModel.getTlxCtg(), dataModel.getId());
            dataModel.setTlxImpEn(importeEnergia);
        }

        // agregar cargo fijo y energia al array de impresion
        printTitles.add(dbAdapter.getItemDescription(1));
        printValues.add(GenLecturas.round(dataModel.getTlxCarFij()));
        printTitles.add("Importe por energía");
        printValues.add(GenLecturas.round(dataModel.getTlxImpEn()));

        // calculo de potencia para mediana demanda
        double importePotencia = 0;
        if (dataModel.getTlxTipDem() == 2) {
            if (!reprint) {
                // correccion de digitos para la potencia leida
                int potenciaLeida = Integer.valueOf(inputPotenciaReading.getText().toString());
                potenciaLeida = correccionDeDigitos(potenciaLeida, dataModel.getTlxDecPot());
                dataModel.setTlxPotLei(potenciaLeida);
                // maximo entre potencia anterior y potencia leida
                int potMax = Math.max(potenciaLeida, dataModel.getTlxPotFac());
                // calculo del importe por potencia
                importePotencia = potMax * dbAdapter.getCargoPotencia(dataModel.getTlxCtg());
                importePotencia = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), 41, importePotencia);
                dataModel.setTlxImpPot(importePotencia);
            }

            // agregar el importe por potencia al array de impresion
            printTitles.add(dbAdapter.getItemDescription(41));
            printValues.add(GenLecturas.round(dataModel.getTlxImpPot()));
        }


        double importeConsumo = GenLecturas.round(dataModel.getTlxCarFij() + dataModel.getTlxImpEn() + dataModel.getTlxImpPot());
        // agregar importe por consumo al array de impresion
        printTitles.add("Importe por consumo");
        printValues.add(importeConsumo);

        // verificar la tarifa dignidad, calcular, guardar el importe en detalle facturacion
        double tarifaDignidad = 0;
        if (dataModel.getTlxDignidad() == 1) {
            if (!reprint) {
                tarifaDignidad = GenLecturas.tarifaDignidad(lectura, importeConsumo);
                tarifaDignidad = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), 192, tarifaDignidad);
                dataModel.setTlxDesTdi(tarifaDignidad);
            }

            // agregar descuento por dignidad al array de impresion
            printTitles.add(dbAdapter.getItemDescription(192));
            printValues.add(dataModel.getTlxDesTdi());
        }

        // verificar que hay ley 1886 calcular su importe y guardarlo en detalle facturacion
        double ley1886 = 0;
        if (dataModel.getTlxLeyTag() == 1) {
            if (!reprint) {
                ley1886 = GenLecturas.ley1886(getContext(), lectura, dataModel.getTlxCtg());
                ley1886 = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), 195, ley1886);
                dataModel.setTlxLey1886(ley1886);
            }

            // agregar ley 1886 al array de impresion
            printTitles.add(dbAdapter.getItemDescription(195));
            printValues.add(dataModel.getTlxLey1886());
        }

        // calcular consumo total
        double totalConsumo = GenLecturas.totalConsumo(importeConsumo, tarifaDignidad);
        // agregar consumo total al array de impresion
        printTitles.add("Importe total por consumo");
        printValues.add(totalConsumo);

        // array de ids de items facturacion de detalle facturacion
        // se pueden agregar mas cargos al array usando el item_facturacion_id
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(461); // intereses por mora
        integers.add(181); // cargo por conexion
        integers.add(186); // cargo por reconexion
        integers.add(185); // cargo por rehabilitacion
        double cargoExtraTotal = 0;
        for (Integer itemId : integers) {
            double cargoExtra = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), itemId);
            cargoExtraTotal += cargoExtra;
            if (cargoExtra > 0) {
                printTitles.add(dbAdapter.getItemDescription(itemId));
                printValues.add(cargoExtra);
            }
        }

        // calculo del importe total del suministro
        double totalSuministro = GenLecturas.totalSuministro(totalConsumo, ley1886, cargoExtraTotal);

        // calculo de suministro tap y suministro por aseo
        if (!reprint) {
            double totalSuministroTap = GenLecturas.totalSuministroTap(dataModel, getContext(), totalConsumo);
            totalSuministroTap = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), 153, totalSuministroTap);
            double totalSuministroAseo = GenLecturas.totalSuministroAseo(dataModel, getContext(), lectura);
            totalSuministroAseo = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), 171, totalSuministroAseo);
            dataModel.setTlxImpFac(totalSuministro);
            dataModel.setTlxImpTap(totalSuministroTap);
            dataModel.setTlxImpAse(totalSuministroAseo);
        }
        // agregar total por el suministro al array de impresion
        printTitles.add("Importe total por el suministro");
        printValues.add(dataModel.getTlxImpFac());

        // calculo de importe a facturar
        importeTotalFactura = GenLecturas.totalFacturar(totalSuministro, dataModel.getTlxImpTap(), dataModel.getTlxImpAse());
        double carDep = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), 427);
        importeMesCancelar = importeTotalFactura + carDep;
        if (!reprint) {
            dataModel.setTlxImpTot(GenLecturas.round(importeMesCancelar + dataModel.getTlxDeuEneI() + dataModel.getTlxDeuAseI()));
            dataModel.setTlxCodCon(getControlCode(dataModel));
            if (dataModel.getTlxTipLec() != 5) {
                dataModel.setEstadoLectura(estados_lectura.Leido.ordinal());
            } else {
                dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
            }
        }
        dbAdapter.close();
    }

    private void validSaved() {
        String impaviString = "";
        if (dataModel.getTlxImpAvi() == 0) {
            impaviString = "No Impreso";
        } else {
            impaviString = "Impreso";
        }
        if (dataModel.getEstadoLectura() == 1) {
            estadoMedidor.setText(estados_lectura.Leido.name() + " - " + impaviString);
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
            estadoMedidor.setText(estados_lectura.Postergado.name() + " - " + impaviString);
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
        if (dataModel.getEstadoLectura() != 0) {
            if (dataModel.getTlxImpAvi() == 0) {
                buttonConfirm.setEnabled(false);
                buttonConfirm.setText("confirmar");
            }
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
        String date = pad(hour) + ":" + pad(minute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String newTime = "";
        try {
            newTime = "" + new SimpleDateFormat("HH:mm").format(simpleDateFormat.parse(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (positionHora) {
            case 1:
                labelHoraBajo.setText(newTime);
                break;
            case 2:
                labelHoraMedio.setText(newTime);
                break;
            case 3:
                labelHoraAlto.setText(newTime);
                break;
        }
    }

    private String pad(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return "" + value;
    }

    public interface OnFragmentListener {
        void onTabListener();

        void onPrinting(String srcToPrint);

        void onAjusteOrden(int idData);
    }

    public enum estados_lectura {
        Pendiente, Leido, Postergado
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
    }
}
