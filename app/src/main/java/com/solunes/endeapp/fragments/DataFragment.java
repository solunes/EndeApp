package com.solunes.endeapp.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.solunes.endeapp.R;
import com.solunes.endeapp.dataset.DBAdapter;
import com.solunes.endeapp.models.DataModel;
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
    private TextView labelEnergiaFacturada;
    private TextView labelImporteConsumo;
    private TextView labelTotalConsumo;
    private TextView labelTotalSuministro;
    private TextView labelTotalFacturar;

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
        Log.e(TAG, "onCreateView: tipo de lectura " + dataModel.getTlxTipLec() + " ---- ultimo tipo " + dataModel.getTlxUltTipL());
        dbAdapter.close();
        setupUI(view, dataModel);
        validSaved();
        return view;
    }

    public void setupUI(View view, final DataModel data) {
        nameData = (TextView) view.findViewById(R.id.data_name);
        nameData.setText(data.getTlxNom());
        clientData = (TextView) view.findViewById(R.id.data_client);
        clientData.setText(String.valueOf(data.getTlxCli()));

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
                calculo(lectura);
                saveLectura(view);
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
        Log.e(TAG, "saveLectura: "+StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT,calendar.getTime()) );
        cv.put(DataModel.Columns.TlxHorLec.name(), StringUtils.getHumanHour(calendar.getTime()));
        cv.put(DataModel.Columns.TlxFecEmi.name(), StringUtils.formateDateFromstring(StringUtils.DATE_FORMAT,calendar.getTime()));
        cv.put(DataModel.Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());

        dbAdapter.updateData(dataModel.getTlxCli(), cv);
        dbAdapter.close();
        onFragmentListener.onTabListener();
        if (dataModel.getTlxTipLec() == 4){
            Snackbar.make(view, "No se imprime factura", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, "Imprimiendo...", Snackbar.LENGTH_LONG).show();
        }
    }

    private void calculo(int lectura) {
        labelEnergiaFacturada.setText("Energia facturada: " + lectura);

        double importeConsumo = GenLecturas.importeConsumo(lectura);
        labelImporteConsumo.setText("importe por consumo: " + importeConsumo);

        double tarifaDignidad = GenLecturas.tarifaDignidad(lectura, importeConsumo);
        dataModel.setTlxDesTdi(tarifaDignidad);

        double ley1886 = GenLecturas.ley1886(lectura);
        dataModel.setTlxLey1886(ley1886);

        double totalConsumo = GenLecturas.totalConsumo(importeConsumo, tarifaDignidad, ley1886);
        labelTotalConsumo.setText("Importe total por consumo: " + totalConsumo);

        double totalSuministro = GenLecturas.totalSuministro(totalConsumo);
        labelTotalSuministro.setText("Importe total por el suminstro: " + totalSuministro);

        double totalSuministroTap = GenLecturas.totalSuministroTap(totalSuministro, lectura);

        double totalFacturar = GenLecturas.totalFacturar(totalSuministroTap);
        labelTotalFacturar.setText("Importe total a facturar: " + totalFacturar);
    }

    private void validSaved(){
        if (dataModel.getTlxNvaLec() > 0){
            inputReading.setText(String.valueOf(dataModel.getTlxNvaLec()));
            int lecturaNormal = GenLecturas.lecturaNormal(dataModel.getTlxUltInd(), dataModel.getTlxNvaLec());
            labelEnergiaFacturada.setText("Energia facturada: " + lecturaNormal);
            buttonConfirm.setEnabled(false);
        }
    }
}
