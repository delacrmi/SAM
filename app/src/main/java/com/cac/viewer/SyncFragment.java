package com.cac.viewer;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.SyncAdapter;
import com.cac.tools.ViewSyncHolder;
import com.delacrmi.connection.SocketConnect;
import com.delacrmi.controller.Entity;
import com.delacrmi.controller.EntityFilter;
import com.delacrmi.controller.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import io.socket.client.IO;

/**
 * Created by miguel on 19/10/15.
 */
public class SyncFragment extends Fragment implements MainComponentEdit<FloatingActionButton[]> {

    private static  SyncFragment ourInstance = null;
    private String uri;
    private SocketConnect connect;
    private boolean connected = false;
    private AppCompatActivity context;
    private EntityManager entityManager;
    private String URI;
    private IO.Options opts;
    private SharedPreferences sharedPreferences;

    private View view;
    public RecyclerView recyclerView;

    private List<Vector<Object>> list;
    private SyncAdapter syncAdapter;
    private Map<String,Integer> mapListPosition = new HashMap<String,Integer>();
    private Map<String,View> syncProgressElements = new HashMap<String,View>();

    private View.OnClickListener onClickListener;
    private int SYNCHRONIZING = 1;
    private int SYNCHRONIZED = 0;
    private int syncCount = 0;

    public int getSyncStatus(){
        if(ourInstance.syncCount == ourInstance.SYNCHRONIZED)
            return ourInstance.SYNCHRONIZED;
        else
            return ourInstance.SYNCHRONIZING;
    }


    public static SyncFragment init(AppCompatActivity context,EntityManager entityManager,String uri){
        if(ourInstance == null){
            ourInstance = new SyncFragment();
            ourInstance.context = context;
            ourInstance.entityManager = entityManager;
            ourInstance.uri = uri;
        }
        return ourInstance;
    }

    public static SyncFragment getInstance(){
        if(ourInstance == null)
            throw new NullPointerException("the SyncFragment isn't created, " +
                    "call the init method first to try use this instance");
        return ourInstance;
    }

    public SyncFragment(){}

    //<editor-fold desc="Override Method">

    @Override
    public String getTAG(){
        return "SyncFragment";
    }

    @Override
    public int getSubTitle() {
        return R.string.sync_title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(ourInstance.view == null)
            ourInstance.view = inflater.inflate(R.layout.sync_fragment_layout,container,false);

        initComponents();
        ourInstance.connect.init();

        return ourInstance.view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ourInstance.connect.getSocket().disconnect();
    }

