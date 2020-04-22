package com.oocode;

import java.io.IOException;
import java.math.*;
import java.text.ParseException;
import java.time.*;


public class TideCalculator {


    private TideAPIAdapter tideAPIAdapter = initTideAPIAdapter();
    private QueryClock queryClock = initQueryClock();

    protected TideAPIAdapter initTideAPIAdapter(){
        return new TideAPIAdapter();
    }
    protected QueryClock initQueryClock() { return new QueryClock();
    }
    String OUTSIDE_WINDOW = "No longer supports dates that are more than 10 days in the future";

    public void main(String[] args) throws Exception {
        String place = args[0];
        String date = args[1];

        if (isWithinWindow(date)){
            System.out.println(MidDayTide(place, date));
        }
        else{
            System.out.println(OUTSIDE_WINDOW);
        }
    }

    protected BigDecimal MidDayTide(String place, String date)
            throws IOException {

            TideTimeHeight[] lowAndHighTides = tideAPIAdapter.getLowAndHighTides(
                    tideAPIAdapter.getTideTimesString(place, date));
            return interpolateTideHeight(lowAndHighTides[0], lowAndHighTides[1]);
    }

    protected boolean isWithinWindow(String date) throws ParseException {
        return queryClock.DaysFromToday(date) <=10;
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

    static class TideTimeHeight {
        final LocalTime localTime;
        final BigDecimal tideHeight;
        TideTimeHeight(LocalTime localTime, BigDecimal tideHeight) {
            this.localTime = localTime;
            this.tideHeight = tideHeight; }
    }
}
