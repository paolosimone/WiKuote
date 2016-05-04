package com.paolosimone.wikuote.api;

import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paolosimone.wikuote.exceptions.ParserException;
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
 * Helper class that contains a collection of useful methods to extract information from the data returned by WikiQuote.org.
 */
public abstract class WikiQuoteUtils {

    public static final int INVALID_INDEX = -1;
    public static final int QUOTE = 0;
    public static final int PAGE_NAME = 1;

    public static final String BEFORE_NAME = "/wiki/";

    /**
     * Capitalize the initials of the given string.
     * @param string the string whose initials must been capitalized
     * @return the capitalized version of the string
     */
    public static String capitalizeInitials(String string){
        String[] words = string.toLowerCase().split(" ");

        if (words.length==0) return "";

        String result = "";
        for (String word : words){
            result =+ word.toUpperCase().charAt(0) +
                    ((word.length()>0) ? word.substring(1) : "") + " ";
        }
        return result.substring(0,result.length()-1);
    }

    /**
     * Extract the name of the page from the url of the web page.
     * @param pageUrl the url of the web page
     * @return the name of the page
     */
    public static String extractPageNameFromUrl(String pageUrl){
        int start = pageUrl.lastIndexOf(BEFORE_NAME) + BEFORE_NAME.length();
        return pageUrl.substring(start);
    }

    /**
     * Extract a list of suggestions from a opensearch JSON response.
     * @param response the JSON response from open search
     * @return the list of suggested pages
     * @throws ParserException
     */
    public static ArrayList<Page> extractSuggestions(JsonArray response) throws ParserException {
        ArrayList<Page> result = new ArrayList<>();
        try {
            JSONArray names = new JSONArray(response.toString()).getJSONArray(1);
            JSONArray descriptions = new JSONArray(response.toString()).getJSONArray(2);
            JSONArray urls = new JSONArray(response.toString()).getJSONArray(3);
            for (int i=0; i<names.length(); i++){
                String name = names.getString(i);
                String description = descriptions.getString(i);
                String url = urls.getString(i);
                // Use mobile version of the website
                url = url.replace(Uri.parse(url).getHost(), WikiQuoteProvider.HOST);
                result.add(new Page(name, description, url));
            }
        } catch (JSONException e) {
            throw new ParserException();
        }
        return result;
    }

    /**
     * Extract the index of the web page from the JSON response identifying a page.
     * @param response the JSON response from WikiQuote containing the page info
     * @return the index of the web page
     * @throws ParserException
     */
    public static long extractPageIndex(JsonObject response) throws ParserException {
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
            throw new ParserException();
        }
        return index;
    }

    /**
     * Extract the list of all subsections index of the first section from the JSON response.
     * @param response the JSON response from WikiQuote containing the web page sections
     * @return the list of all subsections index of the first section
     * @throws ParserException
     */
    public static List<Integer> extractSectionIndexList(JsonObject response) throws ParserException {
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
            throw new ParserException();
        }
        return indexes;
    }

    /**
     * Extract the list of quotes from the HTML of a subsection, contained in the JSON response.
     * @param response the JSON response containing the HTML of a subsection
     * @return the list of quotes contained in the subsection
     * @throws ParserException
     */
    public static List<String> extractQuoteList(JsonObject response) throws ParserException {
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
            throw new ParserException();
        }
        return quotes;
    }

    /**
     * Extract the quote of the day from the HTML of the main page of WikiQuote.org
     * @param mainPage the HTML of WikiQuote.org
     * @return the quote of the day
     * @throws ParserException
     */
    public static String[] extractQuoteOfTheDay(Document mainPage) throws ParserException {
        try {
            String quotdDivId = "#mf-qotd ";
            String quotdArea = "table > tbody > tr > td > table > tbody > tr > td ";
            String quotdRows = "table > tbody > tr ";
            Elements rows = mainPage.select(quotdDivId + quotdArea + quotdRows);

            String[] result = new String[2];
            result[QUOTE] = rows.get(0).text();
            result[PAGE_NAME] = rows.get(1).select("a").first().text();

            return result;
        } catch (Exception e) {
            throw new ParserException();
        }

    }
}
