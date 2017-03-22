package com.solunes.endeapp.models;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jhonlimaster on 26-08-16.
 */
public class DataModel {
    private static final String TAG = "DataModel";
    private int id;
    private int TlxRem;
    private int TlxAre;
    private int TlxRutO;
    private int TlxRutA;
    private int TlxAno;
    private int TlxMes;
    private int TlxCli;
    private int TlxDav;
    private int TlxEstCli;
    private int TlxOrdTpl;
    private String TlxNom;
    private String TlxDir;
    private String TlxCtaAnt;
    private int TlxCtg;
    private int TlxCtgTap;
    private int TlxCtgAseo;
    private String TlxNroMed;
    private int TlxNroDig;
    private double TlxFacMul;
    private String TlxFecAnt;
    private String TlxFecLec;
    private String TlxHorLec;
    private int TlxUltInd;
    private int TlxConPro;
    private int TlxNvaLec;
    private int TlxTipLec;
    private String TlxSgl;
    private int TlxTipDem;
    private int TlxOrdSeq;
    private int TlxPromAseo;
    private int TlxDivAseo;
    private double TlxImpTotCns;
    private double TlxImpSum;
    private double TlxImpFac;
    private double TlxImpMes;
    private double TlxImpTap;
    private double TlxImpAse;
    private double TlxCarFij;
    private double TlxImpEn;
    private double TlxImpPot;
    private double TlxDesTdi;
    private double TlxLey1886;
    private int TlxLeePot;
    private int TlxCotaseo;
    private int TlxTap;
    private int TlxPotCon;
    private int TlxPotFac;
    private String TlxCliNit;
    private String TlxFecCor;
    private String TlxFecVto;
    private String TlxFecproEmi;
    private String TlxFecproMed;
    private int TlxTope;
    private int TlxLeyTag;
    private int TlxDignidad;
    private int TlxTpoTap;
    private double TlxImpTot;
    private int TlxKwhAdi;
    private int TlxImpAvi;
    private int TlxCarFac;
    private int TlxDeuEneC;
    private double TlxDeuEneI;
    private int TlxDeuAseC;
    private double TlxDeuAseI;
    private String TlxFecEmi;
    private String TlxUltPag;
    private int TlxEstado;
    private String TlxUltObs;
    private String TlxActivi;
    private String TlxCiudad;
    private String TlxFacNro;
    private String TlxNroAut;
    private String TlxCodCon;
    private String TlxFecLim;
    private int TlxKwhDev;
    private int TlxUltTipL;
    private int TlxCliNew;
    private int TlxEntEne;
    private int TlxEntPot;
    private int TlxPotFacM;
    private int TlxPotTag;
    private String TlxPreAnt1;
    private String TlxPreAnt2;
    private String TlxPreAnt3;
    private String TlxPreAnt4;
    private String TlxPreNue1;
    private String TlxPreNue2;
    private String TlxPreNue3;
    private String TlxPreNue4;
    private int TlxKwInst;
    private int TlxReactiva;
    private int TlxKwhBajo;
    private int TlxKwhMedio;
    private int TlxKwhAlto;
    private int TlxDemBajo;
    private String TlxFechaBajo;
    private String TlxHoraBajo;
    private int TlxDemMedio;
    private String TlxFechaMedio;
    private String TlxHoraMedio;
    private int TlxDemAlto;
    private String TlxFechaAlto;
    private String TlxHoraAlto;
    private double TlxPerCo3;
    private double TlxPerHr3;
    private double TlxPerCo2;
    private double TlxPerHr2;
    private double TlxPerCo1;
    private double TlxPerHr1;
    private int TlxConsumo;
    private double TlxPerdidas;
    private int TlxConsFacturado;
    private String TlxDebAuto;
    private int estadoLectura;
    private int enviado;

    private int TlxPotLei;
    private int TlxDecEne;
    private int TlxDecPot;
    private String TlxDemPot;
    private String TlxRecordatorio;
    private double TlxImpEnergia;

    public enum Columns {
        id,
        TlxRem,
        TlxAre,
        TlxRutO,
        TlxRutA,
        TlxAno,
        TlxMes,
        TlxCli,
        TlxDav,
        TlxEstCli,
        TlxOrdTpl,
        TlxNom,
        TlxDir,
        TlxCtaAnt,
        TlxCtg,
        TlxCtgTap,
        TlxCtgAseo,
        TlxNroMed,
        TlxNroDig,
        TlxFacMul,
        TlxFecAnt,
        TlxFecLec,
        TlxHorLec,
        TlxUltInd,
        TlxConPro,
        TlxNvaLec,
        TlxTipLec,
        TlxSgl,
        TlxTipDem,
        TlxOrdSeq,
        TlxPromAseo,
        TlxDivAseo,
        TlxImpTotCns,
        TlxImpSum,
        TlxImpFac,
        TlxImpMes,
        TlxImpTap,
        TlxImpAse,
        TlxCarFij,
        TlxImpEn,
        TlxImpPot,
        TlxDesTdi,
        TlxLey1886,
        TlxLeePot,
        TlxCotaseo,
        TlxTap,
        TlxPotCon,
        TlxPotLei,
        TlxPotFac,
        TlxCliNit,
        TlxFecCor,
        TlxFecVto,
        TlxFecproEmi,
        TlxFecproMed,
        TlxTope,
        TlxLeyTag,
        TlxDignidad,
        TlxTpoTap,
        TlxImpTot,
        TlxKwhAdi,
        TlxImpAvi,
        TlxCarFac,
        TlxDeuEneC,
        TlxDeuEneI,
        TlxDeuAseC,
        TlxDeuAseI,
        TlxFecEmi,
        TlxUltPag,
        TlxEstado,
        TlxUltObs,
        TlxActivi,
        TlxCiudad,
        TlxFacNro,
        TlxNroAut,
        TlxCodCon,
        TlxFecLim,
        TlxKwhDev,
        TlxUltTipL,
        TlxCliNew,
        TlxEntEne,
        TlxDecEne,
        TlxEntPot,
        TlxDecPot,
        TlxDemPot,
        TlxPotFacM,
        TlxPotTag,
        TlxPreAnt1,
        TlxPreAnt2,
        TlxPreAnt3,
        TlxPreAnt4,
        TlxPreNue1,
        TlxPreNue2,
        TlxPreNue3,
        TlxPreNue4,
        TlxKwInst,
        TlxReactiva,
        TlxKwhBajo,
        TlxKwhMedio,
        TlxKwhAlto,
        TlxDemBajo,
        TlxFechaBajo,
        TlxHoraBajo,
        TlxDemMedio,
        TlxFechaMedio,
        TlxHoraMedio,
        TlxDemAlto,
        TlxFechaAlto,
        TlxHoraAlto,
        TlxPerCo3,
        TlxPerHr3,
        TlxPerCo2,
        TlxPerHr2,
        TlxPerCo1,
        TlxPerHr1,
        TlxConsumo,
        TlxPerdidas,
        TlxConsFacturado,
        TlxDebAuto,
        TlxRecordatorio,
        TlxImpEnergia,
        estado_lectura,
        enviado
    }

