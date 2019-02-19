package org.androidstudio.santillop.collaexportimportcsv;

import java.util.ArrayList;

/**
 * Created by santi on 11/07/17.
 */

public class Membre {
    private int ID;
    private String nom;
    private String cognoms;
    private String correu;
    private String telefon;
    private ArrayList<Quota> quotes;

    public Membre(int ID, String nom, String cognoms, String correu, String telefon, ArrayList<Quota> quotes) {
        this.ID = ID;
        this.nom = nom;
        this.cognoms = cognoms;
        this.correu = correu;
        this.telefon = telefon;
        this.quotes = quotes;
    }
    public Membre(String nom, String cognoms, String correu, String telefon, ArrayList<Quota> quotes) {
        this.nom = nom;
        this.cognoms = cognoms;
        this.correu = correu;
        this.telefon = telefon;
        this.quotes = quotes;
    }

    public Membre(String nom, String cognoms, ArrayList<Quota> quotes) {
        this.nom = nom;
        this.cognoms = cognoms;
        this.quotes = quotes;
    }

    public Membre(int ID, String nom, String cognoms, String correu, String telefon) {
        this.ID = ID;
        this.nom = nom;
        this.cognoms = cognoms;
        this.correu = correu;
        this.telefon = telefon;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Membre(String nom, String cognoms) {

        this.nom = nom;
        this.cognoms = cognoms;
    }

    public Membre(String nom, String cognoms, String correu, String telefon) {
        this.nom = nom;
        this.cognoms = cognoms;
        this.correu = correu;
        this.telefon = telefon;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognoms() {
        return cognoms;
    }

    public void setCognoms(String cognoms) {
        this.cognoms = cognoms;
    }

    public String getCorreu() {
        return correu;
    }

    public void setCorreu(String correu) {
        this.correu = correu;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public ArrayList<Quota> getQuotes() {
        return quotes;
    }

    public void setQuotes(ArrayList<Quota> quotes) {
        this.quotes = quotes;
    }
}
