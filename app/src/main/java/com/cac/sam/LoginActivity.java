package com.cac.sam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by delacrmi on 12/10/2015.
 */
public class LoginActivity extends FragmentActivity {

    private EditText userName;
    private EditText password;
    private TextView loginStatus;

    private Button signin;
    private Button cancel;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        userName = (EditText)findViewById(R.id.et_username);
        password = (EditText)findViewById(R.id.et_password);
        loginStatus = (TextView)findViewById(R.id.tv_login_status);

        signin = (Button)findViewById(R.id.btn_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent();
                if (userName.getText().toString().equals("mcruz") &&
                        password.getText().toString().equals("1234")){
                    intent.putExtra("user", 2);
                    setResult(50,intent);
                    finish();
                } else Snackbar.make(v,"UserName or Password incorrect",Snackbar.LENGTH_SHORT).show();
            }
        });

        cancel = (Button)findViewById(R.id.btn_sign_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent();

            }
        });

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        if (item.getItemId() == android.R.id.home){
            //finish();
            Log.e("Back","Not values");
            return false;
        }else
            return super.onMenuItemSelected(featureId, item);
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("Button", ""+keyCode);
        return true;
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                Snackbar.make(cancel,"Back",Snackbar.LENGTH_SHORT).show();
                return false;
            case KeyEvent.KEYCODE_HOME:
                Snackbar.make(cancel,"Home",Snackbar.LENGTH_SHORT).show();
                return false;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }*/

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("Button", "" + keyCode);
        return true;
        //return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
