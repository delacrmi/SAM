package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Empresas extends Entity {

    public static String ID_EMPRESA = "id_empresa";
    public static String DIRECCION_COMERCIAL = "direccion_comercial";
    public static String TABLE_NAME = "pg_empresa";

    private boolean selected = false;

    public Empresas(){

    }

    @Override
    public Empresas entityConfig() {
        setName(Empresas.TABLE_NAME);
        setNickName("Empresa");
        addColumn(ID_EMPRESA,"integer");
        addColumn(DIRECCION_COMERCIAL,"text");
        setSynchronizable(true);
        return this;
    }

}