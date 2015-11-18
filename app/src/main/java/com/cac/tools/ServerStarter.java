package com.cac.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.cac.sam.MainActivity;
import com.cac.services.SyncServerService;
import com.cac.viewer.CutterWorkFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miguel on 03/11/15.
 */
public class ServerStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SyncServerService.OBJECT_SYNCHRONIZED)){
            Log.i("BroadcastReceiver", intent.getIntExtra("inserted", 0) + "");
        }
    }
}
