package src.main.java.parkingLot;

import src.main.java.parkingLot.spot.ParkingSpot;
import src.main.java.parkingLot.vehicle.Vehicle;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {
    private final String ticketId;         // Unique ticket identifier
    private final Vehicle vehicle;         // The vehicle associated with the ticket
    private final ParkingSpot parkingSpot; // The parking spot where the vehicle is parked
    private final LocalDateTime entryTime; // The time the vehicle entered the parking lot
    private LocalDateTime exitTime;  // The time the vehicle exited the parking lot (null if vehicle is still parked)
    private BigDecimal fare;         // The final calculated fare (null until the vehicle exits)

    // Constructor to initialize a new ticket
    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot parkingSpot, LocalDateTime entryTime) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.entryTime = entryTime;
        this.exitTime = null;  // Initially, exitTime is null because the vehicle is still parked
    }


    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    public BigDecimal calculateParkingDuration() {
        return new BigDecimal(
                Duration.between(entryTime, Objects.requireNonNullElseGet(exitTime, LocalDateTime::now))
                        .toMinutes());
    }
}
