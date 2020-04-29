package com.jmhy.sdk.sdk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;


public class HashmapToJson {

    private String hashMapToJson(HashMap<String, Object> map) {
        String string = "{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Entry e = (Entry) it.next();
            string += "\"" + e.getKey() + "\":";
            String value = e.getValue() == null ? "" : e.getValue().toString();
            try {
            	
            	
				string += "\"" + URLEncoder.encode(value, "UTF-8") + "\",";
				
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";
        return string;
    }

    public String toJson(HashMap<String, Object> map) {
        HashmapToJson toJson = new HashmapToJson();
       String jsonString ="" ;//"[";

        jsonString += toJson.hashMapToJson(map);

        return jsonString ;//+= "]";

    }
}