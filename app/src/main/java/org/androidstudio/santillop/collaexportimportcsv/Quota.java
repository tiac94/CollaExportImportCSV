package org.androidstudio.santillop.collaexportimportcsv;

/**
 * Created by santi on 11/07/17.
 */

public class Quota {
    private int mes;
    private int quantitat;
    private boolean pagat = false;

    public Quota(int mes, int quantitat) {
        this.mes = mes;
        this.quantitat = quantitat;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(int quantitat) {
        this.quantitat = quantitat;
    }

    public boolean isPagat() {
        return pagat;
    }

    public void setPagat(boolean pagat) {
        this.pagat = pagat;
    }
}
