package com.mezyapps.reportanalyst.view.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.utils.SessionManager;
import com.mezyapps.reportanalyst.network_class.ConnectionClass;
import com.mezyapps.reportanalyst.utils.LicenseSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView nextdate, previousdate, calenderr_btn;
    private TextView todatetexttv, fromdatetexttv, totextet_to;
    private Calendar c, ccc;
    private String formattedDate;
    private SimpleDateFormat df, dff, nextandprevious, nextandpreviousformt;
    private ConnectionClass connectionClass;
    private DatePickerDialog datePickerDialog;
    private TextView saletotal_textview, purchase_total_textview;
    private int SERIESIDD;
    private String fmdt, todt;
    private Dialog dialog, dialogcustomdate;
    private String fromdate_c_string, todate_c_string, quuerycalender;
    private Connection con;
    private String satus;
    private String status_check;
    private SessionManager sessionManager;
    private SQLiteDatabase db;
    private String stringlicense, stringmac;
    private SharedPreferences pref;
    private String stratdate;
    private LicenseSession licenseSession;
    private TextView customerledgertv, supplierledgertv;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        find_View_IdS();
        callConnection();
        events();

    }

    private void find_View_IdS() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fromdatetexttv = findViewById(R.id.datefromview_textview);
        todatetexttv = findViewById(R.id.datetoview_textview);
        nextdate = findViewById(R.id.next_date);
        previousdate = findViewById(R.id.previosdate);
        saletotal_textview = findViewById(R.id.saletotalamt_tv);
        purchase_total_textview = findViewById(R.id.purchasetotalamt_tv);
        totextet_to = findViewById(R.id.to_tetxt_textview);
        toolbar =findViewById(R.id.toolbar);
        customerledgertv = findViewById(R.id.customerledgertotal);
        supplierledgertv = findViewById(R.id.supplierledgertotal);
        drawerLayout=findViewById(R.id.drawer_layout);



        pref = this.getSharedPreferences("CON", Context.MODE_PRIVATE);
        String print = pref.getString("print", "");
        if (print != "") {
            this.setTitle(print);
        }

        db = openOrCreateDatabase("MY_INVOICE", Context.MODE_PRIVATE, null);
        setSupportActionBar(toolbar);
        connectionClass = new ConnectionClass(MainActivity.this);
        licenseSession = new LicenseSession(getApplicationContext());
        licenseSession.checkLogin();
        HashMap<String, String> user = licenseSession.getUserDetails();
        stringlicense = user.get(LicenseSession.NAM);
        stringmac = user.get(LicenseSession.password);

        Cursor c1 = db.rawQuery("SELECT * FROM SEVEN_DAYS ", null);
        if (c1 != null && c1.getCount() > 0) {
            sevendaystrial();
        } else {
            checkConnection();
        }
        sessionManager = new SessionManager(getApplicationContext());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        ccc = Calendar.getInstance();
        nextandprevious = new SimpleDateFormat("dd-MM-yyyy");
        nextandpreviousformt = new SimpleDateFormat("yyyy/MM/dd");
        formattedDate = nextandpreviousformt.format(ccc.getTime());
        calenderr_btn = findViewById(R.id.calender_image_btn);

    }
    private void events() {
        previousdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ccc.add(Calendar.DATE, -1);
                formattedDate = nextandpreviousformt.format(ccc.getTime());

                Log.v("PREVIOUS DATE : ", formattedDate);
                fromdatetexttv.setVisibility(View.VISIBLE);
                todatetexttv.setText("");
                fromdatetexttv.setText(nextandprevious.format(ccc.getTime()));
                todatetexttv.setVisibility(View.GONE);
                totextet_to.setVisibility(View.GONE);
                getcalenderwisedata(formattedDate, "");
                SERIESIDD = 0;
            }
        });

        nextdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ccc.add(Calendar.DATE, 1);
                formattedDate = nextandpreviousformt.format(ccc.getTime());

                Log.v("NEXT DATE : ", formattedDate);
                fromdatetexttv.setVisibility(View.VISIBLE);
                fromdatetexttv.setText(nextandprevious.format(ccc.getTime()));
                todatetexttv.setText("");
                todatetexttv.setVisibility(View.GONE);
                totextet_to.setVisibility(View.GONE);
                getcalenderwisedata(formattedDate, "");
                SERIESIDD = 0;

            }
        });
        calenderr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.calenderlist_for_main);
                dialog.show();
                SERIESIDD = 0;
            }
        });
    }
    private void callConnection() {

        //start series date**********************
        try {
            con = connectionClass.CONN();
            if (con == null) {
                Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
            } else {
                String query = "select  SERIESID,FROMDT,TODT from SERIESMASTER where IS_DEFAULT='Y'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if (rs.next()) {

                    fmdt = rs.getString("FROMDT").replaceAll("/", "-");
                    todt = rs.getString("TODT").replaceAll("/", "-");
                    SERIESIDD = rs.getInt("SERIESID");
                    todatetexttv.setText(todt);
                    fromdatetexttv.setText(fmdt);

                    // Toast.makeText(this, ""+SERIESIDD, Toast.LENGTH_SHORT).show();

                    try {
                        String quuery = "SELECT (SELECT SUM(TOTALBILLAMT) FROM SALHEAD_VIEW WHERE SERIESID=" + SERIESIDD + ") AS SALETOTAL, " +
                                "" + "(SELECT SUM(TOTALBILLAMT) FROM PURHEAD_VIEW WHERE SERIESID=" + SERIESIDD + ") AS PURTOTAL, " +
                                "(SELECT SUM(BALAMT) FROM VCHDET_VIEW WHERE GRP_INFO='SUND_DR' AND SERIESID=" + SERIESIDD + ") AS CBALAMT," +
                                "(SELECT -SUM(BALAMT) FROM VCHDET_VIEW WHERE GRP_INFO='SUND_CR' AND SERIESID=" + SERIESIDD + ") AS SBALAMT";
                        Statement sstmt = con.createStatement();
                        ResultSet rss = sstmt.executeQuery(quuery);
                        if (rss.next()) {
                            String saletotal = String.valueOf(rss.getBigDecimal("SALETOTAL"));
                            String pertotal = String.valueOf(rss.getBigDecimal("PURTOTAL"));
                            String CBALAMT = String.valueOf(rss.getBigDecimal("CBALAMT"));
                            String SBALAMT = String.valueOf(rss.getBigDecimal("SBALAMT"));
                            saletotal_textview.setText(saletotal);
                            purchase_total_textview.setText(pertotal);
                            customerledgertv.setText(CBALAMT);
                            supplierledgertv.setText(SBALAMT);
                            // Toast.makeText(this, ""+saletotal+","+pertotal, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(this, "Exceptions", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Exceptions", Toast.LENGTH_SHORT).show();

        }

    }

    public void stockOutstanding(View view) {
        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
    }


    public void purchaseReport(View view) {
        con = connectionClass.CONN();
        if (con == null) {
            Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), PurchaseReportActivity.class);
            intent.putExtra("serid", String.valueOf(SERIESIDD));
            intent.putExtra("todt", todt);
            intent.putExtra("fdt", fmdt);
            intent.putExtra("hfdt", fromdatetexttv.getText().toString().trim());
            intent.putExtra("htdt", todatetexttv.getText().toString().trim());
            startActivity(intent);
        }
    }

    public void supplierledger(View view) {
        con = connectionClass.CONN();
        if (con == null) {
            Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), SupplierLedgerReportActivity.class);
            intent.putExtra("serid", String.valueOf(SERIESIDD));
            intent.putExtra("todt", todt);
            intent.putExtra("fdt", fmdt);
            intent.putExtra("hfdt", fromdatetexttv.getText().toString().trim());
            intent.putExtra("htdt", todatetexttv.getText().toString().trim());
            startActivity(intent);
        }
    }

    public void customerledger(View view) {
        con = connectionClass.CONN();
        if (con == null) {
            Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), CustomerLedgerReportActivity.class);
            intent.putExtra("serid", String.valueOf(SERIESIDD));
            intent.putExtra("todt", todt);
            intent.putExtra("fdt", fmdt);
            intent.putExtra("hfdt", fromdatetexttv.getText().toString().trim());
            intent.putExtra("htdt", todatetexttv.getText().toString().trim());
            startActivity(intent);
        }
    }


    public void saleReport(View view) {
        con = connectionClass.CONN();
        if (con == null) {
            Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, SaleReportActivity.class);
            intent.putExtra("serid", String.valueOf(SERIESIDD));
            intent.putExtra("todt", todt);
            intent.putExtra("fdt", fmdt);
            intent.putExtra("hfdt", fromdatetexttv.getText().toString().trim());
            intent.putExtra("htdt", todatetexttv.getText().toString().trim());

            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, IPSettingPageListActivity.class));
            finish();
            return true;
        } else if ((id == R.id.action_logout)) {
            sessionManager.logoutUser();
            finish();
            return true;
        } else if ((id == R.id.item1)) {
            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String subject = "Mezyapps";
            String bodymsg = "Application Link";
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, bodymsg);
            startActivity(Intent.createChooser(intent, "Mezyapps"));
        } else if (id == R.id.nav_send) {
            String url = "http://jmd-infotech.com/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //start calender dialog list**************************************
    public void todaydatemethoddd(View view) {
        df = new SimpleDateFormat("yyyy/MM/dd");
        dff = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = df.format(new Date());
        fromdatetexttv.setVisibility(View.VISIBLE);
        fromdatetexttv.setText(dff.format(new Date()));
        todatetexttv.setText("");
        todatetexttv.setVisibility(View.GONE);
        totextet_to.setVisibility(View.GONE);
        getcalenderwisedata(currentDateandTime, "");
        dialog.hide();
        SERIESIDD = 0;
    }

    public void yesterdaydatemethddd(View view) {
        dff = new SimpleDateFormat("dd-MM-yyyy");
        df = new SimpleDateFormat("yyyy/MM/dd");
        c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        fromdatetexttv.setVisibility(View.VISIBLE);
        fromdatetexttv.setText(dff.format(c.getTime()));
        String yseterdy = df.format(c.getTime());
        todatetexttv.setVisibility(View.GONE);
        todatetexttv.setText("");
        totextet_to.setVisibility(View.GONE);
        getcalenderwisedata(yseterdy, "");
        dialog.hide();
        SERIESIDD = 0;
    }

    public void thisweekdatemethoddd(View view) {
        dff = new SimpleDateFormat("dd-MM-yyyy");
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        // Get calendar set to current date and time
        c = Calendar.getInstance();

// Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

// Print dates of the current week starting on Monday

        String startDate, endDate;
        startDate = df.format(c.getTime());
        fromdatetexttv.setVisibility(View.VISIBLE);
        fromdatetexttv.setText(dff.format(c.getTime()));
        c.add(Calendar.DATE, 6);
        endDate = df.format(c.getTime());
        todatetexttv.setVisibility(View.VISIBLE);
        todatetexttv.setText(dff.format(c.getTime()));

        totextet_to.setVisibility(View.VISIBLE);
        getcalenderwisedata(startDate, endDate);
        dialog.hide();
        SERIESIDD = 0;
    }

    public void thismonthmethoddd(View view) {
        dff = new SimpleDateFormat("dd-MM-yyyy");
        df = new SimpleDateFormat("yyyy/MM/dd");
        c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        String startDate, endDate;
        fromdatetexttv.setVisibility(View.VISIBLE);
        fromdatetexttv.setText(dff.format(c.getTime()));
        startDate = df.format(c.getTime());
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DATE, -1);
        todatetexttv.setVisibility(View.VISIBLE);
        todatetexttv.setText(dff.format(c.getTime()));
        endDate = df.format(c.getTime());
        totextet_to.setVisibility(View.VISIBLE);
        getcalenderwisedata(startDate, endDate);
        dialog.hide();
        SERIESIDD = 0;

    }

    public void lastmonthmethoddd(View view) {
        dff = new SimpleDateFormat("dd-MM-yyyy");
        String startDate, endDate;
        df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDateOfPreviousMonth = cal.getTime();
        endDate = df.format(lastDateOfPreviousMonth);
        todatetexttv.setVisibility(View.VISIBLE);
        todatetexttv.setText(dff.format(lastDateOfPreviousMonth));
        cal.set(Calendar.DATE, 1);
        fromdatetexttv.setVisibility(View.VISIBLE);
        Date firstDateOfPreviousMonth = cal.getTime();
        fromdatetexttv.setText(dff.format(firstDateOfPreviousMonth));
        startDate = df.format(firstDateOfPreviousMonth);
        totextet_to.setVisibility(View.VISIBLE);
        getcalenderwisedata(startDate, endDate);
        dialog.hide();
        SERIESIDD = 0;
    }