    public enum EstadoEnviado{
        no_enviado, enviado
    }

    /**
     * Este metodo devuelve el tipo de lectura
     * @param idTipo id del tipo de lectura
     */
    public static String getTipoLectura(int idTipo) {
        if (idTipo == 0) {
            return "Lectura normal";
        } else if (idTipo == 3) {
            return "Lectura promedio";
        } else if (idTipo == 4) {
            return "Último índice";
        } else if (idTipo == 5) {
            return "No existe";
        } else if (idTipo == 6) {
            return "Lectura ajustada";
        } else if (idTipo == 9 ){
            return "Consumo estimado";
        }
        return null;
    }

    public static DataModel fromCursor(Cursor cursor) {
        DataModel dataModel = new DataModel();
        dataModel.setId(cursor.getInt(Columns.id.ordinal()));
        dataModel.setTlxRem(cursor.getInt(Columns.TlxRem.ordinal()));
        dataModel.setTlxAre(cursor.getInt(Columns.TlxAre.ordinal()));
        dataModel.setTlxRutO(cursor.getInt(Columns.TlxRutO.ordinal()));
        dataModel.setTlxRutA(cursor.getInt(Columns.TlxRutA.ordinal()));
        dataModel.setTlxAno(cursor.getInt(Columns.TlxAno.ordinal()));
        dataModel.setTlxMes(cursor.getInt(Columns.TlxMes.ordinal()));
        dataModel.setTlxCli(cursor.getInt(Columns.TlxCli.ordinal()));
        dataModel.setTlxDav(cursor.getInt(Columns.TlxDav.ordinal()));
        dataModel.setTlxEstCli(cursor.getInt(Columns.TlxEstCli.ordinal()));
        dataModel.setTlxOrdTpl(cursor.getInt(Columns.TlxOrdTpl.ordinal()));
        dataModel.setTlxNom(cursor.getString(Columns.TlxNom.ordinal()));
        dataModel.setTlxDir(cursor.getString(Columns.TlxDir.ordinal()));
        dataModel.setTlxCtaAnt(cursor.getString(Columns.TlxCtaAnt.ordinal()));
        dataModel.setTlxCtg(cursor.getInt(Columns.TlxCtg.ordinal()));
        dataModel.setTlxNroMed(cursor.getString(Columns.TlxNroMed.ordinal()));
        dataModel.setTlxNroDig(cursor.getInt(Columns.TlxNroDig.ordinal()));
        dataModel.setTlxFacMul(cursor.getDouble(Columns.TlxFacMul.ordinal()));
        dataModel.setTlxFecAnt(cursor.getString(Columns.TlxFecAnt.ordinal()));
        dataModel.setTlxFecLec(cursor.getString(Columns.TlxFecLec.ordinal()));
        dataModel.setTlxHorLec(cursor.getString(Columns.TlxHorLec.ordinal()));
        dataModel.setTlxUltInd(cursor.getInt(Columns.TlxUltInd.ordinal()));
        dataModel.setTlxConPro(cursor.getInt(Columns.TlxConPro.ordinal()));
        dataModel.setTlxNvaLec(cursor.getInt(Columns.TlxNvaLec.ordinal()));
        dataModel.setTlxTipLec(cursor.getInt(Columns.TlxTipLec.ordinal()));
        dataModel.setTlxSgl(cursor.getString(Columns.TlxSgl.ordinal()));
        dataModel.setTlxOrdSeq(cursor.getInt(Columns.TlxOrdSeq.ordinal()));
        dataModel.setTlxPromAseo(cursor.getInt(Columns.TlxPromAseo.ordinal()));
        dataModel.setTlxDivAseo(cursor.getInt(Columns.TlxDivAseo.ordinal()));
        dataModel.setTlxImpTotCns(cursor.getDouble(Columns.TlxImpTotCns.ordinal()));
        dataModel.setTlxImpSum(cursor.getDouble(Columns.TlxImpSum.ordinal()));
        dataModel.setTlxImpFac(cursor.getDouble(Columns.TlxImpFac.ordinal()));
        dataModel.setTlxImpMes(cursor.getDouble(Columns.TlxImpMes.ordinal()));
        dataModel.setTlxImpTap(cursor.getDouble(Columns.TlxImpTap.ordinal()));
        dataModel.setTlxImpAse(cursor.getDouble(Columns.TlxImpAse.ordinal()));
        dataModel.setTlxCarFij(cursor.getDouble(Columns.TlxCarFij.ordinal()));
        dataModel.setTlxImpEn(cursor.getDouble(Columns.TlxImpEn.ordinal()));
        dataModel.setTlxImpPot(cursor.getDouble(Columns.TlxImpPot.ordinal()));
        dataModel.setTlxDesTdi(cursor.getDouble(Columns.TlxDesTdi.ordinal()));
        dataModel.setTlxLey1886(cursor.getDouble(Columns.TlxLey1886.ordinal()));
        dataModel.setTlxLeePot(cursor.getInt(Columns.TlxLeePot.ordinal()));
        dataModel.setTlxCotaseo(cursor.getInt(Columns.TlxCotaseo.ordinal()));
        dataModel.setTlxTap(cursor.getInt(Columns.TlxTap.ordinal()));
        dataModel.setTlxPotCon(cursor.getInt(Columns.TlxPotCon.ordinal()));
        dataModel.setTlxPotFac(cursor.getInt(Columns.TlxPotFac.ordinal()));
        dataModel.setTlxCliNit(cursor.getString(Columns.TlxCliNit.ordinal()));
        dataModel.setTlxFecCor(cursor.getString(Columns.TlxFecCor.ordinal()));
        dataModel.setTlxFecVto(cursor.getString(Columns.TlxFecVto.ordinal()));
        dataModel.setTlxFecproEmi(cursor.getString(Columns.TlxFecproEmi.ordinal()));
        dataModel.setTlxFecproMed(cursor.getString(Columns.TlxFecproMed.ordinal()));
        dataModel.setTlxTope(cursor.getInt(Columns.TlxTope.ordinal()));
        dataModel.setTlxLeyTag(cursor.getInt(Columns.TlxLeyTag.ordinal()));
        dataModel.setTlxTpoTap(cursor.getInt(Columns.TlxTpoTap.ordinal()));
        dataModel.setTlxImpTot(cursor.getDouble(Columns.TlxImpTot.ordinal()));
        dataModel.setTlxKwhAdi(cursor.getInt(Columns.TlxKwhAdi.ordinal()));
        dataModel.setTlxImpAvi(cursor.getInt(Columns.TlxImpAvi.ordinal()));
        dataModel.setTlxCarFac(cursor.getInt(Columns.TlxCarFac.ordinal()));
        dataModel.setTlxDeuEneC(cursor.getInt(Columns.TlxDeuEneC.ordinal()));
        dataModel.setTlxDeuEneI(cursor.getDouble(Columns.TlxDeuEneI.ordinal()));
        dataModel.setTlxDeuAseC(cursor.getInt(Columns.TlxDeuAseC.ordinal()));
        dataModel.setTlxDeuAseI(cursor.getDouble(Columns.TlxDeuAseI.ordinal()));
        dataModel.setTlxFecEmi(cursor.getString(Columns.TlxFecEmi.ordinal()));
        dataModel.setTlxUltPag(cursor.getString(Columns.TlxUltPag.ordinal()));
        dataModel.setTlxEstado(cursor.getInt(Columns.TlxEstado.ordinal()));
        dataModel.setTlxUltObs(cursor.getString(Columns.TlxUltObs.ordinal()));
        dataModel.setTlxActivi(cursor.getString(Columns.TlxActivi.ordinal()));
        dataModel.setTlxCiudad(cursor.getString(Columns.TlxCiudad.ordinal()));
        dataModel.setTlxFacNro(cursor.getString(Columns.TlxFacNro.ordinal()));
        dataModel.setTlxNroAut(cursor.getString(Columns.TlxNroAut.ordinal()));
        dataModel.setTlxCodCon(cursor.getString(Columns.TlxCodCon.ordinal()));
        dataModel.setTlxFecLim(cursor.getString(Columns.TlxFecLim.ordinal()));
        dataModel.setTlxKwhDev(cursor.getInt(Columns.TlxKwhDev.ordinal()));
        dataModel.setTlxUltTipL(cursor.getInt(Columns.TlxUltTipL.ordinal()));
        dataModel.setTlxCliNew(cursor.getInt(Columns.TlxCliNew.ordinal()));
        dataModel.setTlxEntEne(cursor.getInt(Columns.TlxEntEne.ordinal()));
        dataModel.setTlxEntPot(cursor.getInt(Columns.TlxEntPot.ordinal()));
        dataModel.setTlxPotFacM(cursor.getInt(Columns.TlxPotFacM.ordinal()));
        dataModel.setTlxPerCo3(cursor.getDouble(Columns.TlxPerCo3.ordinal()));
        dataModel.setTlxPerHr3(cursor.getDouble(Columns.TlxPerHr3.ordinal()));
        dataModel.setTlxPerCo2(cursor.getDouble(Columns.TlxPerCo2.ordinal()));
        dataModel.setTlxPerHr2(cursor.getDouble(Columns.TlxPerHr2.ordinal()));
        dataModel.setTlxPerCo1(cursor.getDouble(Columns.TlxPerCo1.ordinal()));
        dataModel.setTlxPerHr1(cursor.getDouble(Columns.TlxPerHr1.ordinal()));
        dataModel.setTlxConsumo(cursor.getInt(Columns.TlxConsumo.ordinal()));
        dataModel.setTlxPerdidas(cursor.getDouble(Columns.TlxPerdidas.ordinal()));
        dataModel.setTlxImpEnergia(cursor.getDouble(Columns.TlxImpEnergia.ordinal()));
        dataModel.setTlxConsFacturado(cursor.getInt(Columns.TlxConsFacturado.ordinal()));
        dataModel.setTlxDebAuto(cursor.getString(Columns.TlxDebAuto.ordinal()));
        dataModel.setEstadoLectura(cursor.getInt(Columns.estado_lectura.ordinal()));
        dataModel.setEnviado(cursor.getInt(Columns.enviado.ordinal()));
        dataModel.setTlxPotLei(cursor.getInt(Columns.TlxPotLei.ordinal()));
        dataModel.setTlxDecPot(cursor.getInt(Columns.TlxDecPot.ordinal()));
        dataModel.setTlxDemPot(cursor.getString(Columns.TlxDemPot.ordinal()));
        dataModel.setTlxDecEne(cursor.getInt(Columns.TlxDecEne.ordinal()));
        dataModel.setTlxRecordatorio(cursor.getString(Columns.TlxRecordatorio.ordinal()));

        dataModel.setTlxCtgTap(cursor.getInt(Columns.TlxCtgTap.ordinal()));
        dataModel.setTlxCtgAseo(cursor.getInt(Columns.TlxCtgAseo.ordinal()));
        dataModel.setTlxTipDem(cursor.getInt(Columns.TlxTipDem.ordinal()));
        dataModel.setTlxDignidad(cursor.getInt(Columns.TlxDignidad.ordinal()));
        dataModel.setTlxPotTag(cursor.getInt(Columns.TlxPotTag.ordinal()));
        dataModel.setTlxPreAnt1(cursor.getString(Columns.TlxPreAnt1.ordinal()));
        dataModel.setTlxPreAnt2(cursor.getString(Columns.TlxPreAnt2.ordinal()));
        dataModel.setTlxPreAnt3(cursor.getString(Columns.TlxPreAnt3.ordinal()));
        dataModel.setTlxPreAnt4(cursor.getString(Columns.TlxPreAnt4.ordinal()));
        dataModel.setTlxPreNue1(cursor.getString(Columns.TlxPreNue1.ordinal()));
        dataModel.setTlxPreNue2(cursor.getString(Columns.TlxPreNue2.ordinal()));
        dataModel.setTlxPreNue3(cursor.getString(Columns.TlxPreNue3.ordinal()));
        dataModel.setTlxPreNue4(cursor.getString(Columns.TlxPreNue4.ordinal()));
        dataModel.setTlxKwInst(cursor.getInt(Columns.TlxKwInst.ordinal()));
        dataModel.setTlxReactiva(cursor.getInt(Columns.TlxReactiva.ordinal()));
        dataModel.setTlxKwhBajo(cursor.getInt(Columns.TlxKwhBajo.ordinal()));
        dataModel.setTlxKwhBajo(cursor.getInt(Columns.TlxKwhBajo.ordinal()));
        dataModel.setTlxKwhMedio(cursor.getInt(Columns.TlxKwhMedio.ordinal()));
        dataModel.setTlxKwhAlto(cursor.getInt(Columns.TlxKwhAlto.ordinal()));
        dataModel.setTlxDemBajo(cursor.getInt(Columns.TlxDemBajo.ordinal()));
        dataModel.setTlxDemMedio(cursor.getInt(Columns.TlxDemMedio.ordinal()));
        dataModel.setTlxDemAlto(cursor.getInt(Columns.TlxDemAlto.ordinal()));
        dataModel.setTlxHoraBajo(cursor.getString(Columns.TlxHoraBajo.ordinal()));
        dataModel.setTlxHoraMedio(cursor.getString(Columns.TlxHoraMedio.ordinal()));
        dataModel.setTlxHoraAlto(cursor.getString(Columns.TlxHoraAlto.ordinal()));
        dataModel.setTlxFechaAlto(cursor.getString(Columns.TlxFechaAlto.ordinal()));
        dataModel.setTlxFechaMedio(cursor.getString(Columns.TlxFechaMedio.ordinal()));
        dataModel.setTlxFechaBajo(cursor.getString(Columns.TlxFechaBajo.ordinal()));
        return dataModel;
    }

