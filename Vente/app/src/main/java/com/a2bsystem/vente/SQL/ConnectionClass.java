package com.a2bsystem.vente.SQL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

import static com.a2bsystem.vente.Activities.config.Config.Config;

public class ConnectionClass {
    public static String ip;
    public static String classs = "net.sourceforge.jtds.jdbc.Driver";
    public static String db;
    public static String user;
    public static String password;

    public static String vendeurCode;
    public static String vendeurLib;


    @SuppressLint("NewApi")
    public Connection CONN(Activity activity) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            // Recupération de la config
            SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences(Config, 0);
            db = sharedPreferences.getString("BDD",null);
            ip = sharedPreferences.getString("URL",null);

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + user + ";password="
                    + password + ";";

            System.out.println(ConnURL);

            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}