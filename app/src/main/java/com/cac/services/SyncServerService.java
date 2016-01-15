package com.cac.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cac.entities.TransactionDetails;
import com.cac.sam.R;
import com.cac.entities.Transaccion;
import com.delacrmi.connection.SocketConnect;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;

public class SyncServerService extends Service {

    public final static String SAM = "com.cac.services.sam";
    public final static int UPDATE_SUCCESS = 0;
    public final static int UPDATE_REJECTED = 1;
    public final static int CONNECTED = 2;
    public final static int UNCONNECTED = 3;
    public final static int LAST_SERVER_SYNC = 4;

    private String TAG = "services";
    private Thread thread;
    private Boolean threadRunning;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String defaultStringDate = "29/12/2015 17:56";
    private boolean toSend = false;

    private EntityManager entityManager;
    IO.Options opts;
    private SocketConnect connect;
    private String URI;
    private SharedPreferences sharedPreferences;

    private int updateCount = 0;

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
                Intent iSend= new Intent();
                iSend.setAction(SAM);

                while (threadRunning){

                    isTimeOver();

                    iSend.putExtra("status", CONNECTED);
                    sendBroadcast(iSend);

                    try{
                        Log.d("connected ",connect.getSocket().connected()+"");
                        if(connect.getSocket().connected()){

                            List<Entity> entities = getEntityManager().find(Transaccion.class, "*",
                                    Transaccion.ESTADO + " = ? ",
                                    new String[]{Transaccion.TransaccionEstado.ACTIVA.toString()});

                            if ( entities != null && entities.size() > 0 ) {
                                toSend = true;
                                connect.sendMessage("synchronizerServer", getInformationToSend(entities, Transaccion.TABLE_NAME));
                            }

                            updateCount += entities.size();

                            List<Entity> entities1 = getEntityManager().find(TransactionDetails.class, "*",
                                    TransactionDetails.ESTADO + " = ? ",
                                    new String[]{TransactionDetails.TransactionDetailsEstado.ACTIVA.toString()});

                            if ( entities1 != null && entities1.size() > 0 ) {
                                toSend = true;
                                connect.sendMessage("synchronizerServer", getInformationToSend(entities1, TransactionDetails.TABLE_NAME));
                            }

                            updateCount += entities.size();

                            if(updateCount == 0 && toSend) updateTimerSync();
                        }else{
                            iSend.putExtra("status", UNCONNECTED);
                            sendBroadcast(iSend);
                        }
                    } catch (NullPointerException e){

                    } catch (JSONException ex) {
                        Log.e("Error","Al insertar una fila en el JSONObject ",ex);
                    }

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

    public JSONObject getInformationToSend(List<Entity> registros, String tableName) throws JSONException {

        JSONArray rows = new JSONArray();
        JSONArray dates = new JSONArray();

        for (Entity entity : registros ) {

            JSONObject row = new JSONObject();
            JSONObject values = new JSONObject();
            row.put("tableName",tableName);

            String[] columns =  entity.getColumnsNameAsString(tableName.equals(TransactionDetails.TABLE_NAME)).split(",");

            for (int i = 0; i < columns.length; i++) {
                if ( columns[i].toLowerCase().contains("fecha") ) {
                    Long fechaLong = Long.valueOf(entity.getColumnValueList().getAsString(columns[i])).longValue();
                    Date fecha = new Date(fechaLong);
                    String fechaString = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
                    values.put(columns[i], fechaString);

                    //Columnas de fechas.
                    if (rows.length() == 0)
                        dates.put(columns[i]);

                } else
                    values.put(columns[i],entity.getColumnValueList().getAsString(columns[i]));
            }
            row.put("values",values);
            rows.put(row);
        }

        if ( rows != null && rows.length() > 0 ) {
            JSONObject wrapper = new JSONObject();
            wrapper.put("value", rows);
            wrapper.put("dates",dates);
            return wrapper;
        }

        return new JSONObject();
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
                    updateCount --;
                    iSend.setAction(SAM);
                    if ( args  != null && args[0] instanceof  JSONObject) {
                        try {
                            JSONObject obj = (JSONObject) args[0];
                            updateInfo(obj.get("tableName").toString(),new JSONObject(obj.get("result").toString()));
                            if(updateCount == 0){
                                iSend.putExtra("inserted", UPDATE_SUCCESS);
                                sendBroadcast(iSend);
                                updateTimerSync();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onSyncReject(Object... args) {
                    super.onSyncReject(args);
                    Intent iSend = new Intent();
                    iSend.setAction(SAM);
                    iSend.putExtra("reject", UPDATE_REJECTED);
                    sendBroadcast(iSend);
                }

                @Override
                public void onSyncSuccess(Object... args) {
                    JSONObject obj = new JSONObject();
                    Intent iSend = new Intent();
                    iSend.setAction(SAM);
                    iSend.putExtra("status", CONNECTED);
                    sendBroadcast(iSend);

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

    private void connectSocket(){
        Intent iSend = new Intent();
        iSend.setAction(SAM);
        iSend.putExtra("status", UNCONNECTED);
        sendBroadcast(iSend);

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

    private void updateInfo(String tableName, JSONObject value){
        try {
            String noEnvio = (String) value.get(Transaccion.NO_ENVIO);
            String empresa = (String) value.get(Transaccion.EMPRESA);
            String periodo = (String) value.get(Transaccion.PERIODO);
            String aplicacion = (String) value.get(Transaccion.APLICACION);
            if ( !noEnvio.equals("0") || !noEnvio.equals(" ") ) {

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
                    String correlativo = (String) value.get(TransactionDetails.CORRELATIVO);
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

    private void updateTimerSync(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        toSend = false;

        String date = sdf.format(new Date());
        editor.putString("etp_last_execution",date);
        editor.commit();

        Intent intent = new Intent();
        intent.setAction(SAM);
        intent.putExtra("status", LAST_SERVER_SYNC);
    }

    private boolean isTimeOver(){
        boolean isOver = true;
        int sendMount = 1;

        try{
            sendMount = Integer.parseInt(sharedPreferences.getString("etp_interval", "1"));
        }catch (ClassCastException e){}

        try {

            Calendar lastTime = Calendar.getInstance();
            lastTime.setTime(sdf.parse(sharedPreferences.getString("etp_last_execution", defaultStringDate)));
            Calendar actualTime = Calendar.getInstance();
            if(actualTime.before(lastTime)
                    || ((actualTime.getTimeInMillis() - lastTime.getTimeInMillis())/1000) < sendMount) isOver = false;

            //Log.i("Calendar",lastTime.toString()+" "+actualTime.toString()
            //        +" "+((actualTime.getTimeInMillis() - lastTime.getTimeInMillis()) / 1000) +" "+ sendMount);

            actualTime.setTime(sdf.parse(defaultStringDate));
            if(lastTime.getTimeInMillis() == actualTime.getTimeInMillis()) updateTimerSync();

        } catch (ParseException e) {}

        return isOver;
    }
}
