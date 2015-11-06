package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Empleados extends Entity{
    //"CREATE TABLE RH_EMPLEADO(ID_EMPRESA INTEGER,ID_EMPLEADO INTEGER, ID_PUESTO INTEGER, NOMBRE TEXT, ESTADO TEXT)";

    @Override
    public Empleados entityConfig() {
        setName("rh_empleado");
        setPrimaryKey("id_empleado");
        addColumn("id_empresa", "integer");
        addColumn("id_puesto", "integer");
        addColumn("nombre", "text");
        addColumn("estado","text");

        return this;
    }
}
