package com.cac.tools;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cac.entities.TransactionDetails;
import com.cac.sam.R;
import com.cac.viewer.CutterWorkFragment;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by miguel on 12/11/15.
 */
public class WorkDetailsAdapter extends RecyclerView.Adapter<ViewWorkHolder> {
    private List<TransactionDetails> transactionDetailsList;
    private int index = 0;

    public WorkDetailsAdapter(List<TransactionDetails> transactionDetailsList){
        this.transactionDetailsList = transactionDetailsList;
    }

    public void add(TransactionDetails transactionDetails){
        try {
            index = (int) ((TransactionDetails) ((LinkedList) transactionDetailsList)
                    .getFirst()).getColumn("index").getValue()+1;
        }catch(NoSuchElementException e){
            index = 1;
        }

        ((LinkedList)transactionDetailsList).addFirst(transactionDetails);
        notifyItemInserted(0);
        ((LinearLayoutManager)CutterWorkFragment.getInstance()
                .getRecycle().getLayoutManager()).scrollToPositionWithOffset(0, 0);

        TextView tvRise = CutterWorkFragment.getInstance().getTotalRaise();
        TextView tvWeight = CutterWorkFragment.getInstance().getTotalWeight();

        tvRise.setText((Integer.parseInt(tvRise.getText()+"")+1)+"");
        tvWeight.setText((Double.parseDouble(tvWeight.getText()+"")+
                ((double)transactionDetails.getColumn("peso").getValue()))+"");
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

        if(transactionDetails.getColumn("index").getValue() == null)
            transactionDetails.getColumn("index").setValue(index);

        holder.bindTableWork(transactionDetails);
    }

    @Override
    public int getItemCount() {
        return transactionDetailsList.size();
    }
}
