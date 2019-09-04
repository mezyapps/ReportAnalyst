package com.mezyapps.reportanalyst.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.mezyapps.reportanalyst.network_class.LicenseSession;
import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.utils.SessionManager;

import java.text.SimpleDateFormat;

public class DemoActivity extends AppCompatActivity {

    SQLiteDatabase db;
    LicenseSession licenseSession;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        licenseSession=new LicenseSession(getApplicationContext());
        sessionManager=new SessionManager(getApplicationContext());
        db = openOrCreateDatabase("MY_INVOICE", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS CON_TABLE(id INTEGER PRIMARY KEY AUTOINCREMENT,con_ip VARCHAR,db_name VARCHAR,username VARCHAR,password VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS SEVEN_DAYS(slno INTEGER PRIMARY KEY AUTOINCREMENT,start_date VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS MAC(id INTEGER PRIMARY KEY AUTOINCREMENT,license VARCHAR,status VARCHAR)");

        if (licenseSession.isLicenseedIn()){
            startActivity(new Intent(DemoActivity.this, MainActivity.class));
            finish();
        }

    }

    public void clicklicensekeybutton(View view) {
       startActivity(new Intent(DemoActivity.this, LincenseActivity.class));
    }

    public void clcikdemobutton(View view) {

                Cursor c1 = db.rawQuery("SELECT * FROM SEVEN_DAYS ", null);
                if (c1 != null && c1.getCount() > 0) {
//                    licenseSession.logoutUser();
//                    sessionManager.logoutUser();
                    Toast.makeText(DemoActivity.this, "Already Use Trial App", Toast.LENGTH_SHORT).show();
                }else {
                    long date = System.currentTimeMillis();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy MM dd");
                    String str_order_date = df.format(date);
                    db.execSQL("INSERT INTO SEVEN_DAYS(start_date)VALUES('" + str_order_date + "')");
                    startActivity(new Intent(getApplicationContext(), IpSettingActivity.class));
                    licenseSession.createLicenseSession("xyz","abc");
                    sessionManager.createLoginSession("aa","bb");
                    finish();

                }


    }
}
