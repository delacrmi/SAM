package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class SubGrupoVehiculos extends Entity {
    //"CREATE TABLE MQ_SUBGRUPO_VEHICULO(ID_EMPRESA INTEGER,CODIGO_GRUPO TEXT,CODIGO_SUBGRUPO INTEGER,DESCRIPCION TEXT)";

    @Override
    public SubGrupoVehiculos entityConfig() {
        setName("mq_subgrupo_vehiculo");
        addColumn("id_empresa", "integer");
        addColumn("codigo_grupo", "text");
        addColumn("codigo_subgrupo","integer");
        addColumn("descripcion","text");

        return this;
    }
}
