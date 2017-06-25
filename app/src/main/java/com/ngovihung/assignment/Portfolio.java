package com.ngovihung.assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ngo.vi.hung on 6/21/2017.
 */

public class Portfolio implements Serializable {

    private String id;

    ArrayList<NavsItem> DailyNavs;

    private ArrayList<NavsItem> MonthlyNavs;
    private ArrayList<NavsItem> quarterlyNavs;


    public Portfolio(String id){
        this.id = id;
        DailyNavs = new ArrayList<>(Collections.nCopies(365, new NavsItem()));
        MonthlyNavs = new ArrayList<>(Collections.nCopies(12, new NavsItem()));
        quarterlyNavs = new ArrayList<>(Collections.nCopies(4, new NavsItem()));
    }

    public ArrayList<NavsItem> getDailyNavs() {
        return DailyNavs;
    }

    public void setNavsItems(ArrayList<NavsItem> navsItems) {
        this.DailyNavs = navsItems;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<NavsItem> getQuarterlyNavs() {
        return quarterlyNavs;
    }


    public ArrayList<NavsItem> getMonthlyNavs() {
        return MonthlyNavs;
    }

    public void setMonthlyNavs(NavsItem fromNavs, NavsItem toNavs){
        if(toNavs.getDate().after(fromNavs.getDate())
                && Utils.compareSameMonth(fromNavs.getDate(),toNavs.getDate())
                    && toNavs.getAmount() >0){
            this.MonthlyNavs.set(Utils.getMonthOfYear(toNavs.getDate()), toNavs);
        }
    }

    public void setQuarterNavs(NavsItem fromNavs, NavsItem toNavs){
        if(toNavs.getDate().after(fromNavs.getDate())
                && Utils.compareSameQuarter(fromNavs.getDate(),toNavs.getDate())
                && toNavs.getAmount() >0){
            this.quarterlyNavs.set(Utils.getQuarterIndex(toNavs.getDate()), toNavs);
        }
    }
}
