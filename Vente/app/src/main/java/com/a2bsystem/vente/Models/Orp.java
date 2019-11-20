package com.a2bsystem.vente.Models;

import java.io.Serializable;

public class Orp implements Serializable {

    private Vente vente;
    private String artnr;
    private String lib;
    private String lot;
    private String ordernr;
    private String ordradnr;
    private Double colis;
    private Double pieces;
    private Double pdsBrut;
    private Double tare;
    private Double pdsNet;
    private Double piecesU;
    private Double pdsBrutU;
    private Double tareU;
    private Double pdsNetU;
    private String uniteFact;
    private Double pu;
    private Double montant;
    private String DLC;
    private String modeSaisie;
    private int statut;

    public Orp() {
    }

    public Orp(Vente vente) {
        this.vente = vente;
        this.artnr = "";
        this.lib = "";
        this.lot = "";
        this.ordernr = "";
        this.ordradnr = "";
        this.colis = 0.0;
        this.pieces = 0.0;
        this.pdsBrut = 0.0;
        this.tare = 0.0;
        this.pdsNet = 0.0;
        this.piecesU = 0.0;
        this.pdsBrutU = 0.0;
        this.tareU = 0.0;
        this.pdsNetU = 0.0;
        this.uniteFact = "";
        this.pu = 0.0;
        this.montant = 0.0;
        this.DLC = "";
        this.statut = 0;
        this.modeSaisie = "";
    }

    public Orp(String ordernr, String ordradnr, String artnr, String lib, Double colis, Double pieces, Double pdsNet, Double montant, int statut) {
        this.ordernr = ordernr;
        this.ordradnr = ordradnr;
        this.artnr = artnr;
        this.lib = lib;
        this.colis = colis;
        this.pieces = pieces;
        this.pdsNet = pdsNet;
        this.montant = montant;
        this.statut = statut;
    }

    public Orp(Vente vente, String artnr, String lib, String lot, String ordernr, String ordradnr, Double colis, Double pieces, Double pdsBrut, Double tare, Double pdsNet, Double piecesU, Double pdsBrutU, Double tareU, Double pdsNetU, String uniteFact, Double pu, Double montant, String DLC,String modeSaisie, int statut) {
        this.vente = vente;
        this.artnr = artnr;
        this.lib = lib;
        this.lot = lot;
        this.ordernr = ordernr;
        this.ordradnr = ordradnr;
        this.colis = colis;
        this.pieces = pieces;
        this.pdsBrut = pdsBrut;
        this.tare = tare;
        this.pdsNet = pdsNet;
        this.piecesU = piecesU;
        this.pdsBrutU = pdsBrutU;
        this.tareU = tareU;
        this.pdsNetU = pdsNetU;
        this.uniteFact = uniteFact;
        this.pu = pu;
        this.montant = montant;
        this.DLC = DLC;
        this.statut = statut;
        this.modeSaisie = modeSaisie;
    }


    public Vente getVente() {
        return vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public String getArtnr() {
        return artnr;
    }

    public void setArtnr(String artnr) {
        this.artnr = artnr;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getOrdernr() {
        return ordernr;
    }

    public void setOrdernr(String ordernr) {
        this.ordernr = ordernr;
    }

    public String getOrdradnr() {
        return ordradnr;
    }

    public void setOrdradnr(String ordradnr) {
        this.ordradnr = ordradnr;
    }

    public Double getColis() {
        return colis;
    }

    public void setColis(Double colis) {
        this.colis = colis;
    }

    public Double getPieces() {
        return pieces;
    }

    public void setPieces(Double pieces) {
        this.pieces = pieces;
    }

    public Double getPdsBrut() {
        return pdsBrut;
    }

    public void setPdsBrut(Double pdsBrut) {
        this.pdsBrut = pdsBrut;
    }

    public Double getTare() {
        return tare;
    }

    public void setTare(Double tare) {
        this.tare = tare;
    }

    public Double getPdsNet() {
        return pdsNet;
    }

    public void setPdsNet(Double pdsNet) {
        this.pdsNet = pdsNet;
    }

    public Double getPiecesU() {
        return piecesU;
    }

    public void setPiecesU(Double piecesU) {
        this.piecesU = piecesU;
    }

    public Double getPdsBrutU() {
        return pdsBrutU;
    }

    public void setPdsBrutU(Double pdsBrutU) {
        this.pdsBrutU = pdsBrutU;
    }

    public Double getTareU() {
        return tareU;
    }

    public void setTareU(Double tareU) {
        this.tareU = tareU;
    }

    public Double getPdsNetU() {
        return pdsNetU;
    }

    public void setPdsNetU(Double pdsNetU) {
        this.pdsNetU = pdsNetU;
    }

    public String getUniteFact() {
        return uniteFact;
    }

    public void setUniteFact(String uniteFact) {
        this.uniteFact = uniteFact;
    }

    public Double getPu() {
        return pu;
    }

    public void setPu(Double pu) {
        this.pu = pu;
    }

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getDLC() {
        return DLC;
    }

    public void setDLC(String DLC) {
        this.DLC = DLC;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public String getModeSaisie() {
        return modeSaisie;
    }

    public void setModeSaisie(String modeSaisie) {
        this.modeSaisie = modeSaisie;
    }
}
