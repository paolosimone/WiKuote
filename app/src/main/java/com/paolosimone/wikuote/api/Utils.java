package com.paolosimone.wikuote.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Paolo Simone on 20/03/2016.
 */
public abstract class Utils {

    public static final int INVALID_INDEX = -1;

    public static String capitalizeInitials(String author){
        String[] words = author.toLowerCase().split(" ");

        if (words.length==0) return "";

        String result = "";
        for (String word : words){
            result =+ word.toUpperCase().charAt(0) +
                    ((word.length()>0) ? word.substring(1) : "") + " ";
        }
        return result.substring(0,result.length()-1);
    }

    public static ArrayList<String> extractSuggestions(JsonArray response){
        ArrayList<String> result = new ArrayList<>();
        try {
            JSONArray suggestions = new JSONArray(response.toString()).getJSONArray(1);
            for (int i=0; i<suggestions.length(); i++){
                result.add(suggestions.getString(i));
            }
        } catch (JSONException e) {
            // do nothing
        }
        return result;
    }

    public static int extractPageIndex(JsonObject response){
        int index = INVALID_INDEX;
        try {
            JSONObject pages = new JSONObject(response.toString())
                    .getJSONObject("query")
                    .getJSONObject("pages");

            Iterator<String> ids = pages.keys();
            while(ids.hasNext()){
                JSONObject page = pages.getJSONObject(ids.next());

                if (!page.has("missing")){
                    index = page.getInt("pageid");
                    break;
                }
            }
        } catch (JSONException e) {
            // do nothing
        }
        return index;
    }

    public static List<Integer> extractSectionIndexList(JsonObject response){
        List<Integer> indexes = new ArrayList<>();
        try {
            JSONArray sections = new JSONObject(response.toString())
                    .getJSONObject("parse")
                    .getJSONArray("sections");

            for (int i=0; i<sections.length(); i++){
                JSONObject section =sections.getJSONObject(i);

                String number = section.getString("number");
                String[] position = number.split(Pattern.quote("."));

                if (position.length>1 && position[0].equals("1")){
                    indexes.add(section.getInt("index"));
                }
            }

            if (indexes.isEmpty()){
                indexes.add(1);
            }
        } catch (JSONException e) {
            // do nothing
        }
        return indexes;
    }

    public static List<String> extractQuoteList(JsonObject response){
        List<String> quotes = new ArrayList<>();
        try {
            String html = new JSONObject(response.toString())
                    .getJSONObject("parse")
                    .getJSONObject("text")
                    .getString("*");

            Document doc = Jsoup.parse(html);
            Elements li = doc.select("html > body > ul > li");
            for(Element e : li){
                Elements bolds = e.select(":root > b");

                if (!bolds.isEmpty()){
                    quotes.add(bolds.get(0).text());
                } else {
                    String newQuote = e.text();

                    Elements extras = e.getElementsByTag("ul");
                    if(!extras.isEmpty()){
                        newQuote = newQuote.replace(extras.get(0).text(),"");
                    }

                    quotes.add(newQuote);
                }
            }
        } catch (JSONException e) {
            // do nothing
        }
        return quotes;
    }
}
