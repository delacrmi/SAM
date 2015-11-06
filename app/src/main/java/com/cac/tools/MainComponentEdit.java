package com.cac.tools;

import android.support.design.widget.FloatingActionButton;

import java.io.Serializable;

/**
 * Created by miguel on 02/11/15.
 */
public interface MainComponentEdit extends Serializable{
    void onClickFloating();
    void FloatingButtonConfig(FloatingActionButton floatingActionButton);
    String getTAG();
    int getSubTitle();
}
