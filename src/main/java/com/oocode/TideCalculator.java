package com.oocode;

import okhttp3.*;
import java.io.IOException;
import java.math.*;
import java.time.*;
import static java.lang.Integer.parseInt;

public class TideCalculator implements TideCalculatorInterface {


    public static void main(String[] args) throws Exception {
        System.out.println(MidDayTide("Folkestone", "12-01-2020"));
    }

    static BigDecimal MidDayTide(String place, String date)
            throws IOException {

        String dailyTideHeightsForPlace = getTideTimesString(place, date);

        String[] tideData = dailyTideHeightsForPlace.split("\n");
        TideTimeHeight lowTide = new TideTimeHeight(
                getLocalTime(tideData[1].split(" ")[1]),
                new BigDecimal(tideData[1].split(" ")[2]));
        TideTimeHeight highTide = new TideTimeHeight(
                getLocalTime(tideData[2].split(" ")[1]),
                new BigDecimal(tideData[2].split(" ")[2]));
        return interpolateTideHeight(lowTide, highTide);
    }

 /** Adapter wrapper to call the dry-fjord API.
  * @param place The place name to search
  * @param date String of the date to search on
  * @return string of the tide data from the API response body
  * */
    protected static String getTideTimesString(String place, String date) throws IOException {
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
    private static String makeApiCall(String place, String date) throws IOException {
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

    private static BigDecimal interpolateTideHeight(TideTimeHeight lowTide, TideTimeHeight highTide) {
        Duration lowToHighDeltaSeconds = Duration.between(lowTide.localTime, highTide.localTime);
        Duration LowToNoonDeltaSeconds = Duration.between(lowTide.localTime, LocalTime.NOON);
        double noonIntersection = (double) LowToNoonDeltaSeconds.toMillis() /
                (double) lowToHighDeltaSeconds.toMillis();
        return lowTide.tideHeight.add(highTide.tideHeight.subtract(lowTide.tideHeight).multiply(
                new BigDecimal(noonIntersection)))
                .setScale(2, RoundingMode.CEILING);
    }

    private static LocalTime getLocalTime(String time) {
        return LocalTime.of(parseInt(time.split(":")[0]),
                parseInt(time.split(":")[1])); }

    private static class TideTimeHeight {
        final LocalTime localTime;
        final BigDecimal tideHeight;
        TideTimeHeight(LocalTime localTime, BigDecimal tideHeight) {
            this.localTime = localTime;
            this.tideHeight = tideHeight; }}
}
