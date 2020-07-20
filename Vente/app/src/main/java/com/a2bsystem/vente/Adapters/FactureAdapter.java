package com.a2bsystem.vente.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2bsystem.vente.Models.Facture;
import com.a2bsystem.vente.Models.Vente;
import com.a2bsystem.vente.R;

import java.util.ArrayList;


public class FactureAdapter extends ArrayAdapter<Facture> {
    private ViewHolder viewHolder;

    private static class ViewHolder {
        private LinearLayout itemView;
    }

    public FactureAdapter(Context context, int textViewResourceId, ArrayList<Facture> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.facture_lines, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = convertView.findViewById(R.id.fact_line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Facture item = getItem(position);
        if (item != null) {

            TextView num = viewHolder.itemView.findViewById(R.id.facture_num);
            num.setText("Numéro de Facture : " + item.getNumFact());

            TextView date = viewHolder.itemView.findViewById(R.id.facture_date);
            date.setText("Du " + item.getDateFact());

            TextView montant = viewHolder.itemView.findViewById(R.id.facture_montant);
            montant.setText("Montant : " + item.getMntFact() + "€");

            if(item.getColor() == 0) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#00FF00"));
            }
            else if(item.getColor() == 1) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FEAA0F"));
            }
            else {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("#FF0000"));
            }

        }

        return convertView;
    }
}