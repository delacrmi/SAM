package com.cac.viewer;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atorres.AndroidUtils;
import com.cac.entities.Empleados;
import com.cac.entities.Rangos;
import com.cac.entities.Transaccion;
import com.cac.entities.TransactionDetails;
import com.cac.entities.Vehiculos;
import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.OnKeyListenerRefactory;
import com.cac.tools.WorkDetailsAdapter;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 11/11/15.
 */
public class CutterWorkFragment extends Fragment implements MainComponentEdit<View[]> {

    private static CutterWorkFragment ourInstance;
    private RelativeLayout layout;
    public boolean writing = false;
    public AppCompatActivity context;

    private View view;
    private EditText etCode;
    private EditText etMap;
    private EditText etLine;

    private AutoCompleteTextView autTractor;
    private Map<String,String> hashTractor = new HashMap<String,String>();

    private AutoCompleteTextView autCutter;
    private Map<String,String> hashCutter = new HashMap<String,String>();
    private TextView tvCutter;
    private BigDecimal latitude;
    private  BigDecimal longitude;

    private EditText etTotalRaise;
    private EditText etTotalWeight;
    private ImageView ivDeleteAll;

    private View.OnClickListener onClickListener;
    private View.OnFocusChangeListener onFocusChangeListener;
    private OnKeyListenerRefactory onKeyListenerRefactory;

    private RecyclerView recyclerView;
    private List<TransactionDetails> transactionDetailsList;
    private WorkDetailsAdapter workDetailsAdapter;
    private BroadcastReceiver receiver;

    private EntityManager entityManager;
    private SharedPreferences sharedPreferences;

    //Filtros queries
    private String EMPRESA, PERIODO, DISPOSITIVO, APLICACION;


