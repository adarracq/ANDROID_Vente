package com.a2bsystem.vente.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a2bsystem.vente.Models.Orp;
import com.a2bsystem.vente.R;

import java.util.ArrayList;

public class VenteDetailAdapter extends ArrayAdapter<Orp> {

    private ViewHolder viewHolder;

    private static class ViewHolder {
        private LinearLayout itemView;
    }

    public VenteDetailAdapter(Context context, int textViewResourceId, ArrayList<Orp> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.vente_lines, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.itemView = convertView.findViewById(R.id.vente_line);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Orp item = getItem(position);
        if (item != null) {


            TextView lib = viewHolder.itemView.findViewById(R.id.saisie_lib_article);
            lib.setText(item.getLib());

            TextView colis = viewHolder.itemView.findViewById(R.id.saisie_colis);
            colis.setText(item.getColis()+"");

            TextView poids = viewHolder.itemView.findViewById(R.id.saisie_poids);
            poids.setText(item.getPdsNet()+"");

            TextView pieces = viewHolder.itemView.findViewById(R.id.saisie_pieces);
            pieces.setText(item.getPieces()+"");

            TextView valeur = viewHolder.itemView.findViewById(R.id.saisie_montant);
            valeur.setText(item.getMontant()+"");


            if(item.getStatut() == 2){
                lib.setTypeface(null, Typeface.ITALIC);
                lib.setBackgroundResource(R.color.red);
                colis.setBackgroundResource(R.color.red);
                pieces.setBackgroundResource(R.color.red);
                poids.setBackgroundResource(R.color.red);
                valeur.setBackgroundResource(R.color.red);
            }
            else if(item.getStatut() == 3){
                colis.setTypeface(null, Typeface.BOLD_ITALIC);
                colis.setBackgroundResource(R.color.border);
                pieces.setTypeface(null, Typeface.BOLD_ITALIC);
                pieces.setBackgroundResource(R.color.border);
                poids.setTypeface(null, Typeface.BOLD_ITALIC);
                poids.setBackgroundResource(R.color.border);
                valeur.setTypeface(null, Typeface.BOLD_ITALIC);
                valeur.setBackgroundResource(R.color.border);
                lib.setTypeface(null, Typeface.BOLD_ITALIC);
                lib.setBackgroundResource(R.color.border);
            }
            else {
                lib.setTypeface(null, Typeface.ITALIC);
                valeur.setBackgroundResource(R.color.colorWhite);
                colis.setBackgroundResource(R.color.colorWhite);
                pieces.setBackgroundResource(R.color.colorWhite);
                poids.setBackgroundResource(R.color.colorWhite);
                lib.setBackgroundResource(R.color.colorWhite);
            }
        }

        return convertView;
    }
}
