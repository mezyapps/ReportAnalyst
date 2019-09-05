package com.mezyapps.reportanalyst.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Map;

public class PurchaseReportDetailActivity extends AppCompatActivity {

    private ConnectionClass connectionClass;
    private GridView gridview;
    private List<Map<String, String>> data = null;
    private SimpleAdapter ADA;
    private ImageView search_closebutton;
    private TextView to_date_tv,from_date_tv,to_text_tv,showTotalamt_tv,customenaeme;
    private int groupid;
    private String hhtdt,hhfdt,gquery;
    private String cname;
    private Toolbar toolbar;
    private SearchView searchView_customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_report_detail);


        find_View_IdS();
        events();
    }

    private void find_View_IdS() {
        toolbar = findViewById(R.id.toolbar);
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

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    private void events() {
        ggeatdataforgrid();

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

    }

    private void ggeatdataforgrid() {

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
           // Toast.makeText(this, ""+dhhftdt+dhhtdt+","+groupid, Toast.LENGTH_SHORT).show();
            gquery = "SELECT ENTRYID, VCHDT,VCHNO,GROUPNAME,TOTALQTY ,TOTALBILLAMT AS AMT , (select SUM(TOTALBILLAMT) from PURHEAD_VIEW WHERE GROUPID="+groupid+" AND VCHDT_Y_M_D BETWEEN '"+dhhftdt+"' AND '"+dhhtdt+"') AS tAMT from PURHEAD_VIEW WHERE GROUPID="+groupid +" AND VCHDT_Y_M_D BETWEEN '"+dhhftdt+"' AND '"+dhhtdt+"'" ;

        } else {
            from_date_tv.setText(hhfdt);
            to_text_tv.setVisibility(View.GONE);
            String s=hhfdt;
            String[] parts = s.split("-"); // escape .
            String   datee= parts[0];
            String month = parts[1];
            String year = parts[2];
            String dhhftdt=year+"/"+month+"/"+datee;

            gquery = "SELECT ENTRYID, VCHDT,VCHNO,GROUPNAME,TOTALQTY ,TOTALBILLAMT AS AMT , (select SUM(TOTALBILLAMT) from PURHEAD_VIEW WHERE GROUPID="+groupid+" AND VCHDT_Y_M_D='"+dhhftdt+"') AS tAMT from PURHEAD_VIEW WHERE GROUPID="+groupid+" AND VCHDT_Y_M_D='"+dhhftdt+"'" ;

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
                    datanum.put("C", String.valueOf(rs.getBigDecimal("AMT")));
                    datanum.put("E", rs.getString("TOTALQTY")+" Qty");
                    datanum.put("D", rs.getString("GROUPNAME"));
                    datanum.put("F",String.valueOf(rs.getInt("ENTRYID")));

                    showTotalamt_tv.setText(String.valueOf(rs.getBigDecimal("tAMT")));
                    data.add(datanum);
                }
                String[] from = {"A", "B","E","C"};
                final int[] views = {R.id.sale_bill, R.id.sale_date,R.id.sale_qty, R.id.sale_amt};
                ADA = new SimpleAdapter(PurchaseReportDetailActivity.this,
                        data, R.layout.purchase_grid_detail_layout, from, views);
                gridview.setAdapter(ADA);


                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int  position, long id) {
                        HashMap<String, String> map = (HashMap<String, String>)parent.getItemAtPosition(position);
                        String ENTRYID = map.get("F");
                        String netamt = map.get("C");
                        String bill_no = map.get("A");
                        String bill_date=map.get("B");
                        Intent intent=new Intent(new Intent(PurchaseReportDetailActivity.this, PurchaseReportBillDetailActivity.class));
                        intent.putExtra("ENTRYID",ENTRYID);
                        intent.putExtra("netamt",netamt);
                        intent.putExtra("cname",cname);
                        intent.putExtra("bd",bill_date);
                        intent.putExtra("bn",bill_no);

                        startActivity(intent);


                    }
                });



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

        final Dialog dialog = new Dialog(PurchaseReportDetailActivity.this);
        dialog.setContentView(R.layout.calender_list_for_sale);

        dialog.show();

//       end calender button task *************************************************

    }

    public void todaydatemethod(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        to_date_tv.setText(currentDateandTime);
        from_date_tv.setVisibility(View.GONE);
        to_text_tv.setVisibility(View.GONE);

    }

    public void yesterdaydatemethd(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        to_date_tv.setText(dateFormat.format(cal.getTime()));
    }

    public void thisweekdatemethod(View view) {
        Toast.makeText(this, "j", Toast.LENGTH_SHORT).show();
    }

    public void thismonthmethod(View view) {
    }

    public void lastmonthmethod(View view) {
    }

    public void thisquartermethod(View view) {
    }

    public void thisyearmethod(View view) {
    }

    public void customdatemethod(View view) {
    }
}
