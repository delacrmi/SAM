package com.cac.viewer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.atorres.AndroidUtils;
import com.atorres.bluetoothprinter.BluetoothPrinterManager;
import com.atorres.bluetoothprinter.PrinterObjectFormat;
import com.cac.entities.*;
import com.cac.sam.MainActivity;
import com.cac.sam.R;
import com.cac.tools.CutterReportCardHolder;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.MyDialogDateListenerFactory;
import com.cac.tools.TransaccionAdapter;
import com.delacrmi.persistences.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Legal on 24/11/2015.
 */
public class CutterReportFragment extends Fragment implements MainComponentEdit<View[]>  {

    public static final String TAG = "CutterReportFragment";
    private static CutterReportFragment ourInstance = null;
    private static BluetoothPrinterManager bluetoothPrinterManager;
    private static List<CutterReportCardHolder> informationList;
    private MainActivity context;
    private View view;
    private ImageButton btnFiltrarRegistros;
    private ListView listOfCutters;
    private EditText editFiltroPorFecha;

    public static CutterReportFragment init(MainActivity context) {
        try {
            if (ourInstance == null) {
                ourInstance = new CutterReportFragment();
                ourInstance.context = context;
                ourInstance.bluetoothPrinterManager = new BluetoothPrinterManager(ourInstance.context);
            }
            return ourInstance;
        } catch (Exception e) {
            Log.e("Constructor Form1", "Error al instanciar CutterReportFragment 1", e);
            Toast.makeText(context, "Error al ejecutar el constructor de CutterReportFragment.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.cutter_report_view,container,false);
        initComponents();
        return view;
    }

    private void initComponents() {

        btnFiltrarRegistros = (ImageButton) ourInstance.view.findViewById(R.id.filtrarRegistros);
        listOfCutters       = (ListView)    ourInstance.view.findViewById(R.id.listViewTransacciones);
        editFiltroPorFecha  = (EditText)    ourInstance.view.findViewById(R.id.editFiltroPorFecha);

        editFiltroPorFecha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), R.style.AppTheme, new MyDialogDateListenerFactory(editFiltroPorFecha),
                            Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    datePicker.show();
                }
            }
        });

        TransaccionAdapter adapter = new TransaccionAdapter(ourInstance.context,findTransacciones());
        listOfCutters.setAdapter(adapter);
        btnFiltrarRegistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarPorFecha(editFiltroPorFecha.getText().toString());
            }
        });
    }

    private List<CutterReportCardHolder> findTransacciones() {
        EntityManager entityManager = ourInstance.context.getEntityManager();
        List<CutterReportCardHolder> informationList = new ArrayList<>();
        for ( Entity entity :  entityManager.find(Transaccion.class, "*", null, null) ) {

            CutterReportCardHolder data = new CutterReportCardHolder();
            data.setTotalUnada(entity.getColumnValueList().getAsString(Transaccion.UNADA));
            data.setTotalPeso(entity.getColumnValueList().getAsString(Transaccion.PESO));
            data.setLinea(entity.getColumnValueList().getAsString(Transaccion.LINEA));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            String fecha = dateFormat.format((Date)entity.getColumn(Transaccion.FECHA_CORTE).getValue());
            data.setFecha(fecha);

            String finca = entityManager.findOnce(Fincas.class,
                                   Fincas.ID_FINCA+"||' - '||"+Fincas.DESCRIPCION + " "+ Fincas.DESCRIPCION,
                                   Fincas.ID_EMPRESA + " = ? and " +Fincas.ID_FINCA + " = ?",
                    new String[]{entity.getColumnValueList().getAsString(Transaccion.EMPRESA),
                                 entity.getColumnValueList().getAsString(Transaccion.ID_FINCA)})
                    .getColumnValueList().getAsString(Fincas.DESCRIPCION);
            data.setIdFinca(finca);

            String canial = entityManager.findOnce(Caniales.class,
                    Caniales.ID_CANIAL+"||' - '||"+Caniales.DESCRIPCION + " "+Caniales.DESCRIPCION,
                    Caniales.ID_EMPRESA + " = ? and " +Caniales.ID_FINCA + " = ? and "+Caniales.ID_CANIAL+" = ?",
                    new String[]{entity.getColumnValueList().getAsString(Transaccion.EMPRESA),
                                 entity.getColumnValueList().getAsString(Transaccion.ID_FINCA),
                                 entity.getColumnValueList().getAsString(Transaccion.ID_CANIAL)})
                    .getColumnValueList().getAsString(Caniales.DESCRIPCION);
            data.setIdCanial(canial);

            String lote = entityManager.findOnce(Lotes.class,
                    Lotes.ID_LOTE+"||' - '||"+Lotes.DESCRIPCION + " "+Lotes.DESCRIPCION,
                    Lotes.ID_EMPRESA + " = ? and " +Lotes.ID_FINCA + " = ? and "+Lotes.ID_CANIAL+" = ?"+
                    Lotes.ID_PERIODO + " = ? and " +Lotes.ID_LOTE,
                    new String[]{entity.getColumnValueList().getAsString(Transaccion.EMPRESA),
                                 entity.getColumnValueList().getAsString(Transaccion.ID_FINCA),
                                 entity.getColumnValueList().getAsString(Transaccion.ID_CANIAL),
                                 entity.getColumnValueList().getAsString(Transaccion.PERIODO),
                                 entity.getColumnValueList().getAsString(Transaccion.ID_LOTE)})
                    .getColumnValueList().getAsString(Fincas.DESCRIPCION);
            data.setIdLote(lote);

            String cortador = entityManager.findOnce(Empleados.class,
                    Empleados.ID_EMPLEADO+"||' - '||"+Empleados.NOMBRE+" "+Empleados.NOMBRE,
                    Empleados.ID_EMPRESA+" = ? and "+Empleados.ID_EMPLEADO+" = ?",
                    new String[]{entity.getColumnValueList().getAsString(Empleados.ID_EMPRESA),
                    entity.getColumnValueList().getAsString(Empleados.ID_EMPLEADO)})
                    .getColumnValueList().getAsString(Empleados.NOMBRE);
            data.setCortador(cortador);

            informationList.add(data);
        }
        return informationList;
    }
    /*private List<CutterReportCardHolder> findTransacciones() {
        List<CutterReportCardHolder> informationList = new ArrayList<>();
        for ( int i = 0; i < 5; i++ ) {
            CutterReportCardHolder data = new CutterReportCardHolder();
            data.setNoTicket(i+1+"");
            data.setLinea(i + "");
            data.setTotalPeso(350.25 + "");
            data.setIdCanial("101 - Santana #101");
            data.setIdFinca("01 - Santana");
            data.setIdLote("01 - Lote 01");
            data.setFecha("0" + (i + 1) + "/01/2015");
            data.setTotalUnada(5 + "");
            data.setCortador("20834 - Armando Torres");
            informationList.add(data);
        }
        return informationList;
    }*/

    private void filtrarPorFecha(String s) {
        List<CutterReportCardHolder> listado = findTransacciones();
        if ( s != null && s.length() > 0 ) {
            List<CutterReportCardHolder> informationList = new ArrayList<>();
            for (CutterReportCardHolder obj : listado) {
                if (obj.getFecha().toLowerCase().contains(s))
                    informationList.add(obj);
            }
            listOfCutters.setAdapter(new TransaccionAdapter(ourInstance.context,informationList));
        } else{
            listOfCutters.setAdapter(new TransaccionAdapter(ourInstance.context,listado));
        }
    }

    public static void printBluetoothReport(CutterReportCardHolder item) {

        if ( ourInstance.bluetoothPrinterManager != null) {
            //Verificamos si existe alguna impresora seleccionada.
            if (!ourInstance.bluetoothPrinterManager.isDeviceSelected()) {
                //De no existe algun dispositivo seleccionado se llama a la lista para que el usuario seleccione un dispositivo.
                // 1-) Creamos el dialogo.
                final Dialog dialog = new Dialog(ourInstance.context);
                dialog.setContentView(R.layout.custom_alert_dialog);
                dialog.setTitle("Elegir dispositivo");
                // 2-) Enlazamos los botones y lista.
                Button customAlertDialogButton = (Button) dialog.findViewById(R.id.customAlertDialogButton);
                ListView customAlertDialogList = (ListView) dialog.findViewById(R.id.customAlertDialogList);
                //3-) Al presionar el boton ocultamos el dialog.
                customAlertDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                //4-) Conseguimos la lista de dispositivos.
                ArrayAdapter<String> adapter = ourInstance.bluetoothPrinterManager.getBluetoothDevices();
                if (adapter != null) {
                    customAlertDialogList.setAdapter(adapter);
                    customAlertDialogList.setItemChecked(0, true);
                    customAlertDialogList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    customAlertDialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ourInstance.bluetoothPrinterManager.setBluetoothDeviceSelected(position);
                        }
                    });
                    dialog.show();
                }
                return;
            }

            // Solo si se selecciono un dispositivo procedemos a imprimir el reporte
            if ( ourInstance.bluetoothPrinterManager.isDeviceSelected() ) {
                //Creamos el arreglo a imprimir.
                List<PrinterObjectFormat> paramToPrint = new ArrayList<>();

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.THREE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getNoTicket()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        "================================================"));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getFecha()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getCortador()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getIdFinca()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getIdCanial()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getIdLote()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getLinea()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.ONE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getTotalUnada()));

                paramToPrint.add(new PrinterObjectFormat(BluetoothPrinterManager.PRINTER_FONT.NORMAL,
                        BluetoothPrinterManager.PRINTER_FONT_SIZE.THREE,
                        BluetoothPrinterManager.PRINTER_OBJECT.TEXT, false,
                        item.getTotalPeso()));

                //Imprimimos el objeto.
                ourInstance.bluetoothPrinterManager.print(paramToPrint);
            } else {
                AndroidUtils.showAlertMsg(ourInstance.context, "Notificación", "Debe seleccionar un dispositivo bluetooth para continuar con la impresion.");
            }
        }
    }


    @Override
    public void mainViewConfig(View[] views) {
        for(int i = 0; i < views.length; i++){
            views[i].setVisibility(View.INVISIBLE);
            if ( views[i] instanceof RelativeLayout ){
                views[i].getLayoutParams().height = 0;
            }
        }
    }

    @Override
    public String getTAG() {
        return this.TAG;
    }

    @Override
    public int getSubTitle() {
        return R.string.cutter_report;
    }

    @Override
    public void setContext(Context context) {
        ourInstance.context = (MainActivity)context;
    }


}