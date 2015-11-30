package com.cac.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.cac.entities.Transaccion;
import com.cac.sam.MainActivity;
import com.cac.services.SyncServerService;
import com.cac.viewer.CutterWorkFragment;
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
                if ( !noEnvio.equals("0") || !noEnvio.equals(" ") ) {
                    MainActivity mainActivity = (MainActivity) context;
                    EntityManager entityManager = mainActivity.getEntityManager();
                    Transaccion transaccion = (Transaccion) entityManager.findOnce(
                            Transaccion.class,"*",
                            Transaccion.NO_ENVIO+" = ? and "+Transaccion.EMPRESA+" = ? and "+
                                    Transaccion.PERIODO+" = ? and "+Transaccion.APLICACION+" = ? ",
                            new String[]{noEnvio, empresa, periodo, aplicacion}
                    );
                    transaccion.setValue(Transaccion.INDICADOR, Transaccion.TransaccionEstado.TRASLADADA.toString());
                    entityManager.update(transaccion,Transaccion.NO_ENVIO+" = ?", new String[]{noEnvio},false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex){
                Log.e("Error","Al recivir la informacion: ",ex);
            }
        }
    }
}
