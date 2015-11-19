package com.cac.entities;

import android.content.Context;
import android.content.res.Resources;

import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;

/**
 * Created by Legal on 15/10/2015.
 */
public class Transaccion extends Entity {

    public static String TABLE_NAME     = "ba_mtransaccion";
    public static String NO_ENVIO       = "no_envio";
    public static String FRENTE_CORTE   = "frente_corte";
    public static String FRENTE_ALCE    = "frente_alce";
    public static String ID_FINCA       = "id_finca";
    public static String ID_CANIAL      = "id_canial";
    public static String ID_LOTE        = "id_lote";
    public static String FECHA_CORTE    = "fecha_corte";
    public static String CODIGO_TRACTOR = "codigo_tractor";
    public static String PERIODO     = "id_periodo";
    public static String EMPRESA     = "id_empresa";
    public static String INDICADOR   = "estado";
    public static String APLICACION  = "aplicaicon";
    public static String CORTADOR = "codigo_cortador";
    public static String UNADA = "unada";
    public static String PESO = "peso";

    @Override
    public Transaccion entityConfig() {
        setName(TABLE_NAME);
        addColumn(EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(PERIODO, EntityColumn.ColumnType.TEXT);
        addColumn(NO_ENVIO, EntityColumn.ColumnType.INTEGER);
        addColumn(ID_FINCA, EntityColumn.ColumnType.INTEGER);
        addColumn(ID_CANIAL, EntityColumn.ColumnType.INTEGER);
        addColumn(ID_LOTE, EntityColumn.ColumnType.INTEGER);
        addColumn(FECHA_CORTE, EntityColumn.ColumnType.DATE);
        addColumn(CODIGO_TRACTOR, EntityColumn.ColumnType.TEXT);
        addColumn(INDICADOR, EntityColumn.ColumnType.TEXT);
        addColumn(new EntityColumn<Integer>(CORTADOR, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Integer>(UNADA, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Double>(PESO, EntityColumn.ColumnType.REAL).setNotNullable());
        addColumn(new EntityColumn<String>(Transaccion.APLICACION, EntityColumn.ColumnType.TEXT)
                .setDefaultValue("SAM"));
        setSynchronizable(false);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {}
}