package com.solunes.endeapp.models;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jhonlimaster on 26-08-16.
 */
public class DataModel {
    private static final String TAG = "DataModel";
    private int _id;
    private int TlxRem;
    private int TlxAre;
    private int TlxRutO;
    private int TlxRutA;
    private int TlxAno;
    private int TlxMes;
    private int TlxCli;
    private int TlxOrdTpl;
    private String TlxNom;
    private String TlxDir;
    private String TlxCtaAnt;
    private int TlxCtg;
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
    private int TlxOrdSeq;
    private double TlxImpFac;
    private double TlxImpTap;
    private double TlxImpAse;
    private double TlxCarFij;
    private double TlxImpEn;
    private double TlxImpPot;
    private double TlxDesTdi;
    private double TlxLey1886;
    private int TlxLeePot;
    private int TlxCotaseo;
    private double TlxTap;
    private int TlxPotCon;
    private int TlxPotFac;
    private double TlxCliNit;
    private String TlxFecCor;
    private String TlxFecVto;
    private String TlxFecproEmi;
    private String TlxFecproMed;
    private int TlxTope;
    private int TlxLeyTag;
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
    private double TlxFacNro;
    private double TlxNroAut;
    private String TlxCodCon;
    private String TlxFecLim;
    private int TlxKwhDev;
    private int TlxUltTipL;
    private int TlxCliNew;
    private int TlxEntEne;
    private int TlxEntPot;
    private int TlxPotFacM;
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
    private String TlxRecordatorio;

