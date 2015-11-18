package com.cac.entities;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;

/**
 * Created by miguel on 11/11/15.
 */
public class TransactionDetails extends Entity {

    public static String ID_TRANSACCION = "id_transaccion";
    public static String CORRELATIVO = "correlativo";
    public static String PESO = "peso";
    public static String UNADA = "unada";
    public static String NAME = "ba_dtransaccion";

    @Override
    public Entity entityConfig() {
        setName(NAME);
        setNickName("Detalle Transaccion");
        addColumn(new EntityColumn<Integer>(ID_TRANSACCION, true));
        addColumn(new EntityColumn<Integer>(CORRELATIVO,EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Double>(PESO,EntityColumn.ColumnType.REAL).setNotNullable());
        addColumn(new EntityColumn<Integer>(UNADA, EntityColumn.ColumnType.INTEGER)
                .setOutServerColumn());
        return this;
    }
}
