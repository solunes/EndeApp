package com.solunes.endeapp.dataset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.solunes.endeapp.models.FacturaDosificacion;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.TarifaAseo;
import com.solunes.endeapp.models.TarifaTap;

/**
 * Created by jhonlimaster on 11-08-16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "endeapp.db";
    private static final int DATABASE_VERSION = 18;

    public static final String USER_TABLE = "user_table";
    public static final String DATA_TABLE = "data_table";
    public static final String TARIFA_TABLE = "tarifa_table";
    public static final String OBS_TABLE = "obs_table";
    public static final String HISTORICO_TABLE = "historico_table";
    public static final String DATA_OBS_TABLE = "data_obs_table";
    public static final String PARAMETRO_TABLE = "parametro_table";
    public static final String ITEM_FACTURACION_TABLE = "item_facturacion_table";
    public static final String PRINT_OBS_DATA_TABLE = "print_obs_data_table";
    public static final String PRINT_OBS_TABLE = "print_obs_table";
    public static final String MED_ENTRE_LINEAS_TABLE = "med_entre_lineas_table";
    public static final String FACTURA_DOSIFICACION_TABLE = "factura_dosificacion_table";
    public static final String TARIFA_TAP_TABLE = "tarifa_tap_table";
    public static final String TARIFA_ASEO_TABLE = "tarifa_aseo_table";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE + " (" +
                "LecId integer, " +
                "LecNom text, " +
                "LecCod text, " +
                "LecPas text, " +
                "LecNiv integer, " +
                "LecAsi integer, " +
                "LecAct integer, " +
                "AreaCod integer, " +
                "RutaCod integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TARIFA_TABLE + " (" +
                "id integer, " +
                "categoria_tarifa_id integer, " +
                "item_facturacion_id integer, " +
                "kwh_desde integer, " +
                "kwh_hasta integer, " +
                "importe numeric)");

        sqLiteDatabase.execSQL("CREATE TABLE " + OBS_TABLE + " (" +
                "id integer, " +
                "ObsDes text, " +
                "ObsTip integer, " +
                "ObsLec integer, " +
                "ObsFac integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + DATA_TABLE + " (" +
                "id INTEGER, " +
                "TlxRem integer, " +
                "TlxAre integer, " +
                "TlxRutO integer, " +
                "TlxRutA integer, " +
                "TlxAno integer, " +
                "TlxMes integer, " +
                "TlxCli integer, " +
                "TlxOrdTpl integer, " +
                "TlxNom text, " +
                "TlxDir text, " +
                "TlxCtaAnt text, " +
                "TlxCtg integer, " +
                "TlxCtgTap integer, " +
                "TlxCtgAseo integer, " +
                "TlxNroMed text, " +
                "TlxNroDig integer, " +
                "TlxFacMul decimal(15, 2), " +
                "TlxFecAnt numeric, " +
                "TlxFecLec numeric, " +
                "TlxHorLec text, " +
                "TlxUltInd integer, " +
                "TlxConPro integer, " +
                "TlxNvaLec integer, " +
                "TlxTipLec integer, " +
                "TlxSgl text, " +
                "TlxTipDem integer, " +
                "TlxOrdSeq integer, " +
                "TlxImpFac decimal(15, 2), " +
                "TlxImpTap decimal(15, 2), " +
                "TlxImpAse decimal(15, 2), " +
                "TlxCarFij decimal(15, 2), " +
                "TlxImpEn decimal(15, 2), " +
                "TlxImpPot decimal(15, 2), " +
                "TlxDesTdi decimal(15, 2), " +
                "TlxLey1886 decimal(15, 2), " +
                "TlxLeePot integer, " +
                "TlxCotaseo integer, " +
                "TlxTap int, " +
                "TlxPotCon integer, " +
                "TlxPotLei integer, " +
                "TlxPotFac integer, " +
                "TlxCliNit integer, " +
                "TlxFecCor numeric, " +
                "TlxFecVto numeric, " +
                "TlxFecproEmi numeric, " +
                "TlxFecproMed numeric, " +
                "TlxTope integer, " +
                "TlxLeyTag integer, " +
                "TlxDignidad integer, " +
                "TlxTpoTap integer, " +
                "TlxImpTot decimal(15, 2), " +
                "TlxKwhAdi integer, " +
                "TlxImpAvi integer, " +
                "TlxCarFac integer, " +
                "TlxDeuEneC integer, " +
                "TlxDeuEneI decimal(15, 2), " +
                "TlxDeuAseC integer, " +
                "TlxDeuAseI decimal(15, 2), " +
                "TlxFecEmi numeric, " +
                "TlxUltPag numeric, " +
                "TlxEstado integer, " +
                "TlxUltObs text, " +
                "TlxActivi text, " +
                "TlxCiudad text, " +
                "TlxFacNro text, " +
                "TlxNroAut text, " +
                "TlxCodCon text, " +
                "TlxFecLim numeric, " +
                "TlxKwhDev integer, " +
                "TlxUltTipL integer, " +
                "TlxCliNew integer, " +
                "TlxCarCon decimal(15, 2), " +
                "TlxCarRec decimal(15, 2), " +
                "TlxCarDep decimal(15, 2), " +
                "TlxEntEne integer, " +
                "TlxDecEne integer, " +
                "TlxEntPot integer, " +
                "TlxDecPot integer, " +
                "TlxPotFacM integer, " +
                "TlxPotTag integer, " +
                "TlxPreAnt1 text, " +
                "TlxPreAnt2 text, " +
                "TlxPreAnt3 text, " +
                "TlxPreAnt4 text, " +
                "TlxPreNue1 text, " +
                "TlxPreNue2 text, " +
                "TlxPreNue3 text, " +
                "TlxPreNue4 text, " +
                "TlxKwInst integer, " +
                "TlxReactiva integer, " +
                "TlxKwhBajo integer, " +
                "TlxKwhMedio integer, " +
                "TlxKwhAlto integer, " +
                "TlxDemBajo integer, " +
                "TlxFechaBajo text, " +
                "TlxHoraBajo text, " +
                "TlxDemMedio integer, " +
                "TlxFechaMedio text, " +
                "TlxHoraMedio text, " +
                "TlxDemAlto integer, " +
                "TlxFechaAlto text, " +
                "TlxHoraAlto text, " +
                "TlxPerCo3 numeric, " +
                "TlxPerHr3 numeric, " +
                "TlxPerCo2 numeric, " +
                "TlxPerHr2 numeric, " +
                "TlxPerCo1 numeric, " +
                "TlxPerHr1 numeric, " +
                "TlxConsumo numeric, " +
                "TlxPerdidas numeric, " +
                "TlxConsFacturado numeric, " +
                "TlxDebAuto text, " +
                "TlxRecordatorio text, " +
                "estado_lectura integer, " +
                "enviado integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + HISTORICO_TABLE + " (" +
                "id integer," +
                "general_id integer," +
                "ConMes01 text," +
                "ConKwh01 integer," +
                "ConMes02 text," +
                "ConKwh02 integer," +
                "ConMes03 text," +
                "ConKwh03 integer," +
                "ConMes04 text," +
                "ConKwh04 integer," +
                "ConMes05 text," +
                "ConKwh05 integer," +
                "ConMes06 text," +
                "ConKwh06 integer," +
                "ConMes07 text," +
                "ConKwh07 integer," +
                "ConMes08 text," +
                "ConKwh08 integer," +
                "ConMes09 text," +
                "ConKwh09 integer," +
                "ConMes10 text," +
                "ConKwh10 integer," +
                "ConMes11 text," +
                "ConKwh11 integer," +
                "ConMes12 text," +
                "ConKwh12 integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + DATA_OBS_TABLE + " (" +
                "id integer," +
                "general_id integer," +
                "observacion_id integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + PARAMETRO_TABLE + " (" +
                Parametro.Columns.id.name() + " integer," +
                Parametro.Columns.codigo.name() + " text," +
                Parametro.Columns.valor.name() + " integer," +
                Parametro.Columns.texto.name() + " text)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ITEM_FACTURACION_TABLE + " (" +
                "id integer," +
                "codigo  integer," +
                "concepto integer," +
                "descripcion text," +
                "estado integer," +
                "credito_fiscal integer)");


        sqLiteDatabase.execSQL("CREATE TABLE " + PRINT_OBS_DATA_TABLE + " (" +
                "_id integer PRIMARY KEY," +
                "general_id integer," +
                "observacion_imp_id integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + PRINT_OBS_TABLE + " (" +
                "id integer," +
                "ObiDes text)");

        sqLiteDatabase.execSQL("CREATE TABLE " + MED_ENTRE_LINEAS_TABLE + " (" +
                "id integer," +
                "MelRem integer," +
                "MelAre integer," +
                "MelMed integer," +
                "MelLec integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + FACTURA_DOSIFICACION_TABLE + " (" +
                FacturaDosificacion.Columns.id.name() + " integer," +
                FacturaDosificacion.Columns.area_id.name() + " integer," +
                FacturaDosificacion.Columns.numero.name() + " integer," +
                FacturaDosificacion.Columns.comprobante.name() + " integer," +
                FacturaDosificacion.Columns.fecha_inicial.name() + " text," +
                FacturaDosificacion.Columns.fecha_limite_emision.name() + " text," +
                FacturaDosificacion.Columns.numero_autorizacion.name() + " integer," +
                FacturaDosificacion.Columns.llave_dosificacion.name() + " text," +
                FacturaDosificacion.Columns.numero_factura.name() + " integer," +
                FacturaDosificacion.Columns.estado.name() + " integer," +
                FacturaDosificacion.Columns.leyenda1.name() + " text," +
                FacturaDosificacion.Columns.leyenda2.name() + " text," +
                FacturaDosificacion.Columns.actividad_economica.name() + " text)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TARIFA_TAP_TABLE + " (" +
                TarifaTap.Columns.id.name() + " integer," +
                TarifaTap.Columns.area_id.name() + " integer," +
                TarifaTap.Columns.categoria_tarifa_id.name() + " integer," +
                TarifaTap.Columns.anio.name() + " integer," +
                TarifaTap.Columns.mes.name() + " integer," +
                TarifaTap.Columns.valor.name() + " decimal(15, 2))");

        sqLiteDatabase.execSQL("CREATE TABLE " + TARIFA_ASEO_TABLE + " (" +
                TarifaAseo.Columns.id.name() + " integer," +
                TarifaAseo.Columns.categoria_tarifa_id.name() + " integer," +
                TarifaAseo.Columns.anio.name() + " integer," +
                TarifaAseo.Columns.mes.name() + " integer," +
                TarifaAseo.Columns.kwh_desde.name() + " integer," +
                TarifaAseo.Columns.kwh_hasta.name() + " integer," +
                TarifaAseo.Columns.importe.name() + " decimal(15, 2))");

        // inserts
        sqLiteDatabase.execSQL("INSERT INTO " + USER_TABLE + " VALUES(" +
                "1, " +
                "'admin', " +
                "'admin', " +
                "'1234', " +
                "1, " +
                "1, " +
                "1, " +
                "1, " +
                "12345)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TARIFA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OBS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORICO_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATA_OBS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PARAMETRO_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ITEM_FACTURACION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRINT_OBS_DATA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PRINT_OBS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MED_ENTRE_LINEAS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FACTURA_DOSIFICACION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TARIFA_TAP_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TARIFA_ASEO_TABLE);

        onCreate(sqLiteDatabase);
    }
}