    public enum Columns {
        _id,
        TlxRem,
        TlxAre,
        TlxRutO,
        TlxRutA,
        TlxAno,
        TlxMes,
        TlxCli,
        TlxOrdTpl,
        TlxNom,
        TlxDir,
        TlxCtaAnt,
        TlxCtg,
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
        TlxOrdSeq,
        TlxImpFac,
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
        TlxPotFacM,
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
        estado_lectura,
        enviado
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getTlxRem() {
        return TlxRem;
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

    public void setTlxRutA(int tlxRutA) {
        TlxRutA = tlxRutA;
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

    public double getTlxTap() {
        return TlxTap;
    }

    public void setTlxTap(double tlxTap) {
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

    public double getTlxCliNit() {
        return TlxCliNit;
    }

    public void setTlxCliNit(double tlxCliNit) {
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

    public double getTlxFacNro() {
        return TlxFacNro;
    }

    public void setTlxFacNro(double tlxFacNro) {
        TlxFacNro = tlxFacNro;
    }

    public double getTlxNroAut() {
        return TlxNroAut;
    }

    public void setTlxNroAut(double tlxNroAut) {
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

    public static DataModel fromCursor(Cursor cursor) {
        DataModel dataModel = new DataModel();
        dataModel.set_id(cursor.getInt(Columns._id.ordinal()));
        dataModel.setTlxRem(cursor.getInt(Columns.TlxRem.ordinal()));
        dataModel.setTlxAre(cursor.getInt(Columns.TlxAre.ordinal()));
        dataModel.setTlxRutO(cursor.getInt(Columns.TlxRutO.ordinal()));
        dataModel.setTlxRutA(cursor.getInt(Columns.TlxRutA.ordinal()));
        dataModel.setTlxAno(cursor.getInt(Columns.TlxAno.ordinal()));
        dataModel.setTlxMes(cursor.getInt(Columns.TlxMes.ordinal()));
        dataModel.setTlxCli(cursor.getInt(Columns.TlxCli.ordinal()));
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
        dataModel.setTlxImpFac(cursor.getDouble(Columns.TlxImpFac.ordinal()));
        dataModel.setTlxImpTap(cursor.getDouble(Columns.TlxImpTap.ordinal()));
        dataModel.setTlxImpAse(cursor.getDouble(Columns.TlxImpAse.ordinal()));
        dataModel.setTlxCarFij(cursor.getDouble(Columns.TlxCarFij.ordinal()));
        dataModel.setTlxImpEn(cursor.getDouble(Columns.TlxImpEn.ordinal()));
        dataModel.setTlxImpPot(cursor.getDouble(Columns.TlxImpPot.ordinal()));
        dataModel.setTlxDesTdi(cursor.getDouble(Columns.TlxDesTdi.ordinal()));
        dataModel.setTlxLey1886(cursor.getDouble(Columns.TlxLey1886.ordinal()));
        dataModel.setTlxLeePot(cursor.getInt(Columns.TlxLeePot.ordinal()));
        dataModel.setTlxCotaseo(cursor.getInt(Columns.TlxCotaseo.ordinal()));
        dataModel.setTlxTap(cursor.getDouble(Columns.TlxTap.ordinal()));
        dataModel.setTlxPotCon(cursor.getInt(Columns.TlxPotCon.ordinal()));
        dataModel.setTlxPotFac(cursor.getInt(Columns.TlxPotFac.ordinal()));
        dataModel.setTlxCliNit(cursor.getDouble(Columns.TlxCliNit.ordinal()));
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
        dataModel.setTlxFacNro(cursor.getDouble(Columns.TlxFacNro.ordinal()));
        dataModel.setTlxNroAut(cursor.getDouble(Columns.TlxNroAut.ordinal()));
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
        dataModel.setTlxConsFacturado(cursor.getInt(Columns.TlxConsFacturado.ordinal()));
        dataModel.setTlxDebAuto(cursor.getString(Columns.TlxDebAuto.ordinal()));
        dataModel.setEstadoLectura(cursor.getInt(Columns.estado_lectura.ordinal()));
        dataModel.setEnviado(cursor.getInt(Columns.enviado.ordinal()));
        dataModel.setTlxPotLei(cursor.getInt(Columns.TlxPotLei.ordinal()));
        dataModel.setTlxDecPot(cursor.getInt(Columns.TlxDecPot.ordinal()));
        dataModel.setTlxDecEne(cursor.getInt(Columns.TlxDecEne.ordinal()));
        dataModel.setTlxRecordatorio(cursor.getString(Columns.TlxRecordatorio.ordinal()));
        return dataModel;
    }

    public static String getJsonToSend(DataModel dataModel, ArrayList<DataObs> obsArray) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Columns.TlxHorLec.name(), dataModel.getTlxHorLec());
            jsonObject.put(Columns.TlxNvaLec.name(), dataModel.getTlxNvaLec());
            jsonObject.put(Columns.TlxTipLec.name(), dataModel.getTlxTipLec());
            jsonObject.put(Columns.TlxImpFac.name(), dataModel.getTlxImpFac());
            jsonObject.put(Columns.TlxImpTap.name(), dataModel.getTlxImpTap());
            jsonObject.put(Columns.TlxImpAse.name(), dataModel.getTlxImpAse());
            jsonObject.put(Columns.TlxCarFij.name(), dataModel.getTlxCarFij());
            jsonObject.put(Columns.TlxImpEn.name(), dataModel.getTlxImpEn());
            jsonObject.put(Columns.TlxImpPot.name(), dataModel.getTlxImpPot());
            jsonObject.put(Columns.TlxDesTdi.name(), dataModel.getTlxDesTdi());
            jsonObject.put(Columns.TlxLey1886.name(), dataModel.getTlxLey1886());
            jsonObject.put(Columns.TlxPotLei.name(), dataModel.getTlxPotLei());
            jsonObject.put(Columns.TlxImpTot.name(), dataModel.getTlxImpTot());
            jsonObject.put(Columns.TlxFecEmi.name(), dataModel.getTlxFecEmi());
            jsonObject.put(Columns.TlxUltObs.name(), dataModel.getTlxUltObs());
            jsonObject.put(Columns.TlxKwhDev.name(), dataModel.getTlxKwhDev());
            jsonObject.put(Columns.TlxConsumo.name(), dataModel.getTlxConsumo());
            jsonObject.put(Columns.TlxConsFacturado.name(), dataModel.getTlxConsFacturado());
            jsonObject.put(Columns.TlxRecordatorio.name(), dataModel.getTlxRecordatorio());
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < obsArray.size(); i++) {
                DataObs dataObs = obsArray.get(i);
                Log.e(TAG, "getJsonToSend: "+dataObs.toJson());
                jsonArray.put(i, dataObs.toJson());
            }
            jsonObject.put("observaciones", jsonArray);
        } catch (JSONException e) {
            Log.e(TAG, "getJsonToSend: ", e);
        }
        return jsonObject.toString();
    }
}
