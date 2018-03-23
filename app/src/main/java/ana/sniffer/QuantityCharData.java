package ana.sniffer;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anast on 25/02/2018.
 */

public class QuantityCharData {
    HashMap<String, Long> squantityPerApp = new HashMap<String, Long>();
    HashMap<String, Long> rquantityPerApp = new HashMap<String, Long>();

    public QuantityCharData() {}

    public QuantityCharData(JSONArray jsonArray) {
        List<Long> counts;

        for(int i=0; i<jsonArray.length(); i++){
            try {
                JSONObject jo = jsonArray.getJSONObject(i);
                String countStr[] = jo.getString("quantity").split(" ");

                squantityPerApp.put(jo.getString("app_name"), Long.parseLong(countStr[0]));
                rquantityPerApp.put(jo.getString("app_name"), Long.parseLong(countStr[1]));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public JSONArray toJSON() {
        JSONArray ja = new JSONArray();
        JSONObject jo;
        for (String key : squantityPerApp.keySet()) {
            for (String key1 : rquantityPerApp.keySet()) {
                if (key.equals(key1)) {
                    String countstr = "";
                    countstr += squantityPerApp.get(key) + " " + rquantityPerApp.get(key1);
                    jo = new JSONObject();
                    try {
                        jo.put("app_name", key);
                        jo.put("quantity", countstr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ja.put(jo);
                }
            }
        }
        return ja;
    }
}
