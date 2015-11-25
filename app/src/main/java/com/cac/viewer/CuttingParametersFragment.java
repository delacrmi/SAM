package com.cac.viewer;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cac.entities.Caniales;
import com.cac.entities.Fincas;
import com.cac.entities.Frentes;
import com.cac.entities.Lotes;
import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.MyOnFocusListenerFactory;
import com.cac.tools.OnKeyListenerRefactory;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Legal on 04/10/2015.
 */
public class CuttingParametersFragment extends Fragment implements MainComponentEdit<View[]>{

    private View view;
    private EditText fteCorte, fteAlce, finca, canial, lote, txtDescFteCorte, txtDescFteAlce, txtDescFinca, txtDescCanial, txtDescLote;
    private static CuttingParametersFragment ourInstance = null;
    private MainActivity context;
    private RelativeLayout layout;

    private EntityManager entityManager;

    public CuttingParametersFragment() {}

    public EditText getFteCorte() {
        return fteCorte;
    }

    public EditText getFteAlce() {
        return fteAlce;
    }

    public EditText getFinca() {
        return finca;
    }

    public EditText getCanial() {
        return canial;
    }

    public EditText getLote() {
        return lote;
    }

    public static CuttingParametersFragment init(MainActivity context,EntityManager entityManager) {
        try {
            if (ourInstance == null) {
                ourInstance = new CuttingParametersFragment();
                ourInstance.context = context;
                ourInstance.entityManager = entityManager;
            }
            return ourInstance;
        } catch (Exception e) {
            Log.e("Constructor Form1", e.getMessage());
            Toast.makeText(context, "Error al ejecutar el constructor de formulario.",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.cutting_parameter_view,container,false);
        initComponents();
        initMetodos();
        return view;
    }


    @Override
    public void mainViewConfig(View[] views) {

        views[0].getLayoutParams().height = MainActivity.VISIBLE_ACTION;

        ((ImageButton)views[1]).setImageResource(R.drawable.siguiente);
        views[1].setVisibility(View.VISIBLE);
        views[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm())
                    ourInstance.context.startTransactionByTagFragment(
                            ourInstance.context.getCutterWorkFragment().getTAG()
                    );
            }
        });

        views[2].setVisibility(View.INVISIBLE);
    }

    @Override
    public String getTAG() {
        return "CuttingParametersFragment";
    }

    @Override
    public int getSubTitle() {
        return R.string.cutting_title_fragment;
    }

    @Override
    public void setContext(Context context) {
        ourInstance.context = (MainActivity)context;
    }

    private void initComponents(){
        fteCorte = (EditText) view.findViewById(R.id.editTextFrenteCorte);
        txtDescFteCorte = (EditText) view.findViewById(R.id.txtDescFteCorte);
        fteAlce = (EditText) view.findViewById(R.id.editTextFteAlce);
        txtDescFteAlce = (EditText) view.findViewById(R.id.txtDescFteAlce);
        finca = (EditText) view.findViewById(R.id.editTextFinca);
        txtDescFinca = (EditText) view.findViewById(R.id.txtDescFinca);
        canial = (EditText) view.findViewById(R.id.editTextCanial);
        txtDescCanial = (EditText) view.findViewById(R.id.txtDescCanial);
        lote = (EditText) view.findViewById(R.id.editTextlote);
        txtDescLote = (EditText) view.findViewById(R.id.txtDescLote);
        layout = (RelativeLayout) view.findViewById(R.id.formulario1);
    }

    private void initMetodos() {

        fteCorte.setOnFocusChangeListener((new MyOnFocusListenerFactory(txtDescFteCorte,
                ourInstance.context.getEntityManager(), Frentes.class, Frentes.DESCRIPCION, Frentes.ID_FRENTE)).setTitle("Frente"));
        fteCorte.setOnKeyListener(new OnKeyListenerRefactory(getInformation(
                Frentes.class, Frentes.ID_FRENTE + " key, " + Frentes.DESCRIPCION + " value", null, null), txtDescFteCorte));

        fteAlce.setOnFocusChangeListener((new MyOnFocusListenerFactory(txtDescFteAlce,
                ourInstance.context.getEntityManager(), Frentes.class, Frentes.DESCRIPCION, Frentes.ID_FRENTE)).setTitle("Frente"));
        fteAlce.setOnKeyListener(new OnKeyListenerRefactory(getInformation(
                Frentes.class, Frentes.ID_FRENTE + " key, " + Frentes.DESCRIPCION + " value", null, null), txtDescFteAlce));

        finca.setOnFocusChangeListener((new MyOnFocusListenerFactory(txtDescFinca,
                ourInstance.context.getEntityManager(), Fincas.class, Fincas.DESCRIPCION, Fincas.ID_FINCA)).setTitle("Finca"));
        finca.setOnKeyListener(new OnKeyListenerRefactory(getInformation(
                Fincas.class, Fincas.ID_FINCA + " key, " + Fincas.DESCRIPCION + " value", null, null), txtDescFinca));

        canial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText field = (EditText) v;
                EntityManager entityManager = ourInstance.context.getEntityManager();
                if (!hasFocus) {
                    if (field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty()) {
                        field.setError("El campo " + field.getHint() + " es Requerido.");
                    }
                    if (ourInstance.context.getEntityManager() != null) {
                        Entity result = entityManager.findOnce(Caniales.class, "*",
                                Caniales.ID_FINCA + " = ? and " + Caniales.ID_CANIAL + " = ? ",
                                new String[]{finca.getText().toString(), canial.getText().toString()});
                        if (result != null && result.getColumnValueList().size() > 0) {
                            txtDescCanial.setText(result.getColumnValueList().getAsString(Caniales.DESCRIPCION));
                        } else {
                            txtDescCanial.setText("");
                            field.setError("El " + field.getHint() + " no se encuentra registrado en la base de datos.");
                        }
                    }
                }
            }
        });
        canial.setOnKeyListener(new OnKeyListenerRefactory(txtDescCanial){
            @Override
            public void beforeOnkeyValidate() {
                setMapValues(getInformation(
                        Caniales.class, Caniales.ID_CANIAL + " key, " + Caniales.DESCRIPCION + " value",
                        Caniales.ID_FINCA + " = ? ", new String[]{finca.getText().toString()}));
            }
        });

        lote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText field = (EditText) v;
                EntityManager entityManager = ourInstance.context.getEntityManager();
                if (!hasFocus) {
                    if (field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty()) {
                        field.setError("El campo " + field.getHint() + " es Requerido.");
                    }
                    if (ourInstance.context.getEntityManager() != null) {
                        Entity result = entityManager.findOnce(Lotes.class, "*",
                                Lotes.ID_FINCA + " = ? and " + Lotes.ID_CANIAL + " = ? and " + Lotes.ID_LOTE + " = ?",
                                new String[]{finca.getText().toString(), canial.getText().toString(), lote.getText().toString()});
                        if (result != null && result.getColumnValueList().size() > 0) {
                            txtDescLote.setText(result.getColumnValueList().getAsString(Lotes.DESCRIPCION));
                        } else {
                            txtDescLote.setText("");
                            field.setError("El " + field.getHint() + " no se encuentra registrado en la base de datos.");
                        }
                    }
                }
            }
        });
        lote.setOnKeyListener(new OnKeyListenerRefactory(txtDescLote){
            @Override
            public void beforeOnkeyValidate() {
                setMapValues(getInformation(
                    Lotes.class, Lotes.ID_LOTE + " key, " + Lotes.DESCRIPCION + " value",
                    Lotes.ID_FINCA + " = ? and " + Lotes.ID_CANIAL + " = ? ",
                    new String[]{finca.getText().toString(), canial.getText().toString()}));
            }
        });
    }

    private Map<String,String> getInformation(Class entityName,String columns,String where, String[] whereValues){

        Map<String,String> values = new HashMap<String,String>();

        List<Entity> entities = ourInstance.entityManager.find(entityName,columns,where,whereValues);
        for(Entity entity: entities){
           values.put(entity.getColumnsFromSelect().getAsString("key"),
                   entity.getColumnsFromSelect().getAsString("value"));
        }

        return values;

    }

    public boolean validateForm() {
        if ( layout != null ){
            for (View a : layout.getFocusables(RelativeLayout.FOCUS_BACKWARD)){
                if ( a instanceof AppCompatEditText) {
                    EditText editText = (EditText) a;
                    a.getOnFocusChangeListener().onFocusChange(editText,false);
                    if ( editText.getError() != null ){
                        Toast.makeText(ourInstance.context,"Debe indicar la informacion para el campo "+editText.getHint(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void clearForm() {
        fteCorte.setText("");
        txtDescFteCorte.setText("");
        fteAlce.setText("");
        txtDescFteAlce.setText("");
        finca.setText("");
        txtDescFinca.setText("");
        canial.setText("");
        txtDescCanial.setText("");
        lote.setText("");
        txtDescLote.setText("");
    }
}