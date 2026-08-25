package src.main.java.parkingLot.fare;

import src.main.java.parkingLot.Ticket;

import java.math.BigDecimal;

public class BaseFareStrategy implements FareStrategy {

    private static final BigDecimal SMALL_VEHICLE_RATE =
            new BigDecimal("1.0");

    private static final BigDecimal MEDIUM_VEHICLE_RATE =
            new BigDecimal("2.0");

    private static final BigDecimal LARGE_VEHICLE_RATE =
            new BigDecimal("3.0");

    @Override
    public BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare) {

        BigDecimal rate;

        switch (ticket.getVehicle().getSize()) {

            case SMALL:
                rate = SMALL_VEHICLE_RATE;
                break;

            case MEDIUM:
                rate = MEDIUM_VEHICLE_RATE;
                break;

            case LARGE:
                rate = LARGE_VEHICLE_RATE;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown vehicle size"
                );
        }

        return inputFare.add(
                rate.multiply(ticket.calculateParkingDuration())
        );
    }
}