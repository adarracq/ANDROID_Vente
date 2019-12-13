package com.a2bsystem.vente.Activities.saisie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SaisieLigne extends AppCompatActivity {

    EditText eClient;
    EditText eSolde;
    EditText eValeur;
    EditText eLib;
    EditText eLot;
    TextView tLot;
    EditText eArtnr;
    TextView tArtnr;
    EditText eColis;
    EditText ePieces;
    EditText ePiecesU;
    EditText ePdsNet;
    EditText ePdsNetU;
    EditText ePdsBrut;
    EditText ePdsBrutU;
    EditText eTare;
    EditText eTareU;
    EditText eUnite;
    EditText ePu;
    EditText eMontant;
    EditText eDLC;
    BottomNavigationView bottomNavigationView;
    Orp orp = new Orp();

    int poids;
    Double poidTotal;

    private String [] Articles;
    private String [] Lots;
    private String [] Lots2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisie_ligne);

        poids = 0;
        poidTotal = 0.0;

        initFields();
        initListeners();
        loadVente();
        manageLot();
        manageFocus();
    }

    private void initFields() {
        eClient     = findViewById(R.id.saisie_ligne_client);
        eSolde       = findViewById(R.id.saisie_ligne_solde);
        eValeur       = findViewById(R.id.saisie_ligne_valeur);
        eArtnr      = findViewById(R.id.saisie_ligne_article);
        tArtnr      = findViewById(R.id.saisie_ligne_tarticle);
        eLib        = findViewById(R.id.saisie_ligne_lib);
        eLot        = findViewById(R.id.saisie_ligne_lot);
        tLot        = findViewById(R.id.saisie_ligne_tlot);
        eColis      = findViewById(R.id.saisie_ligne_colis);
        ePieces     = findViewById(R.id.saisie_ligne_pieces);
        ePiecesU    = findViewById(R.id.saisie_ligne_piecesU);
        ePdsNet     = findViewById(R.id.saisie_ligne_pdsNet);
        ePdsNetU    = findViewById(R.id.saisie_ligne_pdsNetU);
        ePdsBrut    = findViewById(R.id.saisie_ligne_pdsBrut);
        ePdsBrutU   = findViewById(R.id.saisie_ligne_pdsBrutU);
        eTare       = findViewById(R.id.saisie_ligne_tare);
        eTareU      = findViewById(R.id.saisie_ligne_tareU);
        eUnite      = findViewById(R.id.saisie_ligne_u_fact);
        ePu         = findViewById(R.id.saisie_ligne_prix);
        eMontant    = findViewById(R.id.saisie_ligne_montant);
        bottomNavigationView = findViewById(R.id.bottom_navigation_saisie_ligne);
        eDLC   = findViewById(R.id.saisie_ligne_DLC);
    }

    private void initListeners() {

        tArtnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eArtnr.setText("");
                if(eArtnr.getInputType() == InputType.TYPE_CLASS_NUMBER){
                    eArtnr.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else {
                    eArtnr.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });

        tLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eLot.setText("");
                if(eLot.getInputType() == InputType.TYPE_CLASS_NUMBER){
                    eLot.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else {
                    eLot.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.vente_article_onglet:

                        confirm("La saisie d'un nouvel article effacera la saisie en cours.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newOrp();
                            }
                        });

                        break;

                    case R.id.vente_vente_onglet:

                        if(Helper.monoVente == 0) {
                            confirm("La saisie d'une nouvelle vente effacera la saisie en cours.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // TODO nouvelle vente

                                }
                            });
                        }

                        break;

                    case R.id.vente_valid_onglet:

                        setValidVente();

                        break;
                }
                return false;
            }
        });

    }

    private void manageLot() {
        if (Helper.paramLot < 2)
        {
            eLot.setEnabled(false);
            eLot.setBackgroundResource(R.drawable.border);
            eArtnr.requestFocus();
        }
        else {
            eLot.requestFocus();
        }
    }


    private void fillFields(){
        eClient.setText(orp.getVente().getClient());
        eSolde.setText(orp.getVente().getSolde()+"");
        eValeur.setText(orp.getVente().getValeur()+"");
        eArtnr.setText(orp.getArtnr());
        eLib.setText(orp.getLib());
        if(orp.getColis() != 0) {
            eColis.setText(orp.getColis() + "");
        }
        else {
            eColis.setText("");
        }

        eLot.setText(orp.getLot());
        ePieces.setText(orp.getPieces()+"");
        ePiecesU.setText(orp.getPiecesU()+"");
        ePdsNet.setText(orp.getPdsNet()+"");
        ePdsNetU.setText(orp.getPdsNetU()+"");
        eTare.setText(orp.getTare()+"");
        eTareU.setText(orp.getTareU()+"");
        ePdsBrut.setText(orp.getPdsBrut()+"");
        ePdsBrutU.setText(orp.getPdsBrutU()+"");
        eUnite.setText(orp.getUniteFact());
        ePu.setText(orp.getPu()+"");
        eMontant.setText(orp.getMontant()+"");
        eDLC.setText(orp.getDLC());
    }

    private void saveFields(){
        if(eColis.getText().toString().equalsIgnoreCase("")){
            orp.setColis(0.0);
        }
        else {
            orp.setColis(Double.parseDouble(eColis.getText().toString()));
        }
        orp.setPieces(Double.parseDouble(ePieces.getText().toString()));
        orp.setPiecesU(Double.parseDouble(ePiecesU.getText().toString()));
        // Si saisie de plusieurs poids on ne sauvegarde pas chaque poids en poid total
        orp.setPdsNet(Double.parseDouble(ePdsNet.getText().toString()));
        orp.setPdsNetU(Double.parseDouble(ePdsNetU.getText().toString()));
        orp.setTare(Double.parseDouble(eTare.getText().toString()));
        orp.setTareU(Double.parseDouble(eTareU.getText().toString()));
        orp.setPdsBrut(Double.parseDouble(ePdsBrut.getText().toString()));
        orp.setPdsBrutU(Double.parseDouble(ePdsBrutU.getText().toString()));
        orp.setPu(Double.parseDouble(ePu.getText().toString()));
        orp.setMontant(Double.parseDouble(eMontant.getText().toString()));
        orp.setDLC(eDLC.getText().toString());
    }

    private void loadVente(){
        Intent intent = getIntent();
        orp = (Orp) intent.getSerializableExtra("orp");
        if(!orp.getArtnr().equalsIgnoreCase("")){
            eArtnr.setText(orp.getArtnr());
            eColis.requestFocus();
            setGetArticle();
        }
        fillFields();
    }

    public void showPoids(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Poids total : " + poidTotal);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void showPrix(){
        Toast.makeText(getApplicationContext(), "Prix total : " + orp.getPu() * orp.getColis() + " €", Toast.LENGTH_SHORT).show();

        /*
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Prix total : " + orp.getPu() * orp.getColis() + " €");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        */
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

    public void confirm(String message,  DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmer?");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("Oui", listener);
        builder.setPositiveButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public void calculate(int type) {
        saveFields();

        if( orp.getPdsNetU() != 0 && type == 0) {

            orp.setPdsNet(orp.getColis() * orp.getPdsNetU());
        }
        else if (orp.getColis() != 0){
            orp.setPdsNetU(orp.getPdsNet() / orp.getColis());
        }


        if( orp.getPdsBrutU() != 0 && type == 0) {

            orp.setPdsBrut(orp.getColis() * orp.getPdsBrutU());
        }
        else if (orp.getColis() != 0){
            orp.setPdsBrutU(orp.getPdsBrut() / orp.getColis());
        }

        if( orp.getPiecesU() != 0 && type == 0) {

            orp.setPieces(orp.getColis() * orp.getPiecesU());
        }
        else if (orp.getColis() != 0){

            orp.setPiecesU(orp.getPdsBrut() / orp.getColis());
        }

        if( orp.getTareU() != 0 ) {
            orp.setTare(orp.getColis() * orp.getTareU());
        }
        if (orp.getModeSaisie().equalsIgnoreCase("Poids brut total")) {
            if(orp.getPdsBrut() != 0) {
                orp.setTareU(orp.getTare() * orp.getPdsBrutU() / orp.getPdsBrut());
            }
            if(orp.getPdsBrutU() - orp.getTareU() > 0 && orp.getPdsBrut() - orp.getTare() > 0) {
                orp.setPdsNetU(orp.getPdsBrutU() - orp.getTareU());
                orp.setPdsNet(orp.getPdsBrut() - orp.getTare());
            }
            else {
                orp.setPdsNetU(0.0);
                orp.setPdsNet(0.0);
            }
        }


        fillFields();
    }

    private void newOrp(){
        orp =  new Orp(orp.getVente());
        eDLC.setText("");
        fillFields();
        manageLot();
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    public void validOrp() {
        showPrix();
        if(orp.getVente().getDlc().equalsIgnoreCase("1")){
            eDLC.post(new Runnable() {
                @Override
                public void run() {
                    eDLC.requestFocus();
                }
            });
        }
        else {
            if(orp.getOrdernr().equalsIgnoreCase("")){
                setCreateOrp();
            }
            else {
                setUpdateOrp();
            }
        }
    }

    private void selectArticle() {
        ContextThemeWrapper themedContext = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        AlertDialog.Builder b = new AlertDialog.Builder(themedContext);
        b.setTitle("Choix Article");
        b.setItems(Articles, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                eArtnr.setText(Articles[which].substring(1, Articles[which].indexOf("]")));
                eLib.setText(Articles[which].substring(Articles[which].indexOf("]")+1));
                orp.setArtnr(eArtnr.getText().toString());
                orp.setLib(eLib.getText().toString());

                if(Helper.paramLot > 1) {
                    setGetLot(1);
                }
                else {
                    setGetFields();
                }

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }

        });

        b.show();
    }

    private void selectLot() {
        ContextThemeWrapper themedContext = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        AlertDialog.Builder b = new AlertDialog.Builder(themedContext);
        b.setTitle("Choix Lot");
        b.setItems(Lots, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                orp.setLot(Lots2[which]);
                eLot.setText(Lots2[which]);

                setGetFields();

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }

        });

        b.show();
    }

    private void saisiePoidsVar(final EditText editText) {

        if(poids == orp.getColis()-1)
        {
            poidTotal = poidTotal +  Double.parseDouble(ePdsNet.getText().toString());

            ePdsNet.setText(poidTotal+"");
            showPoids();

            if(editText == ePu){
                orp.setPdsNet(poidTotal);
                if(Helper.saisiePrix == 0) {
                    validOrp();
                }
                else {
                    ePu.post(new Runnable() {
                        @Override
                        public void run() {
                            ePu.requestFocus();
                        }
                    });
                }
            }
            else {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        orp.setPdsNet(poidTotal);
                        editText.requestFocus();
                    }
                });
            }

            poids = 0;
        }
        else if (poids == 0)
        {
            poids++;
            poidTotal = Double.parseDouble(ePdsNet.getText().toString());
            ePdsNet.setText("");
            ePdsNet.post(new Runnable() {
                @Override
                public void run() {
                    ePdsNet.requestFocus();
                }
            });
        }
        else
        {
            poids++;
            poidTotal = poidTotal +  Double.parseDouble(ePdsNet.getText().toString());
            ePdsNet.setText("");
            ePdsNet.post(new Runnable() {
                @Override
                public void run() {
                    ePdsNet.requestFocus();
                }
            });
        }
    }

    private void lockUI()
    {
        eArtnr.setEnabled(false);
        if(Helper.paramLot < 2)
        {
            eLot.setEnabled(false);
        }
        eColis.setEnabled(false);
        ePieces.setEnabled(false);
        ePdsNet.setEnabled(false);
        ePdsBrut.setEnabled(false);
        eTare.setEnabled(false);
        ePu.setEnabled(false);
        eMontant.setEnabled(false);
        bottomNavigationView.setEnabled(false);
    }

    private void unlockUI()
    {
        eArtnr.setEnabled(true);
        if(Helper.paramLot < 2)
        {
            eLot.setEnabled(true);
        }
        eColis.setEnabled(true);
        ePieces.setEnabled(true);
        ePdsNet.setEnabled(true);
        ePdsBrut.setEnabled(true);
        eTare.setEnabled(true);
        ePu.setEnabled(true);
        eMontant.setEnabled(true);
        bottomNavigationView.setEnabled(true);
    }


    public void manageFocus(){

        eArtnr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setGetArticle();
                eColis.requestFocus();
                return false;
            }
        });

        eLot.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setGetLot(2);
                eColis.requestFocus();
                return false;
            }
        });

        eColis.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(0);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            ePdsNet.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsNet.requestFocus();
                                }
                            });
                            break;

                        case "poids brut total":
                            ePdsBrut.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsBrut.requestFocus();
                                }
                            });
                            break;

                        case "null":
                            ePiecesU.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePiecesU.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            if(Helper.saisiePrix == 0) {
                                validOrp();
                            }
                            else {
                                ePu.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ePu.requestFocus();
                                    }
                                });
                            }
                            break;

                        case "pieces totales":
                            ePieces.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePieces.requestFocus();
                                }
                            });
                            break;

                        default:
                            ePiecesU.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePiecesU.requestFocus();
                                }
                            });
                            break;

                    }
                }
                return false;
            }

        });

        ePiecesU.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "poids brut total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "null":
                            ePieces.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePieces.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "pieces totales":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        default:
                            ePieces.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePieces.requestFocus();
                                }
                            });
                            break;
                    }
                }
                return false;
            }

        });

        ePieces.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "poids brut total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "null":
                            ePdsBrutU.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsBrutU.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "pieces totales":
                            if(Helper.saisiePrix == 0) {
                                validOrp();
                            }
                            else {
                                ePu.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ePu.requestFocus();
                                    }
                                });
                            }
                            break;

                        default:
                            ePdsBrutU.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsBrutU.requestFocus();
                                }
                            });
                            break;
                    }

                }
                return false;
            }

        });

        ePdsBrutU.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "Poids brut total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "null":
                            ePdsBrut.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsBrut.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "pieces totales":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        default:
                            ePdsBrut.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsBrut.requestFocus();
                                }
                            });
                            break;
                    }

                }
                return false;
            }

        });

        ePdsBrut.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "poids brut total":
                            eTareU.post(new Runnable() {
                                @Override
                                public void run() {
                                    eTareU.requestFocus();
                                }
                            });
                            break;

                        case "null":
                            eTareU.post(new Runnable() {
                                @Override
                                public void run() {
                                    eTareU.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "pieces totales":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        default:
                            eTareU.post(new Runnable() {
                                @Override
                                public void run() {
                                    eTareU.requestFocus();
                                }
                            });
                            break;
                    }

                }
                return false;
            }

        });

        eTareU.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "poids brut total":
                            if(Helper.saisiePrix == 0) {
                                validOrp();
                            }
                            else {
                                ePu.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ePu.requestFocus();
                                    }
                                });
                            }
                            break;

                        case "null":
                            eTare.post(new Runnable() {
                                @Override
                                public void run() {
                                    eTare.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "pieces totales":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        default:
                            eTare.post(new Runnable() {
                                @Override
                                public void run() {
                                    eTare.requestFocus();
                                }
                            });
                            break;
                    }

                }
                return false;
            }

        });

        eTare.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "poids brut total":
                            if(Helper.saisiePrix == 0) {
                                validOrp();
                            }
                            else {
                                ePu.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ePu.requestFocus();
                                    }
                                });
                            }
                            break;

                        case "null":
                            ePdsNetU.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsNetU.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            break;

                        case "pieces totales":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        default:
                            ePdsNetU.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsNetU.requestFocus();
                                }
                            });
                            break;
                    }

                }
                return false;
            }

        });

        ePdsNetU.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "poids brut total":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "null":
                            ePdsNet.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsNet.requestFocus();
                                }
                            });
                            break;

                        case "colis":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        case "pieces totales":
                            eColis.post(new Runnable() {
                                @Override
                                public void run() {
                                    eColis.requestFocus();
                                }
                            });
                            break;

                        default:
                            ePdsNet.post(new Runnable() {
                                @Override
                                public void run() {
                                    ePdsNet.requestFocus();
                                }
                            });
                            break;
                    }

                }
                return false;
            }

        });

        ePdsNet.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    switch (orp.getModeSaisie()) {

                        case "poids net total":

                            if(orp.getPoidsVar() == 1) {
                                saisiePoidsVar(ePu);
                            }
                            else {
                                if(Helper.saisiePrix == 0) {
                                    validOrp();
                                }
                                else {
                                    ePu.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ePu.requestFocus();
                                        }
                                    });
                                }
                            }

                            break;

                        case "poids brut total":
                            if(orp.getPoidsVar() == 1) {
                                saisiePoidsVar(eColis);
                            }
                            else {
                                eColis.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        eColis.requestFocus();
                                    }
                                });
                            }
                            break;

                        case "null":
                            if(orp.getPoidsVar() == 1) {
                                saisiePoidsVar(ePu);
                            }
                            else {
                                if(Helper.saisiePrix == 0) {
                                    validOrp();
                                }
                                else {
                                    ePu.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ePu.requestFocus();
                                        }
                                    });
                                }
                            }
                            break;

                        case "colis":
                            if(orp.getPoidsVar() == 1) {
                                saisiePoidsVar(eColis);
                            }
                            else {
                                eColis.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        eColis.requestFocus();
                                    }
                                });
                            }
                            break;

                        case "pieces totales":
                            if(orp.getPoidsVar() == 1) {
                                saisiePoidsVar(eColis);
                            }
                            else {
                                eColis.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        eColis.requestFocus();
                                    }
                                });
                            }
                            break;

                        default:
                            if(orp.getPoidsVar() == 1) {
                                saisiePoidsVar(ePu);
                            }
                            else {
                                if(Helper.saisiePrix == 0) {
                                    validOrp();
                                }
                                else {
                                    ePu.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ePu.requestFocus();
                                        }
                                    });
                                }
                            }
                            break;
                    }

                }
                return false;
            }

        });


        ePu.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(orp.getVente().getDlc().equalsIgnoreCase("1")){
                        eDLC.post(new Runnable() {
                            @Override
                            public void run() {
                                eDLC.requestFocus();
                            }
                        });
                    }
                    else {
                        if(orp.getOrdernr().equalsIgnoreCase("")){
                            setCreateOrp();
                        }
                        else {
                            setUpdateOrp();
                        }
                    }
                }
                return false;
            }

        });

        eDLC.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculate(1);
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(!isValid(parseDate(eDLC.getText().toString())))
                    {
                        showError("DLC incorrecte", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eDLC.setText("");
                                eDLC.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        eDLC.requestFocus();
                                    }
                                });
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                dialog.dismiss();
                            }
                        });
                    }
                    else {
                        orp.setDLC(parseDate(eDLC.getText().toString()));
                        eDLC.setText(orp.getDLC());

                        if(orp.getOrdernr().equalsIgnoreCase("")){
                            setCreateOrp();
                        }
                        else {
                            setUpdateOrp();
                        }

                    }
                }
                return false;
            }

        });
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    public String parseDate(String date) {
        if(date.length() != 4 && date.length()!= 6) {
            return "NULL";
        }
        else if (date.length() == 6){

            int date2 = Integer.parseInt(date);
            int day = date2 / 10000;
            int month = date2 / 100;
            month = month % 100;

            String m;
            if(month > 9) {
                m = Integer.toString(month);
            }
            else {
                m = "0" + month;
            }

            int year = date2 - (day * 10000 + month * 100);
            return day + "/" + m + "/" + year;

        }
        else {

            int dlc = Integer.parseInt(date);

            Calendar calendar = Calendar.getInstance();
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            int curYear = calendar.get(Calendar.YEAR) - 2000;
            int curMonth = calendar.get(Calendar.MONTH);

            int day = dlc / 100;
            int month = dlc % 100;

            String m;
            if(month > 9) {
                m = Integer.toString(month);
            }
            else {
                m = "0" + month;
            }

            if(month > curMonth) {
                return day + "/" + m + "/" + curYear;
            }
            else if (month < curMonth) {
                int y = curYear + 1;
                return day + "/" + m + "/" + y;
            }
            else if(month == curMonth && day < curDay) {
                int y = curYear + 1;
                return day + "/" + m + "/" + y;
            }
            else {
                return day + "/" + m + "/" + curYear;
            }
        }
    }

    public static boolean isValid(String date) {
        try {
            Date simple = new SimpleDateFormat("dd/MM/yy").parse(date);
            Format format = new SimpleDateFormat("dd/MM/yy");

            if (!date.equals(format.format(simple)))
                return false;

            return true;
        } catch(ParseException e) {
            return false;
        }
    }


    private void setGetFields() {
        RequestParams params = Helper.GenerateParams(SaisieLigne.this);

        if(!eArtnr.getText().toString().equalsIgnoreCase("")){
            params.put("artnr",eArtnr.getText().toString());
        }
        else {
            params.put("artnr","NULL");
        }

        if(!eLot.getText().toString().equalsIgnoreCase("")){
            params.put("lot",eLot.getText().toString());
        }
        else {
            params.put("lot","NULL");
        }

        params.put("depot",Helper.depot);
        String URL = Helper.GenereateURI(SaisieLigne.this, params, "getfields");

        System.out.println(URL);

        //Verouillage de l'interface
        lockUI();

        // Call API JEE

        GetFields task = new GetFields();
        task.execute(new String[] { URL });
    }

    private void setGetLot(int type) {
        RequestParams params = Helper.GenerateParams(SaisieLigne.this);
        if(type == 1) {
            params.put("field",eArtnr.getText().toString());
            params.put("type",1);
        }
        else {
            params.put("field",eLot.getText().toString());
            params.put("type",2);
        }
        String URL = Helper.GenereateURI(SaisieLigne.this, params, "getlots");


        //Verouillage de l'interface
        lockUI();

        // Call API JEE

        GetLot task = new GetLot();
        task.execute(new String[] { URL });
    }

    private void setGetArticle() {
        RequestParams params = Helper.GenerateParams(SaisieLigne.this);
        params.put("field",eArtnr.getText().toString().replace("'","''"));
        params.put("lagstalle",orp.getVente().getDepot());
        String URL = Helper.GenereateURI(SaisieLigne.this, params, "getarticle");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE

        GetArticle task = new GetArticle();
        task.execute(new String[] { URL });
    }

    private void setUpdateOrp() {
        RequestParams params = Helper.GenerateParams(SaisieLigne.this);
        params.put("Ordernr",orp.getOrdernr());
        params.put("Ordradnr",orp.getOrdradnr());
        params.put("ua1", eColis.getText());
        params.put("ua3", ePieces.getText());
        params.put("ua9", ePdsNet.getText());

        params.put("pu",orp.getPu());

        String URL = Helper.GenereateURI(SaisieLigne.this, params, "updateorp");


        //Verouillage de l'interface
        lockUI();

        // Call API JEE

        UpdateOrp task = new UpdateOrp();
        task.execute(new String[] { URL });
    }

    private void setCreateOrp() {

        RequestParams params = Helper.GenerateParams(SaisieLigne.this);
        params.put("Ftgnr",orp.getVente().getCode());
        params.put("Artnr",eArtnr.getText().toString());
        params.put("ua1",eColis.getText().toString());
        params.put("ua3",ePieces.getText().toString());
        params.put("ua5",ePdsBrut.getText().toString());
        params.put("ua6",eTare.getText().toString());
        params.put("ua9",ePdsNet.getText().toString());
        params.put("pu",ePu.getText().toString());
        params.put("DLC",eDLC.getText().toString());
        params.put("Saljare", Helper.vendeur);
        params.put("Lot", eLot.getText().toString());

        String URL = Helper.GenereateURI(SaisieLigne.this, params, "createorp");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE

        CreateOrp task = new CreateOrp();
        task.execute(new String[] { URL });
    }


    private void setValidVente() {
        // Construction de l'URL
        RequestParams params = Helper.GenerateParams(SaisieLigne.this);
        params.put("Ordernr",orp.getVente().getOrderNr());

        String URL = Helper.GenereateURI(SaisieLigne.this, params, "validvente");

        //Verouillage de l'interface
        lockUI();

        // Call API JEE
        ValidVente task = new ValidVente();
        task.execute(new String[] { URL });
    }

    private class GetFields extends AsyncTask<String, Void, String> {
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
                showError("Impossible de récupérer les champs", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
            else {

                try {

                    JSONArray jsonArray = new JSONArray(output);

                    orp.setLib(jsonArray.getJSONObject(0).getString("Lib").trim());
                    orp.setModeSaisie(jsonArray.getJSONObject(0).getString("ModeSaisie").toLowerCase());
                    orp.setUniteFact(jsonArray.getJSONObject(0).getString("UniteFact").trim());
                    orp.setPiecesU(jsonArray.getJSONObject(0).getDouble("PieceU"));
                    orp.setPdsBrutU(jsonArray.getJSONObject(0).getDouble("PdsBrutU"));
                    orp.setTareU(jsonArray.getJSONObject(0).getDouble("TareU"));
                    orp.setPdsNetU(jsonArray.getJSONObject(0).getDouble("PdsNetU"));
                    orp.setPoidsVar(jsonArray.getJSONObject(0).getInt("PoidsVar"));

                    fillFields();

                } catch (Exception ex) {
                    fillFields();
                }
            }
        }
    }


    private class GetArticle extends AsyncTask<String, Void, String> {
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
                showError("Article Inconnu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newOrp();
                    }
                });
            }
            else {

                try {

                    JSONArray jsonArray = new JSONArray(output);

                    // Si c'est un code article complet
                    if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("1")){

                        eArtnr.setText(jsonArray.getJSONObject(0).getString("code"));
                        eLib.setText(jsonArray.getJSONObject(0).getString("lib"));
                        orp.setArtnr(jsonArray.getJSONObject(0).getString("code"));
                        orp.setLib(jsonArray.getJSONObject(0).getString("lib"));

                        if(Helper.paramLot > 1 ) {
                            setGetLot(1);
                        }
                        else {
                            setGetFields();
                        }
                    }
                    // Si c'est un libelle article complet
                    else if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("2")){

                        eArtnr.setText(jsonArray.getJSONObject(0).getString("code"));
                        eLib.setText(jsonArray.getJSONObject(0).getString("lib"));
                        orp.setArtnr(jsonArray.getJSONObject(0).getString("code"));
                        orp.setLib(jsonArray.getJSONObject(0).getString("lib"));

                        if(Helper.paramLot > 1 ) {
                            setGetLot(1);
                        }
                        else {
                            setGetFields();
                        }
                    }
                    // Si il n'y a qu'un seul article correspondant
                    else if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("3") && jsonArray.length() == 1){

                        eArtnr.setText(jsonArray.getJSONObject(0).getString("code"));
                        eLib.setText(jsonArray.getJSONObject(0).getString("lib"));
                        orp.setArtnr(jsonArray.getJSONObject(0).getString("code"));
                        orp.setLib(jsonArray.getJSONObject(0).getString("lib"));

                        if(Helper.paramLot > 1 ) {
                            setGetLot(1);
                        }
                        else {
                            setGetFields();
                        }
                    }
                    // Si c'est un morceau de code ou lib article
                    else if (jsonArray.getJSONObject(0).getString("exist").equalsIgnoreCase("3")){

                        Articles = new String[jsonArray.length()];

                        for(int i=0; i<jsonArray.length();i++)
                        {
                            Articles[i] = "[" + jsonArray.getJSONObject(i).getString("code").trim() + "] "
                                    + jsonArray.getJSONObject(i).getString("lib").trim()+ '\n' + "STOCK : "
                                    + round(Double.parseDouble(jsonArray.getJSONObject(i).getString("stock")), 0);
                        }
                        selectArticle();
                    }
                    else {
                        showError("Article Inconnu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eArtnr.setText("");
                                eArtnr.requestFocus();
                            }
                        });
                    }

                } catch (Exception ex) {}

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }


    private class GetLot extends AsyncTask<String, Void, String> {
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
            if(output.equalsIgnoreCase("[]"))
            {
                showError("Pas de lot", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newOrp();
                    }
                });
            }
            else {

                try {

                    JSONArray jsonArray = new JSONArray(output);

                    // Si il n'y a qu'un lot
                    if (jsonArray.getJSONObject(0).getString("col").equalsIgnoreCase("2")){

                        orp.setLot(jsonArray.getJSONObject(0).getString("lot"));
                        orp.setArtnr(jsonArray.getJSONObject(0).getString("artnr"));
                        orp.setLib(jsonArray.getJSONObject(0).getString("lib"));
                        eLot.setText(orp.getLot());
                        eLib.setText(orp.getLib());
                        eArtnr.setText(orp.getArtnr());

                        setGetFields();
                    }
                    else if (jsonArray.getJSONObject(0).getString("col").equalsIgnoreCase("1") && jsonArray.length() == 1){

                        orp.setLot(jsonArray.getJSONObject(0).getString("lot"));
                        orp.setArtnr(jsonArray.getJSONObject(0).getString("artnr"));
                        orp.setLib(jsonArray.getJSONObject(0).getString("lib"));
                        eLot.setText(orp.getLot());
                        eLib.setText(orp.getLib());
                        eArtnr.setText(orp.getArtnr());

                        setGetFields();

                    }
                    // Si il y a plusieurs lot
                    else if (jsonArray.getJSONObject(0).getString("col").equalsIgnoreCase("1")){

                        Lots = new String[jsonArray.length()];
                        Lots2 = new String[jsonArray.length()];

                        for(int i=0; i<jsonArray.length();i++)
                        {
                            Lots2[i] = jsonArray.getJSONObject(i).getString("lot");
                            Lots[i] = jsonArray.getJSONObject(i).getString("fournisseur").trim()
                                    + '\n' + "STOCK : "
                                    + round(Double.parseDouble(jsonArray.getJSONObject(i).getString("stock")), 0)
                                    + "     " + "PRIX : "
                                    + round(jsonArray.getJSONObject(i).getDouble("prix"),2) + " €";
                        }
                        selectLot();

                    }
                    else {
                        showError("Lot Inconnu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eLot.setText("");
                                eLot.requestFocus();
                            }
                        });
                    }

                } catch (Exception ex) {}

                InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }


    private class CreateOrp extends AsyncTask<String, Void, String> {
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
            System.out.println(output);
            unlockUI();
            if(output.equalsIgnoreCase("-1"))
            {
                showError("Impossible de créer la ligne...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newOrp();
                    }
                });
            }
            else {

                try {

                    JSONArray jsonArray = new JSONArray(output);

                    eMontant.setText(jsonArray.getJSONObject(0).getString("Montant"));
                    orp.setMontant(Double.parseDouble(eMontant.getText().toString()));

                } catch (Exception ex) {}

                newOrp();

            }
        }
    }


    private class UpdateOrp extends AsyncTask<String, Void, String> {
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
            newOrp();
            if(output.equalsIgnoreCase("-1"))
            {
                showError("Problème de mise à jour...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
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
            Saisie.activity.finish();
            Intent saisieActivity = new Intent(SaisieLigne.this, Saisie.class);
            saisieActivity.putExtra("vente", new Vente("","",0.0,0.0,"","","0",0));
            startActivity(saisieActivity);
            SaisieLigne.this.finish();
        }
    }
}
