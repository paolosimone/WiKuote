package com.paolosimone.wikuote.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paolosimone.wikuote.model.Page;
import com.paolosimone.wikuote.model.Quote;

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
public abstract class WikiQuoteUtils {

    public static final int INVALID_INDEX = -1;
    public static final int QUOTE = 0;
    public static final int PAGE_NAME = 1;

    public static final String BEFORE_NAME = "/wiki/";

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

    public static String extractPageNameFromUrl(String pageUrl){
        int start = pageUrl.lastIndexOf(BEFORE_NAME) + BEFORE_NAME.length();
        return pageUrl.substring(start);
    }

    public static ArrayList<Page> extractSuggestions(JsonArray response){
        ArrayList<Page> result = new ArrayList<>();
        try {
            JSONArray names = new JSONArray(response.toString()).getJSONArray(1);
            JSONArray descriptions = new JSONArray(response.toString()).getJSONArray(2);
            JSONArray urls = new JSONArray(response.toString()).getJSONArray(3);
            for (int i=0; i<names.length(); i++){
                String name = names.getString(i);
                String description = descriptions.getString(i);
                String url = urls.getString(i);
                result.add(new Page(name, description, url));
            }
        } catch (JSONException e) {
            // do nothing
        }
        return result;
    }

    public static long extractPageIndex(JsonObject response){
        long index = INVALID_INDEX;
        try {
            JSONObject pages = new JSONObject(response.toString())
                    .getJSONObject("query")
                    .getJSONObject("pages");

            Iterator<String> ids = pages.keys();
            while(ids.hasNext()){
                JSONObject page = pages.getJSONObject(ids.next());

                if (!page.has("missing")){
                    index = page.getLong("pageid");
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

    public static String[] extractQuoteOfTheDay(Document mainPage){
        String quotdDivId = "#mf-qotd ";
        String quotdArea = "table > tbody > tr > td > table > tbody > tr > td ";
        String quotdRows = "table > tbody > tr ";
        Elements rows = mainPage.select(quotdDivId + quotdArea + quotdRows);

        String[] result = new String[2];
        result[QUOTE] = rows.get(0).text();
        result[PAGE_NAME] = rows.get(1).select("a").first().text();

        return result;
    }
}
