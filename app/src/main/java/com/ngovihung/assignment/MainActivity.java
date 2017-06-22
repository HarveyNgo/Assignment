package com.ngovihung.assignment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements   View.OnClickListener{

    LineChart lineChart;
    ArrayList<Portfolio>  portfolios;
    View activity_main_ll_quarter,activity_main_ll_monthly,activity_main_ll_daily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Application.setActiveActivity(this);

        lineChart = (LineChart) findViewById(R.id.activity_main_chart);
        activity_main_ll_quarter  = (View) findViewById(R.id.activity_main_ll_quarter);
        activity_main_ll_quarter.setOnClickListener(this);
        activity_main_ll_monthly  = (View) findViewById(R.id.activity_main_ll_monthly);
        activity_main_ll_monthly.setOnClickListener(this);
        activity_main_ll_daily =  (View) findViewById(R.id.activity_main_ll_daily);
        activity_main_ll_daily.setOnClickListener(this);
        portfolios = new ArrayList<>();
        getData();

    }



    @Override
    protected void onResume() {
        super.onResume();

//        mDatabase = FirebaseDatabase.getInstance().getReference();
        //setDataToFireBase();
//        readDataFireBase();
        Application.setActiveActivity(this);
        setDailyChart();
        setSelectedForView(true,false,false);

    }

    public void getData(){
        String data = Utils.readSampleFile("data_json.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JSONArray portfolioJsonList = null;
        try {
            portfolioJsonList = new JSONArray(data);
            if (portfolioJsonList.length() > 0 ) {
                for (int i = 0; i < portfolioJsonList.length(); i++) {
                    JSONObject  portfolioJson = portfolioJsonList.getJSONObject(i);
                    Portfolio p = new Portfolio(portfolioJson.optString(Constant.PORTFOLIO_ID_TAG));
                    p.getNavsItems().clear();
                    JSONArray navsJsonList = portfolioJson.getJSONArray(Constant.NAVS_TAG);
                    if (navsJsonList != null && navsJsonList.length() > 0) {
                        for (int j = 0; j < navsJsonList.length(); ++j) {
                            p.getNavsItems().add(gson.fromJson(navsJsonList.getJSONObject(j).toString(), NavsItem.class));
                            if(j > 0 ){
                                p.setMonthlyNavs(p.getNavsItems().get(j-1),p.getNavsItems().get(j));
                                p.setQuarterNavs(p.getNavsItems().get(j-1),p.getNavsItems().get(j));
                            }
                        }
                    }
                    portfolios.add(p);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDailyChart(){
        if(lineChart.getData() != null)
            lineChart.getData().clearValues();

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        for(int i=0;i< portfolios.size(); i++){
            ArrayList<Entry> entries = new ArrayList<>();
            for(int j=0;j< portfolios.get(i).getNavsItems().size(); j++){
                entries.add(new Entry(portfolios.get(i).getNavsItems().get(j).getAmount(),
                        Utils.getDayInYear(portfolios.get(i).getNavsItems().get(j).getDate())));
            }
            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
            dataset.setColor(Constant.COLORS[i]);
            dataSets.add(dataset);

        }
        ArrayList<String> xAxisLabels = new ArrayList<>();
        for(int i=0; i< 365; i++){
            xAxisLabels.add(Utils.getDateString(i));
        }
        LineData data = new LineData(xAxisLabels,dataSets);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    private void setMonthlyChart(){
        if(lineChart.getData() != null)
            lineChart.getData().clearValues();
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        for(int i=0;i< portfolios.size(); i++){
            ArrayList<Entry> entries = new ArrayList<>();
            for(int j=0;j< 12; j++){
                entries.add(new Entry(portfolios.get(i).getMonthlyNavs().get(j)
                        .getAmount(),j));
            }
            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
            dataset.setColor(Constant.COLORS[i]);
            dataSets.add(dataset);

        }
        ArrayList<String> xAxisLabels = new ArrayList<>();
        for(String month : Constant.MONTHS){
            xAxisLabels.add(month);
        }
        LineData data = new LineData(xAxisLabels,dataSets);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    private void setQuarterChart(){
        if(lineChart.getData() != null)
            lineChart.getData().clearValues();
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        for(int i=0;i< portfolios.size(); i++){
            ArrayList<Entry> entries = new ArrayList<>();
            for(int j=0;j< 4; j++){
                entries.add(new Entry(portfolios.get(i).getQuarterlyNavs().get(j)
                        .getAmount(),j));
            }
            LineDataSet dataset = new LineDataSet(entries,portfolios.get(i).getId());
            dataset.setColor(Constant.COLORS[i]);
            dataSets.add(dataset);

        }
        ArrayList<String> xAxisLabels = new ArrayList<>();
        for(int i=1; i<5;i++){
            String quarter = Constant.MONTHS[((Constant.MONTHS.length /4)*i) -1];
            xAxisLabels.add(quarter);
        }
        LineData data = new LineData(xAxisLabels,dataSets);
        lineChart.setData(data);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_main_ll_daily:
                setSelectedForView(true,false,false);
                setDailyChart();
                break;

            case R.id.activity_main_ll_monthly :
                setSelectedForView(false,true,false);
                setMonthlyChart();
                break;
            case R.id.activity_main_ll_quarter :
                setSelectedForView(false,false,true);
                setQuarterChart();
                break;
        }
    }

    private void setSelectedForView(boolean daily, boolean monthly, boolean quarter){
        activity_main_ll_daily.setSelected(daily);
        activity_main_ll_monthly.setSelected(monthly);
        activity_main_ll_quarter.setSelected(quarter);
    }

    private DatabaseReference mDatabase;
    private void  setDataToFireBase(){

        Portfolio p = portfolios.get(0);
        mDatabase.child(Constant.PORTFOLIO_ID_TAG).setValue(p.getId());
        DatabaseReference navsDBRef =  mDatabase.child(Constant.NAVS_TAG).push();
        for(NavsItem n : p.getNavsItems()){
            navsDBRef.setValue(n);
        }
    }

    private void readDataFireBase(){
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NavsItem post = dataSnapshot.getValue(NavsItem.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
