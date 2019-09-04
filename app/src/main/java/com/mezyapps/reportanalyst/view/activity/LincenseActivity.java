package com.mezyapps.reportanalyst.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.utils.SessionManager;
import com.mezyapps.reportanalyst.network_class.LicenseSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class LincenseActivity extends AppCompatActivity {
    String licenseString, macAddress;
    SQLiteDatabase db;
    LicenseSession licenseSession;
    String straate;
    EditText licenseEdit;
    ArrayList<String> sss = new ArrayList<>();
    Button btn_call_me_back;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lincense);
        db = openOrCreateDatabase("MY_INVOICE", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS CON_TABLE(id INTEGER PRIMARY KEY AUTOINCREMENT,con_ip VARCHAR,db_name VARCHAR,username VARCHAR,password VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS SEVEN_DAYS(slno INTEGER PRIMARY KEY AUTOINCREMENT,start_date VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS MAC(id INTEGER PRIMARY KEY AUTOINCREMENT,license VARCHAR,status VARCHAR)");

        sessionManager = new SessionManager(getApplicationContext());

        licenseSession = new LicenseSession(getApplicationContext());
        licenseEdit = findViewById(R.id.ed_licensekey);
        Button licenseButton = findViewById(R.id.btn_lecense_next);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        macAddress = telephonyManager.getDeviceId();


//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wInfo = wifiManager.getConnectionInfo();
        // macAddress = wInfo.getMacAddress().replaceAll(":","");
        //Toast.makeText(this,  macAddress, Toast.LENGTH_SHORT).show();
        licenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                licenseString = licenseEdit.getText().toString().trim().replaceAll(":", "");
                if (licenseString.equals("")) {
                    Toast.makeText(LincenseActivity.this, "Enter License Key", Toast.LENGTH_SHORT).show();
                } else {
                    licenseverify();
                }
            }
        });


        Cursor c1 = db.rawQuery("SELECT * FROM MAC ", null);
        if (c1.moveToFirst()) {
            do {
                straate = c1.getString(c1.getColumnIndex("license"));
            } while (c1.moveToNext());

            macMethod();
        } else {
            //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        }
        c1.close();

        btn_call_me_back = findViewById(R.id.btn_call_me_back);
        btn_call_me_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:9890322940"));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(callIntent);
            }
        });

    }

    private void licenseverify() {

        String url = "http://registermykenan.com/mac-api.php?mac=" + macAddress + "&" + "key=" + licenseString;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    // String myObjAsString = jsonResponse.getString("type");
                    String status = jsonResponse.getString("status");
                    if (status.equals("True")) {
                        db.execSQL("INSERT INTO MAC(license)VALUES('" + licenseString + "')");
                        db.delete("SEVEN_DAYS", null, null);
                        Toast.makeText(LincenseActivity.this, "Request Has Been Sent", Toast.LENGTH_SHORT).show();
                        licenseEdit.setText("");
                    } else {
                        if (status.equals("False")) {
                            Toast.makeText(LincenseActivity.this, "Invalid License Key", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(LincenseActivity.this, "Invalid License Key", Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            //utoCompleteTextView_customerName.setError("Not found Customer");
                            // Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(LincenseActivity.this);
        requestQueue.add(stringRequest);


    }

    private void macMethod() {

        String url = "http://registermykenan.com/api.php?key=" + straate;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.toString().split("\\{|\\}")[1];
                    String temst = status.toString().replaceAll("\"", ",").replaceAll("\"", "");
                    String[] namesList = temst.split(",");
                    for (int i = 0; i < namesList.length; i++) {
                        sss.add(namesList[i]);
                        if (sss.contains(macAddress)) {

                            Cursor c = db.rawQuery("SELECT * FROM CON_TABLE ", null);
                            if (c != null && c.getCount() > 0) {
                                licenseSession.createLicenseSession(straate, macAddress);
                                startActivity(new Intent(LincenseActivity.this, LoginActivity.class));
                                finish();

                            } else {
                                licenseSession.createLicenseSession(straate, macAddress);
                                startActivity(new Intent(LincenseActivity.this, IpSettingActivity.class));
                                finish();

                            }

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            //utoCompleteTextView_customerName.setError("Not found Customer");
                            // Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(LincenseActivity.this);
        requestQueue.add(stringRequest);

    }

    public void stratwithdemo(View view) {
        Cursor c1 = db.rawQuery("SELECT * FROM SEVEN_DAYS ", null);
        if (c1 != null && c1.getCount() > 0) {
           Toast.makeText(LincenseActivity.this, "Already Use Trial App", Toast.LENGTH_SHORT).show();
        } else {
            long date = System.currentTimeMillis();
            SimpleDateFormat df = new SimpleDateFormat("yyyy MM dd");
            String str_order_date = df.format(date);
            db.execSQL("INSERT INTO SEVEN_DAYS(start_date)VALUES('" + str_order_date + "')");
            licenseSession.createLicenseSession("xyz", "abc");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }
}