package com.mezyapps.reportanalyst.network_class;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mezyapps.reportanalyst.view.activity.MainActivity;
import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.utils.SessionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IPSettingPageList extends AppCompatActivity {
    GridView gridViewv;
    ConectionAdmin conectionAdmin;
    Connection con;
    SimpleAdapter ADA;
    String quuerycalender;
    SQLiteDatabase db;
    int idip;
    SharedPreferences pref;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipsetting_page_list);
        gridViewv=findViewById(R.id.gridvietipsetting);
        conectionAdmin=new ConectionAdmin();
        db = openOrCreateDatabase("MY_INVOICE", Context.MODE_PRIVATE, null);
        pref = this.getSharedPreferences("CON", Context.MODE_PRIVATE);
        sessionManager=new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
            String userid=  user.get(SessionManager.KEY_password);
            quuerycalender = "Select * from DB_TABLE WHERE USERID="+Integer.parseInt(userid);


        try {

            con = conectionAdmin.CONNN();

            if (con == null) {
                Toast.makeText(this, "connection problem", Toast.LENGTH_SHORT).show();
            } else {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(quuerycalender);


            ArrayList    data = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> datanum = new HashMap<String, String>();
                    datanum.put("A", rs.getString("PRINT_NAME"));
                    datanum.put("B", rs.getString("SERVER_IP"));
                    datanum.put("C", rs.getString("DB_NAME"));
                    datanum.put("D", rs.getString("DB_USERNAME"));
                    datanum.put("E", rs.getString("DB_PASSWARD"));
                    data.add(datanum);
                }
                String[] from = {"A"};
                final int[] views = {R.id.prinnameip,};
                ADA = new SimpleAdapter(IPSettingPageList.this, data, R.layout.ip_listgrid_layout, from, views);
                gridViewv.setAdapter(ADA);


                gridViewv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                        String printname = map.get("A");
                        String ipstrin = map.get("B");
                        String dptext = map.get("C");
                        String userstring = map.get("D");
                        String passstring = map.get("E");
                        //    start sup name;
                        Cursor c = db.rawQuery("SELECT * FROM CON_TABLE ", null);
                        if (c != null && c.getCount() > 0) {

                            if (c.moveToFirst()) {
                                do {
                           idip=  c.getInt(c.getColumnIndex("id"));
                                } while (c.moveToNext());
                                c.close();
                            }
                            db.execSQL("UPDATE CON_TABLE  SET con_ip ='" + ipstrin + "',db_name ='" + dptext + "',username ='" + userstring + "',password ='" + passstring + "' WHERE id =" + idip + "");
                            Toast.makeText(IPSettingPageList.this, "Successfuly Updated", Toast.LENGTH_SHORT).show();
                            pref.edit().clear();
                            pref.edit().putString("print",printname).apply();
                            pref.edit().putString("ip",ipstrin).apply();
                            pref.edit().putString("db",dptext).apply();
                            pref.edit().putString("us",userstring).apply();
                            pref.edit().putString("ps",passstring).apply();
                            pref.edit().commit();
                          startActivity(new Intent(IPSettingPageList.this, MainActivity.class));
                            finish();
                        }  else {
                            db.execSQL("INSERT INTO CON_TABLE(con_ip,db_name,username,password)VALUES('" + ipstrin + "','" + dptext + "', '" + userstring + "','"+passstring+"' )");
                            pref.edit().clear();
                            pref.edit().putString("ip",ipstrin).apply();
                            pref.edit().putString("db",dptext).apply();
                            pref.edit().putString("us",userstring).apply();
                            pref.edit().putString("ps",passstring).apply();
                            pref.edit().commit();
                          startActivity(new Intent(IPSettingPageList.this,MainActivity.class));
                            finish();
                        }
                    }
                });


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(IPSettingPageList.this,MainActivity.class));
        finish();
    }
}