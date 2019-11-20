package com.a2bsystem.vente.Models;

import java.io.Serializable;

public class Vente implements Serializable {
    String code;
    String client;
    Double valeur;
    Double solde;
    String orderNr;
    String depot;
    int statut;



    public Vente() {
    }

    public Vente(String code, String client, Double valeur, Double solde, String orderNr, String depot, int statut) {
        this.code = code;
        this.client = client;
        this.valeur = valeur;
        this.solde = solde;
        this.orderNr = orderNr;
        this.depot = depot;
        this.statut = statut;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public String getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(String orderNr) {
        this.orderNr = orderNr;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

    public String getDepot() {
        return depot;
    }

    public void setDepot(String depot) {
        this.depot = depot;
    }
}
