package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Caniales extends Entity {
    //"CREATE TABLE CP_CANIAL(ID_EMPRESA INTEGER, ID_FINCA INTEGER,ID_CANIAL INTEGER, DESCIPCION TEXT)";

    @Override
    public Caniales entityConfig() {
        setName("cap_canial");
        setPrimaryKey("id_canial");
        addColumn("id_empresa", "integer");
        addColumn("id_finca", "integer");
        addColumn("descripcion","text");

        return this;
    }
}
