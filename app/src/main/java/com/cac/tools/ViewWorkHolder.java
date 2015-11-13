package com.cac.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cac.entities.TransactionDetails;
import com.cac.sam.R;

/**
 * Created by miguel on 11/11/15.
 */
public class ViewWorkHolder extends RecyclerView.ViewHolder {

    private EditText etRaise;
    private EditText etWeight;
    private ImageView ivDelete;

    public ViewWorkHolder(View itemView) {
        super(itemView);

        etRaise = (EditText)itemView.findViewById(R.id.et_cutter_details_raise);
        etWeight = (EditText)itemView.findViewById(R.id.et_cutter_details_weight);
        ivDelete = (ImageView)itemView.findViewById(R.id.iv_cutter_details_delete);
    }

    public void bindTableWork(TransactionDetails details){
        etRaise.setText(details.getColumnValueList()
                 .getAsString("correlativo"));
        etWeight.setText(details.getColumnValueList()
                 .getAsString("peso"));
        ivDelete.setTag(details.getColumnValueList()
                .getAsString(details.getPrimaryKey()));
    }
}