    /**
     * Este metodo crea un json para enviarlo al servidor
     * @param dataModel objeto con los datos para llenar
     * @param obsArray un array de objetos tipo DataObs
     * @param printObsDataArrayList un array de objetos tipo PrintObsData
     * @param detalleFacturaArrayList un array de objetos tipo DetalleFactura
     * @return retorna un objeto listo para enviarlo al servidor
     */
    public static String getJsonToSend(DataModel dataModel,
                                       ArrayList<DataObs> obsArray,
                                       ArrayList<PrintObsData> printObsDataArrayList,
                                       ArrayList<DetalleFactura> detalleFacturaArrayList) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (dataModel.getTlxHorLec() == null) {
                jsonObject.put(Columns.TlxHorLec.name(), "");
            } else {
                jsonObject.put(Columns.TlxHorLec.name(), dataModel.getTlxHorLec());
            }
            jsonObject.put(Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());
            jsonObject.put(Columns.TlxTipLec.name(), dataModel.getTlxTipLec());
            jsonObject.put(Columns.TlxImpTotCns.name(), dataModel.getTlxImpTotCns());
            jsonObject.put(Columns.TlxImpSum.name(), dataModel.getTlxImpSum());
            jsonObject.put(Columns.TlxImpFac.name(), dataModel.getTlxImpFac());
            jsonObject.put(Columns.TlxImpMes.name(), dataModel.getTlxImpMes());
            jsonObject.put(Columns.TlxImpEn.name(), dataModel.getTlxImpEn());
            jsonObject.put(Columns.TlxPotLei.name(), dataModel.getTlxPotLei());
            jsonObject.put(Columns.TlxImpTot.name(), dataModel.getTlxImpTot());
            jsonObject.put(Columns.TlxFecEmi.name(), dataModel.getTlxFecEmi());
            jsonObject.put(Columns.TlxUltObs.name(), dataModel.getTlxUltObs());
            jsonObject.put(Columns.TlxKwhDev.name(), dataModel.getTlxKwhDev());
            jsonObject.put(Columns.TlxOrdTpl.name(), dataModel.getTlxOrdTpl());

