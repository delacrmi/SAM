package com.cac.viewer;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.telephony.TelephonyManager;
import android.view.View;

import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;

/**
 * Created by miguel on 03/11/15.
 */
public class SettingFragment extends PreferenceFragment implements MainComponentEdit<View[]> {

    private static SettingFragment ourInstance;
    private EditTextPreference editTextEmei;
    private MainActivity context;

    public static SettingFragment getInstance(MainActivity context){
        if(ourInstance == null) {
            ourInstance = new SettingFragment();
            ourInstance.context = context;
        }
        return  ourInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_sync);

        editTextEmei = (EditTextPreference) findPreference("EMEI");
        TelephonyManager telephonyManager = (TelephonyManager)ourInstance.context.getSystemService(Context.TELEPHONY_SERVICE);
        editTextEmei.setText(telephonyManager.getDeviceId());

    }

    @Override
    public void mainViewConfig(View[] views) {
        views[0].getLayoutParams().height = 0;
        views[0].invalidate();
        views[1].setVisibility(View.INVISIBLE);
        views[1].invalidate();
        views[2].setVisibility(View.INVISIBLE);
        views[2].invalidate();
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
