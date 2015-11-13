package com.cac.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cac.sam.R;
import com.cac.entities.Transaccion;
import com.delacrmi.connection.SocketConnect;
import com.delacrmi.controller.Entity;
import com.delacrmi.controller.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.IO;

public class SyncServerService extends Service {

    public static String SYNCHRONIZE_STARTED = "com.cac.services.SYNCHRONIZE_STARTED";
    public static String OBJECT_SYNCHRONIZED = "com.cac.services.OBJECT_SYNCHRONIZED";
    public static String SYNCHRONIZE_END = "com.cac.services.SYNCHRONIZE_END";
    private String TAG = "services";
    private Thread thread;
    private Boolean threadRunning;

    private EntityManager entityManager;
    IO.Options opts;
    private SocketConnect connect;
    private String URI;
    private SharedPreferences sharedPreferences;

    public SyncServerService() {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Servicio creado...");
        threadRunning = true;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        getEntityManager()
                .addTable(Transaccion.class)
                .init();

        URI = sharedPreferences.getString("etp_uri1", "");
        opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;
        socketInit(URI);
        connect.init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Servicio iniciado...");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (threadRunning){
                    try{
                        Log.d("connected ",connect.getSocket().connected()+"");
                        if(connect.getSocket().connected()){
                            JSONArray rowArray;
                            JSONObject row;
                            String columns = new Transaccion().entityConfig().getColumnsNameAsString(false);
                            Log.i("columns", columns);
                        }
                    }catch (NullPointerException e){}

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        threadRunning = false;
        Log.d(TAG, "Servicio destruido...");
    }

    public EntityManager getEntityManager() {
        if ( entityManager == null ) {
            entityManager = new EntityManager(this,
                    getResources().getString(R.string.db_name),
                    null,
                    Integer.parseInt(getResources().getString(R.string.db_version)));
        }
        return entityManager;
    }

    private void socketInit(String uri){
        if(connect == null)
            connect = new SocketConnect(uri,opts){

                @Override
                public void onSynchronizeServer(Object... args) {
                    Intent iSend = new Intent();
                    iSend.setAction(OBJECT_SYNCHRONIZED);
                    iSend.putExtra("inserted",2);
                    sendBroadcast(iSend);
                }

                @Override
                public void onSyncSuccess(Object... args) {
                    JSONObject obj = new JSONObject();
                    try{
                        obj.put("login","sync");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected() {
                    super.onDisconnected();
                    connectSocket();
                }

                @Override
                public void onErrorConnection() {
                    super.onErrorConnection();
                    connectSocket();
                }
            };
    }

    public void connectSocket(){
        if (URI.equals(sharedPreferences.getString("etp_uri1", ""))) {
            URI = sharedPreferences.getString("etp_uri2", "");
        } else {
            URI = sharedPreferences.getString("etp_uri1", "");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        connect.setURI(URI);
        connect.init();
    }
}
