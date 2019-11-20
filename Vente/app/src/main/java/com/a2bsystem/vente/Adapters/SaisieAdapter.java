package com.a2bsystem.vente.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.a2bsystem.vente.Activities.saisie.SaisieFragment;
import com.a2bsystem.vente.Models.Vente;

import java.util.ArrayList;

public class SaisieAdapter extends FragmentPagerAdapter {


    private ArrayList<Vente> ventes;

    public SaisieAdapter(FragmentManager mgr, ArrayList<Vente> ventes) {
        super(mgr);
        this.ventes = ventes;
    }

    @Override
    public int getCount() {
        return ventes.size();
    }

    @Override
    public Fragment getItem(int position) {

        return(SaisieFragment.newInstance(position, this.ventes.get(position) , getCount()));
    }
}
