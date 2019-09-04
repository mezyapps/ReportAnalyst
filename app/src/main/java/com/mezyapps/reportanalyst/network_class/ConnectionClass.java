package com.mezyapps.reportanalyst.network_class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionClass {
Context context;
    //    String ip = "192.168.1.125";
//    String classs = "net.sourceforge.jtds.jdbc.Driver";
//    String db = "KENAN_TRD_GST";
//    String un = "sa";
//    String password = "sa123";
//String ip = "67.211.45.179:1091";
   String classs = "net.sourceforge.jtds.jdbc.Driver";
//    String db = "KENAN_APP";
//    String un = "JMDINFOTECH";
//    String password = "Jmd&23Info$10Tech~79";
    //    start sup name;
  String iip;
  String dbb;
  String uss;
  String pss;
    IPAddress ipAddress;
//    public void setIpAddress(String ippp, String dbbb,String usss,String passs){
//        iip=ippp;
//        dbb=dbbb;
//        uss=usss;
//        pss=passs;
//    }
    public  ConnectionClass(Context context){
        this.context=context;
       SharedPreferences pref = context.getSharedPreferences("CON", Context.MODE_PRIVATE);
        iip= pref.getString("ip","");
        dbb= pref.getString("db","");
        uss= pref.getString("us","");
        pss= pref.getString("ps","");


    }

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection conn = null;
        String ConnURL = null;

        ipAddress=new IPAddress();
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" +  iip + ";" + "databaseName=" + dbb + ";user=" + uss + ";password=" + pss + ";";
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
