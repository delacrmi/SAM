package com.cac.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.atorres.bluetoothprinter.BluetoothPrinterManager;
import com.cac.sam.R;
import com.cac.viewer.CutterReportFragment;

import java.util.List;

/**
 * Created by Legal on 24/11/2015.
 */
public class TransaccionAdapter extends ArrayAdapter<CutterReportCardHolder> {

    private BluetoothPrinterManager bluetoothPrinterManager;

    public TransaccionAdapter(Context context, List<CutterReportCardHolder> objects) {
        super(context, R.layout.cutter_card_view, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View item = convertView;
        TransaccionViewHolder holder;

        if ( item == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.cutter_card_view, null);

            holder = new TransaccionViewHolder();
            holder.noTicket = (TextView) item.findViewById(R.id.cutterCardViewTxtNoTicket);
            holder.cortador = (TextView) item.findViewById(R.id.cutterCardViewTxtCortador);
            holder.fecha = (TextView) item.findViewById(R.id.cutterCardViewTxtDate);
            holder.finca = (TextView) item.findViewById(R.id.cutterCardViewTxtFinca);
            holder.canial= (TextView) item.findViewById(R.id.cutterCardViewTxtCanial);
            holder.lote  = (TextView) item.findViewById(R.id.cutterCardViewTxtLote);
            holder.linea = (TextView) item.findViewById(R.id.cutterCardViewTxtLinea);
            holder.totalUnada  = (TextView) item.findViewById(R.id.cutterCardViewTxtUnadas);
            holder.totalPeso   = (TextView) item.findViewById(R.id.cutterCardViewTxtPeso);
            holder.btnPrint    = (ImageButton) item.findViewById(R.id.cutterCardViewBtnPrint);
            item.setTag(holder);
        } else
            holder = (TransaccionViewHolder) item.getTag();

        holder.noTicket.setText(getItem(position).getNoTicket());
        holder.cortador.setText(getItem(position).getCortador());
        holder.fecha.setText(getItem(position).getFecha());
        holder.finca.setText(getItem(position).getIdFinca());
        holder.canial.setText(getItem(position).getIdCanial());
        holder.lote.setText(getItem(position).getIdLote());
        holder.linea.setText(getItem(position).getLinea());
        holder.totalUnada.setText(getItem(position).getTotalUnada());
        holder.totalPeso.setText(getItem(position).getTotalPeso());
        holder.btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CutterReportFragment.printBluetoothReport(getItem(position));
            }
        });
        return item;
    }

    static class TransaccionViewHolder {
        TextView noTicket;
        TextView cortador;
        TextView fecha;
        TextView finca;
        TextView canial;
        TextView lote;
        TextView linea;
        TextView totalUnada;
        TextView totalPeso;
        ImageButton btnPrint;
   }
}