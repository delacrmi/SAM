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
    public static String NOMBRE = "nombre1";
    public static String NOMBRE2 = "nombre2";
    public static String APELLIDO = "apellido1";
    public static String APELLIDO2 = "apellido2";
    public static String ESTADO_EMPLEADO = "estado_empleado";
    public static final String ID_PLANILLA = "id_planilla";
    public static String TABLE_NAME = "rh_empleado";

    @Override
    public Empleados entityConfig() {
        setName(Empleados.TABLE_NAME);
        setNickName("Empleado");
        addColumn(Empleados.ID_EMPLEADO, EntityColumn.ColumnType.INTEGER);
        addColumn(Empleados.ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(Empleados.NOMBRE, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.NOMBRE2, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.APELLIDO, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.APELLIDO2, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.ESTADO_EMPLEADO, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.ID_PLANILLA, EntityColumn.ColumnType.INTEGER);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA", "30");
        String estadoEmpleado = "A";
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA, ESTADO_EMPLEADO}, new String[]{empresa, estadoEmpleado}));
    }

    public String getFullName(){
        return (getColumn(NOMBRE).getValue() != null ? getColumn(NOMBRE).getValue()+"" : "") +
               (getColumn(NOMBRE2).getValue() != null ? " "+getColumn(NOMBRE2).getValue(): "") +
               (getColumn(APELLIDO).getValue() != null ? " "+getColumn(APELLIDO).getValue(): "") +
               (getColumn(APELLIDO2).getValue() != null ? " "+getColumn(APELLIDO2).getValue(): "");
    }
}