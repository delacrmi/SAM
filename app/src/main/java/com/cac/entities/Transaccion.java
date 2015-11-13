package com.cac.entities;

import android.content.Context;

import com.delacrmi.controller.Entity;

/**
 * Created by Legal on 15/10/2015.
 */
public class Transaccion extends Entity {

    public static String TABLE_NAME     = "ba_mtransaccion";
    public static String CORRELATIVO    = "correlativo";
    public static String NO_ENVIO       = "no_envio";
    public static String FRENTE_CORTE   = "frente_corte";
    public static String FRENTE_ALCE    = "frente_alce";
    public static String ORDEN_QUEMA    = "orden_quema";
    public static String ID_FINCA       = "id_finca";
    public static String ID_CANIAL      = "id_canial";
    public static String ID_LOTE        = "id_lote";
    public static String FECHA_CORTE    = "fecha_corte";
    public static String CLAVE_CORTE    = "clave_corte";
    public static String CODIGO_CABEZAL = "codigo_cabezal";
    public static String CONDUCTOR_CABEZAL    = "coductor_cabezal";
    public static String CODIGO_CARRETA       = "codigo_carreta";
    public static String CODIGO_COSECHADORA   = "codigo_cosechadora";
    public static String OPERADOR_COSECHADORA = "operador_cosechadora";
    public static String CODIGO_TRACTOR       = "codigo_tractor";
    public static String OPERADOR_TRACTOR = "operador_tractor";
    public static String CODIGO_APUNTADOR = "codigo_apuntador";
    public static String CODIGO_VAGON     = "codigo_vagon";
    public static String PERIODO     = "id_periodo";
    public static String EMPRESA     = "id_empresa";
    public static String INDICADOR   = "estado";

    @Override
    public Transaccion entityConfig() {
        setName(Transaccion.TABLE_NAME);
        setPrimaryKey(Transaccion.CORRELATIVO);
        addColumn(Transaccion.PERIODO, "text");
        addColumn(Transaccion.EMPRESA,"integer");
        addColumn(Transaccion.NO_ENVIO, "integer");
        addColumn(Transaccion.FRENTE_CORTE, "integer");
        addColumn(Transaccion.FRENTE_ALCE, "integer");
        addColumn(Transaccion.ORDEN_QUEMA, "integer");
        addColumn(Transaccion.ID_FINCA, "integer");
        addColumn(Transaccion.ID_CANIAL, "integer");
        addColumn(Transaccion.ID_LOTE, "integer");
        addColumn(Transaccion.FECHA_CORTE,"date");
        addColumn(Transaccion.CLAVE_CORTE, "text");
        addColumn(Transaccion.CODIGO_CABEZAL,"text");
        addColumn(Transaccion.CONDUCTOR_CABEZAL, "integer");
        addColumn(Transaccion.CODIGO_CARRETA,"text");
        addColumn(Transaccion.CODIGO_COSECHADORA,"text");
        addColumn(Transaccion.OPERADOR_COSECHADORA, "integer");
        addColumn(Transaccion.CODIGO_TRACTOR,"text");
        addColumn(Transaccion.OPERADOR_TRACTOR, "integer");
        addColumn(Transaccion.CODIGO_APUNTADOR, "integer");
        addColumn(Transaccion.CODIGO_VAGON,"text");
        addColumn(Transaccion.INDICADOR,"text");
        setSynchronizable(false);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
    }
}