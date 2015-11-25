package com.cac.tools;

import android.view.View;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

/**
 * Created by Legal on 24/11/2015.
 */
public class MyDialogDateListenerFactory implements DatePickerDialog.OnDateSetListener {

    private View field;

    public MyDialogDateListenerFactory(View field){
        this.field = field;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if ( field instanceof EditText)
            ((EditText) field).setText(dayOfMonth + "/" + monthOfYear + "/" + year);
    }

}