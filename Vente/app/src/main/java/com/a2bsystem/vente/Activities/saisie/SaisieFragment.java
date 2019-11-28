package com.a2bsystem.vente.Activities.saisie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a2bsystem.vente.Activities.listeventes.VentesList;
import com.a2bsystem.vente.Adapters.VenteDetailAdapter;
import com.a2bsystem.vente.Helper;
import com.a2bsystem.vente.Models.Orp;
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

public class SaisieFragment extends Fragment {


    private EditText eClient;
    private EditText eSolde;
    private EditText eValeur;
    private TextView tPage;
    private ListView listView;
    BottomNavigationView bottomNavigationView;
    private Vente vente;


    // Variables
    private String[] Clients;
    private String[] Clients2;


    private ArrayList<Orp> orps = new ArrayList<>();


    public SaisieFragment() { }


    // 2 - Method that will create a new instance of PageFragment, and add data to its bundle.
    public static SaisieFragment newInstance(int position, Vente vente, int total) {

        // 2.1 Create new fragment
        SaisieFragment frag = new SaisieFragment();

        // 2.2 Create bundle and add it some data
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("total", total);
        args.putSerializable("vente", vente);
        frag.setArguments(args);

        return(frag);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_saisie, container, false);

        eClient = result.findViewById(R.id.saisie_client);
        eSolde = result.findViewById(R.id.saisie_solde);
        eValeur = result.findViewById(R.id.saisie_valeur);
        tPage = result.findViewById(R.id.saisie_page);
        bottomNavigationView = result.findViewById(R.id.bottom_navigation_saisie);
        listView = result.findViewById(R.id.saisie_listview);

        initListeners();

        // 5 - Get data from Bundle (created in method newInstance)
        int position = getArguments().getInt("position", -1);
        int total = getArguments().getInt("total", -1);
        vente = (Vente) getArguments().getSerializable("vente");

        if(!vente.getClient().equalsIgnoreCase("")){
            eClient.setText(vente.getClient());
            eClient.setFocusable(false);
            eClient.setBackgroundResource(R.drawable.border);
            eSolde.setText(vente.getSolde()+"");
            eSolde.setFocusable(false);
            eValeur.setText(vente.getValeur()+"");
            eValeur.setFocusable(false);
        }
        else {
            eClient.requestFocus();
        }


        tPage.setText(position + 1 + "/" + total);

