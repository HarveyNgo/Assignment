package com.ngovihung.assignment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ngovihung.assignment.fragment.BaseFragment;
import com.ngovihung.assignment.fragment.DailyFragment;
import com.ngovihung.assignment.fragment.MonthlyFragment;
import com.ngovihung.assignment.fragment.QuarterFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements   View.OnClickListener {


    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private LinearLayout menu_item_daily,menu_item_monthly,menu_item_quarter;
    ArrayList<Portfolio> portfolios;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Application.setActiveActivity(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                R.color.colorPrimary));
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu));
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setToolbarNavigationClickListener(this);
        toggle.syncState();

        menu_item_daily = (LinearLayout) findViewById(R.id.menu_item_daily);
        menu_item_daily.setOnClickListener(this);
        menu_item_monthly = (LinearLayout) findViewById(R.id.menu_item_monthly);
        menu_item_monthly.setOnClickListener(this);
        menu_item_quarter = (LinearLayout) findViewById(R.id.menu_item_quarter);
        menu_item_quarter.setOnClickListener(this);

        portfolios = new ArrayList<>();
        getData();
        fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, DailyFragment.getInstance(portfolios));
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Daily");
    }



    @Override
    protected void onResume() {
        super.onResume();
//
////        mDatabase = FirebaseDatabase.getInstance().getReference();
//        //setDataToFireBase();
////        readDataFireBase();
//        Application.setActiveActivity(this);
//        setDailyChart();

    }

    public void getData(){
        String data = Utils.readSampleFile("data_json.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        JSONArray portfolioJsonList = null;
        try {
            portfolioJsonList = new JSONArray(data);
            if (portfolioJsonList.length() > 0 ) {
                for (int i = 0; i < portfolioJsonList.length(); i++) {
                    JSONObject portfolioJson = portfolioJsonList.getJSONObject(i);
                    Portfolio p = new Portfolio(portfolioJson.optString(Constant.PORTFOLIO_ID_TAG));
                    JSONArray navsJsonList = portfolioJson.getJSONArray(Constant.NAVS_TAG);
                    if (navsJsonList != null && navsJsonList.length() > 0) {
                        for (int j = 0; j < navsJsonList.length(); ++j) {
                            NavsItem  item  = gson.fromJson(navsJsonList.getJSONObject(j).toString(), NavsItem.class);
                            p.getDailyNavs().set(Utils.getDayInYear(item.getDate())-1,item);
                        }
                    }
                    for(int k=1; k<p.getDailyNavs().size(); k++) {
                        NavsItem fromNavItem = p.getDailyNavs().get(k - 1);
                        NavsItem toNavItem = p.getDailyNavs().get(k);
                        if (fromNavItem.getDate() != null && toNavItem.getDate() != null) {
                            p.setMonthlyNavs(fromNavItem, toNavItem);
                            p.setQuarterNavs(fromNavItem, toNavItem);
                        }
                    }
                    portfolios.add(p);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.main_toolbar:
                if (drawer != null && !drawer.isDrawerOpen(GravityCompat.START))
                    drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_item_daily:
                fragmentTransaction  = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,DailyFragment.getInstance(portfolios), "dailyFragment");
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Daily");
                break;
            case R.id.menu_item_monthly:
                fragmentTransaction  = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,MonthlyFragment.getInstance(portfolios));
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Monthly");
                break;
            case R.id.menu_item_quarter:
                fragmentTransaction  = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container,QuarterFragment.getInstance(portfolios));
                fragmentTransaction.commit();
                getSupportActionBar().setTitle("Quarter");
                break;

        }

        if (drawer != null & drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment myFragment =getVisibleFragment(); // (Fragment)getSupportFragmentManager().findFragmentByTag(R.id.main_container);
        if (myFragment != null && myFragment.isVisible()) {
            myFragment.onOptionsItemSelected(item);
        }
        return true;
    }

//    private DatabaseReference mDatabase;
//    private void  setDataToFireBase(){
//
//        Portfolio p = portfolios.get(0);
//        mDatabase.child(Constant.PORTFOLIO_ID_TAG).setValue(p.getId());
//        DatabaseReference navsDBRef =  mDatabase.child(Constant.NAVS_TAG).push();
//        for(NavsItem n : p.getDailyNavs()){
//            navsDBRef.setValue(n);
//        }
//    }
//
//    private void readDataFireBase(){
//        mDatabase.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                NavsItem post = dataSnapshot.getValue(NavsItem.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
}
