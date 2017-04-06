package com.solunes.endeapp.dataset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.solunes.endeapp.R;
import com.solunes.endeapp.models.DataObs;
import com.solunes.endeapp.models.DetalleFactura;
import com.solunes.endeapp.models.FacturaDosificacion;
import com.solunes.endeapp.models.Historico;
import com.solunes.endeapp.models.ItemFacturacion;
import com.solunes.endeapp.models.LimitesMaximos;
import com.solunes.endeapp.models.MedEntreLineas;
import com.solunes.endeapp.models.Obs;
import com.solunes.endeapp.models.Parametro;
import com.solunes.endeapp.models.PrintObs;
import com.solunes.endeapp.models.PrintObsData;
import com.solunes.endeapp.models.Resultados;
import com.solunes.endeapp.models.Tarifa;
import com.solunes.endeapp.models.TarifaAseo;
import com.solunes.endeapp.models.TarifaTap;
import com.solunes.endeapp.models.User;
import com.solunes.endeapp.utils.Encrypt;


/**
 * Esta clase hace la creacion de las tablas de la base de datos
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    // este es el nombre de la base de datos
    public static final String DATABASE_NAME = "endeapp.db";
    // este es el numero de version de la base de datos,
    // cuando se hace un cambio en la base de datos se debe incrementar el numero
    private static final int DATABASE_VERSION = 29;

    // nombres de las tablas de la base de datos

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
    public static final String DETALLE_FACTURA_TABLE = "detalle_factura_table";
    public static final String LIMITES_MAXIMOS_TABLE = "limites_maximos_table";
    public static final String RESULTADOS_TABLE = "resultados_table";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Aqui se crean las tablas de la base de datos
     * Tambien se guarda un usuario para el admin
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE + " (" +
                User.Columns.LecId.name() + " integer, " +
                User.Columns.LecNom.name() + " text, " +
                User.Columns.LecCod.name() + " text, " +
                User.Columns.LecPas.name() + " text, " +
                User.Columns.LecNiv.name() + " integer, " +
                User.Columns.LecAsi.name() + " integer, " +
                User.Columns.LecAct.name() + " integer, " +
                User.Columns.AreaCod.name() + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TARIFA_TABLE + " (" +
                Tarifa.Columns.id.name() + " integer, " +
                Tarifa.Columns.categoria_tarifa_id.name() + " integer, " +
                Tarifa.Columns.item_facturacion_id.name() + " integer, " +
                Tarifa.Columns.kwh_desde.name() + " integer, " +
                Tarifa.Columns.kwh_hasta.name() + " integer, " +
                Tarifa.Columns.importe.name() + " numeric)");

        sqLiteDatabase.execSQL("CREATE TABLE " + OBS_TABLE + " (" +
                Obs.Columns.id.name() + " integer, " +
                Obs.Columns.ObsDes.name() + " text, " +
                Obs.Columns.ObsTip.name() + " integer, " +
                Obs.Columns.ObsInd.name() + " integer, " +
                Obs.Columns.ObsLec.name() + " integer, " +
                Obs.Columns.ObsFac.name() + " integer, " +
                Obs.Columns.ObsCond.name() + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + DATA_TABLE + " (" +
                "id INTEGER, " +
                "TlxRem integer, " +
                "TlxAre integer, " +
                "TlxRutO integer, " +
                "TlxRutA integer, " +
                "TlxAno integer, " +
                "TlxMes integer, " +
                "TlxCli integer, " +
                "TlxDav integer, " +
                "TlxEstCli integer, " +
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
                "TlxPromAseo integer, " +
                "TlxDivAseo integer, " +
                "TlxImpTotCns decimal(15, 2), " +
                "TlxImpSum decimal(15, 2), " +
                "TlxImpFac decimal(15, 2), " +
                "TlxImpMes decimal(15, 2), " +
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
                "TlxCliNit text, " +
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
                "TlxEntEne integer, " +
                "TlxDecEne integer, " +
                "TlxEntPot integer, " +
                "TlxDecPot integer, " +
                "TlxDemPot text, " +
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
                "TlxImpEnergia decimal(15, 2), " +
                "estado_lectura integer, " +
                "enviado integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + HISTORICO_TABLE + " (" +
                Historico.Columns.id.name()+" integer," +
                Historico.Columns.general_id.name()+" integer," +
                Historico.Columns.ConMes01.name()+" text," +
                Historico.Columns.ConKwh01.name()+" integer," +
                Historico.Columns.ConMes02.name()+" text," +
                Historico.Columns.ConKwh02.name()+" integer," +
                Historico.Columns.ConMes03.name()+" text," +
                Historico.Columns.ConKwh03.name()+" integer," +
                Historico.Columns.ConMes04.name()+" text," +
                Historico.Columns.ConKwh04.name()+" integer," +
                Historico.Columns.ConMes05.name()+" text," +
                Historico.Columns.ConKwh05.name()+" integer," +
                Historico.Columns.ConMes06.name()+" text," +
                Historico.Columns.ConKwh06.name()+" integer," +
                Historico.Columns.ConMes07.name()+" text," +
                Historico.Columns.ConKwh07.name()+" integer," +
                Historico.Columns.ConMes08.name()+" text," +
                Historico.Columns.ConKwh08.name()+" integer," +
                Historico.Columns.ConMes09.name()+" text," +
                Historico.Columns.ConKwh09.name()+" integer," +
                Historico.Columns.ConMes10.name()+" text," +
                Historico.Columns.ConKwh10.name()+" integer," +
                Historico.Columns.ConMes11.name()+" text," +
                Historico.Columns.ConKwh11.name()+" integer," +
                Historico.Columns.ConMes12.name()+" text," +
                Historico.Columns.ConKwh12.name()+" integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + DATA_OBS_TABLE + " (" +
                DataObs.Columns.id.name() + " integer," +
                DataObs.Columns.general_id.name() + " integer," +
                DataObs.Columns.observacion_id.name() + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + PARAMETRO_TABLE + " (" +
                Parametro.Columns.id.name() + " integer," +
                Parametro.Columns.codigo.name() + " text," +
                Parametro.Columns.valor.name() + " integer," +
                Parametro.Columns.texto.name() + " text)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ITEM_FACTURACION_TABLE + " (" +
                ItemFacturacion.Columns.id.name() + " integer," +
                ItemFacturacion.Columns.codigo.name() + " integer," +
                ItemFacturacion.Columns.concepto.name() + " integer," +
                ItemFacturacion.Columns.descripcion.name() + " text," +
                ItemFacturacion.Columns.estado.name() + " integer," +
                ItemFacturacion.Columns.credito_fiscal.name() + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + PRINT_OBS_DATA_TABLE + " (" +
                "_id integer PRIMARY KEY," +
                PrintObsData.Columns.general_id.name() + " integer," +
                PrintObsData.Columns.observacion_imp_id.name() + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + PRINT_OBS_TABLE + " (" +
                PrintObs.Columns.id.name() + " integer," +
                PrintObs.Columns.ObiDes.name() + " text)");

        sqLiteDatabase.execSQL("CREATE TABLE " + MED_ENTRE_LINEAS_TABLE + " (" +
                "id integer," +
                MedEntreLineas.Columns.MelRem + " integer," +
                MedEntreLineas.Columns.MelMed + " integer," +
                MedEntreLineas.Columns.MelLec + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + FACTURA_DOSIFICACION_TABLE + " (" +
                FacturaDosificacion.Columns.id.name() + " integer," +
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

        sqLiteDatabase.execSQL("CREATE TABLE " + DETALLE_FACTURA_TABLE + " (" +
                DetalleFactura.Columns.id.name() + " integer," +
                DetalleFactura.Columns.general_id.name() + " integer," +
                DetalleFactura.Columns.item_facturacion_id.name() + " integer," +
                DetalleFactura.Columns.importe.name() + " decimal(15, 2)," +
                DetalleFactura.Columns.imp_redondeo.name() + " decimal(15, 2))");

        sqLiteDatabase.execSQL("CREATE TABLE " + LIMITES_MAXIMOS_TABLE + " (" +
                LimitesMaximos.Columns.id.name() + " integer," +
                LimitesMaximos.Columns.categoria_tarifa_id.name() + " integer," +
                LimitesMaximos.Columns.max_kwh.name() + " integer," +
                LimitesMaximos.Columns.max_bs.name() + " integer)");

        sqLiteDatabase.execSQL("CREATE TABLE " + RESULTADOS_TABLE + " (" +
                Resultados.Columns.id.name() + " integer," +
                Resultados.Columns.general_id.name() + " integer," +
                Resultados.Columns.lectura.name() + " integer," +
                Resultados.Columns.lectura_potencia.name() + " integer," +
                Resultados.Columns.observacion.name() + " integer)");

        // inserts
        // TODO: 25-11-16 encriptacion
        String encriptPass = null;
        try {
            encriptPass = Encrypt.encrypt(context.getResources().getString(R.string.seed), context.getResources().getString(R.string.admin_password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqLiteDatabase.execSQL("INSERT INTO " + USER_TABLE + " VALUES(" +
                "1, " +
                "'" + context.getResources().getString(R.string.admin_nombre) + "', " +
                "'" + context.getResources().getString(R.string.admin_nombre) + "', " +
                "'" + encriptPass + "', " +
                "1, " +
                "1, " +
                "1, " +
                "1)");
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DETALLE_FACTURA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LIMITES_MAXIMOS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RESULTADOS_TABLE);

        onCreate(sqLiteDatabase);
    }
}

