package src.main.java.parkingLot.fare;

import src.main.java.parkingLot.Ticket;

import java.math.BigDecimal;

public interface FareStrategy {

    BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare);
}