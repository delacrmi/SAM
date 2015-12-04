package com.cac.tools;

import android.content.Context;
import android.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * Created by miguel on 02/11/15.
 */
public interface MainComponentEdit<viewsConfig> extends Serializable{
    void mainViewConfig(viewsConfig views);
    String getTAG();
    int getSubTitle();
    void setContext(Context context);
}