        setGetOrp();

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        setGetOrp();
    }


    private void initListeners(){

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.vente_article_onglet:

                        if(eClient.getText().toString().equalsIgnoreCase("")){
                            showError("Veuillez saisir un client", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
                        else {

                            Intent NewSaisieActivity = new Intent(getActivity(), SaisieLigne.class);

                            NewSaisieActivity.putExtra("orp",
                                    new Orp(vente));

                            startActivity(NewSaisieActivity);


                        }

                        break;

                    case R.id.vente_vente_onglet:

                        if(Helper.monoVente == 0) {
                            // Ouvre une nouvelle fenetre de vente
                            Intent saisieActivity = new Intent(getActivity(), Saisie.class);
                            saisieActivity.putExtra("vente", new Vente("","",0.0,0.0,"","","0",0));
                            startActivity(saisieActivity);
                            getActivity().finish();
                        }

                        break;

                    case R.id.vente_valid_onglet:

                        setValidVente();

                        break;
                }
                return false;
            }
        });



        eClient.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    setGetClient();

                    return true; // Focus will do whatever you put in the logic.
                }
                return false;  // Focus will change according to the actionId
            }
        });

    }


    private void setTotal(){
        Orp total = new Orp(
                "",
                "",
                "",
                "TOTAL",
                0.0,
                0.0,
                0.0,
                0.0,
                3
        );
        for ( int i = 0 ; i < orps.size() ; i++ )
        {
            if(orps.get(i).getStatut() == 0)
            {
                total.setColis(total.getColis() + orps.get(i).getColis());
                total.setPieces(total.getPieces() + orps.get(i).getPieces());
                total.setPdsNet(total.getPdsNet() + orps.get(i).getPdsNet());
                total.setMontant(round(total.getMontant(),2) + round(orps.get(i).getMontant(),2));
            }
        }
        vente.setValeur(total.getMontant());
        eValeur.setText(vente.getValeur()+"");
        orps.add(total);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void loadOrp() {

        VenteDetailAdapter adapter = new VenteDetailAdapter(getActivity(), R.layout.vente_lines, orps);
        listView.setAdapter(adapter);

        // Ecoute des clicks sur les lignes
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < orps.size() - 1 && orps.get(position).getStatut() != 2){
                    Intent SaisieLignesActivity = new Intent(getActivity(), SaisieLigne.class);
                    SaisieLignesActivity.putExtra("orp",orps.get(position));
                    startActivity(SaisieLignesActivity);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < orps.size() - 1 && orps.get(position).getStatut() != 2){
                    deleteOrp(position);
                }
                return true;
            }
        });
    }

    private void deleteOrp(final int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Suppression");
        builder.setMessage("Confirmer la suppression?");
        builder.setPositiveButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        builder.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {

                orps.get(pos).setStatut(2);
                setDeleteOrp(pos);
            }
        });
        builder.show();
    }

    private void selectClient() {

        ContextThemeWrapper themedContext = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        AlertDialog.Builder b = new AlertDialog.Builder(themedContext);
        b.setTitle("Choix du Client");
        b.setItems(Clients2, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                eClient.setText(Clients[which].substring(Clients[which].indexOf(']')+2));
                vente.setCode(Clients[which].substring(1,Clients[which].indexOf(']')));
                vente.setClient(eClient.getText().toString());
                eClient.setEnabled(false);
                eClient.setBackgroundResource(R.drawable.border);
                setGetClient();
                setGetOrp();
            }

        });

        b.show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Erreur");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", listener);
        builder.show();
    }


    private void setGetOrp() {
        // Construction de l'URL
        RequestParams params = Helper.GenerateParams(getActivity());
        params.put("Saljare",Helper.vendeur);
        params.put("Code",vente.getCode());
        String URL = Helper.GenereateURI(getActivity(), params, "getorp");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE
        GetOrp task = new GetOrp();
        task.execute(new String[] { URL });
    }

    private void setGetClient() {
        // Construction de l'URL
        RequestParams params = Helper.GenerateParams(getActivity());
        params.put("field", eClient.getText().toString());
        String URL = Helper.GenereateURI(getActivity(), params, "getclients");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE
        GetClients task = new GetClients();
        task.execute(new String[] { URL });
    }

    private void setDeleteOrp(final int pos) {
        // Construction de l'URL
        RequestParams params = Helper.GenerateParams(getActivity());
        params.put("OrderNr",orps.get(pos).getOrdernr());
        params.put("OrdRadNr",orps.get(pos).getOrdradnr());
        String URL = Helper.GenereateURI(getActivity(), params, "deleteorp");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE
        DeleteOrp task = new DeleteOrp();
        task.execute(new String[] { URL });
    }

    private void setValidVente() {
        // Construction de l'URL
        RequestParams params = Helper.GenerateParams(getActivity());
        params.put("Ordernr",vente.getOrderNr());

        String URL = Helper.GenereateURI(getActivity(), params, "validvente");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE
        ValidVente task = new ValidVente();
        task.execute(new String[] { URL });
    }

    private class GetClients extends AsyncTask<String, Void, String> {
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
                showError("Client Inconnu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eClient.setText("");
                    }
                });
            }
            else {


                try {
                    JSONArray jsonArray = new JSONArray(output);

                    // Si c'est un code client complet
                    if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("1")){

                        eClient.setText(jsonArray.getJSONObject(0).getString("lib"));
                        vente.setCode(jsonArray.getJSONObject(0).getString("code"));
                        vente.setClient(jsonArray.getJSONObject(0).getString("lib"));
                        vente.setSolde(Double.parseDouble(jsonArray.getJSONObject(0).getString("solde")));
                        vente.setDlc(jsonArray.getJSONObject(0).getString("dlc"));
                        eSolde.setText(vente.getSolde()+"");
                        eClient.setEnabled(false);
                        eClient.setBackgroundResource(R.drawable.border);
                        setGetOrp();
                    }
                    // Si c'est un libelle client complet
                    else if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("2")){

                        eClient.setText(jsonArray.getJSONObject(0).getString("lib"));
                        vente.setCode(jsonArray.getJSONObject(0).getString("code"));
                        vente.setClient(jsonArray.getJSONObject(0).getString("lib"));
                        vente.setSolde(Double.parseDouble(jsonArray.getJSONObject(0).getString("solde")));
                        vente.setDlc(jsonArray.getJSONObject(0).getString("dlc"));
                        eSolde.setText(vente.getSolde()+"");
                        eClient.setEnabled(false);
                        eClient.setBackgroundResource(R.drawable.border);
                        setGetOrp();
                    }
                    // Si il n'y a qu'un seul client correspondant
                    else if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("3") && jsonArray.length() == 1){

                        eClient.setText(jsonArray.getJSONObject(0).getString("lib"));
                        vente.setCode(jsonArray.getJSONObject(0).getString("code"));
                        vente.setClient(jsonArray.getJSONObject(0).getString("lib"));
                        vente.setSolde(Double.parseDouble(jsonArray.getJSONObject(0).getString("solde")));
                        vente.setDlc(jsonArray.getJSONObject(0).getString("dlc"));
                        eSolde.setText(vente.getSolde()+"");
                        eClient.setEnabled(false);
                        eClient.setBackgroundResource(R.drawable.border);
                        setGetOrp();
                    }
                    // Si c'est un morceau de code ou lib client
                    else if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("3")){


                        Clients = new String[jsonArray.length()];
                        Clients2 = new String[jsonArray.length()];

                        for(int i=0; i<jsonArray.length();i++)
                        {
                            // Si ville inconnue
                            if(jsonArray.getJSONObject(i).getString("ville").equalsIgnoreCase("0")) {
                                Clients2[i] = jsonArray.getJSONObject(i).getString("lib").trim() + '\n';
                            }
                            else {
                                Clients2[i] = jsonArray.getJSONObject(i).getString("lib").trim() + '\n' + jsonArray.getJSONObject(i).getString("ville").trim();
                            }
                            Clients[i] = "[" + jsonArray.getJSONObject(i).getString("code").trim() + "] " + jsonArray.getJSONObject(i).getString("lib").trim();
                        }
                        selectClient();
                    }
                    else {
                        showError("Client Inconnu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eClient.setText("");
                                eClient.requestFocus();
                            }
                        });
                    }

                } catch (Exception ex) {}
            }
        }
    }


    private class GetOrp extends AsyncTask<String, Void, String> {
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
                    orps = new ArrayList<>();

                    for(int i = 0 ; i<jsonArray.length() ; i++){

                        int statut = 0;
                        if (Integer.parseInt(jsonArray.getJSONObject(i).getString("Statut")) == 90)
                        {
                            statut = 2;
                        }

                        orps.add(new Orp(
                                vente,
                                jsonArray.getJSONObject(i).getString("Artnr"),
                                jsonArray.getJSONObject(i).getString("Lib").trim(),
                                jsonArray.getJSONObject(i).getString("Lot").trim(),
                                jsonArray.getJSONObject(i).getString("OrderNr"),
                                jsonArray.getJSONObject(i).getString("OrdRadNr"),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("Colis")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("Pieces")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("PdsBrut")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("Tare")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("PdsNet")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("PiecesU")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("PdsBrutU")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("TareU")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("PdsNetU")),
                                jsonArray.getJSONObject(i).getString("UniteFact").trim(),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("Prix")),
                                Double.parseDouble(jsonArray.getJSONObject(i).getString("Montant")),
                                "", //TODO DLC
                                "",
                                Integer.parseInt(jsonArray.getJSONObject(i).getString("PoidsVar")),
                                statut
                        ));
                    }
                    loadOrp();
                    setTotal();


                } catch (Exception ex) {}
            }
        }
    }


    private class DeleteOrp extends AsyncTask<String, Void, String> {
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
            loadOrp();
        }
    }


    private class ValidVente extends AsyncTask<String, Void, String> {
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
            // Ouvre une nouvelle fenetre de vente
            Intent saisieActivity = new Intent(getActivity(), Saisie.class);
            saisieActivity.putExtra("vente", new Vente("","",0.0,0.0,"","","0",0));
            startActivity(saisieActivity);
            getActivity().finish();
        }
    }

}