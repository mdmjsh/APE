package com.oocode;

import java.io.IOException;
import java.math.*;
import java.time.*;


public class TideCalculator {


    private TideAPIAdapter tideAPIAdapter = initTideAPIAdapter();
    private QueryClock queryClock = initQueryClock();

    protected TideAPIAdapter initTideAPIAdapter(){
        return new TideAPIAdapter();
    }
    protected QueryClock initQueryClock() { return new QueryClock();
    }

    public void main(String[] args) throws Exception {
        System.out.println(MidDayTide("Folkestone", "12-01-2020"));
    }

    protected BigDecimal MidDayTide(String place, String date)
            throws IOException {
        TideTimeHeight[] lowAndHighTides = tideAPIAdapter.getLowAndHighTides(
                tideAPIAdapter.getTideTimesString(place, date));
        return interpolateTideHeight(lowAndHighTides[0], lowAndHighTides[1]);
    }

    protected boolean isWithinWindow(String date){
        return true;
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