            jsonObject.put(Columns.TlxKwInst.name(), dataModel.getTlxKwInst());
            jsonObject.put(Columns.TlxReactiva.name(), dataModel.getTlxReactiva());
            jsonObject.put(Columns.TlxKwhBajo.name(), dataModel.getTlxKwhBajo());
            jsonObject.put(Columns.TlxKwhMedio.name(), dataModel.getTlxKwhMedio());
            jsonObject.put(Columns.TlxKwhAlto.name(), dataModel.getTlxKwhAlto());
            jsonObject.put(Columns.TlxDemBajo.name(), dataModel.getTlxDemBajo());
            jsonObject.put(Columns.TlxDemMedio.name(), dataModel.getTlxDemMedio());
            jsonObject.put(Columns.TlxDemAlto.name(), dataModel.getTlxDemAlto());
            jsonObject.put(Columns.TlxFechaBajo.name(), dataModel.getTlxFechaBajo() == null ? "" : dataModel.getTlxFechaBajo());
            jsonObject.put(Columns.TlxFechaMedio.name(), dataModel.getTlxFechaMedio() == null ? "" : dataModel.getTlxFechaMedio());
            jsonObject.put(Columns.TlxFechaAlto.name(), dataModel.getTlxFechaAlto() == null ? "" : dataModel.getTlxFechaAlto());
            jsonObject.put(Columns.TlxHoraBajo.name(), dataModel.getTlxHoraBajo() == null ? "" : dataModel.getTlxHoraBajo());
            jsonObject.put(Columns.TlxHoraMedio.name(), dataModel.getTlxHoraMedio() == null ? "" : dataModel.getTlxHoraMedio());
            jsonObject.put(Columns.TlxHoraAlto.name(), dataModel.getTlxHoraAlto() == null ? "" : dataModel.getTlxHoraAlto());
            jsonObject.put(Columns.TlxConsumo.name(), dataModel.getTlxConsumo());
            jsonObject.put(Columns.TlxConsFacturado.name(), dataModel.getTlxConsFacturado());
            jsonObject.put(Columns.TlxPreNue1.name(), dataModel.getTlxPreNue1() == null ? "" : dataModel.getTlxPreNue1());
            jsonObject.put(Columns.TlxPreNue2.name(), dataModel.getTlxPreNue2() == null ? "" : dataModel.getTlxPreNue2());
            jsonObject.put(Columns.TlxPreNue3.name(), dataModel.getTlxPreNue3() == null ? "" : dataModel.getTlxPreNue3());
            jsonObject.put(Columns.TlxPreNue4.name(), dataModel.getTlxPreNue4() == null ? "" : dataModel.getTlxPreNue4());

