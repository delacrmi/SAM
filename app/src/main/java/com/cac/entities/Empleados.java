package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityFilter;

/**
 * Created by miguel on 10/10/15.
 */
public class Empleados extends Entity{
    public static String ID_EMPLEADO = "id_empleado";
    public static String ID_EMPRESA = "id_empresa";
    public static String NOMBRE = "nombre";
    public static String TABLE_NAME = "inf_view_empleado_cortador";

    @Override
    public Empleados entityConfig() {
        setName(Empleados.TABLE_NAME);
        setNickName("Cortadores");
        addColumn(Empleados.ID_EMPLEADO, EntityColumn.ColumnType.INTEGER);
        addColumn(Empleados.ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(Empleados.NOMBRE, EntityColumn.ColumnType.TEXT);
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