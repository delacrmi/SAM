package com.cac.viewer;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.entities.TransactionDetails;
import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.ViewWorkHolder;
import com.cac.tools.WorkDetailsAdapter;
import com.delacrmi.persistences.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    private EditText etCutter;
    private EditText etTotalRaise;
    private EditText etTotalWeight;
    private ImageView ivDeleteAll;
    private View.OnClickListener onClickListener;

    private RecyclerView recyclerView;
    private List<TransactionDetails> transactionDetailsList;
    private WorkDetailsAdapter workDetailsAdapter;
    private BroadcastReceiver receiver;


    public static CutterWorkFragment init(AppCompatActivity context){
        if(ourInstance == null) {
            ourInstance = new CutterWorkFragment();
            ourInstance.context = context;
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
            ourInstance.ivDeleteAll = (ImageView)ourInstance.view.findViewById(R.id.iv_cutter_delete_all);
            ourInstance.etLine = (EditText)ourInstance.view.findViewById(R.id.et_line_insert);
            ourInstance.etCutter = (EditText)ourInstance.view.findViewById(R.id.atv_cutter_insert);

            ourInstance.etTotalRaise = (EditText)ourInstance.view.findViewById(R.id.et_cutter_sum_raise);
            ourInstance.etTotalWeight = (EditText)ourInstance.view.findViewById(R.id.et_cutter_sum_weight);

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

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(MainActivity.intelWeight)){
                    Toast.makeText(context,intent.getStringExtra("json"),Toast.LENGTH_SHORT);
                    JSONObject obj;

                    try {
                        obj = new JSONObject(intent.getStringExtra("json"));
                        Entity transactionDetails = new TransactionDetails().entityConfig();
                        transactionDetails.setValue("peso",Double.parseDouble(obj.getString("weight")));
                        workDetailsAdapter.add((TransactionDetails)transactionDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        };
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
                        Toast.makeText(ourInstance.context,
                                ((MainActivity) context).getCuttingParametersFragment().getTAG(),
                                Toast.LENGTH_SHORT).show();
                        TransactionDetails t = (TransactionDetails)new TransactionDetails().entityConfig();
                        t.getColumn("peso").setValue(300);
                        workDetailsAdapter.add(t);
                        break;
                    case R.id.btn_fab_left:
                        ((MainActivity)ourInstance.context).startTransactionByTagFragment(
                                ((MainActivity) ourInstance.context).getCuttingParametersFragment().getTAG());
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


    public void removeViewWorkHolder(int position) {
        ourInstance.workDetailsAdapter.remove(position);
        ((LinearLayoutManager)ourInstance.recyclerView.getLayoutManager()).scrollToPositionWithOffset(0,0);

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
