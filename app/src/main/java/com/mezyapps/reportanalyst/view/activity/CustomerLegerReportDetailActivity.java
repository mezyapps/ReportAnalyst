package com.mezyapps.reportanalyst.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mezyapps.reportanalyst.view.adapter.CustomSpinnerAdapter;
import com.mezyapps.reportanalyst.R;
import com.mezyapps.reportanalyst.network_class.ConnectionClass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerLegerReportDetailActivity extends AppCompatActivity {

    private ConnectionClass connectionClass;
    private GridView gridview;
    private List<Map<String, String>> data = null;
    private SimpleAdapter ADA;
    private ImageView search_closebutton;
    private TextView to_date_tv,from_date_tv,to_text_tv,showTotalamt_tv,customenaeme;
    private int groupid;
    private String hhtdt,hhfdt,gquery;
    private String cname;
    private Spinner spinner_nav;
    private InputMethodManager imm;
    private SearchView searchView_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_leger_report_detail);

        find_View_IdS();
        events();

    }

    private void find_View_IdS() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        connectionClass = new ConnectionClass(getApplicationContext());
        gridview = findViewById(R.id.gridviewsalehyeadsub);
        search_closebutton=findViewById(R.id.search_closebutton);
        searchView_customer = findViewById(R.id.searchautocompleteforcustomer);
        to_date_tv=findViewById(R.id.to_date_textview);
        to_text_tv=findViewById(R.id.to_textview);
        from_date_tv=findViewById(R.id.from_date_textview);
        groupid = Integer.parseInt(getIntent().getStringExtra("gid"));
        cname=getIntent().getStringExtra("cname");
        hhfdt=getIntent().getStringExtra("hfdt").trim();
        hhtdt=getIntent().getStringExtra("htdt").trim();
        customenaeme=findViewById(R.id.customenaeme);
        customenaeme.setText(cname);
        showTotalamt_tv=findViewById(R.id.show_total_amt_head);
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    private void events() {
        //start searchview *****************************

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

                if (newText.toString().length() == 0){
                    search_closebutton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
//end search view##############################################3
        addItemsToSpinner();

    }

    //start spinner
    public void addItemsToSpinner() {

        ArrayList<String> list = new ArrayList<String>();
        list.add("Statementwise");
        list.add("Billwise");


        // Custom ArrayAdapter with spinner item layout to set popup background

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);



        spinner_nav.setAdapter(spinAdapter);

        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int position, long id) {

                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();

                // Showing selected spinner item

                if (item.equals("Statementwise")) {
                    ggeatdataforgrid();

                }else {
                    billwisesumary();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void billwisesumary() {
        if (hhtdt.length() != 0 && hhfdt.length() != 0)
        {
            to_date_tv.setText(hhtdt);
            from_date_tv.setText(hhfdt);
            String s1=hhtdt;
            String[] parts1 = s1.split("-"); // escape .
            String datee1 = parts1[0];
            String month1 = parts1[1];
            String year1 = parts1[2];
            String dhhtdt=year1+"/"+month1+"/"+datee1;

            String s=hhfdt;
            String[] parts = s.split("-"); // escape .
            String datee  = parts[0];
            String month = parts[1];
            String year = parts[2];
            String dhhftdt=year+"/"+month+"/"+datee;

            gquery = "SELECT ENTRYID, VCHDT,VCHNO,GROUPNAME ,BALAMT AS AMT , (select SUM(BALAMT) from BILLADJDET_VIEW WHERE  GRP_INFO='SUND_DR' AND GROUPID="+groupid+" AND VCHDT_Y_M_D BETWEEN '"+dhhftdt+"' AND '"+dhhtdt+"') AS tAMT from  BILLADJDET_VIEW WHERE  GRP_INFO='SUND_DR' AND GROUPID="+groupid +"AND VCHDT_Y_M_D BETWEEN '"+dhhftdt+"' AND '"+dhhtdt+"'" ;

        } else {
            from_date_tv.setText(hhfdt);
            to_text_tv.setVisibility(View.GONE);
            String s=hhfdt;
            String[] parts = s.split("-"); // escape .
            String   datee= parts[0];
            String month = parts[1];
            String year = parts[2];
            String dhhftdt=year+"/"+month+"/"+datee;

            gquery = "SELECT ENTRYID, VCHDT,VCHNO,GROUPNAME ,BALAMT AS AMT , (select SUM(BALAMT) from BILLADJDET_VIEW WHERE   GRP_INFO='SUND_DR' AND GROUPID="+groupid+" AND VCHDT_Y_M_D='"+dhhftdt+"') AS tAMT from BILLADJDET_VIEW WHERE  GRP_INFO='SUND_DR' AND GROUPID="+groupid+" AND VCHDT_Y_M_D='"+dhhftdt+"'" ;

        }

        try {

            Connection con = connectionClass.CONN();

            if (con == null) {
                Toast.makeText(this, "connection problem", Toast.LENGTH_SHORT).show();
            } else {

//                String query = "SELECT GP.GROUPNAME ,SUM(SH.TOTALGROSSAMT) AS AMT FROM GROUPS_PER AS GP INNER JOIN SALHEAD AS SH ON SH.GROUPID =GP.GROUPID \n" +
//                        "WHERE SH.SERIESID="+serid+"  GROUP BY GP.GROUPNAME";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(gquery);


                data = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> datanum = new HashMap<String, String>();
                    // if (rs.getDouble("AMT") > 0){
                    datanum.put("A", rs.getString("VCHNO"));
                    datanum.put("B", String.valueOf(rs.getString("VCHDT")).replaceAll("/", "-"));
                    //  datanum.put("C", String.valueOf(rs.getDouble("AMT"))+" Dr.");
                    if (rs.getDouble("AMT") >= 0) {
                        datanum.put("C", String.valueOf(rs.getDouble("AMT")) + " Dr.");
                    } else {
                        datanum.put("C", String.valueOf(-rs.getDouble("AMT")) + " Cr.");
                    }
                    // }
                    datanum.put("D", rs.getString("GROUPNAME"));
                    datanum.put("F", String.valueOf(rs.getInt("ENTRYID")));

                    showTotalamt_tv.setText(String.valueOf(rs.getBigDecimal("tAMT")));
                    data.add(datanum);
                }
                String[] from = {"A", "B","C"};
                final int[] views = {R.id.sale_bill, R.id.sale_date, R.id.sale_amt};
                ADA = new SimpleAdapter(CustomerLegerReportDetailActivity.this,
                        data, R.layout.cutomerledgerreportdetail_gridview, from, views);
                gridview.setAdapter(ADA);


//                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int  position, long id) {
//                        HashMap<String, String> map = (HashMap<String, String>)parent.getItemAtPosition(position);
//                        String ENTRYID = map.get("F");
//                        String netamt = map.get("C");
//                        String bill_no = map.get("A");
//                        String bill_date=map.get("B");
//                        Intent intent=new Intent(new Intent(CustomerLegerReportDetailActivity.this,SaleReportBillDeatilActivity.class));
//                        intent.putExtra("ENTRYID",ENTRYID);
//                        intent.putExtra("netamt",netamt);
//                        intent.putExtra("cname",cname);
//                        intent.putExtra("bd",bill_date);
//                        intent.putExtra("bn",bill_no);
//
//                        startActivity(intent);
//
//
//                    }
//                });



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //        end spinner
    private void ggeatdataforgrid() {
        //start customer and amt grid**************************************

        if (hhtdt.length() != 0 && hhfdt.length() != 0)
        {
            to_date_tv.setText(hhtdt);
            from_date_tv.setText(hhfdt);
            String s1=hhtdt;
            String[] parts1 = s1.split("-"); // escape .
            String datee1 = parts1[0];
            String month1 = parts1[1];
            String year1 = parts1[2];
            String dhhtdt=year1+"/"+month1+"/"+datee1;

            String s=hhfdt;
            String[] parts = s.split("-"); // escape .
            String datee  = parts[0];
            String month = parts[1];
            String year = parts[2];
            String dhhftdt=year+"/"+month+"/"+datee;

            gquery = "SELECT ENTRYID, VCHDT,VCHNO,GROUPNAME ,BALAMT AS AMT , (select SUM(BALAMT) from VCHDET_VIEW WHERE  GRP_INFO='SUND_DR' AND GROUPID="+groupid+" AND VCHDT_Y_M_D BETWEEN '"+dhhftdt+"' AND '"+dhhtdt+"') AS tAMT from  VCHDET_VIEW WHERE  GRP_INFO='SUND_DR' AND GROUPID="+groupid +"AND VCHDT_Y_M_D BETWEEN '"+dhhftdt+"' AND '"+dhhtdt+"'" ;

        } else {
            from_date_tv.setText(hhfdt);
            to_text_tv.setVisibility(View.GONE);
            String s=hhfdt;
            String[] parts = s.split("-"); // escape .
            String   datee= parts[0];
            String month = parts[1];
            String year = parts[2];
            String dhhftdt=year+"/"+month+"/"+datee;

            gquery = "SELECT ENTRYID, VCHDT,VCHNO,GROUPNAME ,BALAMT AS AMT , (select SUM(BALAMT) from VCHDET_VIEW WHERE   GRP_INFO='SUND_DR' AND GROUPID="+groupid+" AND VCHDT_Y_M_D='"+dhhftdt+"') AS tAMT from VCHDET_VIEW WHERE  GRP_INFO='SUND_DR' AND GROUPID="+groupid+" AND VCHDT_Y_M_D='"+dhhftdt+"'" ;

        }

        try {

            Connection con = connectionClass.CONN();

            if (con == null) {
                Toast.makeText(this, "connection problem", Toast.LENGTH_SHORT).show();
            } else {

//                String query = "SELECT GP.GROUPNAME ,SUM(SH.TOTALGROSSAMT) AS AMT FROM GROUPS_PER AS GP INNER JOIN SALHEAD AS SH ON SH.GROUPID =GP.GROUPID \n" +
//                        "WHERE SH.SERIESID="+serid+"  GROUP BY GP.GROUPNAME";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(gquery);


                data = new ArrayList<Map<String, String>>();

                while (rs.next()) {
                    Map<String, String> datanum = new HashMap<String, String>();

                    datanum.put("A", rs.getString("VCHNO"));
                    datanum.put("B", String.valueOf(rs.getString("VCHDT")).replaceAll("/","-"));
                    if (rs.getDouble("AMT") >= 0){
                        datanum.put("C", String.valueOf(rs.getDouble("AMT"))+" Dr.");
                    }else {
                        datanum.put("C", String.valueOf(-rs.getDouble("AMT"))+" Cr.");
                    }
                    datanum.put("D", rs.getString("GROUPNAME"));
                    datanum.put("F",String.valueOf(rs.getInt("ENTRYID")));

                    showTotalamt_tv.setText(String.valueOf(rs.getBigDecimal("tAMT")));
                    data.add(datanum);
                }
                String[] from = {"A", "B","C"};
                final int[] views = {R.id.sale_bill, R.id.sale_date, R.id.sale_amt};
                ADA = new SimpleAdapter(CustomerLegerReportDetailActivity.this,
                        data, R.layout.cutomerledgerreportdetail_gridview, from, views);
                gridview.setAdapter(ADA);


//                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int  position, long id) {
//                        HashMap<String, String> map = (HashMap<String, String>)parent.getItemAtPosition(position);
//                        String ENTRYID = map.get("F");
//                        String netamt = map.get("C");
//                        String bill_no = map.get("A");
//                        String bill_date=map.get("B");
//                        Intent intent=new Intent(new Intent(CustomerLegerReportDetailActivity.this,SaleReportBillDeatilActivity.class));
//                        intent.putExtra("ENTRYID",ENTRYID);
//                        intent.putExtra("netamt",netamt);
//                        intent.putExtra("cname",cname);
//                        intent.putExtra("bd",bill_date);
//                        intent.putExtra("bn",bill_no);
//
//                        startActivity(intent);
//
//
//                    }
//                });



            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//end customer and grid amt view######################################
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    //search butoon**************************
    public void searchBtuon(View view) {
        LinearLayout toolbarcontainer=findViewById(R.id.tollbarcontainer);
        LinearLayout searchconteainer=findViewById(R.id.search_show_layout);
        toolbarcontainer.setVisibility(View.GONE);
        searchconteainer.setVisibility(View.VISIBLE);

    }

    public void searchlosebutton(View view) {
        LinearLayout toolbarcontainer=findViewById(R.id.tollbarcontainer);
        LinearLayout searchconteainer=findViewById(R.id.search_show_layout);
        toolbarcontainer.setVisibility(View.VISIBLE);
        searchconteainer.setVisibility(View.GONE);
    }
    //end search button ###############################
    public void CalenderListbutoon(View view) {

//  start  calender button task **********************************************

        final Dialog dialog = new Dialog(CustomerLegerReportDetailActivity.this);
        dialog.setContentView(R.layout.calender_list_for_sale);

        dialog.show();

//       end calender button task *************************************************

    }


}
