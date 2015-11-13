package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.delacrmi.controller.Entity;
import com.delacrmi.controller.EntityFilter;

/**
 * Created by miguel on 10/10/15.
 */
public class Fincas extends Entity {
    //CP_FINCA(ID_EMPRESA INTEGER, ID_FINCA INTEGER, DESCRIPCION TEXT, UBICACION TEXT)";

    public static String ID_FINCA    = "id_finca";
    public static String DESCRIPCION = "descripcion";
    public static String TABLE_NAME  = "cp_finca";
    public static String UBICACION   = "ubicacion";
    public static String ID_EMPRESA  = "id_empresa";

    @Override
    public Fincas entityConfig() {
        setName(TABLE_NAME);
        setNickName("Finca");
        addColumn(ID_FINCA, "integer");
        addColumn(DESCRIPCION, "text");
        addColumn(UBICACION,"text");
        addColumn(ID_EMPRESA,"integer");
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA", "30");
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA}, new String[]{empresa}));
    }

}