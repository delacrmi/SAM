package com.cac.tools;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cac.entities.Transaccion;
import com.cac.entities.TransactionDetails;
import com.cac.sam.R;
import com.cac.viewer.CutterWorkFragment;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by miguel on 12/11/15.
 */
public class WorkDetailsAdapter extends RecyclerView.Adapter<ViewWorkHolder> {
    private List<TransactionDetails> transactionDetailsList;
    private int index = 0;

    private TextView tvRise;
    private TextView tvWeight;

    public WorkDetailsAdapter(List<TransactionDetails> transactionDetailsList){
        this.transactionDetailsList = transactionDetailsList;
        tvRise = CutterWorkFragment.getInstance().getTotalRaise();
        tvWeight = CutterWorkFragment.getInstance().getTotalWeight();
    }

    public void add(TransactionDetails transactionDetails){
        try {
            index = (int) ((TransactionDetails) ((LinkedList) transactionDetailsList)
                    .getFirst()).getColumn(TransactionDetails.UNADA).getValue()+1;
        }catch(NoSuchElementException e){
            index = 1;
        }

        ((LinkedList)transactionDetailsList).addFirst(transactionDetails);

        tvRise.setText((Integer.parseInt(tvRise.getText() + "") + 1) + "");

        BigDecimal newWeight = new BigDecimal(tvWeight.getText().toString())
                .add(new BigDecimal(transactionDetails.getColumn(TransactionDetails.PESO).getValue()+""));
        tvWeight.setText(newWeight.toString());

        notifyItemInserted(0);
        ((LinearLayoutManager)CutterWorkFragment.getInstance()
                .getRecycle().getLayoutManager()).scrollToPositionWithOffset(0, 0);
    }

    public void remove(int position){

        tvRise.setText((Integer.parseInt(tvRise.getText() + "")-1)+"");

        BigDecimal newWeight = new BigDecimal(tvWeight.getText().toString())
                .subtract(new BigDecimal(transactionDetailsList.get(position).getColumn(TransactionDetails.PESO).getValue()+""));

        tvWeight.setText(newWeight.toString());

        transactionDetailsList.remove(position);
        notifyItemRemoved(position);
        ((LinearLayoutManager)CutterWorkFragment.getInstance()
                .getRecycle().getLayoutManager()).scrollToPositionWithOffset(0, 0);

    }

    @Override
    public ViewWorkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("view",""+viewType);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cutter_details_view,null);
        ViewWorkHolder vwh = new ViewWorkHolder(v);
        return vwh;
    }

    @Override
    public void onBindViewHolder(ViewWorkHolder holder, int position) {
        TransactionDetails transactionDetails = transactionDetailsList.get(position);

        if(transactionDetails.getColumn(TransactionDetails.UNADA).getValue() == null)
            transactionDetails.getColumn(TransactionDetails.UNADA).setValue(index);

        holder.bindTableWork(transactionDetails);
    }

    @Override
    public int getItemCount() {
        return transactionDetailsList.size();
    }
}
