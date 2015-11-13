package com.cac.entities;



import android.content.Context;
import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class SubGrupoVehiculos extends Entity {
    //"CREATE TABLE MQ_SUBGRUPO_VEHICULO(ID_EMPRESA INTEGER,CODIGO_GRUPO TEXT,CODIGO_SUBGRUPO INTEGER,DESCRIPCION TEXT)";

    public static String ID_EMPRESA = "id_empresa";
    public static String CODIGO_GRUPO = "codigo_grupo";
    public static String CODIGO_SUBGRUPO = "codigo_subgrupo";
    public static String DESCRIPCION  = "descripcion";
    public static String TABLE_NAME   = "mq_subgrupo_vehiculo";

    @Override
    public SubGrupoVehiculos entityConfig() {
        setName(TABLE_NAME);
        addColumn(ID_EMPRESA, "integer");
        addColumn(CODIGO_GRUPO, "text");
        addColumn(CODIGO_SUBGRUPO,"integer");
        addColumn(DESCRIPCION,"text");
        setSynchronizable(false);
        return this;
    }
    @Override
    public void configureEntityFilter(Context context) {
    }

}