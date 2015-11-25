package com.cac.tools;

/**
 * Created by Legal on 24/11/2015.
 */
public class CutterReportCardHolder {

    private String noTicket;
    private String idFinca;
    private String fecha;
    private String idCanial;
    private String idLote;
    private String linea;
    private String totalUnada;
    private String totalPeso;
    private String cortador;

    public CutterReportCardHolder() {}

    public String getIdFinca() {return "Finca: "+idFinca;}

    public String getIdCanial() {return "Canial: "+idCanial;}

    public String getIdLote() {return "Lote: "+idLote;}

    public String getTotalPeso() {return "Total Peso: "+totalPeso;}

    public String getFecha() {return "Fecha: "+fecha;}

    public String getNoTicket() {return "Ticket: "+noTicket;}

    public String getLinea() {return "Linea: "+linea;}

    public String getTotalUnada() {return "Total Unada: "+totalUnada;}

    public String getCortador() {return "Cortador: "+cortador;}

    public void setCortador(String cortador) {this.cortador = cortador;}

    public void setLinea(String linea) { this.linea = linea;}

    public void setNoTicket(String noTicket) {this.noTicket = noTicket;}

    public void setTotalUnada(String totalUnada) {this.totalUnada = totalUnada;}

    public void setTotalPeso(String totalPeso) {this.totalPeso = totalPeso;}

    public void setFecha(String fecha) {this.fecha = fecha;}

    public void setIdFinca(String idFinca) {this.idFinca = idFinca;}

    public void setIdCanial(String idCanial) {this.idCanial = idCanial;}

    public void setIdLote(String idLote) {this.idLote = idLote;}

}