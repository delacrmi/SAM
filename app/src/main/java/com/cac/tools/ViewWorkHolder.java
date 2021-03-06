package com.cac.tools;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cac.entities.TransactionDetails;
import com.cac.sam.R;
import com.cac.viewer.CutterWorkFragment;

/**
 * Created by miguel on 11/11/15.
 */
public class ViewWorkHolder extends RecyclerView.ViewHolder {

    public EditText etRaise;
    public EditText etWeight;
    private ImageView ivDelete;

    public ViewWorkHolder(View itemView) {
        super(itemView);

        etRaise = (EditText)itemView.findViewById(R.id.et_cutter_details_raise);
        etWeight = (EditText)itemView.findViewById(R.id.et_cutter_details_weight);
        ivDelete = (ImageView)itemView.findViewById(R.id.iv_cutter_details_delete);
        events();
    }

    public void bindTableWork(TransactionDetails details){
        etRaise.setText(details.getColumn(TransactionDetails.UNADA).getValue()+"");
        etWeight.setText(details.getColumn(TransactionDetails.PESO).getValue()+"");
    }

    private void events(){
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v,""+getPosition()+" "+((int)v.getTag()),Snackbar.LENGTH_SHORT).show();
                CutterWorkFragment.getInstance().removeViewWorkHolder(getPosition());
            }
        });
    }
}
