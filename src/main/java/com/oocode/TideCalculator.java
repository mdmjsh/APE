package com.oocode;

import java.io.IOException;
import java.math.*;
import java.time.*;
import static java.lang.Integer.parseInt;

public class TideCalculator {


    private TideAPIAdapter tideAPIAdapter = initTideAPIAdapter();

    public void main(String[] args) throws Exception {
        System.out.println(MidDayTide("Folkestone", "12-01-2020"));
    }

    protected BigDecimal MidDayTide(String place, String date)
            throws IOException {

        String dailyTideHeightsForPlace = tideAPIAdapter.getTideTimesString(place, date);

        String[] tideData = dailyTideHeightsForPlace.split("\n");
        TideTimeHeight lowTide = new TideTimeHeight(
                getLocalTime(tideData[0].split(" ")[1]),
                new BigDecimal(tideData[0].split(" ")[2]));
        TideTimeHeight highTide = new TideTimeHeight(
                getLocalTime(tideData[1].split(" ")[1]),
                new BigDecimal(tideData[1].split(" ")[2]));
        return interpolateTideHeight(lowTide, highTide);
    }

    protected TideAPIAdapter initTideAPIAdapter(){
        return new TideAPIAdapter();
    }

    private BigDecimal interpolateTideHeight(TideTimeHeight lowTide, TideTimeHeight highTide) {
        Duration lowToHighDeltaSeconds = Duration.between(lowTide.localTime, highTide.localTime);
        Duration LowToNoonDeltaSeconds = Duration.between(lowTide.localTime, LocalTime.NOON);

        double noonIntersection = (double) LowToNoonDeltaSeconds.toMillis() /
                (double) lowToHighDeltaSeconds.toMillis();
        BigDecimal fullRiseInSeaLevelHighTide = highTide.tideHeight.subtract(lowTide.tideHeight);
        BigDecimal noonRiseInSeaLevel = new BigDecimal(noonIntersection).multiply(fullRiseInSeaLevelHighTide);

        return lowTide.tideHeight.add(noonRiseInSeaLevel).setScale(2, RoundingMode.CEILING);
    }

    private LocalTime getLocalTime(String time) {
        return LocalTime.of(parseInt(time.split(":")[0]),
                parseInt(time.split(":")[1])); }


    private static class TideTimeHeight {
        final LocalTime localTime;
        final BigDecimal tideHeight;
        TideTimeHeight(LocalTime localTime, BigDecimal tideHeight) {
            this.localTime = localTime;
            this.tideHeight = tideHeight; }
    }
}
