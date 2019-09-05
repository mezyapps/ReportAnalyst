package com.mezyapps.reportanalyst.view.activity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.network_class.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PurchaseReportActivity extends AppCompatActivity {

    private Dialog dialog, dialogcustomdate;
    private ConnectionClass connectionClass;
    private SimpleDateFormat df, dff;
    private GridView gridview;
    private List<Map<String, String>> data = null;
    private Connection con;
    private SimpleAdapter ADA;
    private ImageView search_closebutton;
    private TextView todatetexttv, fromdatetexttv, totextet_to, showTotalamt_tv;
    private int serid;
    private DatePickerDialog datePickerDialog;
    private String todateseries, fromdtseries, htdt, hfdt, gquery, fromdate_c_string, todate_c_string, quuerycalender;
    private Toolbar toolbar;
    private SearchView searchView_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_report);

        find_View_IdS();
        events();
    }

    private void find_View_IdS() {
        toolbar = findViewById(R.id.toolbar);
        connectionClass = new ConnectionClass(getApplicationContext());
        gridview = findViewById(R.id.gridviewsalehyead);
        search_closebutton = findViewById(R.id.search_closebutton);
        searchView_customer = findViewById(R.id.searchautocompleteforcustomer);
        todatetexttv = findViewById(R.id.to_date_textview);
        totextet_to = findViewById(R.id.to_textview);
        fromdatetexttv = findViewById(R.id.from_date_textview);
        serid = Integer.parseInt(getIntent().getStringExtra("serid"));
        todateseries = getIntent().getStringExtra("todt");
        fromdtseries = getIntent().getStringExtra("fdt");
        hfdt = getIntent().getStringExtra("hfdt");
        htdt = getIntent().getStringExtra("htdt");
        showTotalamt_tv = findViewById(R.id.show_total_amt_head);
        // Toast.makeText(this, ""+hfdt+htdt, Toast.LENGTH_SHORT).show();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ggeatdataforgrid();

    }

    private void events() {
        searchView_customer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_closebutton.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                search_closebutton.setVisibility(View.GONE);
                ADA.getFilter().filter(newText);

                if (newText.toString().length() == 0) {
                    search_closebutton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    private void ggeatdataforgrid() {

        if (serid != 0) {
            gquery = "SELECT GROUPID, GROUPNAME ,SUM(TOTALBILLAMT) AS AMT , (select SUM(TOTALBILLAMT) from PURHEAD_VIEW WHERE SERIESID=" + serid + ") AS tAMT from PURHEAD_VIEW WHERE SERIESID=" + serid + " GROUP BY GROUPID, GROUPNAME ORDER BY GROUPNAME";
            todatetexttv.setText(todateseries);
            fromdatetexttv.setText(fromdtseries);

        } else {
            if (htdt.length() != 0 && hfdt.length() != 0) {
                fromdatetexttv.setText(hfdt);
                todatetexttv.setText(htdt);
                String s1 = htdt;
                String[] parts1 = s1.split("-"); // escape .
                String datee1 = parts1[0];
                String month1 = parts1[1];
                String year1 = parts1[2];
                String dhhtdt = year1 + "/" + month1 + "/" + datee1;

                String s = hfdt;
                String[] parts = s.split("-"); // escape .
                String datee = parts[0];
                String month = parts[1];
                String year = parts[2];
                String dhhftdt = year + "/" + month + "/" + datee;
                // Toast.makeText(this, ""+dhhftdt+dhhtdt, Toast.LENGTH_SHORT).show();

                gquery = "SELECT GROUPID, GROUPNAME ,SUM(TOTALBILLAMT) AS AMT , (select SUM(TOTALBILLAMT) from PURHEAD_VIEW WHERE VCHDT_Y_M_D BETWEEN '" + dhhftdt + "' AND '" + dhhtdt + "') AS tAMT from PURHEAD_VIEW WHERE VCHDT_Y_M_D BETWEEN '" + dhhftdt + "' AND '" + dhhtdt + "' GROUP BY GROUPID, GROUPNAME ORDER BY GROUPNAME ";
                // gquery="SELECT GP.GROUPID, GP.GROUPNAME ,SUM(SH.TOTALBILLAMT) AS AMT ,(select SUM(TOTALBILLAMT) from SALHEAD_VIEW WHERE VCHDT_Y_M_D BETWEEN '"+hfdt+"' AND '"+htdt+"') AS tAMT FROM GROUPS_PER AS GP INNER JOIN SALHEAD_VIEW AS SH ON SH.GROUPID =GP.GROUPID WHERE SH.VCHDT_Y_M_D BETWEEN '"+hfdt+"' AND '"+htdt+"'  GROUP BY GP.GROUPNAME";

            } else {
                fromdatetexttv.setText(hfdt);
                totextet_to.setText("");
                todatetexttv.setVisibility(View.GONE);
                totextet_to.setVisibility(View.GONE);
                String s = hfdt;
                String[] parts = s.split("-"); // escape .
                String datee = parts[0];
                String month = parts[1];
                String year = parts[2];
                String dhhftdt = year + "/" + month + "/" + datee;
                gquery = "SELECT GROUPID, GROUPNAME ,SUM(TOTALBILLAMT) AS AMT , (select SUM(TOTALBILLAMT) from PURHEAD_VIEW WHERE VCHDT_Y_M_D ='" + dhhftdt + "') AS tAMT from PURHEAD_VIEW WHERE VCHDT_Y_M_D ='" + dhhftdt + "' GROUP BY GROUPID, GROUPNAME ORDER BY GROUPNAME";
                // Toast.makeText(this, ""+dhhftdt, Toast.LENGTH_SHORT).show();

            }
        }

        try {

            con = connectionClass.CONN();

            if (con == null) {
                Toast.makeText(this, "connection problem", Toast.LENGTH_SHORT).show();
            } else {

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(gquery);


                data = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> datanum = new HashMap<String, String>();
                    datanum.put("A", rs.getString("GROUPNAME"));
                    datanum.put("B", String.valueOf(rs.getBigDecimal("AMT")));
                    datanum.put("C", String.valueOf(rs.getInt("GROUPID")));
                    showTotalamt_tv.setText(String.valueOf(rs.getBigDecimal("tAMT")));
                    data.add(datanum);
                }
                String[] from = {"A", "B"};
                final int[] views = {R.id.customername, R.id.saleamtcustmerwise};
                ADA = new SimpleAdapter(PurchaseReportActivity.this,
                        data, R.layout.sale_grid_layout, from, views);
                gridview.setAdapter(ADA);


                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                        String name = map.get("A");
                        String amt = map.get("B");
                        String GID = map.get("C");
                        Intent intent = new Intent(new Intent(PurchaseReportActivity.this, PurchaseReportDetailActivity.class));
                        intent.putExtra("cname", name);
                        intent.putExtra("hfdt", fromdatetexttv.getText().toString().trim());
                        intent.putExtra("htdt", todatetexttv.getText().toString().trim());
                        intent.putExtra("gid", GID);
                        // Toast.makeText(SaleReportActivity.this, ""+GID, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public void searchBtuon(View view) {
        LinearLayout toolbarcontainer = findViewById(R.id.tollbarcontainer);
        LinearLayout searchconteainer = findViewById(R.id.search_show_layout);
        toolbarcontainer.setVisibility(View.GONE);
        searchconteainer.setVisibility(View.VISIBLE);

    }

    public void searchlosebutton(View view) {
        LinearLayout toolbarcontainer = findViewById(R.id.tollbarcontainer);
        LinearLayout searchconteainer = findViewById(R.id.search_show_layout);
        toolbarcontainer.setVisibility(View.VISIBLE);
        searchconteainer.setVisibility(View.GONE);
    }

    public void CalenderListbutoon(View view) {
        dialog = new Dialog(PurchaseReportActivity.this);
        dialog.setContentView(R.layout.calender_list_for_sale);
        dialog.show();

    }
    public void todaydatemethod(View view) {
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

    }

    public void yesterdaydatemethd(View view) {
        dff = new SimpleDateFormat("dd-MM-yyyy");
        df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        fromdatetexttv.setVisibility(View.VISIBLE);
        fromdatetexttv.setText(dff.format(c.getTime()));
        String yseterdy = df.format(c.getTime());
        todatetexttv.setVisibility(View.GONE);
        todatetexttv.setText("");
        totextet_to.setVisibility(View.GONE);
        getcalenderwisedata(yseterdy, "");
        dialog.hide();

    }

    public void thisweekdatemethod(View view) {
        dff = new SimpleDateFormat("dd-MM-yyyy");
        df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        // Get calendar set to current date and time
        Calendar c = Calendar.getInstance();

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

    }

    public void thismonthmethod(View view) {
        dff = new SimpleDateFormat("dd-MM-yyyy");
        df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();   // this takes current date
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


    }

    public void lastmonthmethod(View view) {
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

    }


    public void customdatemethod(View view) {
        dialogcustomdate = new Dialog(PurchaseReportActivity.this);
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
                datePickerDialog = new DatePickerDialog(PurchaseReportActivity.this,
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
                datePickerDialog = new DatePickerDialog(PurchaseReportActivity.this,
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

            }
        });

        btn_from_to_date_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getcalenderwisedata(fromdate_c_string, todate_c_string);
                dialogcustomdate.hide();

            }
        });


        dialog.hide();
        dialogcustomdate.show();
    }

    //    call get data calenderlist
    private void getcalenderwisedata(String fdate, String tdate) {
        gridview = findViewById(R.id.gridviewsalehyead);
        if (fdate != "" && tdate != "") {

            quuerycalender = "SELECT GROUPID, GROUPNAME ,SUM(TOTALBILLAMT) AS AMT , (select SUM(TOTALBILLAMT) from PURHEAD_VIEW WHERE VCHDT_Y_M_D BETWEEN '" + fdate + "' AND '" + tdate + "') AS tAMT from PURHEAD_VIEW WHERE VCHDT_Y_M_D BETWEEN '" + fdate + "' AND '" + tdate + "' GROUP BY GROUPID, GROUPNAME ORDER BY GROUPNAME ";

        } else {

            quuerycalender = "SELECT GROUPID, GROUPNAME ,SUM(TOTALBILLAMT) AS AMT , (select SUM(TOTALBILLAMT) from PURHEAD_VIEW WHERE VCHDT_Y_M_D ='" + fdate + "') AS tAMT from PURHEAD_VIEW WHERE VCHDT_Y_M_D ='" + fdate + "' GROUP BY GROUPID, GROUPNAME ORDER BY GROUPNAME";

        }

        try {

            con = connectionClass.CONN();

            if (con == null) {
                Toast.makeText(this, "connection problem", Toast.LENGTH_SHORT).show();
            } else {

                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(quuerycalender);


                data = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> datanum = new HashMap<String, String>();
                    datanum.put("A", rs.getString("GROUPNAME"));
                    datanum.put("B", String.valueOf(rs.getBigDecimal("AMT")));
                    datanum.put("C", String.valueOf(rs.getInt("GROUPID")));
                    showTotalamt_tv.setText(String.valueOf(rs.getBigDecimal("tAMT")));
                    data.add(datanum);
                }
                String[] from = {"A", "B"};
                final int[] views = {R.id.customername, R.id.saleamtcustmerwise};
                ADA = new SimpleAdapter(PurchaseReportActivity.this,
                        data, R.layout.purchase_grid_view_layout, from, views);
                gridview.setAdapter(ADA);


                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
                        String name = map.get("A");
                        String amt = map.get("B");
                        String GID = map.get("C");
                        Intent intent = new Intent(new Intent(PurchaseReportActivity.this, PurchaseReportDetailActivity.class));
                        intent.putExtra("cname", name);
                        intent.putExtra("hfdt", fromdatetexttv.getText().toString().trim());
                        intent.putExtra("htdt", todatetexttv.getText().toString().trim());
                        intent.putExtra("gid", GID);
                        // Toast.makeText(SaleReportActivity.this, ""+GID, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
