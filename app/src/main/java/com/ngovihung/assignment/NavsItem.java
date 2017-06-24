package com.ngovihung.assignment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by ngo.vi.hung on 6/21/2017.
 */

public class NavsItem implements Serializable {

    @SerializedName("date")
    private Date date;

    @SerializedName("amount")
    private float amount;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public NavsItem(){
        //this.date = null;
        //this.amount =null;
    }
}
