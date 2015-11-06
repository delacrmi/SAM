package com.cac.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cac.sam.R;
import com.delacrmi.connection.SocketConnect;
import com.delacrmi.controller.EntityManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SyncServerService extends Service {

    public static String SYNCHRONIZE_STARTED = "com.cac.services.SYNCHRONIZE_STARTED";
    public static String OBJECT_SYNCHRONIZED = "com.cac.services.OBJECT_SYNCHRONIZED";
    public static String SYNCHRONIZE_END = "com.cac.services.SYNCHRONIZE_END";
    private String TAG = "services";
    private Thread thread;
    private Boolean threadRunning;

    private EntityManager entityManager;
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
        getEntityManager();

        URI = sharedPreferences.getString("etp_uri1","");
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
                    Log.i("Test", "Intent Service Test "+connect.getSocket().connected());
                    Intent iSend = new Intent();
                    iSend.setAction(OBJECT_SYNCHRONIZED);
                    iSend.putExtra("inserted",2);
                    sendBroadcast(iSend);
                    try {
                        Thread.sleep(60000);
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
            connect = new SocketConnect(uri){

                @Override
                public void onSynchronizeServer(Object... args) {

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
                public void onErrorConnection() {
                    super.onErrorConnection();
                    if(URI.equals(sharedPreferences.getString("etp_uri1",""))){
                        URI = sharedPreferences.getString("etp_uri2","");
                        connect.setURI(URI);
                    }else{
                        URI = sharedPreferences.getString("etp_uri1","");
                        connect.setURI(URI);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    connect.init();

                }
            };
    }
}
