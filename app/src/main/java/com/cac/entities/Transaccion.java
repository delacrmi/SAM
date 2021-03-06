package com.cac.entities;

import android.content.Context;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityManager;

import java.util.List;

/**
 * Created by Legal on 15/10/2015.
 */
public class Transaccion extends Entity {

    public static String TABLE_NAME     = "ba_mtransaccion";
    public static String NO_ENVIO       = "no_rango";
    public static String FRENTE_CORTE   = "frente_corte";
    public static String FRENTE_ALCE    = "frente_alce";
    public static String ID_FINCA       = "id_finca";
    public static String ID_CANIAL      = "id_canial";
    public static String ID_LOTE        = "id_lote";
    public static String FECHA_CORTE    = "fecha_corte";
    public static String CODIGO_TRACTOR = "codigo_tractor";
    public static String PERIODO     = "id_periodo";
    public static String EMPRESA     = "id_empresa";
    public static String ESTADO = "estado";
    public static String APLICACION  = "aplicacion";
    public static String CORTADOR = "codigo_cortador";
    public static String UNADA = "unada";
    public static String PESO = "peso";
    public static String LINEA = "linea";
    public static String DISPOSITIVO = "dispositivo";
    public static String MAPA_CORTE = "mapa_corte";
    public static String FECHA_ENVIO = "fecha_envio";

    public Transaccion(){}
    public Transaccion(EntityManager manager){
        setEntityManager(manager);
    }

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
        addColumn(FECHA_ENVIO, EntityColumn.ColumnType.DATE);
        addColumn(FRENTE_CORTE, EntityColumn.ColumnType.INTEGER);
        addColumn(FRENTE_ALCE, EntityColumn.ColumnType.INTEGER);
        addColumn(CODIGO_TRACTOR, EntityColumn.ColumnType.TEXT);
        addColumn(ESTADO, EntityColumn.ColumnType.TEXT);
        addColumn(LINEA, EntityColumn.ColumnType.INTEGER);
        addColumn(DISPOSITIVO, EntityColumn.ColumnType.TEXT);
        addColumn(new EntityColumn<Integer>(CORTADOR, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Integer>(UNADA, EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Double>(PESO, EntityColumn.ColumnType.REAL).setNotNullable());
        addColumn(new EntityColumn<String>(Transaccion.APLICACION, EntityColumn.ColumnType.TEXT)
                .setDefaultValue("SAM"));
        addColumn(new EntityColumn<Integer>(MAPA_CORTE, EntityColumn.ColumnType.INTEGER).setNotNullable());
        setSynchronizable(false);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {}

    @Override
    public List<Entity> getDefaultInsert(){
        return getEntityManager().find(getClass(), "*" , null, null);
    }

    public enum TransaccionEstado {
        ACTIVA, TRASLADADA
    }
}