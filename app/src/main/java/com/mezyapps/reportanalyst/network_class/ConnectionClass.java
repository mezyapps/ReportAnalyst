package com.mezyapps.reportanalyst.network_class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import com.mezyapps.reportanalyst.model.IPAddress;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionClass {

    private Context context;
    private String classs = "net.sourceforge.jtds.jdbc.Driver";
    private String iip;
    private String dbb;
    private String uss;
    private String pss;
    private IPAddress ipAddress;

    public ConnectionClass(Context context) {
        this.context = context;
        SharedPreferences pref = context.getSharedPreferences("CON", Context.MODE_PRIVATE);
        iip = pref.getString("ip", "");
        dbb = pref.getString("db", "");
        uss = pref.getString("us", "");
        pss = pref.getString("ps", "");


    }

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn = null;
        String ConnURL = null;

        ipAddress = new IPAddress();
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + iip + ";" + "databaseName=" + dbb + ";user=" + uss + ";password=" + pss + ";";
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
