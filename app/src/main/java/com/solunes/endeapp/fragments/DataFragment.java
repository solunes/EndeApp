package com.solunes.endeapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.DataModel;
import com.solunes.endeapp.utils.GenLecturas;

/**
 * Created by jhonlimaster on 01-12-15.
 */
public class DataFragment extends Fragment {
    private static final String TAG = "PostFragment";
    private static final String KEY_POSITION = "position";
    private OnFragmentListener onFragmentListener;

    private EditText inputReading;
    private Button buttonCalc;
    private Button buttonPromedio;
    private TextView labelEnergiaFacturada;
    private TextView labelSubtotal;
    private TextView labelImporteConsumo;
    private TextView labelTarifaDignidad;
    private TextView labelLey1886;
    private TextView labelTotalConsumo;
    private TextView labelTotalSuministro;
    private TextView labelTotalSuministroTap;
    private TextView labelTotalFacturar;
    private Button save;

    private TextView nameData;
    private TextView clientData;

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
        dbAdapter.close();
        setupUI(view, dataModel);
        return view;
    }

    public void setupUI(View view, final DataModel data) {
        nameData = (TextView) view.findViewById(R.id.data_name);
        nameData.setText(data.getTlxNom());
        clientData = (TextView) view.findViewById(R.id.data_client);
        clientData.setText(String.valueOf(data.getTlxCli()));

        labelEnergiaFacturada = (TextView) view.findViewById(R.id.label_energia_facturada);
        labelSubtotal = (TextView) view.findViewById(R.id.label_subtotal);
        labelImporteConsumo = (TextView) view.findViewById(R.id.label_importe_consumo);
        labelTarifaDignidad = (TextView) view.findViewById(R.id.label_tarifa_dignidad);
        labelLey1886 = (TextView) view.findViewById(R.id.label_ley_1886);
        labelTotalConsumo = (TextView) view.findViewById(R.id.label_total_consumo);
        labelTotalSuministro = (TextView) view.findViewById(R.id.label_total_suministro);
        labelTotalSuministroTap = (TextView) view.findViewById(R.id.label_total_suministro_tap);
        labelTotalFacturar = (TextView) view.findViewById(R.id.label_total_facturar);
        inputReading = (EditText) view.findViewById(R.id.input_reading);
        inputReading.setSelected(false);
        buttonCalc = (Button) view.findViewById(R.id.button_calc);
        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputReading.getText().toString();
                if (input.isEmpty()){
                    return;
                }
                int lectura = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), Integer.parseInt(input));
                calculo(lectura);
            }
        });
        buttonPromedio = (Button) view.findViewById(R.id.button_promedio);
        buttonPromedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculo(dataModel.getTlxConPro());
                buttonCalc.setEnabled(false);
            }
        });
        save = (Button) view.findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBAdapter dbAdapter = new DBAdapter(getContext());
                ContentValues cv = new ContentValues();
                cv.put(DataModel.Columns.save_state.name(), 1);
                dbAdapter.updateData(dataModel.getTlxCli(), cv);
                dbAdapter.close();
                onFragmentListener.onTabListener();
            }
        });
    }

    public interface OnFragmentListener {
        void onTabListener();
    }

    private void calculo(int lectura){
        labelEnergiaFacturada.setText("Energia facturada: " + lectura);
        labelSubtotal.setText("Subtotal: " + GenLecturas.subTotal(lectura));

        double importeConsumo = GenLecturas.importeConsumo(lectura);
        labelImporteConsumo.setText("importe por consumo: " + importeConsumo);

        double tarifaDignidad = GenLecturas.tarifaDignidad(lectura, importeConsumo);
        labelTarifaDignidad.setText("tarifa dignidad: " + tarifaDignidad);

        double ley1886 = GenLecturas.ley1886(lectura);
        labelLey1886.setText("Ley 1886: " + ley1886);

        double totalConsumo = GenLecturas.totalConsumo(importeConsumo, tarifaDignidad, ley1886);
        labelTotalConsumo.setText("Importe total por consumo: " + totalConsumo);

        double totalSuministro = GenLecturas.totalSuministro(totalConsumo);
        labelTotalSuministro.setText("Importe total por el suminstro: " + totalSuministro);

        double totalSuministroTap = GenLecturas.totalSuministroTap(totalSuministro, lectura);
        labelTotalSuministroTap.setText("Total suministro mas TAP: " + totalSuministroTap);

        double totalFacturar = GenLecturas.totalFacturar(totalSuministroTap);
        labelTotalFacturar.setText("Importe total a facturar: " + totalFacturar);
    }
}
