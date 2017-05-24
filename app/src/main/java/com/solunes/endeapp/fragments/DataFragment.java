package com.solunes.endeapp.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.adapters.SingleChoiceAdapter;
import com.solunes.endeapp.control_code.ControlCode;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.dataset.DBHelper;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.DataObs;
import com.solunes.endeapp.models.DetalleFactura;
import com.solunes.endeapp.models.FacturaDosificacion;
import com.solunes.endeapp.models.Historico;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.PrintObs;
import com.solunes.endeapp.models.PrintObsData;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.utils.FileUtils;
import com.solunes.endeapp.utils.GenLecturas;
import com.solunes.endeapp.utils.PrintGenerator;
import com.solunes.endeapp.utils.SingleChoiceItem;
import com.solunes.endeapp.utils.StringUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Este fragmento muestra los detalles de cada una de las lecturas
 */
public class DataFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "DataFragment";
    public static final String KEY_ID_DATA = "id_data";
    public static final String KEY_ID_USER = "id_user";
    private OnFragmentListener onFragmentListener;

    private EditText inputReading;
    private EditText inputPotenciaReading;
    private EditText inputObsCode;
    private Button buttonConfirm;
    private Button buttonPostergar;
    private Button buttonObs;
    private Button buttonObsImped;
    private ImageButton buttonObsAdd;
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
    private EditText inputPreNue3;
    private EditText inputPreNue4;
    private LinearLayout linearLayoutObs;

    private ArrayList<String> printTitles;
    private ArrayList<Double> printValues;
    private ArrayList<String> printBottomTitles;
    private ArrayList<Double> printBottomValues;
    private ArrayList<PrintObs> listPrintObs;

    private DataModel dataModel;

    private int positionPrintObs;
    private int positionFecha;
    private int positionHora;
    private int countAlert;
    private ArrayList<Integer> autoObs;
    private ArrayList<Integer> obsArray;

    private User user;

    public DataFragment() {
        printTitles = new ArrayList<>();
        printValues = new ArrayList<>();
        printBottomTitles = new ArrayList<>();
        printBottomValues = new ArrayList<>();
        listPrintObs = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onFragmentListener = (OnFragmentListener) context;
    }

    public static DataFragment newInstance(int idData, int lecId) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID_DATA, idData);
        bundle.putInt(KEY_ID_USER, lecId);
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
        cursor.close();
        user = dbAdapter.getUser(arguments.getInt(KEY_ID_USER));
        dataModel = dbAdapter.getData(arguments.getInt(KEY_ID_DATA));
        Log.e(TAG, "onCreateView: " +
                "\n " + dataModel.getTlxNom() +
                "\n ultimo indice: " + dataModel.getTlxUltInd() +
                "\n consumo promedio: " + dataModel.getTlxConPro());
        dbAdapter.close();
        setupUI(view, dataModel);
        actionButtons();
        validSaved();
        return view;
    }

    /**
     * Este metodo maneja todas las vistas iniciales y validaciones para cuando se crea el fragment
     *
     * @param view es una view para obtener las vistas
     * @param data un objeto DataModel para llenar las vistas
     */
    public void setupUI(View view, final DataModel data) {
        TextView nameData = (TextView) view.findViewById(R.id.data_name);
        nameData.setText(data.getTlxNom());
        TextView dataClient = (TextView) view.findViewById(R.id.data_client);
        dataClient.setText("N° Consumidor: " + data.getTlxCli() + "-" + data.getTlxDav());
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
        if (user.getLecNiv() == 3) {
            TextView supportUltind = (TextView) view.findViewById(R.id.support_ultind);
            supportUltind.setText("Último índice: " + data.getTlxUltInd());
            supportUltind.setVisibility(View.VISIBLE);
        }

        obsArray = new ArrayList<>();

        linearLayoutObs = (LinearLayout) view.findViewById(R.id.container_obs);
        inputReading = (EditText) view.findViewById(R.id.input_reading);
        inputReading.setSelected(false);
        buttonConfirm = (Button) view.findViewById(R.id.button_confirm);
        buttonPostergar = (Button) view.findViewById(R.id.button_postergar);
        buttonObs = (Button) view.findViewById(R.id.button_obs);
        buttonObsImped = (Button) view.findViewById(R.id.button_obs_imped);
        buttonObsAdd = (ImageButton) view.findViewById(R.id.button_obs_add);
        inputObsCode = (EditText) view.findViewById(R.id.obs_code);
        TextView estadoCliente = (TextView) view.findViewById(R.id.estado_client);
        if (data.getTlxEstCli() == 1) {
            estadoCliente.setText(estados_cliente.Conectado.name());
            estadoCliente.setTextColor(getResources().getColor(R.color.colorConectado));
        } else if (data.getTlxEstCli() == 3) {
            estadoCliente.setText(estados_cliente.Cortado.name());
        } else {
            estadoCliente.setText(estados_cliente.Suspendido.name());
        }
        estadoMedidor = (TextView) view.findViewById(R.id.estado_medidor);
        estadoMedidor.setText(estados_lectura.values()[data.getEstadoLectura()].name());
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
        if (dataModel.getTlxLeePot() == 1) {
            inputPotenciaReading.setVisibility(View.VISIBLE);
            if (dataModel.getTlxPotLei() > 0) {
                inputPotenciaReading.setText(String.valueOf(dataModel.getTlxPotLei()));
                inputPotenciaReading.setEnabled(false);
            }
        }
        inputPreNue1 = (EditText) view.findViewById(R.id.input_pre_nue1);
        inputPreNue2 = (EditText) view.findViewById(R.id.input_pre_nue2);
        inputPreNue3 = (EditText) view.findViewById(R.id.input_pre_nue3);
        inputPreNue4 = (EditText) view.findViewById(R.id.input_pre_nue4);
        if (dataModel.getTlxPreNue1() != null) {
            inputPreNue1.setText(dataModel.getTlxPreNue1());
            inputPreNue1.setEnabled(false);
            inputPreNue2.setEnabled(false);
            inputPreNue3.setEnabled(false);
            inputPreNue4.setEnabled(false);
        }
        if (dataModel.getTlxPreNue2() != null) {
            inputPreNue2.setText(dataModel.getTlxPreNue2());
        }
        if (dataModel.getTlxPreNue3() != null) {
            inputPreNue3.setText(dataModel.getTlxPreNue3());
        }
        if (dataModel.getTlxPreNue4() != null) {
            inputPreNue4.setText(dataModel.getTlxPreNue4());
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

    /**
     * Este metodo tiene validaciones en las acciones de los botones
     */
    private void actionButtons() {
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (dataModel.getEstadoLectura() == estados_lectura.Leido.ordinal()) {
                    rePrint();
                } else {

                    DBAdapter dbAdapter = new DBAdapter(getContext());

                    if (obsArray.size() == 0) {
                        obsArray.add(104);
                    }
                    Obs obsPrioritario = null;
                    for (Integer integer : obsArray) {
                        Obs obss = Obs.fromCursor(dbAdapter.getObs(integer));
                        if (obsPrioritario == null) {
                            obsPrioritario = obss;
                        } else {
                            if (obsPrioritario.getObsLec() == obss.getObsLec()) {
                                if (obss.getObsCond() > 0) {
                                    obsPrioritario = obss;
                                }
                            } else {
                                // prioridad 5,9,0
                                if (obss.getObsLec() == 5) {
                                    obsPrioritario = obss;
                                } else if (obss.getObsLec() == 9 && obsPrioritario.getObsLec() == 0) {
                                    obsPrioritario = obss;
                                }
                            }
                        }
                    }

                    final Obs obs = obsPrioritario;

                    // obtener lectura de energia y verificar digitos
                    String input = inputReading.getText().toString();
                    if (input.length() >= String.valueOf(Integer.MAX_VALUE).length()) {
                        Snackbar.make(view, "No puede tener mas de 10 digitos", Snackbar.LENGTH_SHORT).show();
                    }
                    if (input.isEmpty()) {
                        input = "0";
                    }

                    // obtener tipo de lectura
                    int tipoLectura = dataModel.getTlxTipLec();
                    if (obs.getId() != 104) {
                        tipoLectura = obs.getObsLec();
                    }

                    final String finalInput = input;

                    // Precintos
                    if (dataModel.getTlxPotTag() == 1) {
                        String sPreNue1 = inputPreNue1.getText().toString();
                        String sPreNue2 = inputPreNue2.getText().toString();
                        String sPreNue3 = inputPreNue3.getText().toString();
                        String sPreNue4 = inputPreNue4.getText().toString();
                        if (sPreNue1.isEmpty() && tipoLectura != 3 && tipoLectura != 9) {
                            Snackbar.make(view, "Ingrese un precinto", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (!sPreNue1.isEmpty()) {
                            dataModel.setTlxPreNue1(sPreNue1);
                        }
                        if (!sPreNue2.isEmpty()) {
                            dataModel.setTlxPreNue2(sPreNue2);
                        }
                        if (!sPreNue3.isEmpty()) {
                            dataModel.setTlxPreNue3(sPreNue3);
                        }
                        if (!sPreNue4.isEmpty()) {
                            dataModel.setTlxPreNue4(sPreNue4);
                        }
                    }

                    // se procede a gran demanda
                    if (dataModel.getTlxTipDem() == 3) {
                        methodGranDemanda(view, input, tipoLectura, obs);
                        return;
                    }

                    // advertencia de lectura 0, luego se procede al calculo de pequeña y gran demanda
                    if (input.isEmpty()) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setTitle("Advertencia");
                        dialog.setMessage("La lectura va ser 0 de consumo.\n¿Esta seguro?");
                        dialog.setNegativeButton("Cancelar", null);
                        final int finalTipoLectura = tipoLectura;
                        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                methodPequeñaMedianaDemanda(view, finalInput, finalTipoLectura, obs);
                            }
                        });
                        dialog.show();
                    } else {
                        methodPequeñaMedianaDemanda(view, input, tipoLectura, obs);
                    }
                }
            }
        });

        buttonObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // se selecciona una observacion para la lectura, por defecto 104
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Selecionar una observación");
                DBAdapter dbAdapter = new DBAdapter(getContext());
                final Cursor cursor = dbAdapter.getObsTip2();
                final ArrayList<SingleChoiceItem> items = new ArrayList<>();
                boolean[] checkedItems = new boolean[cursor.getCount()];
                String[] titles = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    Obs obs = Obs.fromCursor(cursor);
                    SingleChoiceItem choiceItem = new SingleChoiceItem();
                    choiceItem.setCode(obs.getId());
                    choiceItem.setTitle(obs.getObsDes());
                    choiceItem.setObsInd(obs.getObsInd());
                    titles[i] = obs.getId() + " - " + obs.getObsDes();
                    if (obsArray.contains(obs.getId())) {
                        choiceItem.setCheck(true);
                        checkedItems[i] = true;
                    }
                    items.add(choiceItem);
                }
                cursor.close();

                dbAdapter.close();
                alertDialog.setMultiChoiceItems(titles, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean checked) {
                        SingleChoiceItem choiceItem = items.get(position);
                        if (checked) {
                            if (!obsArray.contains(choiceItem.getCode())) {
                                obsArray.add(choiceItem.getCode());
                            }
                        } else {
                            obsArray.remove((Integer) choiceItem.getCode());
                        }
                    }
                });
                alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        notifiDataContainer();
                    }
                });
                alertDialog.setNegativeButton("Cancelar", null);
                alertDialog.show();
            }
        });

        buttonPostergar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (dataModel.getEstadoLectura() == estados_lectura.Leido.ordinal()) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setTitle(R.string.confirmar);
                    builder1.setMessage("¿Esta seguro de no entregar la factura?");
                    builder1.setNegativeButton("Cancelar", null);
                    builder1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                            builder2.setMessage("¿Esta completamente seguro de no entregar la factura?");
                            builder2.setTitle(R.string.confirmar);
                            builder2.setNegativeButton("Cancelar", null);
                            builder2.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DBAdapter dbAdapter = new DBAdapter(getContext());
                                    PrintObs printObs = dbAdapter.getPrintObs(6);
                                    ContentValues cvData = new ContentValues();
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(PrintObsData.Columns.general_id.name(), dataModel.getId());
                                    contentValues.put(PrintObsData.Columns.observacion_imp_id.name(), printObs.getId());
                                    cvData.put(DataModel.Columns.enviado.name(), DataModel.EstadoEnviado.no_enviado.ordinal());
                                    dbAdapter.saveObject(DBHelper.PRINT_OBS_DATA_TABLE, contentValues);
                                    dbAdapter.updateData(dataModel.getId(), cvData);
                                    dbAdapter.close();
                                    Snackbar.make(view, "Cliente se factura en oficina", Snackbar.LENGTH_SHORT).show();
                                    buttonPostergar.setEnabled(false);
                                }
                            });
                            builder2.show();
                        }
                    });
                    builder1.show();
                } else {
                    dataModel.setEstadoLectura(estados_lectura.PostergadoTmp.ordinal());
                    buttonPostergar.setEnabled(false);
                    estadoMedidor.setText("Postergado Temp.");
                    estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));

                    DBAdapter dbAdapter = new DBAdapter(getContext());
                    ContentValues cv = new ContentValues();
                    cv.put(DataModel.Columns.estado_lectura.name(), dataModel.getEstadoLectura());
                    dbAdapter.updateData(dataModel.getId(), cv);
                    dbAdapter.close();
                    onFragmentListener.onAjusteOrden(dataModel.getId());
                    onFragmentListener.onNextPage();
                }
            }
        });

        buttonObsImped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Selecionar una observación");
                final DBAdapter dbAdapter = new DBAdapter(getContext());
                final Cursor cursor = dbAdapter.getObsTip1();
                final ArrayList<SingleChoiceItem> items = new ArrayList<>();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    Obs obs = Obs.fromCursor(cursor);
                    SingleChoiceItem choiceItem = new SingleChoiceItem();
                    choiceItem.setCode(obs.getId());
                    choiceItem.setTitle(obs.getObsDes());
                    choiceItem.setObsInd(obs.getObsInd());
                    items.add(choiceItem);
                }
                cursor.close();
                dbAdapter.close();

                alertDialog.setSingleChoiceItems(new SingleChoiceAdapter(getContext(), items, true), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        SingleChoiceItem choiceItem = items.get(position);
                        Cursor cursor1 = dbAdapter.getObsTip1(choiceItem.getCode());
                        Obs obs = Obs.fromCursor(cursor1);
                        cursor1.close();
                        autoObs = new ArrayList<>();
                        obsArray = new ArrayList<>();
                        obsArray.add(obs.getId());
                        methodPequeñaMedianaDemanda(view, "0", obs.getObsLec(), obs);
                        buttonObsImped.setEnabled(false);
                        buttonObsImped.setTextColor(getResources().getColor(R.color.colorDisableImped));
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        buttonObsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String obsCode = inputObsCode.getText().toString();
                DBAdapter dbAdapter = new DBAdapter(getContext());
                if (!obsCode.isEmpty()) {
                    Cursor cursor = dbAdapter.getObsTip2(Integer.parseInt(obsCode));
                    if (cursor.getCount() != 0) {
                        Obs obs = Obs.fromCursor(cursor);
                        if (!obsArray.contains(obs.getId())) {
                            obsArray.add(obs.getId());
                            notifiDataContainer();
                        }
                    } else {
                        Snackbar.make(view, "No hay esa observación", Snackbar.LENGTH_SHORT).show();
                    }
                    cursor.close();
                    inputObsCode.setText("");
                }
            }
        });
    }

    /**
     * Metodo de verificacion de pequeña y mediana demanda
     *
     * @param view
     */
    private void methodPequeñaMedianaDemanda(final View view, String input, int tipoLectura, final Obs obs) {

        int nuevaLectura = 0;
        int lecturaKwh;
        boolean giro = false;
        boolean indiceIgualado = false;
        DBAdapter dbAdapter = new DBAdapter(getContext());

        // si es mediana demanda se verifica que tiene potencia
        if (dataModel.getTlxLeePot() == 1 && tipoLectura != 3 && tipoLectura != 9) {
            if (inputPotenciaReading.getText().toString().isEmpty()) {
                Snackbar.make(view, "Ingresar indice de potencia", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }

        boolean isAlert = false;
        String message = "Se ha detectado:";

        // obtener lectura de energia y verificar digitos
        nuevaLectura = Integer.parseInt(input);

        if (nuevaLectura > dataModel.getTlxTope()) {
            Snackbar.make(view, "La lectura no puede tener mas de " + dataModel.getTlxNroDig() + " digitos", Snackbar.LENGTH_SHORT).show();
            return;
        }
        nuevaLectura = correccionDeDigitos(nuevaLectura, dataModel.getTlxDecEne());

        boolean isCond = false;

        // condicionante de observacion 1
        if (obs.getObsCond() == 1) {
            if (nuevaLectura > 0) {
                tipoLectura = 0;
            } else {
                isCond = true;
                tipoLectura = 0;
            }
        } else if (obs.getObsCond() == 3) {
            if (dataModel.getTlxUltInd() == nuevaLectura) {
                tipoLectura = 5;
            } else {
                nuevaLectura = dataModel.getTlxUltInd();
            }
        } else if (obs.getObsCond() == 4) {
            if (nuevaLectura > dataModel.getTlxUltInd()) {
                tipoLectura = 0;
            } else {
                tipoLectura = 9;
            }
        } else if (obs.getObsCond() == 5) {
            if (nuevaLectura > 0) {
                tipoLectura = 0;
            } else {
                tipoLectura = 5;
            }
        }

        // correction de obs ind
        if (tipoLectura != 0) {
            if (obs.getObsInd() == 1) {
                nuevaLectura = dataModel.getTlxUltInd();
            } else if (obs.getObsInd() == 2) {
                nuevaLectura = 0;
            }
        }

        // Calcular la lectura en Kwh segun el tipo de lectura
        if (tipoLectura == 3 || tipoLectura == 9) {
            lecturaKwh = dataModel.getTlxConPro();
        } else {
            if (nuevaLectura < dataModel.getTlxUltInd()) {
                if (Math.abs(dataModel.getTlxUltInd() - nuevaLectura) <= 10) {
                    nuevaLectura = dataModel.getTlxUltInd();
                    indiceIgualado = true;
                } else {
                    giro = true;
                }
            }
            lecturaKwh = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), nuevaLectura, dataModel.getTlxNroDig());
        }

        // condicionante de observacion 2
        if (indiceIgualado) {
            lecturaKwh = dataModel.getTlxConPro();
            tipoLectura = 9;
        }

        int conPro = dataModel.getTlxConPro();
        autoObs = new ArrayList<>();
        // Alertas de consumo bajo, consumo elevado y giro de medidor
        boolean isPostergado = false;
        if (tipoLectura == 5) {
            isPostergado = true;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_Dialog);
        builder.setTitle("Alerta!");
        if (dataModel.getTlxCliNew() == 0) {

            if (isConsumoElevado(lecturaKwh, dataModel.getTlxCtg(), dataModel.getTlxConPro())) {
                message += "\n- Consumo elevado";
                isAlert = true;
                autoObs.add(80);
                if (obsLec(dbAdapter, 80)) {
                    tipoLectura = 5;
                }
            } else if (lecturaKwh < (conPro * (dbAdapter.getParametroValor(Parametro.Values.consumo_bajo.name()) / 100))) {
                message += "\n- Consumo bajo";
                isAlert = true;
                autoObs.add(81);
                if (obsLec(dbAdapter, 81)) {
                    tipoLectura = 5;
                }
            }
            if (giro) {
                message += "\n- Giro de medidor";
                isAlert = true;
                if (autoObs.contains(80)) {
                    autoObs.add(36);
                }
            }
            if (indiceIgualado) {
                message += "\n- Índice igualado";
                isAlert = true;
                autoObs.add(50);
            }
        }

        // Verificacion si el estado de cliente es cortado o suspendido y se introduce el mismo indice al anterior, se posterga
        if (dataModel.getTlxEstCli() == 3 || dataModel.getTlxEstCli() == 5) {
            if (dataModel.getTlxUltInd() == nuevaLectura) {
                tipoLectura = 5;
                isPostergado = true;
            } else {
                autoObs.add(10);
            }
        }

        if (isPostergado) {
            isAlert = false;
        }

        // Verificar y obtener la lectura inicial cuando sea requerida
        if (obs.getObsInd() == 0 && input.equals("0")) {
            isAlert = true;
            message += "\n- Índice introducido es 0";
        }

        message += "\n\n¿Es correcto el índice " + input + "?";

        final int finalLecturaKwh = lecturaKwh;
        final int finalNuevaLectura = nuevaLectura;
        final int finalTipoLectura = tipoLectura;
        if (isCond && giro) {
            message += "\n\n¿Desea confirmar un giro de medidor?";
            builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    confirmarLectura(9, dataModel.getTlxUltInd(), dataModel.getTlxConPro(), obs, view);
                }
            });
            isAlert = true;
        } else {
            builder.setNegativeButton("Cancelar", null);
        }
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                confirmarLectura(finalTipoLectura, finalNuevaLectura, finalLecturaKwh, obs, view);
            }
        });
        builder.setMessage(message);
        // si hay alerta y el tipo de lectura no es postergada
        if (isAlert) {
            if (countAlert < 2) {
                builder.show();
                countAlert++;
            } else {
                confirmarLectura(5, finalNuevaLectura, finalLecturaKwh, obs, view);
            }
        } else {
            confirmarLectura(finalTipoLectura, finalNuevaLectura, finalLecturaKwh, obs, view);
        }
        dbAdapter.close();
    }

    /**
     * Este metodo guarda los datos de gran demanda
     */
    private void methodGranDemanda(View view, String input, int tipoLectura, Obs obs) {
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

        dataModel.setTlxTipLec(tipoLectura);
        dataModel.setTlxNvaLec(Integer.parseInt(input));
        dataModel.setTlxImpAvi(0);

        hidingViews();
        onFragmentListener.onAjusteOrden(dataModel.getId());
        autoObs = new ArrayList<>();
        obsArray = new ArrayList<>();
        saveLectura();
        onFragmentListener.onNextPage();
        Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
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
        Log.e(TAG, "confirmarLectura: " + obs.getId());
        int potenciaLeida = 0;
        if (!inputPotenciaReading.getText().toString().isEmpty()) {
            potenciaLeida = Integer.valueOf(inputPotenciaReading.getText().toString());
        }
        boolean isCalculo = calculo(getContext(),
                dataModel,
                finalTipoLectura,
                finalNuevaLectura,
                finalLecturaKwh,
                potenciaLeida,
                obs);

        printArrays();
        if (isCalculo) {
            hidingViews();
            onFragmentListener.onAjusteOrden(dataModel.getId());
            saveLectura();
            printFactura(view);
            onFragmentListener.onNextPage();
        }
    }

    /**
     * Metodo de calculo de pequeña y mediana demanda
     *
     * @param tipoLectura  Este campo tiene el tipo de lectura
     * @param nuevaLectura Esta es la nueva lectura en kwh para guardar
     * @param lectura      Esta es la lectura a facturar
     * @param obs          la observacion de la lectura
     */
    public static boolean calculo(Context context,
                                  DataModel dataModel,
                                  int tipoLectura,
                                  int nuevaLectura,
                                  int lectura,
                                  int potenciaLeida,
                                  Obs obs) {
        DBAdapter dbAdapter = new DBAdapter(context);
        // revisar si no es reimpresion, para no realizar el calculo de nuevo
        if (obs.getObsFac() == 0) {
            dataModel.setTlxImpAvi(0);
        }

        // verificacion de limites maximos para consumos muy elevados
//            int maxKwh = dbAdapter.getMaxKwh(dataModel.getTlxCtg());
//            if (maxKwh == -1) {
//                Toast.makeText(getContext(), "No hay un límite máximo para el consumo", Toast.LENGTH_LONG).show();
//                return false;
//            }
//            if (lectura >= maxKwh) {
//                tipoLectura = 5;
//                Log.e(TAG, "calculo: postergado");
//            }

        if (tipoLectura == 5) {
            dataModel.setTlxNvaLec(nuevaLectura);
            if (dataModel.getTlxLeePot() == 1) {
                potenciaLeida = correccionPotencia(potenciaLeida, dataModel.getTlxDecPot());
                dataModel.setTlxPotLei(potenciaLeida);
            }
            dataModel.setTlxTipLec(tipoLectura);
            dataModel.setTlxImpAvi(0);
            return true;
        }

        // multiplicar la lectura con el multiplicador de energia
        lectura = (int) (lectura * dataModel.getTlxFacMul());

        dataModel.setTlxNvaLec(nuevaLectura);
        dataModel.setTlxTipLec(tipoLectura);
        dataModel.setTlxConsumo(lectura);

        // correccion para consumo promedio
        if (tipoLectura == 3 || tipoLectura == 9) {
            dataModel.setTlxKwhDev(lectura);
            dataModel.setTlxConsumo(0);
        }

        Log.e(TAG, "calculo: KwhDev2 " + dataModel.getTlxKwhDev2());
        // correccion de kwh a devolver sino es consumo promedio o lectura estinada
        if (dataModel.getTlxKwhDev() > 0 && tipoLectura != 3 && tipoLectura != 9) {
            int lectura2 = lectura - dataModel.getTlxKwhDev();
            if (lectura2 > 0) {
                dataModel.setTlxKwhDev(0);
            } else {
                dataModel.setTlxKwhDev(Math.abs(lectura2));
                dataModel.setTlxKwhDev2(lectura);
                Log.e(TAG, "calculo: KwhDev2 " + dataModel.getTlxKwhDev2());
                lectura = 0;
            }
        }

        // lectura final
        if (dataModel.getTlxKwhAdi() > 0 && tipoLectura != 3 && tipoLectura != 9) {
            lectura = lectura + dataModel.getTlxKwhAdi();
            dataModel.setTlxKwhAdi(0);
        }
        dataModel.setTlxConsFacturado(lectura);

        // obtener cargo fijo de la base de datos para la categoria
        double cargoFijo = dbAdapter.getCargoFijo(dataModel.getTlxCtg());
        // redondeo del cargo fijo
        cargoFijo = DetalleFactura.crearDetalle(context, dataModel.getId(), 1, cargoFijo);
        dataModel.setTlxCarFij(cargoFijo);

        // obtener y calcular el importe de energia por rangos
        double importeEnergia = GenLecturas.importeEnergia(context, lectura, dataModel.getTlxCtg(), dataModel.getId());
        dataModel.setTlxImpEnergia(importeEnergia);

        // calculo de potencia para mediana demanda
        double importePotencia = 0;
        if (dataModel.getTlxLeePot() == 1) {
            // correccion de digitos para la potencia leida
            potenciaLeida = correccionPotencia(potenciaLeida, dataModel.getTlxDecPot());
            dataModel.setTlxPotLei(potenciaLeida);
            // maximo entre potencia anterior y potencia leida
            int potMax = Math.max(potenciaLeida, dataModel.getTlxPotFac());
            // calculo del importe por potencia
            double cargoPotencia = dbAdapter.getCargoPotencia(dataModel.getTlxCtg());
            if (cargoPotencia == -1) {
                Toast.makeText(context, "No hay cargo de potencia", Toast.LENGTH_LONG).show();
                return false;
            }
            importePotencia = potMax * cargoPotencia;
            importePotencia = DetalleFactura.crearDetalle(context, dataModel.getId(), 41, importePotencia);
            dataModel.setTlxImpPot(importePotencia);
        }


        double importeConsumo = GenLecturas.round(dataModel.getTlxCarFij() + dataModel.getTlxImpEnergia() + dataModel.getTlxImpPot());
        dataModel.setTlxImpEn(importeConsumo);

        // verificar la tarifa dignidad, calcular, guardar el importe en detalle facturacion
        double tarifaDignidad = 0;
        if (dataModel.getTlxDignidad() == 1) {
            tarifaDignidad = GenLecturas.tarifaDignidad(context, lectura, dataModel.getTlxImpEn());
            tarifaDignidad = DetalleFactura.crearDetalle(context, dataModel.getId(), 192, tarifaDignidad);
            dataModel.setTlxDesTdi(tarifaDignidad);
        }

        // calcular consumo total
        dataModel.setTlxImpTotCns(GenLecturas.totalConsumo(dataModel.getTlxImpEn(), tarifaDignidad));

        // verificar que hay ley 1886 calcular su importe y guardarlo en detalle facturacion
        double ley1886 = 0;
        if (dataModel.getTlxLeyTag() == 1) {
            ley1886 = GenLecturas.ley1886(context, lectura, dataModel.getTlxCtg());
            ley1886 = DetalleFactura.crearDetalle(context, dataModel.getId(), 195, ley1886);
            dataModel.setTlxLey1886(ley1886);
        }

        double cargoExtraTotal = 0;
        for (Integer itemId : arrayCargos1()) {
            double cargoExtra = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), itemId);
            cargoExtra = DetalleFactura.crearDetalle(context, dataModel.getId(), itemId, cargoExtra);
            cargoExtraTotal += cargoExtra;
        }

        // calculo del importe total del suministro
        double totalSuministro = GenLecturas.totalSuministro(dataModel.getTlxImpTotCns(), dataModel.getTlxLey1886(), cargoExtraTotal);
        dataModel.setTlxImpSum(totalSuministro);
        // calculo de suministro tap y suministro por aseo
        double totalSuministroTap = GenLecturas.totalSuministroTap(dataModel, context, dataModel.getTlxConsFacturado());
        if (totalSuministroTap < 0) {
            Toast.makeText(context, "No hay tarifa para el TAP", Toast.LENGTH_LONG).show();
            return false;
        }
        totalSuministroTap = DetalleFactura.crearDetalle(context, dataModel.getId(), 153, totalSuministroTap);

        double consumoAseo = (double) (dataModel.getTlxPromAseo() + dataModel.getTlxConsFacturado()) / (dataModel.getTlxDivAseo() + 1);
        double totalSuministroAseo = GenLecturas.totalSuministroAseo(dataModel, context, consumoAseo);
        if (totalSuministroAseo == -1) {
            Toast.makeText(context, "No hay tarifa para el aseo", Toast.LENGTH_LONG).show();
            return false;
        }
        totalSuministroAseo = DetalleFactura.crearDetalle(context, dataModel.getId(), 171, totalSuministroAseo);
        dataModel.setTlxImpTap(totalSuministroTap);
        dataModel.setTlxImpAse(totalSuministroAseo);
        double importeTotalFactura = GenLecturas.totalFacturar(dataModel.getTlxImpSum(), dataModel.getTlxImpTap(), dataModel.getTlxImpAse());
        double importeTotalFactura2 = GenLecturas.roundDecimal(importeTotalFactura, 1);
        double diff = importeTotalFactura - importeTotalFactura2;
        DetalleFactura.crearDetalle(context, dataModel.getId(), 999, diff);
        if (dataModel.getTlxImpEnergia() > 0) {
            dataModel.setTlxImpEnergia(GenLecturas.roundDecimal(dataModel.getTlxImpEnergia() - diff, 2));
        } else {
            dataModel.setTlxCarFij(GenLecturas.roundDecimal(dataModel.getTlxCarFij() - diff, 2));
        }
        dataModel.setTlxImpEn(GenLecturas.roundDecimal(dataModel.getTlxImpEn() - diff, 2));
        dataModel.setTlxImpTotCns(GenLecturas.roundDecimal(dataModel.getTlxImpTotCns() - diff, 2));
        dataModel.setTlxImpSum(GenLecturas.roundDecimal(dataModel.getTlxImpSum() - diff, 2));
        dataModel.setTlxImpFac(importeTotalFactura2);

        double cargosTotal2 = 0;
        for (Integer itemId : arrayCargos2()) {
            double cargoExtra = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), itemId);
            cargoExtra = DetalleFactura.crearDetalle(context, dataModel.getId(), itemId, cargoExtra);
            cargosTotal2 += cargoExtra;
        }

        // calculo de importe a facturar
        double importeMesCancelar = dataModel.getTlxImpFac() + cargosTotal2;
        dataModel.setTlxImpMes(importeMesCancelar);
        dataModel.setTlxImpTot(GenLecturas.round(importeMesCancelar + dataModel.getTlxDeuEneI() + dataModel.getTlxDeuAseI()));
        dataModel.setTlxCodCon(getControlCode(context, dataModel));
        if (dataModel.getTlxTipLec() != 5) {
            dataModel.setEstadoLectura(estados_lectura.Leido.ordinal());
        } else {
            dataModel.setEstadoLectura(estados_lectura.Postergado.ordinal());
        }
        dbAdapter.close();
        return true;
    }

    private void printArrays() {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        // agregar cargo fijo y energia al array de impresion
        printTitles.add("Importe por energía");
        printValues.add(GenLecturas.round(dataModel.getTlxImpEnergia() + dataModel.getTlxCarFij()));

        // calculo de potencia para mediana demanda
        if (dataModel.getTlxLeePot() == 1) {
            // agregar el importe por potencia al array de impresion
            printTitles.add(dbAdapter.getItemDescription(41));
            printValues.add(GenLecturas.round(dataModel.getTlxImpPot()));
        }

        // agregar importe por consumo al array de impresion
        printTitles.add("Importe por consumo");
        printValues.add(dataModel.getTlxImpEn());

        // verificar la tarifa dignidad, calcular, guardar el importe en detalle facturacion
        double tarifaDignidad = 0;
        if (dataModel.getTlxDignidad() == 1) {

            // agregar descuento por dignidad al array de impresion
            if (dataModel.getTlxDesTdi() < 0) {
                printTitles.add(dbAdapter.getItemDescription(192));
                printValues.add(dataModel.getTlxDesTdi());
            }
        }

        // agregar consumo total al array de impresion
        printTitles.add("Importe total por consumo");
        printValues.add(dataModel.getTlxImpTotCns());

        // verificar que hay ley 1886 calcular su importe y guardarlo en detalle facturacion
        if (dataModel.getTlxLeyTag() == 1) {
            // agregar ley 1886 al array de impresion
            if (dataModel.getTlxLey1886() < 0) {
                printTitles.add(dbAdapter.getItemDescription(195));
                printValues.add(dataModel.getTlxLey1886());
            }
        }

        // array de ids de items facturacion de detalle facturacion
        // se pueden agregar mas cargos al array usando el item_facturacion_id

        for (Integer itemId : arrayCargos1()) {
            double cargoExtra = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), itemId);
            cargoExtra = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), itemId, cargoExtra);
            if (cargoExtra > 0) {
                printTitles.add(dbAdapter.getItemDescription(itemId));
                printValues.add(cargoExtra);
            }
        }

        // agregar total por el suministro al array de impresion
        printTitles.add("Importe total por el suministro");
        printValues.add(dataModel.getTlxImpSum());

        for (Integer itemId : arrayCargos2()) {
            double cargoExtra = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), itemId);
            cargoExtra = DetalleFactura.crearDetalle(getContext(), dataModel.getId(), itemId, cargoExtra);
            if (cargoExtra > 0) {
                printBottomTitles.add(dbAdapter.getItemDescription(itemId));
                printBottomValues.add(cargoExtra);
            }
        }

        dbAdapter.close();
    }

    private static ArrayList<Integer> arrayCargos1() {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(461); // intereses por mora
        integers.add(181); // cargo por conexion
        integers.add(186); // cargo por reconexion
        integers.add(185); // cargo por rehabilitacion
        return integers;
    }

    private static ArrayList<Integer> arrayCargos2() {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(427); // deposito de garantia
        return integers;
    }

    /**
     * Este metodo oculta las vistas dependiendo a los estados de lectura y muestra el estado en pantalla
     */
    private void hidingViews() {
        String impaviString = "";
        if (dataModel.getTlxImpAvi() == 0) {
            impaviString = "No Impreso";
        } else {
            impaviString = "Impreso";
        }
        buttonObs.setEnabled(false);
        buttonObs.setVisibility(View.GONE);
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
            inputPreNue1.setEnabled(false);
            inputPreNue2.setEnabled(false);
            inputPreNue3.setEnabled(false);
            inputPreNue4.setEnabled(false);
        }
        if (dataModel.getEstadoLectura() == 1) {
            estadoMedidor.setText(estados_lectura.Leido.name() + " - " + impaviString);
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
            buttonConfirm.setText(R.string.re_print);
            buttonObsImped.setEnabled(false);
            buttonObsImped.setTextColor(getResources().getColor(R.color.colorDisableImped));
            buttonPostergar.setText(R.string.postergar_impresion);
            inputPotenciaReading.setEnabled(false);
            inputPreNue1.setEnabled(false);
            inputPreNue2.setEnabled(false);
            inputPreNue3.setEnabled(false);
            inputPreNue4.setEnabled(false);
            if (dataModel.getTlxImpAvi() == 0) {
                buttonConfirm.setEnabled(false);
            }
        } else {
            buttonConfirm.setEnabled(false);
            buttonPostergar.setEnabled(false);
            buttonObsImped.setEnabled(false);
            buttonObsImped.setTextColor(getResources().getColor(R.color.colorDisableImped));
            estadoMedidor.setText(estados_lectura.Postergado.name() + " - " + impaviString);
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
            buttonConfirm.setEnabled(false);
            buttonConfirm.setText(R.string.confirmar);
            buttonPostergar.setText(R.string.postergar_lectura);
        }
    }

    /**
     * Este metodo guarda los datos de lectura y las observaciones en la base de datos
     */
    private void saveLectura() {
        saveDataModel(getContext(), dataModel);

        saveAutoObs();

        onFragmentListener.onTabListener();
        Map<String, ?> all = PreferenceManager.getDefaultSharedPreferences(getActivity()).getAll();
        JSONObject jsonObject = new JSONObject(all);
        FileUtils.exportDB(getActivity(), jsonObject.toString(), new FileUtils.FileUtilsCallback() {
            @Override
            public void suceess() {
            }

            @Override
            public void error() {
                Snackbar.make(inputPotenciaReading, "Error al exportar a SD", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void noSD() {
                Snackbar.make(inputPotenciaReading, "No se encuentra una tarjeta SD", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static void saveDataModel(Context context, DataModel dataModel) {
        DBAdapter dbAdapter = new DBAdapter(context);

        ContentValues cv = new ContentValues();
        Calendar calendar = Calendar.getInstance();

        cv.put(DataModel.Columns.TlxHorLec.name(), StringUtils.getHumanHour(calendar.getTime()));
        cv.put(DataModel.Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());
        cv.put(DataModel.Columns.TlxImpEn.name(), dataModel.getTlxImpEn());
        cv.put(DataModel.Columns.TlxConsumo.name(), dataModel.getTlxConsumo());
        cv.put(DataModel.Columns.TlxConsFacturado.name(), dataModel.getTlxConsFacturado());
        cv.put(DataModel.Columns.TlxImpTot.name(), dataModel.getTlxImpTot());
        cv.put(DataModel.Columns.TlxImpPot.name(), dataModel.getTlxImpPot());
        cv.put(DataModel.Columns.TlxImpEnergia.name(), dataModel.getTlxImpEnergia());

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

        cv.put(DataModel.Columns.TlxImpTotCns.name(), dataModel.getTlxImpTotCns());
        cv.put(DataModel.Columns.TlxImpSum.name(), dataModel.getTlxImpSum());
        cv.put(DataModel.Columns.TlxImpFac.name(), dataModel.getTlxImpFac());
        cv.put(DataModel.Columns.TlxImpMes.name(), dataModel.getTlxImpMes());
        cv.put(DataModel.Columns.TlxCarFij.name(), dataModel.getTlxCarFij());
        cv.put(DataModel.Columns.TlxImpTap.name(), dataModel.getTlxImpTap());
        cv.put(DataModel.Columns.TlxImpAse.name(), dataModel.getTlxImpAse());
        cv.put(DataModel.Columns.TlxKwhDev.name(), dataModel.getTlxKwhDev());
        cv.put(DataModel.Columns.TlxKwhDev2.name(), dataModel.getTlxKwhDev2());
        cv.put(DataModel.Columns.TlxRecordatorio.name(), dataModel.getTlxRecordatorio());
        cv.put(DataModel.Columns.estado_lectura.name(), dataModel.getEstadoLectura());

        cv.put(DataModel.Columns.TlxCodCon.name(), dataModel.getTlxCodCon());
        cv.put(DataModel.Columns.TlxTipLec.name(), dataModel.getTlxTipLec());
        cv.put(DataModel.Columns.TlxImpAvi.name(), dataModel.getTlxImpAvi());

        dbAdapter.updateData(dataModel.getId(), cv);

//        cv = new ContentValues();
//        cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
//        cv.put(DataObs.Columns.observacion_id.name(), obs.getId());
//        dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        dbAdapter.close();
    }

    private void saveAutoObs() {
        obsArray.addAll(autoObs);
        ContentValues cv;
        DBAdapter dbAdapter = new DBAdapter(getContext());
        for (Integer idObs : obsArray) {
            cv = new ContentValues();
            cv.put(DataObs.Columns.general_id.name(), dataModel.getId());
            cv.put(DataObs.Columns.observacion_id.name(), idObs);
            dbAdapter.saveObject(DBHelper.DATA_OBS_TABLE, cv);
        }
        dbAdapter.close();
    }

    /**
     * Este metodo valida si se imprime o no
     */
    private void printFactura(View view) {
        if (dataModel.getTlxTipDem() == 3) {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (dataModel.getTlxImpAvi() == 1) {
            sendPrint();
        } else {
            printValues = new ArrayList<>();
            printTitles = new ArrayList<>();
            printBottomTitles = new ArrayList<>();
            printBottomValues = new ArrayList<>();
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Este metodo obtiene datos extras y de la base de datos para llenar la hoja de impresion
     */
    private void sendPrint() {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        String[] leyenda = dbAdapter.getLeyenda();
        Historico historico = dbAdapter.getHistorico(dataModel.getId());
        if (historico == null) {
            historico = new Historico();
        }
        String garantiaString = dbAdapter.getItemDescription(427);
        String aseoTitle = dbAdapter.getItemDescription(171);
        String tapTitle = dbAdapter.getItemDescription(153);
        String nit = dbAdapter.getParametroTexto(Parametro.Values.nit.name());
        double carDep = dbAdapter.getDetalleFacturaImporte(dataModel.getId(), 427);
        FacturaDosificacion llaveDosificacion = dbAdapter.getLlaveDosificacion();
        dbAdapter.close();
        onFragmentListener.onPrinting(PrintGenerator.creator(
                dataModel,
                printTitles,
                printValues,
                printBottomTitles,
                printBottomValues,
                carDep,
                historico,
                garantiaString,
                aseoTitle,
                tapTitle,
                nit,
                llaveDosificacion.getFechaLimiteEmision(),
                leyenda));
        printValues = new ArrayList<>();
        printTitles = new ArrayList<>();
        printBottomTitles = new ArrayList<>();
        printBottomValues = new ArrayList<>();
    }

    /**
     * Este metodo obtiene datos para la reimpresion y guarda la observacion de impresion
     */
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

                printArrays();

                sendPrint();
            }
        });
        dbAdapter.close();
        reprintDialog.show();
    }

    /**
     * Este metodo valida las vistas para el inicio del fragmento
     */
    private void validSaved() {
        String impaviString;
        if (dataModel.getTlxImpAvi() == 0) {
            impaviString = "No Impreso";
        } else {
            impaviString = "Impreso";
        }
        DBAdapter dbAdapter = new DBAdapter(getContext());
        ArrayList<PrintObsData> printObsDatas = dbAdapter.getPrintObsData(dataModel.getId());
        boolean isEnabledbuttonPostergar = false;
        for (PrintObsData printObsData : printObsDatas) {
            if (printObsData.getOigObs() == 6) {
                isEnabledbuttonPostergar = true;
            }
        }
        if (dataModel.getEstadoLectura() == 1) {
            estadoMedidor.setText(estados_lectura.Leido.name() + " - " + impaviString);
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPrint));
            buttonConfirm.setText(R.string.re_print);
            buttonPostergar.setText(R.string.postergar_impresion);
            if (isEnabledbuttonPostergar) {
                buttonPostergar.setEnabled(false);
            }
            inputReading.setEnabled(false);
            buttonObsAdd.setEnabled(false);
            buttonObs.setEnabled(false);
            buttonObs.setVisibility(View.GONE);
            inputObsCode.setEnabled(false);
            inputReading.setEnabled(false);
            inputReading.setText(String.valueOf(dataModel.getTlxNvaLec()));
            buttonObsImped.setEnabled(false);
            buttonObsImped.setTextColor(getResources().getColor(R.color.colorDisableImped));
            fillObsArray();
        } else if (dataModel.getEstadoLectura() == 2) {
            buttonObs.setEnabled(false);
            buttonObs.setVisibility(View.GONE);
            inputObsCode.setEnabled(false);
            inputReading.setEnabled(false);
            buttonConfirm.setEnabled(false);
            buttonObsAdd.setEnabled(false);
            buttonObsImped.setEnabled(false);
            buttonObsImped.setTextColor(getResources().getColor(R.color.colorDisableImped));
            buttonPostergar.setEnabled(false);
            estadoMedidor.setText(estados_lectura.Postergado.name() + " - " + impaviString);
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
            fillObsArray();
        } else if (dataModel.getEstadoLectura() == 3) {
            estadoMedidor.setText("Postergado Temp.");
            buttonPostergar.setEnabled(false);
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPostponed));
        } else {
            estadoMedidor.setText(estados_lectura.Pendiente.name());
            estadoMedidor.setTextColor(getResources().getColor(R.color.colorPendiente));
        }
        if (dataModel.getTlxLeePot() == 1 && dataModel.getTlxPotLei() > 0) {
            inputPotenciaReading.setText(String.valueOf(dataModel.getTlxPotLei()));
        }
        if (dataModel.getEstadoLectura() != 0 && dataModel.getEstadoLectura() != 3) {
            if (dataModel.getTlxImpAvi() == 0) {
                buttonConfirm.setEnabled(false);
                buttonObsImped.setEnabled(false);
                buttonObsImped.setTextColor(getResources().getColor(R.color.colorDisableImped));
                buttonObsAdd.setEnabled(false);
                buttonConfirm.setText(R.string.confirmar);
                buttonPostergar.setEnabled(false);
                buttonPostergar.setText(R.string.postergar_lectura);
            }
        }
    }

    private void fillObsArray() {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        obsArray = dbAdapter.getObsByCli(dataModel.getId());
        notifiDataContainer();
        dbAdapter.close();
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

    /**
     * Interface para comunicarse con la actividad
     */
    public interface OnFragmentListener {
        void onTabListener();

        void onPrinting(String srcToPrint);

        void onAjusteOrden(int idData);

        void onNextPage();
    }

    /**
     * Este metodo inicializa la vista para gran demanda
     *
     * @param view para obtener vistas
     */
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

            if (dataModel.getEstadoLectura() != estados_lectura.Pendiente.ordinal()
                    && dataModel.getEstadoLectura() != estados_lectura.PostergadoTmp.ordinal()) {
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

    /**
     * Este metodo hace la correccion de digitos cuando hay decimales en la lectura del medidor
     *
     * @param nuevaLectura lectura en kwh
     * @param decimales    decimales del medidor
     * @return un entero con los digitos corregidos
     */
    public static int correccionDeDigitos(int nuevaLectura, int decimales) {
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

    /**
     * Metodo para redondear decimales para la correccion de digitos
     *
     * @param as double para redondear
     * @return retorna un nuebo entero
     */
    private static int roundDouble(double as) {
        long factor = (long) Math.pow(10, 0);
        as = as * factor;
        long tmp = Math.round(as);
        int newInt = (int) ((double) tmp / factor);
        return newInt;
    }

    /**
     * Este metodo genera el codigo de control para la factura
     *
     * @return Un string con el codigo de control generado
     */
    public static String getControlCode(Context context, DataModel dataModel) {
        ControlCode controlCode = new ControlCode();
        DBAdapter dbAdapter = new DBAdapter(context);
        FacturaDosificacion llaveDosificacion = dbAdapter.getLlaveDosificacion();
        dbAdapter.close();
        return controlCode.generate(llaveDosificacion.getNumeroAutorizacion(),
                dataModel.getTlxFacNro(),
                String.valueOf(dataModel.getTlxCliNit()),
                dataModel.getTlxFecEmi().replace("-", ""),
                String.valueOf(GenLecturas.roundDecimal(dataModel.getTlxImpSum(), 0)),
                llaveDosificacion.getLlaveDosificacion());
    }

    public enum estados_lectura {
        Pendiente, Leido, Postergado, PostergadoTmp
    }

    public enum estados_cliente {
        Conectado, Cortado, Suspendido
    }

    private String pad(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return "" + value;
    }

    public static int correccionPotencia(int lecturaPotencia, int decimales) {
        if (decimales == 0) {
            return (lecturaPotencia);
        }
        String strlectura = String.valueOf(lecturaPotencia);
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

    private boolean obsLec(DBAdapter dbAdapter, int obsCod) {
        Obs obs1 = Obs.fromCursor(dbAdapter.getObs(obsCod));
        if (obs1.getObsLec() == 5) {
            return true;
        }
        return false;
    }

    private void notifiDataContainer() {
        linearLayoutObs.removeAllViews();
        DBAdapter dbAdapter = new DBAdapter(getContext());
        for (Integer idObs : obsArray) {
            if (idObs != 104) {
                Cursor cursor = dbAdapter.getObs(idObs);
                Obs obs = Obs.fromCursor(cursor);
                cursor.close();
                View inflate = LayoutInflater.from(getContext()).inflate(R.layout.item_single_choice, null);
                TextView itemCode = (TextView) inflate.findViewById(R.id.item_code);
                TextView itemTitle = (TextView) inflate.findViewById(R.id.item_title);
                itemCode.setText("" + obs.getId());
                itemTitle.setText(obs.getObsDes());
                linearLayoutObs.addView(inflate);
            }
        }
        dbAdapter.close();
    }

    private boolean isConsumoElevado(int lecturaKwh, int categoriaId, int consumoPro) {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        double porcentaje = dbAdapter.getPorcentaje(categoriaId, consumoPro);
        if (porcentaje == 0){
            return false;
        }
        return lecturaKwh > (consumoPro + consumoPro * (porcentaje / 100));
    }
}
