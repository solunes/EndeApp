package com.solunes.endeapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.utils.GenLecturas;
import com.solunes.endeapp.utils.StringUtils;

import java.util.Calendar;

/**
 * Created by jhonlimaster on 01-12-15.
 */
public class DataFragment extends Fragment {
    private static final String TAG = "PostFragment";
    private static final String KEY_POSITION = "position";
    private OnFragmentListener onFragmentListener;

    private EditText inputReading;
    private Button buttonConfirm;
    private Button buttonObs;
    private TextView labelEnergiaFacturada;
    private TextView labelImporteConsumo;
    private TextView labelTotalConsumo;
    private TextView labelTotalSuministro;
    private TextView labelTotalFacturar;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        Bundle arguments = getArguments();
        DBAdapter dbAdapter = new DBAdapter(getContext());
        dataModel = dbAdapter.getData(arguments.getInt(KEY_POSITION));
        Log.e(TAG, "onCreateView: tipo de lectura " + dataModel.getTlxTipLec() + " ---- ultimo tipo " + dataModel.getTlxUltTipL());
        dbAdapter.close();
        setupUI(view, dataModel);
        validSaved();
        return view;
    }

    public void setupUI(View view, final DataModel data) {
        TextView nameData = (TextView) view.findViewById(R.id.data_name);
        nameData.setText("Nombre: " + data.getTlxNom());
        TextView dataClient = (TextView) view.findViewById(R.id.data_client);
        dataClient.setText("N° Cliente: " + data.getTlxCli());
        TextView adressCliente = (TextView) view.findViewById(R.id.adress_client);
        adressCliente.setText("Direccion: " + data.getTlxDir());
        TextView categoryCliente = (TextView) view.findViewById(R.id.category_client);
        categoryCliente.setText("Categoria: " + data.getTlxSgl());
        TextView medidorCliente = (TextView) view.findViewById(R.id.medidor_client);
        medidorCliente.setText("N° Medidor: " + data.getTlxNroMed());
//        TextView monthCliente = (TextView) view.findViewById(R.id.month_client);
//        monthCliente.setText("Fecha");

        labelEnergiaFacturada = (TextView) view.findViewById(R.id.label_energia_facturada);
        labelImporteConsumo = (TextView) view.findViewById(R.id.label_importe_consumo);
        labelTotalConsumo = (TextView) view.findViewById(R.id.label_total_consumo);
        labelTotalSuministro = (TextView) view.findViewById(R.id.label_total_suministro);
        labelTotalFacturar = (TextView) view.findViewById(R.id.label_total_facturar);
        inputReading = (EditText) view.findViewById(R.id.input_reading);
        inputReading.setSelected(false);
        buttonConfirm = (Button) view.findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputReading.getText().toString();
                if (input.isEmpty()) {
                    return;
                }
                int nuevaLectura = Integer.parseInt(input);
                dataModel.setTlxNvaLec(nuevaLectura);

                // TODO: 02-09-16 validar los tipos de lecturas y si es nuevo cliente

                int lectura = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), nuevaLectura);

                // lectura ajustada
                if (dataModel.getTlxTipLec() == 6) {
                    lectura = lectura - dataModel.getTlxUltInd();
                    if (lectura > 0) {
                        dataModel.setTlxKwhDev(0);
                    } else {
                        dataModel.setTlxKwhDev(lectura);
                        lectura = 0;
                    }
                }
                calculo(lectura);
                saveLectura(view);
            }
        });
        buttonObs = (Button) view.findViewById(R.id.button_obs);
        buttonObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Selecionar una observacion");
                DBAdapter dbAdapter = new DBAdapter(getContext());
                Cursor obs = dbAdapter.getObs();
                String[] stringObs = new String[obs.getCount()];
                for (int i = 0; i < obs.getCount(); i++) {
                    obs.moveToNext();
                    stringObs[i] = Obs.fromCursor(obs).getObsDes();
                }
                alertDialog.setSingleChoiceItems(stringObs, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        Log.e(TAG, "onClick: " + position);
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }

    public interface OnFragmentListener {
        void onTabListener();
    }

    private void saveLectura(View view) {
        DBAdapter dbAdapter = new DBAdapter(getContext());
        ContentValues cv = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        Log.e(TAG, "saveLectura: " + StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT, calendar.getTime()));
        cv.put(DataModel.Columns.TlxHorLec.name(), StringUtils.getHumanHour(calendar.getTime()));
        cv.put(DataModel.Columns.TlxFecEmi.name(), StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT, calendar.getTime()));
        cv.put(DataModel.Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());
        cv.put(DataModel.Columns.TlxImpEn.name(), dataModel.getTlxImpEn());
        cv.put(DataModel.Columns.TlxConsumo.name(), dataModel.getTlxConsumo());
        cv.put(DataModel.Columns.TlxConsFacturado.name(), dataModel.getTlxConsFacturado());
        cv.put(DataModel.Columns.TlxImpTot.name(), dataModel.getTlxImpTot());

        dbAdapter.updateData(dataModel.getTlxCli(), cv);
        dbAdapter.close();
        onFragmentListener.onTabListener();
        if (dataModel.getTlxTipLec() == 4) {
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, "Imprimiendo...", Snackbar.LENGTH_LONG).show();
        }
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
            int lecturaNormal = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), dataModel.getTlxNvaLec());
            labelEnergiaFacturada.setText("Energia facturada: " + lecturaNormal);
            labelImporteConsumo.setText("importe por consumo: " + dataModel.getTlxImpEn());
            labelTotalConsumo.setText("Importe total por consumo: " + dataModel.getTlxConsumo());
            labelTotalSuministro.setText("Importe total por el suminstro: " + dataModel.getTlxConsFacturado());
            labelTotalFacturar.setText("Importe total a facturar: " + dataModel.getTlxImpTot());
        }
    }
}
