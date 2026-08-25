package src.main.java.parkingLot.fare;

import src.main.java.parkingLot.Ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PeakHoursFareStrategy implements FareStrategy {

    private static final BigDecimal PEAK_HOURS_MULTIPLIER =
            new BigDecimal("1.5");

    @Override
    public BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare) {

        if (isPeakHours(ticket.getEntryTime())) {
            return inputFare.multiply(PEAK_HOURS_MULTIPLIER);
        }

        return inputFare;
    }

    private boolean isPeakHours(LocalDateTime time) {

        int hour = time.getHour();

        // Morning peak: 7:00 - 10:59
        // Evening peak: 16:00 - 19:59
        return (hour >= 7 && hour <= 10)
                || (hour >= 16 && hour <= 19);
    }
}