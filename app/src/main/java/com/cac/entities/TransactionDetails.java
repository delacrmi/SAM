package com.cac.entities;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;

/**
 * Created by miguel on 11/11/15.
 */
public class TransactionDetails extends Entity {

    public static String TABLE_NAME = "ba_dtransaccion";
    public static String EMPRESA = "id_empresa";
    public static String ID_PERIODO = "id_periodo";
    public static String APLICACION = "aplicacion";
    public static String NO_RANGO = "no_rango";
    public static String CORRELATIVO = "correlativo";
    public static String PESO = "peso";
    public static String UNADA = "unada";
    public static String ID_TRACTOR = "codigo_tractor";
    public static String MAPA_CORTE = "mapa_corte";
    public static String ESTADO = "estado";
    public static String LATITUD ="latitud";
    public static String LONGITUD = "longitud";

    @Override
    public Entity entityConfig() {
        setName(TABLE_NAME);
        setNickName("Detalle Transaccion");
        addColumn(new EntityColumn<Integer>(EMPRESA, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Integer>(ID_PERIODO, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<String>(APLICACION, EntityColumn.ColumnType.TEXT).setNotNullable());
        addColumn(new EntityColumn<Integer>(NO_RANGO, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<String>(ID_TRACTOR, EntityColumn.ColumnType.TEXT).setNotNullable());
        addColumn(new EntityColumn<Integer>(CORRELATIVO,true).setAutoIncrement());
        addColumn(new EntityColumn<Integer>(MAPA_CORTE, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Double>(PESO,EntityColumn.ColumnType.REAL).setNotNullable());
        addColumn(new EntityColumn<Double>(LATITUD,EntityColumn.ColumnType.REAL).setNotNullable());
        addColumn(new EntityColumn<Double>(LONGITUD,EntityColumn.ColumnType.REAL).setNotNullable());
        addColumn(new EntityColumn<String>(Transaccion.ESTADO, EntityColumn.ColumnType.TEXT)
                .setDefaultValue(TransactionDetailsEstado.ACTIVA.toString()));
        addColumn(new EntityColumn<Integer>(UNADA, EntityColumn.ColumnType.INTEGER)
                .setOutServerColumn());
        return this;
    }

    public enum TransactionDetailsEstado {
        ACTIVA, TRASLADADA
    }
}
