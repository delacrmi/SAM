package com.cac.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ListAdapter;

import com.delacrmi.persistences.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 17/11/15.
 */
public class AutoCompleteTextViewFactory extends AutoCompleteTextView {

    private EntityManager entityManager;
    private Class entity;
    private List<OnItemSelected> onItemSelectedList = new ArrayList<OnItemSelected>();

    public AutoCompleteTextViewFactory(Context context) {
        super(context);
    }

    public AutoCompleteTextViewFactory(Context context, AttributeSet attrs) {
        super(context, attrs);
        events();
    }

    public AutoCompleteTextViewFactory(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        events();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoCompleteTextViewFactory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        events();
    }

    @Override
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        super.setAdapter(adapter);
    }

    public void setEntityManager(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    private void events(){
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(OnItemSelected selected: onItemSelectedList)
                    selected.onSelected(getText().toString());
            }
        });
    }

    public void addOnItemSelected(OnItemSelected onItemSelected){
        onItemSelectedList.add(onItemSelected);
    }

    public interface OnItemSelected{
        void onSelected(String value);
    }

}
