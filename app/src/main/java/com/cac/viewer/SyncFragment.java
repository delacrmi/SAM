package com.cac.viewer;

import android.app.Fragment;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * Created by miguel on 19/10/15.
 */
public class SyncFragment extends Fragment implements MainComponentEdit {
    private static  SyncFragment outInstance = null;
    private String uri;
    private SocketConnect connect;
    private AppCompatActivity context;
    private EntityManager entityManager;

    private View view;
    public RecyclerView recyclerView;

    private List<Vector<String>> list;
    private SyncAdapter syncAdapter;
    private Map<String,Integer> mapListPosition = new HashMap<String,Integer>();
    private Map<String,View> syncProgressElements = new HashMap<String,View>();

    private View.OnClickListener onClickListener;
    private int SYNCHRONIZING = 1;
    private int SYNCHRONIZED = 0;
    private int syncCount = 0;

    public int getSyncStatus(){
        if(outInstance.syncCount == outInstance.SYNCHRONIZED)
            return outInstance.SYNCHRONIZED;
        else
            return outInstance.SYNCHRONIZING;
    }


    public static SyncFragment init(AppCompatActivity context,EntityManager entityManager,String uri){
        if(outInstance == null){
            outInstance = new SyncFragment();
            outInstance.context = context;
            outInstance.entityManager = entityManager;
            outInstance.uri = uri;
        }
        return outInstance;
    }

    public static SyncFragment getInstance(){
        if(outInstance == null)
            throw new NullPointerException("the SyncFragment isn't created, " +
                    "call the init method first to try use this instance");
        return outInstance;
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
        super.onCreateView(inflater, container, savedInstanceState);
        if(view == null)
            view = inflater.inflate(R.layout.sync_fragment_layout,container,false);

        initComponents();
        outInstance.connect.init();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        outInstance.connect.getSocket().disconnect();
    }

    @Override
    public void onClickFloating() {
        outInstance.syncAllTables();
    }

    @Override
    public void FloatingButtonConfig(FloatingActionButton floatingActionButton) {
        floatingActionButton.setImageResource(R.drawable.actualizar);
        floatingActionButton.setVisibility(outInstance.view.VISIBLE);
    }

    //</editor-fold>

    /*
    *==============================================================================================
    * Configuring the sync class information
    * =============================================================================================
    */
    private void initComponents() {

        outInstance.socketInit();

        outInstance.recyclerView = (RecyclerView)view.findViewById(R.id.recycleSync);

        //The recycleView Size not change
        outInstance.recyclerView.setHasFixedSize(true);

        fillList();

        outInstance.syncAdapter = new SyncAdapter(outInstance.list);

        outInstance.recyclerView.setAdapter(syncAdapter);
        outInstance.recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

    }

    private void fillList(){
        outInstance.list = new ArrayList<Vector<String>>();

        int count = 0;
        try{
            for (String name : outInstance.entityManager.getTablesNames()){

                if(!name.equals("ba_mtransaccion")){
                    Vector<String> values = new Vector<String>();

                    values.add(0, outInstance.entityManager.getEntityNicName(name));
                    values.add(1, name);
                    values.add(2, String.valueOf(0));
                    outInstance.list.add(values);

                    mapListPosition.put(name, count);
                    count++;
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
        if(outInstance.connect == null)
            outInstance.connect = new SocketConnect(outInstance.context,outInstance.uri){

                @Override
                public void onSynchronizeClient(Object... args) {
                    try{
                        JSONObject obj = (JSONObject) args[0];
                        String tableName = obj.getString("tableName");

                        ProgressUpdate progressUpdate = new ProgressUpdate(tableName,
                                outInstance.entityManager,obj.getJSONArray("result"));
                        outInstance.syncCount += outInstance.SYNCHRONIZING;
                        progressUpdate.execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                public void onSyncReject(Object... args) {
                    JSONObject obj = (JSONObject) args[0];
                    /*try {
                        outInstance.syncProgressElements.get(obj.getString("tableName")).pgb_sync.setIndeterminate(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

                    super.onSyncReject(args);
                }
            };
        else
            outInstance.connect.getSocket().connect();

        return outInstance.connect != null;
    }

    public AppCompatActivity getContext(){
        return outInstance.context;
    }

    /*
    *==============================================================================================
    * This method to initialize all the event
    * =============================================================================================
    */

    public void events(){
        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        };
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

        Iterator iterator = outInstance.entityManager.
                initInstance(outInstance.entityManager.getClassByName(tableName)).iterator();

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
        if(outInstance.socketInit())
            outInstance.connect.sendMessage("synchronizerClient",tableJSONObject);
        else
            Log.e("Socket","is null outInstance.connect ");

    }

    /*
    * Trying to sync all the client catalog information.
    */
    public void syncAllTables(){
        Iterator iterator = outInstance.list.iterator();
        while (iterator.hasNext()){
            threadSynchronizer(((Vector<String>)iterator.next()).get(1));
        }
    }

    public void moveItemPosition(){
        Vector<String> obj = outInstance.list.get(0);
        for(int ind = 1; ind <= outInstance.list.size()-1;ind++)
            outInstance.list.set(ind-1, outInstance.list.get(ind));

        outInstance.list.set(7, obj);
        outInstance.syncAdapter.notifyItemMoved(0,7);

        ((LinearLayoutManager)outInstance.recyclerView.getLayoutManager())
                .scrollToPositionWithOffset(0, 0);
    }

    public void threadSynchronizer(String tableName){
        outInstance.syncTable(outInstance.getJSONSelect(tableName, null, null));
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

        Vector<String> persistenceItem;
        EntityManager entityManager;
        JSONArray rows;
        String tableName;
        int position;

        public ProgressUpdate(String tableName, EntityManager entityManager,JSONArray rows){
            this.tableName = tableName;
            this.entityManager = entityManager;
            this.rows = rows;

            position = outInstance.mapListPosition.get(tableName);
        }

        public void setPosition(int position){this.position = position;}

        @Override
        protected Boolean doInBackground(Void... params) {
            Class className = entityManager.getClassByName(tableName);

            entityManager.delete(className, null, null);

            int parts = (int)Math.round(100 / rows.length());
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
                        publishProgress(parts*index);
                }

                outInstance.syncCount -= outInstance.SYNCHRONIZING;
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
                        outInstance.syncAdapter.getViewOfTable(tableName).getTag();
                if(vsh.tableName.equals(tableName))
                    vsh.pgb_sync.setProgress(progress);
                    vsh.tvProgress.setText(progress+"%");
            }catch (NullPointerException e){}

            if(outInstance.syncCount == outInstance.SYNCHRONIZED)
                Snackbar.make(outInstance.view, R.string.syncSuccess, Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {

            persistenceItem = outInstance.list.get(position);
            persistenceItem.add(2, String.valueOf(0));
        }
    }
}
