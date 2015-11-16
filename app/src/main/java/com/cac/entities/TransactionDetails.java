package com.cac.entities;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;

/**
 * Created by miguel on 11/11/15.
 */
public class TransactionDetails extends Entity {

    @Override
    public Entity entityConfig() {
        setName("ba_dtransaccion");
        setNickName("Detalle Transaccion");
        addColumn(new EntityColumn<Integer>("id_transaccion", true));
        addColumn(new EntityColumn<Integer>("correlativo",EntityColumn.ColumnType.INTEGER).setNotNullable());
        addColumn(new EntityColumn<Double>("peso",EntityColumn.ColumnType.REAL).setNotNullable());
        addColumn(new EntityColumn<Integer>("index", EntityColumn.ColumnType.INTEGER)
                .setNotNullable()
                .setOutServerColumn());
        return this;
    }
}