    public static CutterWorkFragment init(AppCompatActivity context,EntityManager entityManager){
        if(ourInstance == null) {
            ourInstance = new CutterWorkFragment();
            ourInstance.context = context;
            ourInstance.entityManager = entityManager;
            ourInstance.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return ourInstance;
    }

    public static CutterWorkFragment getInstance(){
        if(ourInstance == null)
            ourInstance = new CutterWorkFragment();
        return ourInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(ourInstance.view == null) {

            ourInstance.view = inflater.inflate(R.layout.cutter_work_view, container, false);
            ourInstance.layout = (RelativeLayout)ourInstance.view.findViewById(R.id.cutter_head_layout);

            ourInstance.etCode = (EditText)ourInstance.view.findViewById(R.id.tv_code_master_row);

            ourInstance.etMap = (EditText)ourInstance.view.findViewById(R.id.et_cutter_map);
            ourInstance.etMap.setOnFocusChangeListener(onFocusChangeListener);
            ourInstance.etLine = (EditText)ourInstance.view.findViewById(R.id.et_line_insert);
            ourInstance.etLine.setOnFocusChangeListener(onFocusChangeListener);

            ourInstance.autTractor = (AutoCompleteTextView)ourInstance.view.findViewById(R.id.aut_tractor_insert);
            ourInstance.autTractor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    etLine.requestFocus();
                    /*InputMethodManager inputMethodManager =
                            (InputMethodManager) ourInstance.context.getSystemService(ourInstance.context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            ourInstance.context.getCurrentFocus().getWindowToken(), 0);*/
                }
            });
            ourInstance.autTractor.setAdapter(findCodigosVehiculos(Vehiculos.class, "A"));
            ourInstance.autTractor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus && ((AutoCompleteTextView) v).getText().length() <= 1) addInitValue((TextView)v,"A"+((AutoCompleteTextView) v).getText());
                }
            });
            /*ourInstance.autTractor.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    String values;
                    try{
                        if (((AutoCompleteTextView) v).getText().length() == 1 &&
                                Integer.parseInt(((AutoCompleteTextView) v).getText()+"") != 2){
                            values = ((AutoCompleteTextView) v).getText()+"";
                            ((AutoCompleteTextView) v).setText("");

                            if(Integer.parseInt(values) <= 1) ((AutoCompleteTextView) v).append("A180" + values);
                            else if(Integer.parseInt(values) <= 3) ((AutoCompleteTextView) v).append("A090" + values);

                        }else if(((AutoCompleteTextView) v).getText().length() == 2){
                            values = ((AutoCompleteTextView) v).getText()+"";
                            ((AutoCompleteTextView) v).setText("");

                            if(Integer.parseInt(values) > 20) ((AutoCompleteTextView) v).append("A090" + values);
                            else ((AutoCompleteTextView) v).append("A180" + values);
                        }
                    }catch (NumberFormatException e){
                        ((AutoCompleteTextView) v).setText("");
                        return true;
                    }
                    return false;
                }
            });*/
            ourInstance.autTractor.setThreshold(2);

            ourInstance.autCutter = (AutoCompleteTextView)ourInstance.view.findViewById(R.id.aut_cutter_insert);
            ourInstance.tvCutter = (TextView)ourInstance.view.findViewById(R.id.tv_cutter_name_insert);
            ourInstance.autCutter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ourInstance.tvCutter.setText(hashCutter.get(ourInstance.autCutter.getText().toString()));
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) ourInstance.context.getSystemService(ourInstance.context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            ourInstance.context.getCurrentFocus().getWindowToken(), 0);
                }
            });
            ourInstance.autCutter.setOnKeyListener(new OnKeyListenerRefactory(hashCutter, ourInstance.tvCutter));
            ourInstance.autCutter.setAdapter(
                    getAdapter(Empleados.class, Empleados.ID_EMPLEADO + " key," + Empleados.NOMBRE + " value", null, null, hashCutter)
            );
            ourInstance.autCutter.setThreshold(1);

            ourInstance.etTotalRaise = (EditText)ourInstance.view.findViewById(R.id.et_cutter_sum_raise);
            ourInstance.etTotalWeight = (EditText)ourInstance.view.findViewById(R.id.et_cutter_sum_weight);

            ourInstance.ivDeleteAll = (ImageView)ourInstance.view.findViewById(R.id.iv_cutter_delete_all);
            ourInstance.ivDeleteAll.setOnClickListener(onClickListener);

            ourInstance.transactionDetailsList = new LinkedList<TransactionDetails>();
            //addValueTest();

            ourInstance.workDetailsAdapter = new WorkDetailsAdapter(ourInstance.transactionDetailsList);

            ourInstance.recyclerView = (RecyclerView)ourInstance.view.findViewById(R.id.recycle_cutter_insert);
            ourInstance.recyclerView.setLayoutManager(
                    new LinearLayoutManager(ourInstance.context, LinearLayoutManager.VERTICAL, false));
            ourInstance.recyclerView.setAdapter(ourInstance.workDetailsAdapter);
        }

        EMPRESA =  sharedPreferences.getString("EMPRESA","30");
        PERIODO =  sharedPreferences.getString("PERIODO","19");
        APLICACION = sharedPreferences.getString("NOMBRE_APLICACION","SAM");
        TelephonyManager telephonyManager = (TelephonyManager)ourInstance.context.getSystemService(Context.TELEPHONY_SERVICE);
        DISPOSITIVO = telephonyManager.getDeviceId();

        ourInstance.writing = true;
        return ourInstance.view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ourInstance.receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JSONObject obj;

                try {
                    JSONArray json = new JSONArray(intent.getStringExtra("json"));
                    obj = new JSONObject(json.getString(0));
                    TransactionDetails transactionDetails = (TransactionDetails)new TransactionDetails().entityConfig();
                    transactionDetails.getColumn(TransactionDetails.PESO).setValue(obj.getDouble("weight")/1000);

                    latitude = new BigDecimal(obj.optDouble("latitude")+"");
                    longitude = new BigDecimal(obj.optDouble("longitude")+"");
                    transactionDetails.getColumn(TransactionDetails.LATITUD).setValue(latitude.doubleValue());
                    transactionDetails.getColumn(TransactionDetails.LONGITUD).setValue(longitude.doubleValue());
                    transactionDetails.getColumn(TransactionDetails.ID_TRACTOR).setValue(
                            autTractor.getText().toString()
                    );
                    ourInstance.workDetailsAdapter.add(transactionDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter("se.oioi.intelweigh");
        try {
            ourInstance.context.registerReceiver(ourInstance.receiver, intentFilter);
        }catch (NullPointerException npe){}

        ourInstance.etCode.setText(findMaxEnvio()+"");
        etMap.requestFocus();

    }

    @Override
    public void onDestroy() {
        ourInstance.context.unregisterReceiver(ourInstance.receiver);
        super.onDestroy();
    }

    public void insert(JSONObject obj){
        try {
            Entity transactionDetails = new TransactionDetails().entityConfig();
            transactionDetails.setColumnFromSelect(TransactionDetails.PESO,
                    Double.parseDouble(obj.getString("weight")));
            ourInstance.workDetailsAdapter.add((TransactionDetails)transactionDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mainViewConfig(View[] views) {
        events();

        views[0].getLayoutParams().height = MainActivity.VISIBLE_ACTION;
        views[0].invalidate();

        ((ImageButton)views[1]).setImageResource(R.drawable.grabar);
        views[1].setOnClickListener(onClickListener);
        views[1].setVisibility(View.VISIBLE);
        views[1].invalidate();

        ((ImageButton)views[2]).setImageResource(R.drawable.anterior);
        views[2].setOnClickListener(onClickListener);
        views[2].setVisibility(View.VISIBLE);
        views[2].invalidate();

    }

    private void events(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_fab_right:

                        if(!validateForm(layout))
                            return;

                        int envio = Integer.parseInt(ourInstance.etCode.getText().toString());
                        if ( envio == 0 ) {
                            AndroidUtils.showAlertMsg(ourInstance.context, "Notificación", "No se encontro el número de envio.");
                            return;
                        }
                        Transaccion transaccion = new Transaccion().entityConfig();

                        transaccion.getColumn(Transaccion.EMPRESA).setValue(EMPRESA);
                        transaccion.getColumn(Transaccion.PERIODO).setValue(PERIODO);
                        transaccion.getColumn(Transaccion.NO_ENVIO).setValue(envio);

                        CuttingParametersFragment parameters =
                                ((MainActivity) ourInstance.context).getCuttingParametersFragment();
                        transaccion.getColumn(Transaccion.FRENTE_CORTE)
                                .setValue(parameters.getFteCorte());
                        transaccion.getColumn(Transaccion.FRENTE_ALCE)
                                .setValue(parameters.getFteAlce());
                        transaccion.getColumn(Transaccion.ID_FINCA)
                                .setValue(Integer.parseInt(parameters.getFinca().getText() + ""));
                        transaccion.getColumn(Transaccion.ID_CANIAL)
                                .setValue(Integer.parseInt(parameters.getCanial()));
                        transaccion.getColumn(Transaccion.ID_LOTE)
                                .setValue(Integer.parseInt(parameters.getLote()));

                        transaccion.getColumn(Transaccion.FECHA_CORTE).setValue(new Date());
                        transaccion.getColumn(Transaccion.FECHA_ENVIO).setValue(new Date());

                        transaccion.getColumn(Transaccion.CORTADOR)
                                .setValue(Integer.parseInt(ourInstance.autCutter.getText() + ""));
                        transaccion.getColumn(Transaccion.UNADA)
                                .setValue(Integer.parseInt(ourInstance.etTotalRaise.getText() + ""));
                        transaccion.getColumn(Transaccion.PESO)
                                .setValue(Double.parseDouble(ourInstance.etTotalWeight.getText() + ""));
                        transaccion.getColumn(Transaccion.LINEA)
                                .setValue(Integer.parseInt(ourInstance.etLine.getText().toString()));


                        transaccion.getColumn(Transaccion.APLICACION).setValue(APLICACION);
                        transaccion.getColumn(Transaccion.DISPOSITIVO).setValue(DISPOSITIVO);
                        transaccion.getColumn(Transaccion.ESTADO).setValue(Transaccion.TransaccionEstado.ACTIVA.toString());
                        transaccion.getColumn(transaccion.MAPA_CORTE).setValue(
                                Integer.parseInt(etMap.getText().toString()));

                        transaccion = (Transaccion)ourInstance.entityManager.save(transaccion);

                        int detailsindex = 1;

                        if(transaccion != null){
                            for(TransactionDetails details : ourInstance.transactionDetailsList) {

                                details.getColumn(TransactionDetails.EMPRESA).setValue(EMPRESA);
                                details.getColumn(TransactionDetails.ID_PERIODO).setValue(PERIODO);
                                details.getColumn(TransactionDetails.APLICACION).setValue(APLICACION);
                                details.getColumn(TransactionDetails.NO_RANGO).setValue(envio);
                                details.getColumn(TransactionDetails.CORRELATIVO).setValue(detailsindex);
                                details.getColumn(TransactionDetails.ESTADO).setValue(
                                        TransactionDetails.TransactionDetailsEstado.ACTIVA.toString());
                                if(ourInstance.entityManager.save(details) != null)
                                    Log.i("inserted","uñada "+details.getColumn(TransactionDetails.UNADA).getValue()+
                                            " peso "+details.getColumn(TransactionDetails.PESO).getValue());

                                detailsindex++;
                            }

                            Snackbar.make(ourInstance.view,"Registro Guardado",Snackbar.LENGTH_SHORT).show();

                            autCutter.setText("");
                            tvCutter.setText("");
                            deleteDetails();

                            ourInstance.etCode.setText(findMaxEnvio()+"");
                        }

                        break;
                    case R.id.btn_fab_left:
                        ((MainActivity)ourInstance.context).startTransactionByTagFragment(
                                ((MainActivity) ourInstance.context).getCuttingParametersFragment().getTAG());
                        break;
                    case R.id.iv_cutter_delete_all:
                        deleteAll();
                        break;
                }
            }
        };

        onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText edT =(EditText) v;
                if(!hasFocus && edT.getText()
                        .toString().equals("")){
                    edT.setError("");
                }
            }
        };
    }

    public void deleteAll(){
        etMap.setText("");
        etLine.setText("");
        autCutter.setText("");
        tvCutter.setText("");
        autTractor.setText("");
        deleteDetails();
    }

    private void deleteDetails(){
        while (ourInstance.transactionDetailsList.size()>0)
            ourInstance.workDetailsAdapter.remove(0);
    }

    public int findMaxEnvio (){

        int envioActual = 0;

        //Buscamos los numeros permitidos a generar.

        Log.d("Parametros", EMPRESA+" "+PERIODO+" "+APLICACION+" "+DISPOSITIVO);

        Rangos rangos =  (Rangos) ourInstance.entityManager.findOnce(Rangos.class,"*",
                        Rangos.ID_EMPRESA+" = ? and "+Rangos.ID_PERIODO+" = ? and "+
                        Rangos.APLICACION+" = ? and "+Rangos.DISPOSITIVO+" = ?",
                new String[]{EMPRESA, PERIODO, APLICACION, DISPOSITIVO});
        if ( rangos == null ) {
            AndroidUtils.showAlertMsg(ourInstance.context, "Notificación", "Debe actualizar la tabla de rangos para proseguir.");
            return 0;
        }

        int minEnvio = (Integer)rangos.getValue(Rangos.RANGO_DESDE);
        int maxEnvio = (Integer)rangos.getValue(Rangos.RANGO_HASTA);

        Entity transaccionTemp = ourInstance.entityManager.findOnce(
                Transaccion.class,"max("+Transaccion.NO_ENVIO+")+1 "+Transaccion.NO_ENVIO,
                Transaccion.EMPRESA+" = ? and "+Rangos.ID_PERIODO+" = ?",
                new String[]{EMPRESA, PERIODO});
        if ( transaccionTemp != null ){
            try {
                envioActual = transaccionTemp.getColumnValueList().getAsInteger(Transaccion.NO_ENVIO);
            } catch (Exception ex) { envioActual = minEnvio; }
        }

        if ( maxEnvio == 0 || minEnvio == 0 ) {
            AndroidUtils.showAlertMsg(ourInstance.context,"Notificación","No se han definido los correlativos de envio, favor actualizar la información.");
            return 0;
        }else if ( envioActual > maxEnvio ){
            AndroidUtils.showAlertMsg(ourInstance.context,"Notificación","El dispositivo ha excedido el número maximo de envios, solo tiene permitido generar hasta "+maxEnvio+" Envios y el envio actual es el "+envioActual);
            return 0;
        } else if ( envioActual == 0 ) {
            envioActual = minEnvio;
        } else if (minEnvio > envioActual) envioActual = minEnvio;

        return envioActual;
    }

    @Override
    public String getTAG() {
        return "CutterWorkFragment";
    }

    @Override
    public int getSubTitle() {
        return R.string.work;
    }

    @Override
    public void setContext(Context context) {
        ourInstance.context = (AppCompatActivity)context;
    }

    private ArrayAdapter<String> getAdapter(Class entityClass, String columns, String where, String[] whereValue, Map<String, String> map){
        List<Entity> entities = ourInstance.entityManager.find(entityClass,columns,where,whereValue);

        String[] values = new String[entities.size()];
        int index = 0;

        for(Entity entity: entities){
            map.put(entity.getColumnsFromSelect().getAsString("key")
                    ,entity.getColumnsFromSelect().getAsString("value"));
            values[index] = entity.getColumnsFromSelect().getAsString("key");
            index++;
        }

         ArrayAdapter<String> adapter = new ArrayAdapter<String>(ourInstance.context,
                android.R.layout.simple_dropdown_item_1line, values);
        return adapter;

    }

    public void removeViewWorkHolder(int position) {
        ourInstance.workDetailsAdapter.remove(position);
    }

    public RecyclerView getRecycle(){
        return ourInstance.recyclerView;
    }

    public EditText getTotalRaise() {
        return etTotalRaise;
    }

    public void setEtTotalRaise(EditText etTotalRaise) {
        this.etTotalRaise = etTotalRaise;
    }

    public EditText getTotalWeight() {
        return etTotalWeight;
    }

    public void setEtTotalWeight(EditText etTotalWeight) {
        this.etTotalWeight = etTotalWeight;
    }

    public boolean validateForm(RelativeLayout layout) {
        if ( layout != null ){
            for (View a : layout.getFocusables(RelativeLayout.FOCUS_BACKWARD)){
                if ( a instanceof EditText) {
                    EditText editText = (EditText) a;
                    if(editText.getText().toString().equals("") ||
                            editText.getText().toString().equals(" ")){
                        Snackbar.make(a,"Campo Requerido",Snackbar.LENGTH_SHORT).show();
                        a.requestFocus();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void addValueTest(){
        for(int a=0; a<3; a++){
            TransactionDetails transactionDetails = (TransactionDetails)new TransactionDetails().entityConfig();
            transactionDetails.getColumn(TransactionDetails.UNADA).setValue(a + 1);
            transactionDetails.getColumn(TransactionDetails.PESO).setValue(300.0 / 1000);

            ((LinkedList)ourInstance.transactionDetailsList).addFirst(transactionDetails);

            ourInstance.getTotalRaise().setText((a + 1) + "");
            ourInstance.getTotalWeight().setText(
                    new BigDecimal(ourInstance.getTotalWeight().getText() + "")
                            .add(new BigDecimal(transactionDetails.getColumn(Transaccion.PESO).getValue()+"")).toString()
            );
        }
    }

    private ArrayAdapter<String> findCodigosVehiculos(Class entidad, String codigoGrupo ) {

        List<Entity> entidades =  ourInstance.entityManager.find(entidad, "*", Vehiculos.CODIGO_GRUPO + " = '" + codigoGrupo + "' and " + Vehiculos.STATUS + " = 1", null);
        List<String> listado = new ArrayList<>();
        for ( Entity a : entidades ){
            String descripcion = a.getColumnValueList().getAsString(Vehiculos.CODIGO_GRUPO);
            descripcion += String.format("%02d", Integer.parseInt(a.getColumnValueList().getAsString(Vehiculos.CODIGO_SUBGRUPO)));
            descripcion += String.format("%03d", Integer.parseInt(a.getColumnValueList().getAsString(Vehiculos.CODIGO_VEHICULO)));
            listado.add(descripcion);
        }

        if ( listado != null && !listado.isEmpty() ) {
            Collections.sort(listado);
            return new ArrayAdapter<>(ourInstance.context, android.R.layout.simple_selectable_list_item, listado);
        }else
            return new ArrayAdapter<>(ourInstance.context, android.R.layout.simple_selectable_list_item, new ArrayList<String>());
    }

    public void addInitValue(TextView t, String value){
        t.append(value);
    }

}
