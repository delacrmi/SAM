package com.cac.viewer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cac.tools.MainComponentEdit;
import com.cac.sam.R;

/**
 * Created by miguel on 02/11/15.
 */
public class MainFragment extends Fragment implements MainComponentEdit<FloatingActionButton[]> {
    //instance variables
    private static MainFragment ourInstance;
    private AppCompatActivity context;
    private View view;

    public static MainFragment init(AppCompatActivity context){
        if(ourInstance == null){
            ourInstance = new MainFragment();
            ourInstance.context = context;
        }
        return ourInstance;
    }

    public static MainFragment getInstance(){
        if(ourInstance == null)
            throw new NullPointerException("the "+ ourInstance.getClass().getSimpleName()+" isn't created, " +
                    "call the init method first to try use this instance");
        return ourInstance;
    }

    @Override
    public void mainViewConfig(FloatingActionButton[] buttons) {
        buttons[0].setVisibility(view.INVISIBLE);
        buttons[1].setVisibility(view.INVISIBLE);

    }

    @Override
    public void setContext(Context context) {
        ourInstance.context = (AppCompatActivity)context;
    }

    public String getTAG(){
        return "MainFragment";
    }

    //<editor-fold desc="Override Methods">

    @Override
    public int getSubTitle() {
        return R.string.app_description_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
            view = inflater.inflate(R.layout.main_fragment,container,false);

        return view;
    }

    //</editor-fold>
}
