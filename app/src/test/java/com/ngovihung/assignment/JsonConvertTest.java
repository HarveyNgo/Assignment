package com.ngovihung.assignment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by ngo.vi.hung on 6/22/2017.
 */

public class JsonConvertTest {

    @Test
    public void read_file_success(){
        String data = Utils.readSampleFile("data_json.json");
        assertEquals(data,!Utils.isEmpty(data)); //data is not empty
    }


    @Test
    public void convert_Json_to_Portfolio_isCorrect(){
        ArrayList<Portfolio>  portfolios = new ArrayList<>();
        String data = Utils.readSampleFile("data_json.json");
        JSONArray portfolioJsonList = null;
        try {
            portfolioJsonList = new JSONArray(data);
            if (portfolioJsonList.length() > 0 ) {
                for (int i = 0; i < portfolioJsonList.length(); i++) {
                    JSONObject  portfolioJson = portfolioJsonList.getJSONObject(i);
                    Portfolio p = new Portfolio(portfolioJson.optString(Constant.PORTFOLIO_ID_TAG));
                    assertNotNull(p);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void convert_Json_to_Navs_isCorrect(){
        ArrayList<Portfolio>  portfolios = new ArrayList<>();
        String data = "\"navs\":[\n" +
                "{\n" +
                "\"date\":\"2017-01-01\",\n" +
                "\"amount\":9837.51504\n" +
                "},\n" +
                "{\n" +
                "\"date\":\"2017-01-02\",\n" +
                "\"amount\":9894.264455\n" +
                "}]";

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        NavsItem navsItems = gson.fromJson(data, NavsItem.class);
        assertNotNull(navsItems);

    }
}