    @Override
    public void mainViewConfig(FloatingActionButton[] buttons) {
        buttons[0].setImageResource(R.drawable.actualizar);
        buttons[0].setVisibility(View.VISIBLE);
        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ourInstance.syncAllTables();
            }
        });

        buttons[1].setVisibility(View.INVISIBLE);
    }

    @Override
    public void setContext(Context context) {
        ourInstance.context = (AppCompatActivity)context;
    }

    //</editor-fold>

    /*
    *==============================================================================================
    * Configuring the sync class information
    * =============================================================================================
    */
    private void initComponents() {

        ourInstance.recyclerView = (RecyclerView)ourInstance.view.findViewById(R.id.recycleSync);

        //The recycleView Size not change
        ourInstance.recyclerView.setHasFixedSize(true);

        fillList();

        ourInstance.syncAdapter = new SyncAdapter(ourInstance.list);

        ourInstance.recyclerView.setAdapter(syncAdapter);
        ourInstance.recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        ourInstance.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ourInstance.context);
        ourInstance.URI = ourInstance.sharedPreferences.getString("etp_uri1", "");

        ourInstance.opts = new IO.Options();
        ourInstance.opts.forceNew = true;
        ourInstance.opts.reconnection = false;
        ourInstance.socketInit();

    }

    private void fillList(){
        ourInstance.list = new ArrayList<Vector<Object>>();

        int count = 0;
        try{
            for (String name : ourInstance.entityManager.getTablesNames()){

                try {
                    Entity entity = ((Entity)entityManager.getClassByName(name).newInstance()).entityConfig();

                    if( entity.isSynchronizable() ) {
                        entity.configureEntityFilter(ourInstance.context);

                        Vector<Object> values = new Vector<Object>();

                        values.add(0, ourInstance.entityManager.getEntityNicName(name));
                        values.add(1, name);
                        values.add(2, String.valueOf(0));
                        if ( entity.getEntityFilter() != null ) {
                            values.add(3, entity.getEntityFilter().getCoditions(EntityFilter.ParamType.TWO_POINT));
                            values.add(4, entity.getEntityFilter().getValues());
                        }

                        ourInstance.list.add(values);

                        mapListPosition.put(name, count);
                        count++;
                    }

                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }catch (NullPointerException e){}

    }

    /*
    *==============================================================================================
    * Starting the socket connection class
    *==============================================================================================
    */
    private boolean socketInit(){
        if(ourInstance.connect == null)
            ourInstance.connect = new SocketConnect(ourInstance.context, ourInstance.uri,opts){

                @Override
                public void onSynchronizeClient(Object... args) {
                    try{
                        JSONObject obj = (JSONObject) args[0];
                        String tableName = obj.getString("tableName");

                        ProgressUpdate progressUpdate = new ProgressUpdate(tableName,
                                ourInstance.entityManager,obj.getJSONArray("result"));
                        ourInstance.syncCount += ourInstance.SYNCHRONIZING;
                        progressUpdate.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSyncSuccess(Object... args) {
                    super.onSyncSuccess(args);
                    //ourInstance.connected = true;
                    JSONObject obj = new JSONObject();
                    try{
                        obj.put("login","sync");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSyncReject(Object... args) {
                    super.onSyncReject(args);

                    JSONObject obj = (JSONObject) args[0];
                    try {
                        Log.e("onSyncReject","Message from server: "+obj.get("select"));
                        Log.e("onSyncReject","Message from server: "+obj.get("params"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorConnection() {
                    super.onErrorConnection();
                    if (ourInstance.URI.equals(ourInstance.sharedPreferences.getString("etp_uri1", ""))) {
                        ourInstance.URI = ourInstance.sharedPreferences.getString("etp_uri2", "");
                    } else {
                        ourInstance.URI = ourInstance.sharedPreferences.getString("etp_uri1", "");
                    }

                    ourInstance.connect.setURI(ourInstance.URI);
                    ourInstance.connect.init();
                }
            };
        else
            ourInstance.connect.getSocket().connect();

        return ourInstance.connect != null;
    }

    public AppCompatActivity getContext(){
        return ourInstance.context;
    }

    /**
    *==============================================================================================
    * @args: String tableName
    *        String where
    *        JSONArray whereValues
    *
    * This method return a json object with the attribute table
    * information to sync the client tables information
    * =============================================================================================
    */
    public JSONObject getJSONSelect(String tableName,String where, JSONArray whereValues){
        JSONArray array = new JSONArray();
        JSONObject obj  = new JSONObject();

        Iterator iterator = ourInstance.entityManager.
                initInstance(ourInstance.entityManager.getClassByName(tableName)).iterator();

        while (iterator.hasNext()){
            String columnName = ((Map.Entry)iterator.next()).getKey().toString();
            if(!columnName.equals(tableName + "_id"))
                array.put(columnName);
        }

        try {
            obj.put("table",tableName);
            obj.put("columns",array);

            if(where != null && !where.equals("") && !where.equals(" "))
                obj.put("where",where);

            if(whereValues != null && !whereValues.equals("") && !whereValues.equals(" "))
                obj.put("whereValue",whereValues);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  obj;
    }

    /**
    * =============================================================================================
    * @args JSONObject
    * This method send the client information if the application is connected
    * with the synchronizer server
    * =============================================================================================
    */
    public void syncTable(JSONObject tableJSONObject){
        if(ourInstance.socketInit())
            ourInstance.connect.sendMessage("synchronizerClient",tableJSONObject);
        else
            Log.e("Socket","is null ourInstance.connect ");
    }

    /*
    * Trying to sync all the client catalog information.
    */
    public void syncAllTables(){
        Iterator iterator = ourInstance.list.iterator();
        while (iterator.hasNext()){
            Vector<Object> vector = (Vector<Object>)iterator.next();
            if ( ((Vector<Object>)iterator.next()).size() == 5  ) {
                threadSynchronizer((String)vector.get(1), (String)vector.get(3), (JSONArray)vector.get(4));
            } else
                threadSynchronizer((String)vector.get(1), null, null);
        }
    }

    public void moveItemPosition(){
        Vector<Object> obj = ourInstance.list.get(0);
        for(int ind = 1; ind <= ourInstance.list.size()-1;ind++)
            ourInstance.list.set(ind-1, ourInstance.list.get(ind));

        ourInstance.list.set(7, obj);
        ourInstance.syncAdapter.notifyItemMoved(0,7);

        ((LinearLayoutManager) ourInstance.recyclerView.getLayoutManager())
                .scrollToPositionWithOffset(0, 0);
    }

    public void threadSynchronizer(String tableName, String whereCondition, JSONArray whereValues){
        ourInstance.syncTable(ourInstance.getJSONSelect(tableName, whereCondition, whereValues));
    }

    /**
    * =============================================================================================
    * Class ProgressUpdate
    * In this class we change the progress status of our progressBar view
    * if the progressUpdate table name is equals to view table name.
    *
    * @args: String TableName
    *        EntityManager entityManager
    *        JSONArray rows
    * =============================================================================================
    */
    public class ProgressUpdate extends AsyncTask<Void,Integer,Boolean>{

        Vector<Object> persistenceItem;
        EntityManager entityManager;
        JSONArray rows;
        String tableName;
        int position;

        public ProgressUpdate(String tableName, EntityManager entityManager,JSONArray rows){
            this.tableName = tableName;
            this.entityManager = entityManager;
            this.rows = rows;

            position = ourInstance.mapListPosition.get(tableName);
        }

        public void setPosition(int position){this.position = position;}

        @Override
        protected Boolean doInBackground(Void... params) {
            Class className = entityManager.getClassByName(tableName);

            entityManager.delete(className, null, null);

            try {
                for (int index = 0; index < rows.length(); index++) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    JSONObject row = (JSONObject) rows.getJSONObject(index);
                    ContentValues columns = new ContentValues();

                    Iterator iteratorKeys = row.keys();
                    while (iteratorKeys.hasNext()) {
                        String key = iteratorKeys.next().toString();
                        String value = row.getString(key);

                        if (value != null && !value.equals("") && !value.equals(" "))
                            columns.put(key.toLowerCase(), value);
                    }

                    Entity ent = entityManager.save(className, columns);
                    if(ent.getColumnValueList().getAsInteger(ent.getPrimaryKey()) > 0)
                        publishProgress(((int)((double)100/rows.length()*index)));
                }

                ourInstance.syncCount -= ourInstance.SYNCHRONIZING;
                publishProgress(100);

            }catch (JSONException e){
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            int progress = values[0];
            persistenceItem.set(2, String.valueOf(progress));

            try {
                ViewSyncHolder vsh = (ViewSyncHolder)
                        ourInstance.syncAdapter.getViewOfTable(tableName).getTag();
                if(vsh.tableName.equals(tableName))
                    vsh.pgb_sync.setProgress(progress);
                    vsh.tvProgress.setText(progress+"%");
            }catch (NullPointerException e){}

            if(ourInstance.syncCount == ourInstance.SYNCHRONIZED)
                Snackbar.make(ourInstance.view, R.string.syncSuccess, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {

            persistenceItem = ourInstance.list.get(position);
            persistenceItem.add(2, String.valueOf(0));
        }
    }
}
