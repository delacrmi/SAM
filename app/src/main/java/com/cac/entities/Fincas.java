package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Fincas extends Entity {
    //CP_FINCA(ID_EMPRESA INTEGER, ID_FINCA INTEGER, DESCRIPCION TEXT, UBICACION TEXT)";

    @Override
    public Fincas entityConfig() {
        setName("id_finca");
        addColumn("id_empresa", "integer");
        addColumn("descripcion", "text");
        addColumn("ubicacion","text");

        return this;
    }
}
