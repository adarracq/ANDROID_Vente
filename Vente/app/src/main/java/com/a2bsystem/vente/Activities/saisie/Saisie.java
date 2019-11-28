package com.a2bsystem.vente.Activities.saisie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.a2bsystem.vente.Adapters.SaisieAdapter;
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

public class Saisie extends AppCompatActivity {


    private ArrayList<Vente> ventes;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisie);

        activity = this;

        RequestParams params = Helper.GenerateParams(Saisie.this);
        params.put("Saljare",Helper.vendeur);
        String URL = Helper.GenereateURI(Saisie.this, params, "getventes");

        GetVentes task = new GetVentes();
        task.execute(new String[] { URL });
    }

    private void configureViewPager(){
        ViewPager pager = findViewById(R.id.activity_main_viewpager);

        pager.setAdapter(new SaisieAdapter(getSupportFragmentManager(), ventes) {
        });
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

                    Intent intent = getIntent();
                    Vente vente = (Vente) intent.getSerializableExtra("vente");

                    // Si nouvelle vente
                    if(vente.getClient().equalsIgnoreCase("")){
                        ventes.add(vente);
                    }

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

                    Saisie.this.configureViewPager();


                } catch (Exception ex) {}
            }
        }
    }
}