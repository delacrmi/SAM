package com.cac.entities;

import com.delacrmi.controller.Entity;

/**
 * Created by miguel on 11/11/15.
 */
public class TransactionDetails extends Entity {

    @Override
    public Entity entityConfig() {
        setName("ba_dtransaccion");
        setNickName("Detalle Transaccion");
        return this;
    }
}
