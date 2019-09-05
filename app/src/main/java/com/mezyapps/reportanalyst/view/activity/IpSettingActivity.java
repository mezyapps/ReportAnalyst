package com.mezyapps.reportanalyst.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mezyapps.reportanalyst.R;

public class IpSettingActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private TextInputEditText iptext, dbtext, usertext, passtext;
    private Button submitbtoon;
    private int idip;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_setting);

        find_View_IdS();
        events();
    }

    private void find_View_IdS() {
        db = openOrCreateDatabase("MY_INVOICE", Context.MODE_PRIVATE, null);
        //  db.execSQL("CREATE TABLE IF NOT EXISTS CON_TABLE(id INTEGER PRIMARY KEY AUTOINCREMENT,con_ip VARCHAR,db_name VARCHAR,username VARCHAR,password VARCHAR)");
        //db.execSQL("INSERT INTO PRODUCT_DUMMY(pd_name,pd_size,pd_price)VALUES('" + productname + "','" + size + "', '" + pricee + "' )");

        iptext = findViewById(R.id.edt_ip_con);
        dbtext = findViewById(R.id.edt_dbname_con);
        usertext = findViewById(R.id.edt_username_con);
        passtext = findViewById(R.id.edt_password_con);
        pref = this.getSharedPreferences("CON", Context.MODE_PRIVATE);
        String iip = pref.getString("ip", "");
        String dbb = pref.getString("db", "");
        String uss = pref.getString("us", "");
        String pss = pref.getString("ps", "");

//
//        iptext.setText(iip);
//        dbtext.setText(dbb);
//        usertext.setText(uss);
//        passtext.setText(pss);

        submitbtoon = findViewById(R.id.btn_submit_ip);
    }

    private void events() {
        submitbtoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipstrin = iptext.getText().toString().trim();
                String dptext = dbtext.getText().toString().trim();
                String userstring = usertext.getText().toString().trim();
                String passstring = passtext.getText().toString().trim();
                if (ipstrin.equals("") || dptext.equals("") || userstring.equals("") || passstring.equals("")) {
                    Toast.makeText(IpSettingActivity.this, "Required All Field", Toast.LENGTH_SHORT).show();
                } else {
                    //    start sup name;
                    Cursor c = db.rawQuery("SELECT * FROM CON_TABLE ", null);
                    if (c != null && c.getCount() > 0) {

                        if (c.moveToFirst()) {
                            do {
                                idip = c.getInt(c.getColumnIndex("id"));
                            } while (c.moveToNext());
                            c.close();
                        }
                        db.execSQL("UPDATE CON_TABLE  SET con_ip ='" + ipstrin + "',db_name ='" + dptext + "',username ='" + userstring + "',password ='" + passstring + "' WHERE id =" + idip + "");
                        Toast.makeText(IpSettingActivity.this, "Successfuly Updated", Toast.LENGTH_SHORT).show();
                        pref.edit().clear();
                        pref.edit().putString("ip", ipstrin).apply();
                        pref.edit().putString("db", dptext).apply();
                        pref.edit().putString("us", userstring).apply();
                        pref.edit().putString("ps", passstring).apply();
                        pref.edit().commit();
                        startActivity(new Intent(IpSettingActivity.this, MainActivity.class));
                        finish();
                    } else {
                        db.execSQL("INSERT INTO CON_TABLE(con_ip,db_name,username,password)VALUES('" + ipstrin + "','" + dptext + "', '" + userstring + "','" + passstring + "' )");
                        pref.edit().clear();
                        pref.edit().putString("ip", ipstrin).apply();
                        pref.edit().putString("db", dptext).apply();
                        pref.edit().putString("us", userstring).apply();
                        pref.edit().putString("ps", passstring).apply();
                        pref.edit().commit();
                        startActivity(new Intent(IpSettingActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

        });
    }
}
