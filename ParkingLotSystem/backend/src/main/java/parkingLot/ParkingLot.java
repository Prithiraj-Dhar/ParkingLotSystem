package src.main.java.parkingLot;

import src.main.java.parkingLot.fare.FareCalculator;
import src.main.java.parkingLot.spot.ParkingManager;
import src.main.java.parkingLot.spot.ParkingSpot;
import src.main.java.parkingLot.vehicle.Vehicle;


import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class ParkingLot {

    // Monotonically increasing counter so concurrent entries
    // in the same millisecond can't collide on ticket ID.
    private static final AtomicLong TICKET_SEQUENCE = new AtomicLong(1);
    private final ParkingManager parkingManager; // Manages parking spots and vehicle assignments
    private final FareCalculator fareCalculator; // Calculates fare for parking sessions

    public ParkingLot(ParkingManager parkingManager, FareCalculator fareCalculator) {
        this.parkingManager = parkingManager;
        this.fareCalculator = fareCalculator;
    }

    // Method to handle vehicle entry into the parking lot
    public Ticket enterVehicle(Vehicle vehicle) {
        // Delegate parking logic to ParkingManager
        ParkingSpot spot = parkingManager.parkVehicle(vehicle);
        
        if (spot != null) {
            // Create ticket with entry time
            Ticket ticket = new Ticket(generateTicketId(), vehicle, spot, LocalDateTime.now());
            return ticket;
        } else {
            return null;  // No spot available
        }
    }

    // Method to handle vehicle exit from the parking lot using the main.parkinglot.fare.Ticket object
    // Returns the calculated fare, or null if the ticket was invalid / already exited.
    public BigDecimal leaveVehicle(Ticket ticket) {
        if (ticket != null && ticket.getExitTime() == null) {  // Ensure the ticket is valid and the vehicle hasn't already left
            // Set exit time
            ticket.setExitTime(LocalDateTime.now());

            // Calculate the fare BEFORE unparking, so fare strategies
            // could safely inspect spot/vehicle state if ever needed.
            BigDecimal fare = fareCalculator.calculateFare(ticket);
            ticket.setFare(fare);

            // Delegate unparking logic to ParkingManager
            parkingManager.unparkVehicle(ticket.getVehicle());

            return fare;
        } else {
            // Invalid ticket or vehicle already exited.
            return null;
        }
    }

    // Helper method to generate a unique ticket ID
    private String generateTicketId() {
        return "T" + TICKET_SEQUENCE.getAndIncrement();
    }
}
