package com.oocode;

import okhttp3.*;
import java.io.IOException;
import java.math.*;
import java.time.*;
import static java.lang.Integer.parseInt;

public class TideCalculator {
    public static void main(String[] args) throws Exception {
        System.out.println(MidDayTide("Folkestone", "12-01-2020 "));
    }

    public static BigDecimal MidDayTide(String place, String date)
            throws IOException {

        String dailyTideHeightsForPlace = makeApiCall(place, date);

        String[] tideData = dailyTideHeightsForPlace.split("\n");
        TideTimeHeight lowTide = new TideTimeHeight(time(tideData[1].split(" ")[1]),
                new BigDecimal(tideData[1].split(" ")[2]));
        TideTimeHeight highTide = new TideTimeHeight(time(tideData[2].split(" ")[1]),
                new BigDecimal(tideData[2].split(" ")[2]));
        return interpolateTideHeight(lowTide, highTide);
    }

    private static String makeApiCall(String place, String date) throws IOException {
        String dailyTideHeightsForPlace;
        Request request = new Request.Builder()
                .url(String.format(
                        "https://dry-fjord-40481.herokuapp.com/tides/%s/%s",
                        place, date))
                .build();

        try (Response response = new OkHttpClient.Builder().build()
                .newCall(request).execute()) {
            try (ResponseBody responseBody = response.body()) {

                assert responseBody != null;
//              n.b. this could be lead to a nullPointerException
                dailyTideHeightsForPlace = responseBody.string();
                String newline = System.getProperty("line.separator");
                System.out.println("responseString: " + newline + newline + dailyTideHeightsForPlace);
            }
        }
        return dailyTideHeightsForPlace;
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

    private static LocalTime time(String time) {
        return LocalTime.of(parseInt(time.split(":")[0]),
                parseInt(time.split(":")[1])); }

    private static class TideTimeHeight {
        public final LocalTime localTime;
        public final BigDecimal tideHeight;
        public TideTimeHeight(LocalTime localTime, BigDecimal tideHeight) {
            this.localTime = localTime;
            this.tideHeight = tideHeight; }}
}
