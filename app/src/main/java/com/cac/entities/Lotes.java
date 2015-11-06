package com.cac.entities;

import android.util.Log;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Lotes extends Entity {
    //"CREATE TABLE CP_LOTE(ID_EMPRESA INTEGER, ID_FINCA INTEGER, ID_CANIAL INTEGER, ID_LOTE INTEGER, DESCIPCION TEXT)";

    @Override
    public Lotes entityConfig() {
        setName("cp_lote");
        setPrimaryKey("id_lote");
        addColumn("id_empresa", "integer");
        addColumn("id_finca", "integer");
        addColumn("id_canial", "integer");
        addColumn("descripcion", "text");

        return this;
    }
}
