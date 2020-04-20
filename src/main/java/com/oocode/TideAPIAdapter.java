package com.oocode;

import com.oocode.TideCalculator.TideTimeHeight;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;

import static java.lang.Integer.parseInt;

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

    TideTimeHeight[] getLowAndHighTides(String dailyTideHeightsForPlace){
        int LowTideIndex = getFirstLowTideIndex(dailyTideHeightsForPlace);

        String[] tideData = dailyTideHeightsForPlace.split("\n");
        TideCalculator.TideTimeHeight lowTide = getTideTimeHeight(tideData[LowTideIndex]);

        TideCalculator.TideTimeHeight highTide = getTideTimeHeight(tideData[LowTideIndex + 1]);
        return new TideTimeHeight[]{lowTide, highTide};
    }

    static TideTimeHeight getTideTimeHeight(String tideDatum) {
        return new TideTimeHeight(
                getLocalTime(tideDatum.split(" ")[1]),
                new BigDecimal(tideDatum.split(" ")[2]));
    }

    static int getFirstLowTideIndex(String dailyTideHeightsForPlace) {
        return (dailyTideHeightsForPlace.substring(0, 2).equals("HW"))? 1:0;
    }
    static LocalTime getLocalTime(String time) {
        return LocalTime.of(parseInt(time.split(":")[0]),
                parseInt(time.split(":")[1])); }
}
