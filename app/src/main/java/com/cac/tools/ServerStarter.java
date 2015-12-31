package com.cac.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.cac.services.SyncServerService;

/**
 * Created by miguel on 03/11/15.
 */
public class ServerStarter extends BroadcastReceiver {

    private MainActivity mainActivity;

    public ServerStarter(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SyncServerService.STATUS_CONNECTION)){
            int status = intent.getIntExtra("status",SyncServerService.UNCONNECTED);
            try {
                if (status == SyncServerService.CONNECTED) {
                    mainActivity.getStatusConnection()
                            .setIcon(R.drawable.plugged)
                            .setTitle(R.string.plugged);
                } else if (status == SyncServerService.UNCONNECTED) {
                    mainActivity.getStatusConnection()
                            .setIcon(R.drawable.unplugged)
                            .setTitle(R.string.unplugged);
                }
            }catch(NullPointerException e){}
        }
    }
}
