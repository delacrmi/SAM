package com.cac.viewer;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;

/**
 * Created by miguel on 03/11/15.
 */
public class SettingFragment extends PreferenceFragment implements MainComponentEdit<FloatingActionButton[]> {

    private static SettingFragment ourInstance;

    public static SettingFragment getInstance(){
        if(ourInstance == null)
            ourInstance = new SettingFragment();
        return ourInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_sync);
    }

    @Override
    public void mainViewConfig(FloatingActionButton[] buttons) {
        buttons[0].setVisibility(View.INVISIBLE);
        buttons[1].setVisibility(View.INVISIBLE);
    }

    @Override
    public void setContext(Context context) {}

    @Override
    public String getTAG() {
        return "SettingFragment";
    }

    @Override
    public int getSubTitle() {
        return R.string.settings;
    }
}
