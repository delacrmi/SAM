package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.delacrmi.controller.Entity;
import com.delacrmi.controller.EntityFilter;

/**
 * Created by miguel on 10/10/15.
 */
public class Vehiculos extends Entity {
    //"CREATE TABLE MQ_VEHICULO(ID_EMPRESA INTEGER,AREA INTEGER,CODIGO_GRUPO TEXT, CODIGO_SUBGRUPO INTEGER, CODIGO_VEHICULO INTEGER)";

    public final static String ID_EMPRESA      = "id_empresa";
    public final static String ID_AREA         = "id_area";
    public final static String CODIGO_GRUPO    = "codigo_grupo";
    public final static String CODIGO_SUBGRUPO = "codigo_subgrupo";
    public final static String CODIGO_VEHICULO = "codigo_vehiculo";
    public final static String STATUS          = "status";
    public final static String TABLE_NAME      = "mq_mae_vehiculo";

    @Override
    public Vehiculos entityConfig() {
        setName(TABLE_NAME);
        setNickName("Vehiculo");
        addColumn(ID_EMPRESA, "integer");
        addColumn(ID_AREA, "integer");
        addColumn(CODIGO_GRUPO, "text");
        addColumn(CODIGO_SUBGRUPO,"integer");
        addColumn(CODIGO_VEHICULO,"integer");
        addColumn(STATUS,"integer");
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA", "30");
        String status  = "1";
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA, STATUS}, new String[]{empresa, status}));
    }
}