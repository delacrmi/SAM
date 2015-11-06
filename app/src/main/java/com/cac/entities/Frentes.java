package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Frentes extends Entity {
    //"CREATE TABLE RH_FRENTE(ID_FRENTE INTEGER, DESCRIPCION TEXT)"

    @Override
    public Frentes entityConfig() {
        setName("rh_frente");
        setPrimaryKey("id_frente");
        addColumn("description","text");
        return this;
    }
}
