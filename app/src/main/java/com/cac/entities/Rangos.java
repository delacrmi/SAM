package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityFilter;

/**
 * Created by Legal on 19/10/2015.
 */
public class Rangos extends Entity {

    public static String EMPRESA      = "id_empresa";
    public static String PERIODO      = "id_periodo";
    public static String DISPOSITIVO  = "dispositivo";
    public static String ENVIO_DESDE  = "envio_desde";
    public static String ENVIO_HASTA  = "envio_hasta";
    public static String ENVIO_ACTUAL = "envio_actual";
    public static String TICKET_DESDE = "ticket_desde";
    public static String TICKET_HASTA = "ticket_hasta";
    public static String TICKET_ACTUAL = "ticket_actual";
    public static String STATUS       = "status";
    public static String CORRELATIVO  = "correlativo";
    public static String TABLE_NAME   = "ba_rango_envio";

    @Override
    public Rangos entityConfig() {
        setName(Rangos.TABLE_NAME);
        setNickName("Rango");
        setPrimaryKey(Rangos.CORRELATIVO);
        addColumn(Rangos.EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.PERIODO,EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.DISPOSITIVO, EntityColumn.ColumnType.TEXT);
        addColumn(Rangos.ENVIO_DESDE, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.ENVIO_HASTA, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.ENVIO_ACTUAL, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.TICKET_DESDE, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.TICKET_HASTA, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.TICKET_ACTUAL, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.STATUS, EntityColumn.ColumnType.TEXT);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa     = sharedPreferences.getString("EMPRESA", "30");
        String dispositivo = sharedPreferences.getString("DISPOSITIVO", "Dispositivo 1");
        setEntityFilter(new EntityFilter(new String[]{EMPRESA, DISPOSITIVO}, new String[]{empresa, dispositivo}));
    }
}
