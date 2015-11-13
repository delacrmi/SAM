package com.cac.entities;

import android.content.Context;
import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 10/10/15.
 */
public class Frentes extends Entity {
    //"CREATE TABLE RH_FRENTE(ID_FRENTE INTEGER, DESCRIPCION TEXT)"

    public static String ID_FRENTE   = "id_frente";
    public static String DESCRIPCION = "descripcion";
    public static String TABLE_NAME  = "RH_FRENTE";
    public static String TIPO_CANIA  = "tipo_cania";

    @Override
    public Frentes entityConfig() {
        setName(TABLE_NAME);
        setNickName("Frente");
        addColumn(ID_FRENTE, "integer");
        addColumn(DESCRIPCION,"text");
        addColumn(TIPO_CANIA,"text");
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
    }
}
