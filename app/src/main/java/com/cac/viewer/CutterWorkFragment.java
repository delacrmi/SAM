package com.cac.viewer;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.entities.Empleados;
import com.cac.entities.Transaccion;
import com.cac.entities.TransactionDetails;
import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.WorkDetailsAdapter;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 11/11/15.
 */
public class CutterWorkFragment extends Fragment implements MainComponentEdit<FloatingActionButton[]> {

    private static CutterWorkFragment ourInstance;
    public boolean writing = false;
    public AppCompatActivity context;

    private View view;
    private TextView tvCode;
    private EditText etLine;

    private AutoCompleteTextView autCutter;
    private Map<Integer,String> hashCutter = new HashMap<Integer,String>();
    private TextView tvCutter;

    private EditText etTotalRaise;
    private EditText etTotalWeight;
    private ImageView ivDeleteAll;
    private View.OnClickListener onClickListener;

    private RecyclerView recyclerView;
    private List<TransactionDetails> transactionDetailsList;
    private WorkDetailsAdapter workDetailsAdapter;
    private BroadcastReceiver receiver;

    private EntityManager entityManager;
    private SharedPreferences sharedPreferences;


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

            ourInstance.tvCode = (TextView)ourInstance.view.findViewById(R.id.tv_code_master_row);
            ourInstance.etLine = (EditText)ourInstance.view.findViewById(R.id.et_line_insert);


            ourInstance.autCutter = (AutoCompleteTextView)ourInstance.view.findViewById(R.id.atv_cutter_insert);
            ourInstance.tvCutter = (TextView)ourInstance.view.findViewById(R.id.tv_cutter_name_insert);
            ourInstance.autCutter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ourInstance.tvCutter.setText(hashCutter.get(Integer.parseInt(ourInstance.autCutter.getText() + "")));
                    InputMethodManager inputMethodManager =
                            (InputMethodManager)  ourInstance.context.getSystemService(ourInstance.context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            ourInstance.context.getCurrentFocus().getWindowToken(), 0);
                }
            });
            setCutterInformation();

            ourInstance.etTotalRaise = (EditText)ourInstance.view.findViewById(R.id.et_cutter_sum_raise);
            ourInstance.etTotalWeight = (EditText)ourInstance.view.findViewById(R.id.et_cutter_sum_weight);

            ourInstance.ivDeleteAll = (ImageView)ourInstance.view.findViewById(R.id.iv_cutter_delete_all);
            ourInstance.ivDeleteAll.setOnClickListener(onClickListener);

            ourInstance.transactionDetailsList = new LinkedList<TransactionDetails>();
            ourInstance.workDetailsAdapter = new WorkDetailsAdapter(ourInstance.transactionDetailsList);

            ourInstance.recyclerView = (RecyclerView)ourInstance.view.findViewById(R.id.recycle_cutter_insert);
            ourInstance.recyclerView.setLayoutManager(
                    new LinearLayoutManager(ourInstance.context, LinearLayoutManager.VERTICAL, false));
            ourInstance.recyclerView.setAdapter(workDetailsAdapter);


        }

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
                    transactionDetails.getColumn(TransactionDetails.PESO).setValue(obj.getDouble("weight"));
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
    }

    @Override
    public void onDestroy() {
        ourInstance.context.unregisterReceiver(ourInstance.receiver);
        super.onDestroy();
    }

    public void insert(JSONObject obj){
        try {
            Entity transactionDetails = new TransactionDetails().entityConfig();
            transactionDetails.setColumnValue(TransactionDetails.PESO,
                    Double.parseDouble(obj.getString("weight")));
            ourInstance.workDetailsAdapter.add((TransactionDetails)transactionDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mainViewConfig(FloatingActionButton[] buttons) {
        events();

        buttons[0].setImageResource(R.drawable.grabar);
        buttons[0].setOnClickListener(onClickListener);
        buttons[0].setVisibility(View.VISIBLE);

        buttons[1].setImageResource(R.drawable.anterior);
        buttons[1].setOnClickListener(onClickListener);
        buttons[1].setVisibility(View.VISIBLE);

    }

    private void events(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_fab_right:

                        //TODO: Auto Increament the envio options
                        int envio = 2;
                        Transaccion transaccion = new Transaccion().entityConfig();

                        transaccion.getColumn(Transaccion.EMPRESA).setValue(sharedPreferences.getInt("empresa",30));
                        transaccion.getColumn(Transaccion.PERIODO).setValue(sharedPreferences.getInt("periodo", 20));
                        transaccion.getColumn(Transaccion.NO_ENVIO).setValue(envio);
                        transaccion.getColumn(Transaccion.FRENTE_CORTE).setValue(1);
                        transaccion.getColumn(Transaccion.FRENTE_ALCE).setValue(1);

                        CuttingParametersFragment parameters =
                                ((MainActivity) ourInstance.context).getCuttingParametersFragment();
                        transaccion.getColumn(Transaccion.ID_FINCA)
                                .setValue(Integer.parseInt(parameters.getFinca().getText()+""));
                        transaccion.getColumn(Transaccion.ID_CANIAL)
                                .setValue(Integer.parseInt(parameters.getFinca().getText() + ""));
                        transaccion.getColumn(Transaccion.ID_LOTE)
                                .setValue(Integer.parseInt(parameters.getFinca().getText() + ""));

                        transaccion.getColumn(Transaccion.FECHA_CORTE).setValue(new Date());
                        transaccion = (Transaccion)ourInstance.entityManager.save(transaccion,true);

                        int detailsindex = 1;
                        if(transaccion != null){
                            for(TransactionDetails details : ourInstance.transactionDetailsList) {

                                details.getColumn(TransactionDetails.ID_TRANSACCION).setValue(envio);
                                details.getColumn(TransactionDetails.CORRELATIVO).setValue(detailsindex);
                                if(ourInstance.entityManager.save(details, true) != null)
                                    Log.i("inserted","uñada "+details.getColumn(TransactionDetails.UNADA).getValue()+
                                            " peso "+details.getColumn(TransactionDetails.PESO).getValue());

                                detailsindex++;
                            }

                            Snackbar.make(ourInstance.view,"Registro Guardado",Snackbar.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.btn_fab_left:
                        ((MainActivity)ourInstance.context).startTransactionByTagFragment(
                                ((MainActivity) ourInstance.context).getCuttingParametersFragment().getTAG());
                        break;
                    case R.id.iv_cutter_delete_all:
                        etLine.setText("");
                        autCutter.setText("");
                        tvCutter.setText("");
                        while (ourInstance.transactionDetailsList.size()>0)
                            ourInstance.workDetailsAdapter.remove(0);
                        break;
                }
            }
        };
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


    private void setCutterInformation(){
        List<Entity> entities = ourInstance.entityManager.find(Empleados.class,
                Empleados.ID_EMPLEADO+","+Empleados.NOMBRE,Empleados.ID_PLANILLA+" = ?",new String[]{"5"});

        String[] values = new String[entities.size()];
        int index = 0;

        for(Entity entity: entities){
            hashCutter.put((Integer)entity.getColumn(Empleados.ID_EMPLEADO).getValue()
                    ,(String)((Empleados)entity).getFullName());
            values[index] = entity.getColumn(Empleados.ID_EMPLEADO).getValue()+"";
            index++;
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ourInstance.context,
                android.R.layout.simple_dropdown_item_1line, values);
        ourInstance.autCutter.setAdapter(adapter);
        ourInstance.autCutter.setThreshold(1);

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
}
