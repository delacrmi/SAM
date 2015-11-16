package com.cac.tools;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cac.entities.TransactionDetails;
import com.cac.sam.R;

import java.util.List;

/**
 * Created by miguel on 12/11/15.
 */
public class WorkDetailsAdapter extends RecyclerView.Adapter<ViewWorkHolder> {
    private List<TransactionDetails> transactionDetailsList;

    public WorkDetailsAdapter(List<TransactionDetails> transactionDetailsList){
        this.transactionDetailsList = transactionDetailsList;
    }

    public void add(TransactionDetails transactionDetails,int position){
        transactionDetailsList.add(position,transactionDetails);
        notifyItemInserted(position);
    }

    public void add(TransactionDetails transactionDetails){
        transactionDetailsList.add(transactionDetails);
        notifyItemInserted((transactionDetailsList.size()-1));
    }

    public void remove(int position){
        transactionDetailsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewWorkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cutter_details_view,null);
        ViewWorkHolder vwh = new ViewWorkHolder(v);
        return vwh;
    }

    @Override
    public void onBindViewHolder(ViewWorkHolder holder, int position) {
        TransactionDetails transactionDetails = transactionDetailsList.get(position);
        holder.bindTableWork(transactionDetails,position+1);
    }

    @Override
    public int getItemCount() {
        return transactionDetailsList.size();
    }
}
