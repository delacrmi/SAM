package com.delacrmi.connection;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by miguel on 13/10/15.
 */
public abstract class SocketConnect {

    private Socket socket;
    private AppCompatActivity context;
    private String URI;

    public SocketConnect(AppCompatActivity context,String URI){
        this.context = context;
        initComponents(URI);
    }

    public SocketConnect(String URI){
        initComponents(URI);
    }

    private void initComponents(String URI){
        this.URI = URI;
        try {
            socket = IO.socket(URI);
            socket.on("synchronizeClient", onSynchronizeClient);
            socket.on("synchronizeServer", onSynchronizeServer);
            socket.on("syncReject",onSynchronizeReject);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT,onConnectSuccess);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setURI(String URI){
        socket.disconnect();
        initComponents(URI);
    }

    public void init(){
        socket.connect();
    }

    public Socket getSocket() {
        return socket;
    }

    public void onSynchronizeClient(final Object ... args){}
    public void onSynchronizeServer(final Object ... args){}
    public abstract void onSyncSuccess(final Object ... args);

    public void onErrorConnection(){
        Log.e("Connection", "The socket.io isn't connected to "+URI);
    }

    public void onSyncReject(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        try {
            Log.e("error", obj.getString("error"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String event,JSONObject json){
        socket.emit(event,json);
    }

    private Emitter.Listener onConnectError = new Emitter.Listener(){

        @Override
        public void call(Object... args) {
            if (context != null)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onErrorConnection();
                    }
                });
            else
                onErrorConnection();
        }
    };

    private Emitter.Listener onSynchronizeClient = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            if (context != null)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSynchronizeClient(args);
                    }
                });
            else
                onSynchronizeClient(args);
        }
    };

    private Emitter.Listener onSynchronizeServer = new Emitter.Listener(){
            @Override
            public void call(final Object... args) {
                onSynchronizeServer(args);
            }
    };

    private Emitter.Listener onSynchronizeReject = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            if (context != null)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSyncReject(args);
                    }
                });
            else
                onSyncReject(args);
        }
    };

    private Emitter.Listener onConnectSuccess = new Emitter.Listener(){
        @Override
        public void call(final Object... args) {
            if (context != null)
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSyncSuccess(args);
                    }
                });
            else
                onSyncSuccess(args);
        }
    };
}
