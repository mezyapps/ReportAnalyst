package com.mezyapps.reportanalyst.network_class;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.utils.SessionManager;

import java.sql.Connection;
import java.sql.Statement;

public class ChnagePassword extends AppCompatActivity {
    TextInputEditText username_ed,password_ed,re_password_ed;
    Button submit_chnage;
    ConectionAdmin connectionClass;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chnage_password);
        sessionManager=new SessionManager(getApplicationContext());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        connectionClass = new ConectionAdmin();
        username_ed=findViewById(R.id.username_ed);
        password_ed=findViewById(R.id.password_ed);
        re_password_ed=findViewById(R.id.re_password_ed);
        submit_chnage=findViewById(R.id.submit_chnage);
        submit_chnage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoChange doChange = new DoChange();
                doChange.execute("");
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    public class DoChange extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;
        String usenaame=username_ed.getText().toString().trim();
        String password=password_ed.getText().toString().trim();
        String repassword=re_password_ed.getText().toString().trim();
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {

            Toast.makeText(ChnagePassword.this,r,Toast.LENGTH_SHORT).show();

            if(isSuccess) {
                Toast.makeText(ChnagePassword.this,r,Toast.LENGTH_SHORT).show();
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected String doInBackground(String... params) {
            if(usenaame.trim().equals("")|| password.trim().equals("") || repassword.trim().equals("")){
                z = "Please enter User Name and Password";
            } else {
                if (password.equals(repassword)){
                    try {
                        Connection con = connectionClass.CONNN();
                        if (con == null) {
                            z = "Error in connection with SQL server";
                        } else {
                            String query = "UPDATE UACCESS SET  USERPASSWORD='" + password + "' WHERE  USERNAME='" + usenaame + "' ";
                            Statement stmt = con.createStatement();
                            int rs=stmt.executeUpdate(query);
                            if(rs == 1)
                            {
                                z = "Successfuly";
                                isSuccess=true;
                                finishAffinity();
                                sessionManager.logoutUser();

                            }
                            else
                            {
                                z = "Invalid Usename";
                                isSuccess = false;
                            }

                        }
                    }
                    catch (Exception ex)
                    {
                        z = "Invalid Usename";
                        isSuccess = false;
                    }

                } else {
                    z = "Password Not Match";
                }
            }
            return z;
        }

    }

}
