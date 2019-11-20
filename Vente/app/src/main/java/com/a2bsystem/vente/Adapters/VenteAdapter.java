package com.a2bsystem.vente.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2bsystem.vente.Models.Vente;
import com.a2bsystem.vente.R;

import java.util.ArrayList;


public class VenteAdapter extends ArrayAdapter<Vente> {
    private ViewHolder viewHolder;

    private static class ViewHolder {
        private LinearLayout itemView;
    }

    public VenteAdapter(Context context, int textViewResourceId, ArrayList<Vente> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.ventes_lines, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = convertView.findViewById(R.id.vente_line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Vente item = getItem(position);
        if (item != null) {

            TextView client = viewHolder.itemView.findViewById(R.id.ventes_client);
            client.setText(item.getClient());

            TextView valeur = viewHolder.itemView.findViewById(R.id.ventes_valeur);
            valeur.setText(Double.toString(item.getValeur()));

        }

        return convertView;
    }
}