            jsonObject.put(Columns.TlxImpAvi.name(), dataModel.getTlxImpAvi());
            jsonObject.put("TlxImpEst", dataModel.getTlxImpAvi());
            jsonObject.put(Columns.TlxEstado.name(), dataModel.getEstadoLectura());
            jsonObject.put(Columns.TlxCodCon.name(), dataModel.getTlxCodCon());

            jsonObject.put(Columns.TlxRecordatorio.name(), dataModel.getTlxRecordatorio());
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < obsArray.size(); i++) {
                DataObs dataObs = obsArray.get(i);
                jsonArray.put(i, dataObs.toJson());
            }
            jsonObject.put("observaciones", jsonArray);

            JSONArray jsonArrayPrintObs = new JSONArray();
            for (int i = 0; i < printObsDataArrayList.size(); i++) {
                PrintObsData printObsData = printObsDataArrayList.get(i);
                jsonArrayPrintObs.put(i, printObsData.toJson());
            }
            jsonObject.put("observaciones_imp", jsonArrayPrintObs);

            JSONArray jsonArrayDetalleFactura = new JSONArray();
            for (int i = 0; i < detalleFacturaArrayList.size(); i++) {
                jsonArrayDetalleFactura.put(i, detalleFacturaArrayList.get(i).toJson());
            }
            jsonObject.put("detalle_factura", jsonArrayDetalleFactura);
        } catch (JSONException e) {
            Log.e(TAG, "getJsonToSend: ", e);
        }
        return jsonObject.toString();
    }

    public int getTlxEstCli() {
        return TlxEstCli;
    }

    public void setTlxEstCli(int tlxEstCli) {
        TlxEstCli = tlxEstCli;
    }

    public int getTlxDav() {
        return TlxDav;
    }

    public void setTlxDav(int tlxDav) {
        TlxDav = tlxDav;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTlxImpEnergia() {
        return TlxImpEnergia;
    }

    public void setTlxImpEnergia(double tlxImpEnergia) {
        TlxImpEnergia = tlxImpEnergia;
    }

    public int getTlxPromAseo() {
        return TlxPromAseo;
    }

    public void setTlxPromAseo(int tlxPromAseo) {
        TlxPromAseo = tlxPromAseo;
    }

    public int getTlxDivAseo() {
        return TlxDivAseo;
    }

    public void setTlxDivAseo(int tlxDivAseo) {
        TlxDivAseo = tlxDivAseo;
    }

    public int getTlxRem() {
        return TlxRem;
    }

    public String getTlxDemPot() {
        return TlxDemPot;
    }

    public void setTlxDemPot(String tlxDemPot) {
        TlxDemPot = tlxDemPot;
    }

    public void setTlxRem(int tlxRem) {
        TlxRem = tlxRem;
    }

    public int getTlxAre() {
        return TlxAre;
    }

    public void setTlxAre(int tlxAre) {
        TlxAre = tlxAre;
    }

    public int getTlxRutO() {
        return TlxRutO;
    }

    public void setTlxRutO(int tlxRutO) {
        TlxRutO = tlxRutO;
    }

    public int getTlxRutA() {
        return TlxRutA;
    }

    public double getTlxImpTotCns() {
        return TlxImpTotCns;
    }

    public void setTlxImpTotCns(double tlxImpTotCns) {
        TlxImpTotCns = tlxImpTotCns;
    }

    public double getTlxImpSum() {
        return TlxImpSum;
    }

    public void setTlxImpSum(double tlxImpSum) {
        TlxImpSum = tlxImpSum;
    }

    public double getTlxImpMes() {
        return TlxImpMes;
    }

    public void setTlxImpMes(double tlxImpMes) {
        TlxImpMes = tlxImpMes;
    }

    public void setTlxRutA(int tlxRutA) {
        TlxRutA = tlxRutA;
    }

    public int getTlxCtgTap() {
        return TlxCtgTap;
    }

    public void setTlxCtgTap(int tlxCtgTap) {
        TlxCtgTap = tlxCtgTap;
    }

    public int getTlxCtgAseo() {
        return TlxCtgAseo;
    }

    public void setTlxCtgAseo(int tlxCtgAseo) {
        TlxCtgAseo = tlxCtgAseo;
    }

    public int getTlxTipDem() {
        return TlxTipDem;
    }

    public void setTlxTipDem(int tlxTipDem) {
        TlxTipDem = tlxTipDem;
    }

    public int getTlxDignidad() {
        return TlxDignidad;
    }

    public void setTlxDignidad(int tlxDignidad) {
        TlxDignidad = tlxDignidad;
    }

    public int getTlxPotTag() {
        return TlxPotTag;
    }

    public void setTlxPotTag(int tlxPotTag) {
        TlxPotTag = tlxPotTag;
    }

    public String getTlxPreAnt1() {
        return TlxPreAnt1;
    }

    public void setTlxPreAnt1(String tlxPreAnt1) {
        TlxPreAnt1 = tlxPreAnt1;
    }

    public String getTlxPreAnt2() {
        return TlxPreAnt2;
    }

    public void setTlxPreAnt2(String tlxPreAnt2) {
        TlxPreAnt2 = tlxPreAnt2;
    }

    public String getTlxPreAnt3() {
        return TlxPreAnt3;
    }

    public void setTlxPreAnt3(String tlxPreAnt3) {
        TlxPreAnt3 = tlxPreAnt3;
    }

    public String getTlxPreAnt4() {
        return TlxPreAnt4;
    }

    public void setTlxPreAnt4(String tlxPreAnt4) {
        TlxPreAnt4 = tlxPreAnt4;
    }

    public String getTlxPreNue1() {
        return TlxPreNue1;
    }

    public void setTlxPreNue1(String tlxPreNue1) {
        TlxPreNue1 = tlxPreNue1;
    }

    public String getTlxPreNue2() {
        return TlxPreNue2;
    }

    public void setTlxPreNue2(String tlxPreNue2) {
        TlxPreNue2 = tlxPreNue2;
    }

    public String getTlxPreNue3() {
        return TlxPreNue3;
    }

    public void setTlxPreNue3(String tlxPreNue3) {
        TlxPreNue3 = tlxPreNue3;
    }

    public String getTlxPreNue4() {
        return TlxPreNue4;
    }

    public void setTlxPreNue4(String tlxPreNue4) {
        TlxPreNue4 = tlxPreNue4;
    }

    public int getTlxKwInst() {
        return TlxKwInst;
    }

    public void setTlxKwInst(int tlxKwInst) {
        TlxKwInst = tlxKwInst;
    }

    public int getTlxReactiva() {
        return TlxReactiva;
    }

    public void setTlxReactiva(int tlxReactiva) {
        TlxReactiva = tlxReactiva;
    }

    public int getTlxKwhBajo() {
        return TlxKwhBajo;
    }

    public void setTlxKwhBajo(int tlxKwhBajo) {
        TlxKwhBajo = tlxKwhBajo;
    }

    public int getTlxKwhMedio() {
        return TlxKwhMedio;
    }

    public void setTlxKwhMedio(int tlxKwhMedio) {
        TlxKwhMedio = tlxKwhMedio;
    }

    public int getTlxKwhAlto() {
        return TlxKwhAlto;
    }

    public void setTlxKwhAlto(int tlxKwhAlto) {
        TlxKwhAlto = tlxKwhAlto;
    }

    public int getTlxDemBajo() {
        return TlxDemBajo;
    }

    public void setTlxDemBajo(int tlxDemBajo) {
        TlxDemBajo = tlxDemBajo;
    }

    public String getTlxFechaBajo() {
        return TlxFechaBajo;
    }

    public void setTlxFechaBajo(String tlxFechaBajo) {
        TlxFechaBajo = tlxFechaBajo;
    }

    public String getTlxHoraBajo() {
        return TlxHoraBajo;
    }

    public void setTlxHoraBajo(String tlxHoraBajo) {
        TlxHoraBajo = tlxHoraBajo;
    }

    public int getTlxDemMedio() {
        return TlxDemMedio;
    }

    public void setTlxDemMedio(int tlxDemMedio) {
        TlxDemMedio = tlxDemMedio;
    }

    public String getTlxFechaMedio() {
        return TlxFechaMedio;
    }

    public void setTlxFechaMedio(String tlxFechaMedio) {
        TlxFechaMedio = tlxFechaMedio;
    }

    public String getTlxHoraMedio() {
        return TlxHoraMedio;
    }

    public void setTlxHoraMedio(String tlxHoraMedio) {
        TlxHoraMedio = tlxHoraMedio;
    }

    public int getTlxDemAlto() {
        return TlxDemAlto;
    }

    public void setTlxDemAlto(int tlxDemAlto) {
        TlxDemAlto = tlxDemAlto;
    }

    public String getTlxFechaAlto() {
        return TlxFechaAlto;
    }

    public void setTlxFechaAlto(String tlxFechaAlto) {
        TlxFechaAlto = tlxFechaAlto;
    }

    public String getTlxHoraAlto() {
        return TlxHoraAlto;
    }

    public void setTlxHoraAlto(String tlxHoraAlto) {
        TlxHoraAlto = tlxHoraAlto;
    }

    public int getTlxAno() {
        return TlxAno;
    }

    public void setTlxAno(int tlxAno) {
        TlxAno = tlxAno;
    }

    public int getTlxMes() {
        return TlxMes;
    }

    public void setTlxMes(int tlxMes) {
        TlxMes = tlxMes;
    }

    public int getTlxCli() {
        return TlxCli;
    }

    public void setTlxCli(int tlxCli) {
        TlxCli = tlxCli;
    }

    public int getTlxOrdTpl() {
        return TlxOrdTpl;
    }

    public void setTlxOrdTpl(int tlxOrdTpl) {
        TlxOrdTpl = tlxOrdTpl;
    }

    public String getTlxNom() {
        return TlxNom;
    }

    public void setTlxNom(String tlxNom) {
        TlxNom = tlxNom;
    }

    public String getTlxDir() {
        return TlxDir;
    }

    public void setTlxDir(String tlxDir) {
        TlxDir = tlxDir;
    }

    public String getTlxCtaAnt() {
        return TlxCtaAnt;
    }

    public void setTlxCtaAnt(String tlxCtaAnt) {
        TlxCtaAnt = tlxCtaAnt;
    }

    public int getTlxCtg() {
        return TlxCtg;
    }

    public void setTlxCtg(int tlxCtg) {
        TlxCtg = tlxCtg;
    }

    public int getTlxPotLei() {
        return TlxPotLei;
    }

    public void setTlxPotLei(int tlxPotLei) {
        TlxPotLei = tlxPotLei;
    }

    public int getTlxDecEne() {
        return TlxDecEne;
    }

    public void setTlxDecEne(int tlxDecEne) {
        TlxDecEne = tlxDecEne;
    }

    public int getTlxDecPot() {
        return TlxDecPot;
    }

    public void setTlxDecPot(int tlxDecPot) {
        TlxDecPot = tlxDecPot;
    }

    public String getTlxRecordatorio() {
        return TlxRecordatorio;
    }

    public void setTlxRecordatorio(String tlxRecordatorio) {
        TlxRecordatorio = tlxRecordatorio;
    }

    public String getTlxNroMed() {
        return TlxNroMed;
    }

    public void setTlxNroMed(String tlxNroMed) {
        TlxNroMed = tlxNroMed;
    }

    public int getTlxNroDig() {
        return TlxNroDig;
    }

    public void setTlxNroDig(int tlxNroDig) {
        TlxNroDig = tlxNroDig;
    }

    public double getTlxFacMul() {
        return TlxFacMul;
    }

    public void setTlxFacMul(double tlxFacMul) {
        TlxFacMul = tlxFacMul;
    }

    public String getTlxFecAnt() {
        return TlxFecAnt;
    }

    public void setTlxFecAnt(String tlxFecAnt) {
        TlxFecAnt = tlxFecAnt;
    }

    public String getTlxFecLec() {
        return TlxFecLec;
    }

    public void setTlxFecLec(String tlxFecLec) {
        TlxFecLec = tlxFecLec;
    }

    public String getTlxHorLec() {
        return TlxHorLec;
    }

    public void setTlxHorLec(String tlxHorLec) {
        TlxHorLec = tlxHorLec;
    }

    public int getTlxUltInd() {
        return TlxUltInd;
    }

    public void setTlxUltInd(int tlxUltInd) {
        TlxUltInd = tlxUltInd;
    }

    public int getTlxConPro() {
        return TlxConPro;
    }

    public void setTlxConPro(int tlxConPro) {
        TlxConPro = tlxConPro;
    }

    public int getTlxNvaLec() {
        return TlxNvaLec;
    }

    public void setTlxNvaLec(int tlxNvaLec) {
        TlxNvaLec = tlxNvaLec;
    }

    public int getTlxTipLec() {
        return TlxTipLec;
    }

    public void setTlxTipLec(int tlxTipLec) {
        TlxTipLec = tlxTipLec;
    }

    public String getTlxSgl() {
        return TlxSgl;
    }

    public void setTlxSgl(String tlxSgl) {
        TlxSgl = tlxSgl;
    }

    public int getTlxOrdSeq() {
        return TlxOrdSeq;
    }

    public void setTlxOrdSeq(int tlxOrdSeq) {
        TlxOrdSeq = tlxOrdSeq;
    }

    public double getTlxImpFac() {
        return TlxImpFac;
    }

    public void setTlxImpFac(double tlxImpFac) {
        TlxImpFac = tlxImpFac;
    }

    public double getTlxImpTap() {
        return TlxImpTap;
    }

    public void setTlxImpTap(double tlxImpTap) {
        TlxImpTap = tlxImpTap;
    }

    public double getTlxImpAse() {
        return TlxImpAse;
    }

    public void setTlxImpAse(double tlxImpAse) {
        TlxImpAse = tlxImpAse;
    }

    public double getTlxCarFij() {
        return TlxCarFij;
    }

    public void setTlxCarFij(double tlxCarFij) {
        TlxCarFij = tlxCarFij;
    }

    public double getTlxImpEn() {
        return TlxImpEn;
    }

    public void setTlxImpEn(double tlxImpEn) {
        TlxImpEn = tlxImpEn;
    }

    public double getTlxImpPot() {
        return TlxImpPot;
    }

    public void setTlxImpPot(double tlxImpPot) {
        TlxImpPot = tlxImpPot;
    }

    public double getTlxDesTdi() {
        return TlxDesTdi;
    }

    public void setTlxDesTdi(double tlxDesTdi) {
        TlxDesTdi = tlxDesTdi;
    }

    public double getTlxLey1886() {
        return TlxLey1886;
    }

    public void setTlxLey1886(double tlxLey1886) {
        TlxLey1886 = tlxLey1886;
    }

    public int getTlxLeePot() {
        return TlxLeePot;
    }

    public void setTlxLeePot(int tlxLeePot) {
        TlxLeePot = tlxLeePot;
    }

    public int getTlxCotaseo() {
        return TlxCotaseo;
    }

    public void setTlxCotaseo(int tlxCotaseo) {
        TlxCotaseo = tlxCotaseo;
    }

    public int getTlxTap() {
        return TlxTap;
    }

    public void setTlxTap(int tlxTap) {
        TlxTap = tlxTap;
    }

    public int getTlxPotCon() {
        return TlxPotCon;
    }

    public void setTlxPotCon(int tlxPotCon) {
        TlxPotCon = tlxPotCon;
    }

    public int getTlxPotFac() {
        return TlxPotFac;
    }

    public void setTlxPotFac(int tlxPotFac) {
        TlxPotFac = tlxPotFac;
    }

    public String getTlxCliNit() {
        return TlxCliNit;
    }

    public void setTlxCliNit(String tlxCliNit) {
        TlxCliNit = tlxCliNit;
    }

    public String getTlxFecCor() {
        return TlxFecCor;
    }

    public void setTlxFecCor(String tlxFecCor) {
        TlxFecCor = tlxFecCor;
    }

    public String getTlxFecVto() {
        return TlxFecVto;
    }

    public void setTlxFecVto(String tlxFecVto) {
        TlxFecVto = tlxFecVto;
    }

    public String getTlxFecproEmi() {
        return TlxFecproEmi;
    }

    public void setTlxFecproEmi(String tlxFecproEmi) {
        TlxFecproEmi = tlxFecproEmi;
    }

    public String getTlxFecproMed() {
        return TlxFecproMed;
    }

    public void setTlxFecproMed(String tlxFecproMed) {
        TlxFecproMed = tlxFecproMed;
    }

    public int getTlxTope() {
        return TlxTope;
    }

    public void setTlxTope(int tlxTope) {
        TlxTope = tlxTope;
    }

    public int getTlxLeyTag() {
        return TlxLeyTag;
    }

    public void setTlxLeyTag(int tlxLeyTag) {
        TlxLeyTag = tlxLeyTag;
    }

    public int getTlxTpoTap() {
        return TlxTpoTap;
    }

    public void setTlxTpoTap(int tlxTpoTap) {
        TlxTpoTap = tlxTpoTap;
    }

    public double getTlxImpTot() {
        return TlxImpTot;
    }

    public void setTlxImpTot(double tlxImpTot) {
        TlxImpTot = tlxImpTot;
    }

    public int getTlxKwhAdi() {
        return TlxKwhAdi;
    }

    public void setTlxKwhAdi(int tlxKwhAdi) {
        TlxKwhAdi = tlxKwhAdi;
    }

    public int getTlxImpAvi() {
        return TlxImpAvi;
    }

    public void setTlxImpAvi(int tlxImpAvi) {
        TlxImpAvi = tlxImpAvi;
    }

    public int getTlxCarFac() {
        return TlxCarFac;
    }

    public void setTlxCarFac(int tlxCarFac) {
        TlxCarFac = tlxCarFac;
    }

    public int getTlxDeuEneC() {
        return TlxDeuEneC;
    }

    public void setTlxDeuEneC(int tlxDeuEneC) {
        TlxDeuEneC = tlxDeuEneC;
    }

    public double getTlxDeuEneI() {
        return TlxDeuEneI;
    }

    public void setTlxDeuEneI(double tlxDeuEneI) {
        TlxDeuEneI = tlxDeuEneI;
    }

    public int getTlxDeuAseC() {
        return TlxDeuAseC;
    }

    public void setTlxDeuAseC(int tlxDeuAseC) {
        TlxDeuAseC = tlxDeuAseC;
    }

    public double getTlxDeuAseI() {
        return TlxDeuAseI;
    }

    public void setTlxDeuAseI(double tlxDeuAseI) {
        TlxDeuAseI = tlxDeuAseI;
    }

    public String getTlxFecEmi() {
        return TlxFecEmi;
    }

    public void setTlxFecEmi(String tlxFecEmi) {
        TlxFecEmi = tlxFecEmi;
    }

    public String getTlxUltPag() {
        return TlxUltPag;
    }

    public void setTlxUltPag(String tlxUltPag) {
        TlxUltPag = tlxUltPag;
    }

    public int getTlxEstado() {
        return TlxEstado;
    }

    public void setTlxEstado(int tlxEstado) {
        TlxEstado = tlxEstado;
    }

    public String getTlxUltObs() {
        return TlxUltObs;
    }

    public void setTlxUltObs(String tlxUltObs) {
        TlxUltObs = tlxUltObs;
    }

    public String getTlxActivi() {
        return TlxActivi;
    }

    public void setTlxActivi(String tlxActivi) {
        TlxActivi = tlxActivi;
    }

    public String getTlxCiudad() {
        return TlxCiudad;
    }

    public void setTlxCiudad(String tlxCiudad) {
        TlxCiudad = tlxCiudad;
    }

    public String getTlxFacNro() {
        return TlxFacNro;
    }

    public void setTlxFacNro(String tlxFacNro) {
        TlxFacNro = tlxFacNro;
    }

    public String getTlxNroAut() {
        return TlxNroAut;
    }

    public void setTlxNroAut(String tlxNroAut) {
        TlxNroAut = tlxNroAut;
    }

    public String getTlxCodCon() {
        return TlxCodCon;
    }

    public void setTlxCodCon(String tlxCodCon) {
        TlxCodCon = tlxCodCon;
    }

    public String getTlxFecLim() {
        return TlxFecLim;
    }

    public void setTlxFecLim(String tlxFecLim) {
        TlxFecLim = tlxFecLim;
    }

    public int getTlxKwhDev() {
        return TlxKwhDev;
    }

    public void setTlxKwhDev(int tlxKwhDev) {
        TlxKwhDev = tlxKwhDev;
    }

    public int getTlxUltTipL() {
        return TlxUltTipL;
    }

    public void setTlxUltTipL(int tlxUltTipL) {
        TlxUltTipL = tlxUltTipL;
    }

    public int getTlxCliNew() {
        return TlxCliNew;
    }

    public void setTlxCliNew(int tlxCliNew) {
        TlxCliNew = tlxCliNew;
    }

    public int getTlxEntEne() {
        return TlxEntEne;
    }

    public void setTlxEntEne(int tlxEntEne) {
        TlxEntEne = tlxEntEne;
    }

    public int getTlxEntPot() {
        return TlxEntPot;
    }

    public void setTlxEntPot(int tlxEntPot) {
        TlxEntPot = tlxEntPot;
    }

    public int getTlxPotFacM() {
        return TlxPotFacM;
    }

    public void setTlxPotFacM(int tlxPotFacM) {
        TlxPotFacM = tlxPotFacM;
    }

    public double getTlxPerCo3() {
        return TlxPerCo3;
    }

    public void setTlxPerCo3(double tlxPerCo3) {
        TlxPerCo3 = tlxPerCo3;
    }

    public double getTlxPerHr3() {
        return TlxPerHr3;
    }

    public void setTlxPerHr3(double tlxPerHr3) {
        TlxPerHr3 = tlxPerHr3;
    }

    public double getTlxPerCo2() {
        return TlxPerCo2;
    }

    public void setTlxPerCo2(double tlxPerCo2) {
        TlxPerCo2 = tlxPerCo2;
    }

    public double getTlxPerHr2() {
        return TlxPerHr2;
    }

    public void setTlxPerHr2(double tlxPerHr2) {
        TlxPerHr2 = tlxPerHr2;
    }

    public double getTlxPerCo1() {
        return TlxPerCo1;
    }

    public void setTlxPerCo1(double tlxPerCo1) {
        TlxPerCo1 = tlxPerCo1;
    }

    public double getTlxPerHr1() {
        return TlxPerHr1;
    }

    public void setTlxPerHr1(double tlxPerHr1) {
        TlxPerHr1 = tlxPerHr1;
    }

    public int getTlxConsumo() {
        return TlxConsumo;
    }

    public void setTlxConsumo(int tlxConsumo) {
        TlxConsumo = tlxConsumo;
    }

    public double getTlxPerdidas() {
        return TlxPerdidas;
    }

    public void setTlxPerdidas(double tlxPerdidas) {
        TlxPerdidas = tlxPerdidas;
    }

    public int getTlxConsFacturado() {
        return TlxConsFacturado;
    }

    public void setTlxConsFacturado(int tlxConsFacturado) {
        TlxConsFacturado = tlxConsFacturado;
    }

    public String getTlxDebAuto() {
        return TlxDebAuto;
    }

    public void setTlxDebAuto(String tlxDebAuto) {
        TlxDebAuto = tlxDebAuto;
    }

    public int getEstadoLectura() {
        return estadoLectura;
    }

    public void setEstadoLectura(int estadoLectura) {
        this.estadoLectura = estadoLectura;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }
}
