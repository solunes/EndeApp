package com.solunes.endeapp.dataset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jhonlimaster on 11-08-16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "endeapp.db";
    private static final int DATABASE_VERSION = 3;
    public static final String USER_TABLE = "user_table";
    public static final String DATA_TABLE = "data_table";
    public static final String TARIFA_TABLE = "tarifa_table";
    public static final String OBS_TABLE = "obs_table";
    public static final String HISTORICO_TABLE = "historico_table";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE + " (" +
                "id integer, " +
                "lecNro integer, " +
                "lecNom text, " +
                "lecCod text, " +
                "lecPas text, " +
                "lecNiv integer, " +
                "lecAsi integer, " +
                "lecAct integer, " +
                "areaCod integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TARIFA_TABLE + " (" +
                "id integer, " +
                "categoria_tarifa_id integer, " +
                "item_facturacion_id integer, " +
                "kwh_desde integer, " +
                "kwh_hasta integer, " +
                "importe numeric)");

        sqLiteDatabase.execSQL("CREATE TABLE " + OBS_TABLE + " (" +
                "ObsCod integer, " +
                "ObsDes text, " +
                "ObsTip integer, " +
                "ObsLec integer, " +
                "ObsFac integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + DATA_TABLE + " (" +
                "_id INTEGER PRIMARY KEY, " +
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
                "TlxCtg text, " +
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
                "TlxTap numeric, " +
                "TlxPotCon integer, " +
                "TlxPotFac integer, " +
                "TlxCliNit numeric, " +
                "TlxFecCor numeric, " +
                "TlxFecVto numeric, " +
                "TlxFecproEmi numeric, " +
                "TlxFecproMed numeric, " +
                "TlxTope decimal(15, 2), " +
                "TlxLeyTag integer, " +
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
                "TlxFacNro numeric, " +
                "TlxNroAut numeric, " +
                "TlxCodCon text, " +
                "TlxFecLim numeric, " +
                "TlxKwhDev integer, " +
                "TlxUltTipL integer, " +
                "TlxCliNew integer, " +
                "TlxEntEne integer, " +
                "TlxEntPot integer, " +
                "TlxPotFacM integer, " +
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
                "save_state integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + HISTORICO_TABLE + " (" +
                "id integer," +
                "ConRem integer," +
                "ConAre integer," +
                "ConCli integer," +
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

        // inserts
        sqLiteDatabase.execSQL("INSERT INTO " + USER_TABLE + " VALUES(" +
                "1, " +
                "null, " +
                "'Administrador', " +
                "'admin', " +
                "'1234', " +
                "1, " +
                "null, " +
                "null, " +
                "null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TARIFA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OBS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORICO_TABLE);

        onCreate(sqLiteDatabase);
    }


}

