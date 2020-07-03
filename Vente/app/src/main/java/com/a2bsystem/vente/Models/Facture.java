package com.a2bsystem.vente.Models;

public class Facture {
    String DateFact;
    String DateEch;
    String NumFact;
    String MntFact;
    String MntRest;
    int color;


    public Facture(String dateFact, String dateEch, String numFact, String mntFact, String mntRest, int color) {
        DateFact = dateFact;
        DateEch = dateEch;
        NumFact = numFact;
        MntFact = mntFact;
        MntRest = mntRest;
        this.color = color;
    }

    public String getDateFact() {
        return DateFact;
    }

    public void setDateFact(String dateFact) {
        DateFact = dateFact;
    }

    public String getDateEch() {
        return DateEch;
    }

    public void setDateEch(String dateEch) {
        DateEch = dateEch;
    }

    public String getNumFact() {
        return NumFact;
    }

    public void setNumFact(String numFact) {
        NumFact = numFact;
    }

    public String getMntFact() {
        return MntFact;
    }

    public void setMntFact(String mntFact) {
        MntFact = mntFact;
    }

    public String getMntRest() {
        return MntRest;
    }

    public void setMntRest(String mntRest) {
        MntRest = mntRest;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
