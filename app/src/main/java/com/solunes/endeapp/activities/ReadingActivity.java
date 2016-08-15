package com.solunes.endeapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.solunes.endeapp.Constants;
import com.solunes.endeapp.R;
import com.solunes.endeapp.utils.GenLecturas;

public class ReadingActivity extends AppCompatActivity {

    private static final String TAG = "ReadingActivity";

    private EditText inputReading;
    private Button buttonCalc;
    private TextView labelEnergiaFacturada;
    private TextView labelSubtotal;
    private TextView labelImporteConsumo;
    private TextView labelTarifaDignidad;
    private TextView labelLey1886;
    private TextView labelTotalConsumo;
    private TextView labelTotalSuministro;
    private TextView labelTotalSuministroTap;
    private TextView labelTotalFacturar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        labelEnergiaFacturada = (TextView) findViewById(R.id.label_energia_facturada);
        labelSubtotal = (TextView) findViewById(R.id.label_subtotal);
        labelImporteConsumo = (TextView) findViewById(R.id.label_importe_consumo);
        labelTarifaDignidad = (TextView) findViewById(R.id.label_tarifa_dignidad);
        labelLey1886 = (TextView) findViewById(R.id.label_ley_1886);
        labelTotalConsumo = (TextView) findViewById(R.id.label_total_consumo);
        labelTotalSuministro = (TextView) findViewById(R.id.label_total_suministro);
        labelTotalSuministroTap = (TextView) findViewById(R.id.label_total_suministro_tap);
        labelTotalFacturar = (TextView) findViewById(R.id.label_total_facturar);
        inputReading = (EditText) findViewById(R.id.input_reading);
        inputReading.setSelected(false);
        buttonCalc = (Button) findViewById(R.id.button_calc);
        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lectura = GenLecturas.lecturaNormal(200, Integer.parseInt(inputReading.getText().toString()));
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
        });
    }
}
