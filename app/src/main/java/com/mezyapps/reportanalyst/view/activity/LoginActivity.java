package com.mezyapps.reportanalyst.view.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.utils.SessionManager;
import com.mezyapps.reportanalyst.network_class.ConectionAdmin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText textInputEditTextname, textInputEditTextpass;
    private String user_id;
    private SessionManager sessionManager;
    private ConectionAdmin connectionClass;
    private ProgressBar pbbar;
    private SQLiteDatabase db;
    private String pass;
    private String ASCII_VALUE;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        find_View_IdS();
        events();
    }

    private void find_View_IdS() {
        sessionManager = new SessionManager(getApplicationContext());
        textInputEditTextname = findViewById(R.id.ed_username_signin);
        textInputEditTextpass = findViewById(R.id.ed_user_pass_signin);
        login = findViewById(R.id.btn_signin);
        db = openOrCreateDatabase("MY_INVOICE", Context.MODE_PRIVATE, null);

        // db.execSQL("CREATE TABLE IF NOT EXISTS CON_TABLE(id INTEGER PRIMARY KEY AUTOINCREMENT,con_ip VARCHAR,db_name VARCHAR,username VARCHAR,password VARCHAR)");

        connectionClass = new ConectionAdmin();
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);
        HashMap<String, String> user = sessionManager.getUserDetails();
//        if (user.get(SessionManager.KEY_NAME) == "aa"){
//            startActivity(new Intent(LoginActivity.this,MainActivity.class));
//            finish();
//        }else {
//
//               Cursor c = db.rawQuery("SELECT * FROM CON_TABLE ", null);
//        if (c != null && c.getCount() > 0) {
//
//        }else {
//            startActivity(new Intent(LoginActivity.this,IpSettingActivity.class));
//            finish();
//        }

        //    }

    }

    private void events() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ASCII_VALUE = "";
                pass = textInputEditTextpass.getText().toString().trim();

//                    //===========================================
//                    for (int i = 0; i < pass.length(); i++)
//                    {
//                        Character A = pass.charAt(i);
//                        int VALINT = Integer.valueOf(A);
//                        VALINT = VALINT + 150;
//                        char c = (char)VALINT;
//                        ASCII_VALUE += String.valueOf(c);
//                    }

                DoLogin doLogin = new DoLogin();
                doLogin.execute("");
            }
        });
    }

    public class DoLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;

        String name = textInputEditTextname.getText().toString();

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();

            if (isSuccess) {
                Toast.makeText(LoginActivity.this, r, Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (name.trim().equals("") || pass.trim().equals(""))
                z = "Please enter User Name and Password";
            else {
                try {
                    Connection con = connectionClass.CONNN();
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "select USERID from UACCESS where USERNAME='" + name + "' and USERPASSWORD='" + pass + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        if (rs.next()) {
                            String abbb = String.valueOf(rs.getInt("USERID"));
                            sessionManager.createLoginSession(name, abbb);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            z = "Successfuly";
                            isSuccess = true;

                        } else {
                            z = "Invalid Usename Or Password";
                            isSuccess = false;
                        }

                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = "Exceptions";
                }
            }
            return z;
        }
    }

}
