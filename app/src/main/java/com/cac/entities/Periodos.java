package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Periodos extends Entity {
    //"CREATE TABLE PG_PERIODO(ID_EMPRESA INTEGER, ID_PERIODO INTEGER,FECHA_INI REAL,FECHA_FIN REAL,DESCRIPCION TEXT)"

    @Override
    public Periodos entityConfig() {
        setName("periodo");
        setPrimaryKey("id_periodo");
        addColumn("id_empresa", "integer");
        addColumn("fecha_ini", "date");
        addColumn("fecha_fin", "date");
        addColumn("descripcion","text");

        return this;
    }


}
