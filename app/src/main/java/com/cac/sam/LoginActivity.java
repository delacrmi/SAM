package com.cac.sam;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

/**
 * Created by delacrmi on 12/10/2015.
 */
public class LoginActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }
}
