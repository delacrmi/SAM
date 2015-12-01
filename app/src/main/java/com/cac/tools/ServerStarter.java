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
            try {
                JSONObject obj = new JSONObject(intent.getStringExtra("inserted"));
                String noEnvio = (String) obj.get(Transaccion.NO_ENVIO);
                String empresa = (String) obj.get(Transaccion.EMPRESA);
                String periodo = (String) obj.get(Transaccion.PERIODO);
                String aplicacion = (String) obj.get(Transaccion.APLICACION);
                String tableName = intent.getStringExtra("tableName");
                if ( !noEnvio.equals("0") || !noEnvio.equals(" ") ) {

                    MainActivity mainActivity = (MainActivity) context;
                    EntityManager entityManager = mainActivity.getEntityManager();

                    if ( tableName.equalsIgnoreCase(Transaccion.TABLE_NAME) ) {
                        Transaccion transaccion = (Transaccion) entityManager.findOnce(
                                Transaccion.class, "*",
                                Transaccion.NO_ENVIO + " = ? and " + Transaccion.EMPRESA + " = ? and " +
                                Transaccion.PERIODO + " = ? and " + Transaccion.APLICACION + " = ? ",
                                new String[]{noEnvio, empresa, periodo, aplicacion}
                        );
                        transaccion.setValue(Transaccion.ESTADO,Transaccion.TransaccionEstado.TRASLADADA.toString());
                        entityManager.update(Transaccion.class,
                                transaccion.getColumnValueList(),
                                Transaccion.NO_ENVIO + " = ? and " + Transaccion.EMPRESA + " = ? and " +
                                Transaccion.PERIODO + " = ? and " + Transaccion.APLICACION + " = ? ",
                                new String[]{noEnvio, empresa, periodo, aplicacion},false);
                    } else if ( tableName.equalsIgnoreCase(TransactionDetails.TABLE_NAME ) ) {
                        String correlativo = (String) obj.get(TransactionDetails.CORRELATIVO);
                        TransactionDetails transaccion = (TransactionDetails) entityManager.findOnce(
                                TransactionDetails.class, "*",
                                TransactionDetails.NO_RANGO + " = ? and " + TransactionDetails.EMPRESA + " = ? and " +
                                TransactionDetails.ID_PERIODO + " = ? and " +
                                TransactionDetails.APLICACION + " = ? and " +
                                TransactionDetails.CORRELATIVO+ " = ?",
                                new String[]{noEnvio, empresa, periodo, aplicacion, correlativo}
                        );
                        transaccion.setValue(TransactionDetails.ESTADO,TransactionDetails.TransactionDetailsEstado.TRASLADADA.toString());
                        entityManager.update(TransactionDetails.class,
                                transaccion.getColumnValueList(),
                                TransactionDetails.NO_RANGO + " = ? and " + TransactionDetails.EMPRESA + " = ? and " +
                                TransactionDetails.ID_PERIODO + " = ? and " +
                                TransactionDetails.APLICACION + " = ? and " +
                                TransactionDetails.CORRELATIVO+ " = ?",
                                new String[]{noEnvio, empresa, periodo, aplicacion, correlativo},false);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex){
                Log.e("Error","Al recivir la informacion: ",ex);
            }
        }
    }
}
