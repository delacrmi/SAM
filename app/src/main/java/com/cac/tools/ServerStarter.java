package com.cac.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cac.sam.MainActivity;
import com.cac.services.SyncServerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miguel on 03/11/15.
 */
public class ServerStarter extends BroadcastReceiver {
    public static JSONObject obj;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SyncServerService.OBJECT_SYNCHRONIZED)){
            Log.i("BroadcastReceiver",intent.getIntExtra("inserted",0)+"");
        }else if(intent.getAction().equals(MainActivity.intelWeight)){
            createObject(intent.getStringExtra("name"));
        }
    }

    private static void createObject(String params){
        try {
            obj = new JSONObject(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
