package com.cac.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cac.sam.R;
import com.cac.viewer.SyncFragment;

import java.util.Vector;


/**
 * Created by miguel on 28/10/15.
 */
public class ViewSyncHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public ImageButton btn_sync;
    public ProgressBar pgb_sync;
    public TextView tvProgress;
    public String tableName;
    private View view;

    private View.OnClickListener onClickListener;

    public ViewSyncHolder(View itemView) {
        super(itemView);

        events();

        view = itemView;

        title = (TextView)itemView.findViewById(R.id.tv_sync_name);
        btn_sync = (ImageButton)itemView.findViewById(R.id.btn_sync);

        pgb_sync = (ProgressBar)itemView.findViewById(R.id.pgb_sync);
        pgb_sync.getLayoutParams().height = 10;

        tvProgress = (TextView) itemView.findViewById(R.id.tv_sync_progress);

        btn_sync.setOnClickListener(onClickListener);
        //btn_sync.setTag(itemView);

        itemView.setTag(this);

    }

    public void bindTableSync(Vector<String> tableInfo){
        title.setText(tableInfo.get(0));
        tableName = tableInfo.get(1);
        pgb_sync.setProgress(Integer.parseInt(tableInfo.get(2)));
        tvProgress.setText(tableInfo.get(2)+"%");
    }

    public View getView(){
        return view;
    }

    public void events(){
        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SyncFragment.getInstance().threadSynchronizer(ViewSyncHolder.this.tableName);
            }
        };
    }
}
