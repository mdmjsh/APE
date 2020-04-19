package com.oocode;

import okhttp3.*;
import java.io.IOException;
import java.math.*;
import java.time.*;
import static java.lang.Integer.parseInt;

public class TideCalculator {


    private static TideAPIAdapter tideAPIAdapter;

    public static void main(String[] args) throws Exception {
        System.out.println(MidDayTide("Folkestone", "12-01-2020"));
    }

    protected static BigDecimal MidDayTide(String place, String date)
            throws IOException {

        String dailyTideHeightsForPlace = tideAPIAdapter.getTideTimesString(place, date);

        String[] tideData = dailyTideHeightsForPlace.split("\n");
        TideTimeHeight lowTide = new TideTimeHeight(
                getLocalTime(tideData[1].split(" ")[1]),
                new BigDecimal(tideData[1].split(" ")[2]));
        TideTimeHeight highTide = new TideTimeHeight(
                getLocalTime(tideData[2].split(" ")[1]),
                new BigDecimal(tideData[2].split(" ")[2]));
        return interpolateTideHeight(lowTide, highTide);
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

    public static void setTideAPIAdapter(TideAPIAdapter tideAPIAdapter) {
        TideCalculator.tideAPIAdapter = tideAPIAdapter;
    }

    private static class TideTimeHeight {
        final LocalTime localTime;
        final BigDecimal tideHeight;
        TideTimeHeight(LocalTime localTime, BigDecimal tideHeight) {
            this.localTime = localTime;
            this.tideHeight = tideHeight; }}
}
