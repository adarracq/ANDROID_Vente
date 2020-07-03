package com.a2bsystem.vente.Activities.listeventes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.a2bsystem.vente.Activities.saisie.Saisie;
import com.a2bsystem.vente.Adapters.SwipeDismissListViewTouchListener;
import com.a2bsystem.vente.Adapters.VenteAdapter;
import com.a2bsystem.vente.Helper;
import com.a2bsystem.vente.Models.Vente;
import com.a2bsystem.vente.R;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class VentesList extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private ArrayList<Vente> ventes;

    ProgressBar pbbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventes_list);

        initFields();
        initListeners();

        setGetVentes();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetVentes();
    }

    private void initFields(){
        bottomNavigationView = findViewById(R.id.bottom_navigation_ventes_list);
        pbbar = findViewById(R.id.ventes_list_pbbar);
        pbbar.setVisibility(View.GONE);
    }

    private void initListeners(){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ventes_list_onglet:

                        if(Helper.monoVente == 0) {
                            Intent saisieActivity = new Intent(VentesList.this, Saisie.class);
                            saisieActivity.putExtra("vente", new Vente("","",0.0,0.0,"","","0",0));
                            startActivity(saisieActivity);

                        }
                        break;

                }
                return false;
            }
        });

    }

    private void loadVentes() {

        if(ventes.size() == 0) {
            Intent saisieActivity = new Intent(VentesList.this, Saisie.class);
            saisieActivity.putExtra("vente", new Vente("","",0.0,0.0,"","","0",0));
            startActivity(saisieActivity);
            VentesList.this.finish();
        }

        ListView listView = findViewById(R.id.ventes_listview);


        VenteAdapter adapter = new VenteAdapter(VentesList.this, R.layout.ventes_lines, ventes);
        listView.setAdapter(adapter);

        // Ecoute des clicks sur les lignes
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Si la ligne n'est pas annulée
                if(ventes.get(position).getStatut() != -1 ) {
                    Intent SaisieActivity = new Intent(VentesList.this, Saisie.class);
                    SaisieActivity.putExtra("vente", ventes.get(position));
                    startActivity(SaisieActivity);
                }

            }
        });


        listView.setOnTouchListener(new SwipeDismissListViewTouchListener(
                listView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            setDeleteVente(position);
                        }

                    }
                }));
    }


    private void lockUI()
    {
        bottomNavigationView.setEnabled(false);
    }

    private void unlockUI()
    {
        bottomNavigationView.setEnabled(true);
    }

    public void showError(String message, DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Erreur");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", listener);
        builder.show();
    }


    private void setGetVentes() {
        RequestParams params = Helper.GenerateParams(VentesList.this);
        params.put("Saljare",Helper.vendeur);
        String URL = Helper.GenereateURI(VentesList.this, params, "getventes");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE

        GetVentes task = new GetVentes();
        task.execute(new String[] { URL });
    }

    private void setDeleteVente(int position) {
        RequestParams params = Helper.GenerateParams(VentesList.this);
        params.put("ordernr",ventes.get(position).getOrderNr());
        String URL = Helper.GenereateURI(VentesList.this, params, "deletevente");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE

        DeleteVente task = new DeleteVente();
        task.execute(new String[] { URL });
    }



    private class GetVentes extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }

        private String getOutputFromUrl(String url) {
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return output.toString();
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(String output) {
            unlockUI();
            if(output.equalsIgnoreCase("-1"))
            {
                showError("Impossible de récupérer les ventes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
            else {

                try {

                    JSONArray jsonArray = new JSONArray(output);
                    ventes = new ArrayList<>();

                    for(int i = 0 ; i<jsonArray.length() ; i++){

                        ventes.add(new Vente(
                                jsonArray.getJSONObject(i).getString("Code"),
                                jsonArray.getJSONObject(i).getString("Client"),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("Montant")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("Solde")),
                                jsonArray.getJSONObject(i).getString("OrderNr"),
                                jsonArray.getJSONObject(i).getString("Depot"),
                                jsonArray.getJSONObject(i).getString("Dlc"),
                                0)
                        );
                    }
                    loadVentes();


                } catch (Exception ex) {}
            }
        }
    }


    private class DeleteVente extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String output = null;
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }

        private String getOutputFromUrl(String url) {
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = getHttpConnection(url);
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return output.toString();
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }

        @Override
        protected void onPostExecute(String output) {
            unlockUI();
            if(output.equalsIgnoreCase("-1"))
            {
                showError("Impossible de supprimer la vente...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
            else {

                setGetVentes();

            }
        }
    }

}
