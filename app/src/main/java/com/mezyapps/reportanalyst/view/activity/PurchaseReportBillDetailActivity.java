package com.mezyapps.reportanalyst.view.activity;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class PurchaseReportBillDetailActivity extends AppCompatActivity {
    ConnectionClass connectionClass;
    GridView gridview;
    List<Map<String, String>> data = null;
    TextView netamount_bill ,customername_bill ,bill_date,bill_no_invo;
    int ENTRYID;
    String gquery;
    SimpleAdapter ADA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_report_bill_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        connectionClass = new ConnectionClass(getApplicationContext());
        ENTRYID = Integer.parseInt(getIntent().getStringExtra("ENTRYID"));
        netamount_bill= findViewById(R.id.netamount_bill);
        netamount_bill.setText(getIntent().getStringExtra("netamt"));
        gridview = findViewById(R.id.gridviewsalehyeadsubbill);
        customername_bill=findViewById(R.id.customrname_bill);
        customername_bill.setText(getIntent().getStringExtra("cname"));
        bill_no_invo=findViewById(R.id.bill_no_or_inoviceno);
        bill_no_invo.setText(getIntent().getStringExtra("bn"));
        bill_date=findViewById(R.id.bill_date);
        bill_date.setText(getIntent().getStringExtra("bd"));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ggeatdataforgrid();



    }

    private void ggeatdataforgrid() {
        //start customer and amt grid**************************************

        gquery = "select * from PURDET_VIEW  WHERE ENTRYID ="+ENTRYID ;



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

                    datanum.put("A", rs.getString("ITEM"));
                    datanum.put("B", String.valueOf("Qty:  "+rs.getBigDecimal("PRODQTY")+" Nos."));
                    datanum.put("C", String.valueOf("Rate: "+rs.getBigDecimal("PRODRATE")+"/Nos."));
                    datanum.put("D", String.valueOf("HSN/SAC: "+"HSN"+rs.getInt("HSN_CODE")));
                    if (String.valueOf(rs.getBigDecimal("CGST_PER")) == "0.00")
                    {
                        datanum.put("E", String.valueOf(" IGST: "+rs.getBigDecimal("IGST_PER")+"%"));
                        datanum.put("F", "");
                    }else{
                        datanum.put("E", String.valueOf(" CGST: "+rs.getBigDecimal("CGST_PER")+"%"));
                        datanum.put("F", String.valueOf(" SGST: "+rs.getBigDecimal("SGST_PER")+"%"));
                    }

                    datanum.put("G", String.valueOf(rs.getBigDecimal("FINAL_AMT")));

                    //   showTotalamt_tv.setText(String.valueOf(rs.getBigDecimal("tAMT")));
                    data.add(datanum);
                }
                String[] from = {"A", "G","B","C","D","E","F"};
                final int[] views = {R.id.product_name,R.id.product_price,R.id.product_qty,R.id.product_rate,R.id.product_hsnno,R.id.product_cgst,R.id.product_sgst};
                ADA = new SimpleAdapter(PurchaseReportBillDetailActivity.this,
                        data, R.layout.purchase_report_bill_grid_layout, from, views);
                gridview.setAdapter(ADA);
                setDynamicHeight(gridview);

                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int  position, long id) {
                        HashMap<String, String> map = (HashMap<String, String>)parent.getItemAtPosition(position);
//                        String name = map.get("A");
//                        String amt = map.get("B");
//                        String GID = map.get("C");
//                        Intent intent=new Intent(new Intent(SaleReportDetailActivity.this,SaleReportDetailActivity.class));
//                        intent.putExtra("name",name);
//                        intent.putExtra("amt",amt);
//                        intent.putExtra("gid",GID);
                        //  Toast.makeText(SaleReportBillDeatilActivity.this, "1", Toast.LENGTH_SHORT).show();
                        //    startActivity(intent);


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

        final Dialog dialog = new Dialog(PurchaseReportBillDetailActivity.this);
        dialog.setContentView(R.layout.calender_list_for_sale);

        dialog.show();

//       end calender button task *************************************************

    }

    private void setDynamicHeight(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = gridViewAdapter.getCount();
        int rows = 0;

        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > 1 ){
            x = items/1;
            rows = (int) (x + 0);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }


}
