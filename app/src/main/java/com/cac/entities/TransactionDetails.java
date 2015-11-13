package com.cac.entities;

import com.delacrmi.controller.Entity;
import com.delacrmi.controller.EntityColumn;

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
        addColumn(new EntityColumn<Integer>("peso",EntityColumn.ColumnType.INTEGER).setNotNullable());
        return this;
    }
}
