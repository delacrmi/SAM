package com.cac.viewer;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;

/**
 * Created by miguel on 03/11/15.
 */
public class SettingFragment extends PreferenceFragment implements MainComponentEdit {

    private static SettingFragment outInstance;

    public static SettingFragment getInstance(){
        if(outInstance == null)
            outInstance = new SettingFragment();
        return  outInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_sync);
    }

    @Override
    public void onClickFloating() {

    }

    @Override
    public void FloatingButtonConfig(FloatingActionButton floatingActionButton) {
        floatingActionButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public String getTAG() {
        return "SettingFragment";
    }

    @Override
    public int getSubTitle() {
        return R.string.settings;
    }
}
