package com.oocode;

import okhttp3.*;
import java.io.IOException;
import java.math.*;
import java.time.*;
import static java.lang.Integer.parseInt;

public class TideCalculator {
    public static void main(String[] args) throws Exception {
        System.out.println(getMidDayTide("Folkestone", "11-01-2020"));
    }

    public static BigDecimal getMidDayTide(String place, String date)
            throws IOException {

        String responseString;
        Request request = new Request.Builder()
                .url(String.format(
                        "https://dry-fjord-40481.herokuapp.com/tides/%s/%s",
                        place, date))
                .build();

        try (Response response = new OkHttpClient.Builder().build()
                .newCall(request).execute()) {
            try (ResponseBody responseBody = response.body()) {
                assert responseBody != null;
                responseString = responseBody.string();
                System.out.println("responseString = " + responseString);
            }
        }

        String[] tideData = responseString.split("\n");
        Value lowTideValue = new Value(time(tideData[1].split(" ")[1]),
                new BigDecimal(tideData[1].split(" ")[2]));
        Value highTideValue = new Value(time(tideData[2].split(" ")[1]),
                new BigDecimal(tideData[2].split(" ")[2]));
        return interpolateTideHeight(lowTideValue, highTideValue);
    }

    private static BigDecimal interpolateTideHeight(Value lowTideValue, Value highTideValue) {
        Duration between = Duration.between(lowTideValue.localTime, highTideValue.localTime);
        Duration since = Duration.between(lowTideValue.localTime, LocalTime.NOON);
        BigDecimal betweenLevels = highTideValue.tideHeight.subtract(lowTideValue.tideHeight);
        double proportionOfWayThrough = (double) since.toMillis() /
                (double) between.toMillis();
        BigDecimal sinceLevelChange = betweenLevels.multiply(
                new BigDecimal(proportionOfWayThrough));
        return lowTideValue.tideHeight.add(sinceLevelChange)
                .setScale(2, RoundingMode.CEILING);
    }

    private static LocalTime time(String time) {
        return LocalTime.of(parseInt(time.split(":")[0]),
                parseInt(time.split(":")[1])); }

    private static class Value {
        public final LocalTime localTime;
        public final BigDecimal tideHeight;
        public Value(LocalTime localTime, BigDecimal tideHeight) {
            this.localTime = localTime;
            this.tideHeight = tideHeight; }}
}
