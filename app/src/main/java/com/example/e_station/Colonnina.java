package com.example.e_station;

public class Colonnina {

    private String marca;
    private String n_posti;
    private String via;
    private String citta;
    private String stato;
    private double latitudine;
    private double longitudine;
    private String coordinate;

    public Colonnina() {
    }

    public Colonnina(String marca, String n_posti, String via, String citta, String stato, double latitudine, double longitudine , String coordinate) {
        this.marca = marca;
        this.n_posti = n_posti;
        this.via = via;
        this.citta = citta;
        this.stato = stato;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.coordinate = coordinate;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getN_posti() {
        return n_posti;
    }

    public void setN_posti(String n_posti) {
        this.n_posti = n_posti;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }
}