//    public void thisquartermethoddd(View view) {
//        Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
//        dialog.hide();
//    }

    public void thisyearmethoddd(View view) {
        fromdatetexttv.setVisibility(View.VISIBLE);
        fromdatetexttv.setText(fmdt);

        todatetexttv.setVisibility(View.VISIBLE);
        todatetexttv.setText(todt);
        totextet_to.setVisibility(View.VISIBLE);
        con = connectionClass.CONN();
        if (con == null) {
            Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show();
        } else {
            String strinfrom = fromdatetexttv.getText().toString();
            String strintom = todatetexttv.getText().toString();
            String s = strinfrom;
            String[] parts = s.split("-"); // escape .
            String datee = parts[0];
            String month = parts[1];
            String year = parts[2];
            String dhhftdt = year + "/" + month + "/" + datee;

            String s1 = strintom;
            String[] parts1 = s1.split("-"); // escape .
            String datee1 = parts1[0];
            String month1 = parts1[1];
            String year1 = parts1[2];
            String dhhtdt = year1 + "/" + month1 + "/" + datee1;

            getcalenderwisedata(dhhftdt, dhhtdt);
        }
        dialog.hide();
        SERIESIDD = 0;
    }


    public void customdatemethoddd(View view) {
        dialogcustomdate = new Dialog(MainActivity.this);
        dialogcustomdate.setContentView(R.layout.calenderfor_from_datetodate);

        LinearLayout tolineardate = dialogcustomdate.findViewById(R.id.btnTodateclick);
        LinearLayout fromlineardate = dialogcustomdate.findViewById(R.id.btnFromdatreclick);
        final EditText fromdatemain = dialogcustomdate.findViewById(R.id.fromdate_main);
        final EditText todatemain = dialogcustomdate.findViewById(R.id.mainTodate);
        Button btn_from_to_date_main = dialogcustomdate.findViewById(R.id.btn_from_to_date_main);

        fromlineardate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                fromdatetexttv.setVisibility(View.VISIBLE);
                                totextet_to.setVisibility(View.VISIBLE);
                                todatetexttv.setText("");
                                fromdatetexttv.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fromdatemain.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                String monthString = String.valueOf(monthOfYear + 1);
                                if (monthString.length() == 1) {
                                    monthString = "0" + monthString;
                                }
                                String dayString = String.valueOf(dayOfMonth);
                                if (dayString.length() == 1) {
                                    dayString = "0" + dayString;
                                }
                                fromdate_c_string = year + "/" + monthString + "/" + dayString;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                SERIESIDD = 0;
            }
        });


        tolineardate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                df = new SimpleDateFormat("yyyy/MM/dd");
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                todatetexttv.setVisibility(View.VISIBLE);
                                todatemain.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                todatetexttv.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                String monthString = String.valueOf(monthOfYear + 1);
                                if (monthString.length() == 1) {
                                    monthString = "0" + monthString;
                                }
                                String dayString = String.valueOf(dayOfMonth);
                                if (dayString.length() == 1) {
                                    dayString = "0" + dayString;
                                }
                                todate_c_string = year + "/" + monthString + "/" + dayString;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                SERIESIDD = 0;
            }
        });

        btn_from_to_date_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getcalenderwisedata(fromdate_c_string, todate_c_string);
                dialogcustomdate.hide();
                SERIESIDD = 0;
            }
        });


        dialog.hide();
        dialogcustomdate.show();
    }

    //    call get data calenderlist
    private void getcalenderwisedata(String fdate, String tdate) {

        if (fdate != "" && tdate != "") {
            quuerycalender = "SELECT (SELECT SUM(TOTALBILLAMT) FROM SALHEAD_VIEW WHERE VCHDT_Y_M_D BETWEEN '" + fdate + "' AND '" + tdate + "') AS SALETOTAL, (SELECT SUM(TOTALBILLAMT) FROM PURHEAD_VIEW WHERE VCHDT_Y_M_D BETWEEN '" + fdate + "' AND '" + tdate + "') AS PURTOTAL ,(SELECT SUM(BALAMT) FROM VCHDET_VIEW WHERE GRP_INFO='SUND_DR' AND VCHDT_Y_M_D BETWEEN '" + fdate + "' AND '" + tdate + "') AS CBALAMT,(SELECT -SUM(BALAMT) FROM VCHDET_VIEW WHERE GRP_INFO='SUND_CR' AND VCHDT_Y_M_D BETWEEN '" + fdate + "' AND '" + tdate + "') AS SBALAMT";

        } else {
            quuerycalender = "SELECT (SELECT SUM(TOTALBILLAMT) FROM SALHEAD_VIEW WHERE VCHDT_Y_M_D ='" + fdate + "' ) AS SALETOTAL, (SELECT SUM(TOTALBILLAMT) FROM PURHEAD_VIEW WHERE VCHDT_Y_M_D= '" + fdate + "') AS PURTOTAL, (SELECT SUM(BALAMT) FROM VCHDET_VIEW WHERE GRP_INFO='SUND_DR' AND VCHDT_Y_M_D = '" + fdate + "') AS CBALAMT,(SELECT -SUM(BALAMT) FROM VCHDET_VIEW WHERE GRP_INFO='SUND_CR' AND VCHDT_Y_M_D = '" + fdate + "') AS SBALAMT ";

        }


        try {
            con = connectionClass.CONN();
            Statement sstmt = con.createStatement();
            ResultSet rss = sstmt.executeQuery(quuerycalender);
            if (rss.next()) {
                String saletotal = String.valueOf(rss.getBigDecimal("SALETOTAL"));
                String pertotal = String.valueOf(rss.getBigDecimal("PURTOTAL"));
                String SBALAMT = String.valueOf(rss.getBigDecimal("SBALAMT"));
                String CBALAMT = String.valueOf(rss.getBigDecimal("CBALAMT"));

                if (saletotal.equals("null")) {
                    findViewById(R.id.ruppees_symoble_sale).setVisibility(View.GONE);
                    saletotal_textview.setText("No Data Available");
                } else {
                    findViewById(R.id.ruppees_symoble_sale).setVisibility(View.VISIBLE);
                    saletotal_textview.setText(saletotal);
                }
                if (pertotal.equals("null")) {
                    findViewById(R.id.ruppees_symoble_perchase).setVisibility(View.GONE);
                    purchase_total_textview.setText("No Data Available");
                } else {
                    findViewById(R.id.ruppees_symoble_perchase).setVisibility(View.VISIBLE);
                    purchase_total_textview.setText(pertotal);
                }
                if (SBALAMT.equals("null")) {
                    findViewById(R.id.ruppees_symoble_sale).setVisibility(View.GONE);
                    supplierledgertv.setText("No Data Available");
                } else {
                    findViewById(R.id.ruppees_symoble_sale).setVisibility(View.VISIBLE);
                    supplierledgertv.setText(SBALAMT);
                }
                if (CBALAMT.equals("null")) {
                    findViewById(R.id.ruppees_symoble_sale).setVisibility(View.GONE);
                    customerledgertv.setText("No Data Available");
                } else {
                    findViewById(R.id.ruppees_symoble_sale).setVisibility(View.VISIBLE);
                    customerledgertv.setText(CBALAMT);
                }

                // Toast.makeText(this, ""+saletotal+","+pertotal, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Exceptions", Toast.LENGTH_SHORT).show();
        }
    }


    //end calender dialoglist#################################################3
//    check mac statuss start***************************
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void checkConnection() {
        if (isOnline()) {

            macMethod();

        } else {

            macStatus();
        }
    }


    private void macMethod() {

        String url = "http://registermykenan.com/api.php?key=" + stringlicense;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.toString().split("\\{|\\}")[1];
                    String temst = status.toString().replaceAll(":", ",").replaceAll("\"", "");
                    String[] namesList = temst.split(",");
                    ArrayList<String> sss = new ArrayList<>();
                    for (int i = 0; i < namesList.length; i++) {
                        sss.add(namesList[i]);

                    }
                    if (sss.contains(stringmac)) {
                        satus = "true";
                        macStatusupdate();
                    } else {
                        satus = "false";
                        macStatusupdate();
                        startActivity(new Intent(MainActivity.this, LincenseActivity.class));
                        finish();
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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }

    private void macStatusupdate() {

        db.execSQL("UPDATE MAC  SET status ='" + satus + "' WHERE license ='" + stringlicense + "'");


    }

    private void macStatus() {
        Cursor c1 = db.rawQuery("SELECT * FROM MAC ", null);
        if (c1.moveToFirst()) {
            do {
                status_check = c1.getString(c1.getColumnIndex("status"));
            } while (c1.moveToNext());

            macMethod();
        } else {
            //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
        }
        c1.close();
        if (status_check.equals("false")) {
            startActivity(new Intent(MainActivity.this, LincenseActivity.class));
            finish();
        } else {

        }

    }

//    end mac

    private void sevendaystrial() {
        Cursor c1 = db.rawQuery("SELECT * FROM SEVEN_DAYS ", null);
        if (c1.moveToFirst()) {
            do {

                stratdate = c1.getString(c1.getColumnIndex("start_date"));


            } while (c1.moveToNext());
        }

        c1.close();

        String str = stratdate;
        String[] temp;
        String delimiter = " ";
        temp = str.split(delimiter);
        int year = Integer.parseInt(temp[0]);
        int month = Integer.parseInt(temp[1]);
        int day = Integer.parseInt(temp[2]);
        //tstri_date.setText(name +","+surname +","+lastName1);

        long date = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy MM dd");
        String end_date = df.format(date);
        String strend = end_date;
        String[] tempend;
        String delimiterend = " ";
        tempend = strend.split(delimiterend);
        int yearend = Integer.parseInt(tempend[0]);
        int monthend = Integer.parseInt(tempend[1]);
        int dayend = Integer.parseInt(tempend[2]);
        Calendar startDate = Calendar.getInstance();
        startDate.set(year, month, day);
        long startDateMillis = startDate.getTimeInMillis();

        Calendar endDate = Calendar.getInstance();
        endDate.set(yearend, monthend, dayend);
        long endDateMillis = endDate.getTimeInMillis();

        long differenceMillis = endDateMillis - startDateMillis;
        int daysDifference = (int) (differenceMillis / (1000 * 60 * 60 * 24));
        // eee.setText(Integer.toString(daysDifference));
        if (daysDifference >= 5) {
            startActivity(new Intent(getApplicationContext(), LincenseActivity.class));
            // db.execSQL("DROP TABLE IF EXISTS SEVEN_DAYS");
            sessionManager.logoutUser();
            finish();
        }

    }


}
