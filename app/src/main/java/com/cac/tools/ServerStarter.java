package com.cac.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cac.entities.Transaccion;
import com.cac.entities.TransactionDetails;
import com.cac.sam.MainActivity;
import com.cac.services.SyncServerService;
import com.delacrmi.persistences.EntityManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miguel on 03/11/15.
 */
public class ServerStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SyncServerService.OBJECT_SYNCHRONIZED)){

        }
    }
}
