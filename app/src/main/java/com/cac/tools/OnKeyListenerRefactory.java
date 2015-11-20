package com.cac.tools;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by miguel on 20/11/15.
 */
public class OnKeyListenerRefactory implements View.OnKeyListener {
    Map<String,String> values;
    View target;
    public OnKeyListenerRefactory(Map<String,String> values,View target){
        this.values = values;
        this.target = target;
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == 66){
            EditText view = (EditText)v;

            if(values.containsKey(view.getText().toString())){
                if(target instanceof TextView)
                    ((TextView)target).setText(values.get(view.getText().toString()));
            }else{
                ((TextView)target).setText("");
                view.setError("Valor Incorrecto");
                return true;
            }

        }
        return false;
    }
}
