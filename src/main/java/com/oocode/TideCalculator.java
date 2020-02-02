package com.oocode;

import okhttp3.*;
import java.io.IOException;
import java.math.*;
import java.time.*;
import static java.lang.Integer.parseInt;

public class TideCalculator {
    public static void main(String[] args) throws Exception {
        System.out.println(midDayTide("Folkestone", "11-01-2020"));
    }

    public static BigDecimal midDayTide(String place, String date)
            throws IOException {
        String l;
        Request request = new Request.Builder()
                .url(String.format(
                        "https://dry-fjord-40481.herokuapp.com/tides/%s/%s",
                        place, date))
                .build();

        try (Response e = new OkHttpClient.Builder().build()
                .newCall(request).execute()) {
            try (ResponseBody x = e.body()) {
                assert x != null;
                l = x.string();
            }
        }

        String[] result = l.split("\n");
        Value first = new Value(time(result[1].split(" ")[1]),
                new BigDecimal(result[1].split(" ")[2]));
        Value second = new Value(time(result[2].split(" ")[1]),
                new BigDecimal(result[2].split(" ")[2]));
        Duration between = Duration.between(first.first, second.first);
        Duration since = Duration.between(first.first, LocalTime.NOON);
        BigDecimal betweenLevels = second.second.subtract(first.second);
        double proportionOfWayThrough = (double) since.toMillis() /
                (double) between.toMillis();
        BigDecimal sinceLevelChange = betweenLevels.multiply(
                new BigDecimal(proportionOfWayThrough));
        return first.second.add(sinceLevelChange)
                .setScale(2, RoundingMode.CEILING); }

    private static LocalTime time(String time) {
        return LocalTime.of(parseInt(time.split(":")[0]),
                parseInt(time.split(":")[1])); }

    private static class Value {
        public final LocalTime first;
        public final BigDecimal second;
        public Value(LocalTime first, BigDecimal second) {
            this.first = first;
            this.second = second; }}
}
