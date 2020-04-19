package com.oocode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class TideAPIAdapter implements TideAPIAdapterInterface{
    /** Adapter wrapper to call the dry-fjord API.
     * @param place The place name to search
     * @param date String of the date to search on
     * @return string of the tide data from the API response body
     * */
     String getTideTimesString(String place, String date) throws IOException {
        String dailyTideHeightsForPlace = makeApiCall(place, date);
        String newline = System.getProperty("line.separator");
        System.out.println("responseString: " + newline + newline + dailyTideHeightsForPlace);
        return  dailyTideHeightsForPlace;
    }
    /** Calls the dry-fjord API and returns the ResponseBody string
     * @param place The place name to search
     * @param date String of the date to search on
     * @return string of the tide data from the API response body
     * */
    private String makeApiCall(String place, String date) throws IOException {
        Request request = new Request.Builder()
                .url(String.format(
                        "https://dry-fjord-40481.herokuapp.com/tides/%s/%s",
                        place, date))
                .build();
        try (Response response = new OkHttpClient.Builder().build()
                .newCall(request).execute()) {
            try (ResponseBody responseBody = response.body()) {
                return responseBody.string();
            }
        }
    }
}
