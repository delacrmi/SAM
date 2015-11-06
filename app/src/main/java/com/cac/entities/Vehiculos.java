package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Vehiculos extends Entity {
    //"CREATE TABLE MQ_VEHICULO(ID_EMPRESA INTEGER,AREA INTEGER,CODIGO_GRUPO TEXT, CODIGO_SUBGRUPO INTEGER, CODIGO_VEHICULO INTEGER)";

    @Override
    public Vehiculos entityConfig() {
        setName("mq_vehiculo");
        addColumn("id_empresa", "integer");
        addColumn("area", "integer");
        addColumn("codigo_grupo", "text");
        addColumn("codigo_vehiculo","integer");
        return this;
    }
}
