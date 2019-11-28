package com.a2bsystem.vente;

import android.app.Activity;
import android.content.SharedPreferences;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.a2bsystem.vente.Activities.config.Config.Config;


public class Helper {

    // Vendeur
    public static String User;
    public static String Mdp;

    public static String vendeur;

    // Paramètres client
    public static int paramLot;
    public static String depot;
    public static int saisiePrix;
    public static int monoVente;

    // Certificat SSL sur le serveur web?
    public static Boolean SSL = true;


    // Récupère le statut de la requête
    public static Boolean GetSuccess(byte[] source) {
        String s = new String(source);
        try {
            return(new JSONObject(s).getInt("status")==100);
        }catch(JSONException e) {
            return false;
        }
    }

    //Retourne la premiere colonne
    public static JSONObject GetFirstRow(byte[] source) {
        return GetRowAt(source, 0);
    }

    public static JSONArray GetList(byte[] source) {
        String s = new String(source);
        try {
            return new JSONObject(s).getJSONArray("recordsets").getJSONArray(0);
        } catch (JSONException e) {
            return null;
        }
    }

    // Retourne la colonne x
    public static JSONObject GetRowAt(byte[] source, int index) {
        try {
            return GetList(source).getJSONObject(index);
        } catch (JSONException e) {
            return null;
        }
    }

    // Retourne le statut de la requête
    public static int GetStatus(byte[] source)
    {
        String s = new String(source);

        try
        {
            return new JSONObject(s).getInt("status");
        }
        catch(JSONException e)
        {
            return 0;
        }
    }

    // Génère les paramètres de la requêtes depuis la config
    public static RequestParams GenerateParams(Activity activity)
    {
        SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences(Config, 0);
        String BDD = sharedPreferences.getString("BDD",null);
        String Foretagkod = sharedPreferences.getString("Foretagkod",null);
        RequestParams params = new RequestParams();
        params.setUseJsonStreamer(true);
        params.put("BDD", BDD);
        params.put("Foretagkod", Foretagkod);
        params.put("User", User);
        params.put("Mdp", Mdp);
        return params;
    }

    // Génère l'url
    public static String GenereateURI(Activity activity, RequestParams params, String route) {
        SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences(Config, 0);
        String URL = sharedPreferences.getString("URL",null);



            return "http://"
                    + URL
                    + "/Vente/"
                    + route
                    +"?"
                    + params.toString();
    }


    public static SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }
}
