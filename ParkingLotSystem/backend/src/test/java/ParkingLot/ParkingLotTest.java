package src.test.java.parkingLot;

import org.junit.jupiter.api.Test;
import src.main.java.parkingLot.ParkingLot;
import src.main.java.parkingLot.Ticket;
import src.main.java.parkingLot.fare.BaseFareStrategy;
import src.main.java.parkingLot.fare.FareCalculator;
import src.main.java.parkingLot.fare.FareStrategy;
import src.main.java.parkingLot.fare.PeakHoursFareStrategy;
import src.main.java.parkingLot.spot.ParkingManager;
import src.main.java.parkingLot.spot.ParkingSpot;
import src.main.java.parkingLot.spot.RegularSpot;
import src.main.java.parkingLot.vehicle.Car;
import src.main.java.parkingLot.vehicle.Vehicle;
import src.main.java.parkingLot.vehicle.VehicleSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotTest {

    @Test
    public void testVehicleJourney() {

        System.out.println(
                "\n=== Testing Parking Lot System: Complete Vehicle Journey ==="
        );

        // --------------------------------------------------
        // Setting Up Parking Spots
        // --------------------------------------------------

        System.out.println("\n--- Setting Up Parking Spots ---");

        Map<VehicleSize, List<ParkingSpot>> availableSpots =
                new HashMap<>();

        availableSpots.put(
                VehicleSize.MEDIUM,
                new ArrayList<>()
        );

        availableSpots.get(VehicleSize.MEDIUM)
                .add(new RegularSpot(1));

        availableSpots.get(VehicleSize.MEDIUM)
                .add(new RegularSpot(2));

        System.out.println(
                "✓ Created 2 regular parking spots for medium-sized vehicles"
        );

        System.out.println("  - Spot 1: Regular parking spot");
        System.out.println("  - Spot 2: Regular parking spot");

        // --------------------------------------------------
        // Initializing Parking Manager
        // --------------------------------------------------

        System.out.println("\n--- Initializing Parking Manager ---");

        ParkingManager parkingManager =
                new ParkingManager(availableSpots);

        System.out.println(
                "✓ Parking manager initialized with available spots"
        );

        // --------------------------------------------------
        // Setting Up Fare Calculation System
        // --------------------------------------------------

        System.out.println("\n--- Setting Up Fare Calculation System ---");

        List<FareStrategy> strategies = new ArrayList<>(
                List.of(
                        new BaseFareStrategy(),
                        new PeakHoursFareStrategy()
                )
        );

        FareCalculator fareCalculator =
                new FareCalculator(strategies);

        System.out.println(
                "✓ Fare calculator initialized with multiple strategies:"
        );

        System.out.println("  - Base fare strategy");
        System.out.println("  - Peak hours fare strategy");

        // --------------------------------------------------
        // Creating Parking Lot
        // --------------------------------------------------

        ParkingLot parkingLot =
                new ParkingLot(parkingManager, fareCalculator);

        // --------------------------------------------------
        // Creating Vehicle
        // --------------------------------------------------

        System.out.println("\n--- Creating Test Vehicle ---");

        Vehicle car = new Car("ABC123");

        System.out.println(
                "✓ Created car with license plate: ABC123"
        );

        System.out.println(
                "  - Vehicle type: Car (MEDIUM size)"
        );

        // --------------------------------------------------
        // Vehicle Entering Parking Lot
        // --------------------------------------------------

        System.out.println("\n--- Vehicle Entering Parking Lot ---");

        Ticket ticket = parkingLot.enterVehicle(car);

        assertNotNull(
                ticket,
                "Ticket should not be null"
        );

        assertEquals(
                car,
                ticket.getVehicle(),
                "Vehicle should match the one that entered"
        );

        assertNotNull(
                ticket.getParkingSpot(),
                "Parking spot should not be null"
        );

        System.out.println(
                "✓ Ticket generated for vehicle ABC123"
        );

        System.out.println(
                "✓ Parking spot assigned: "
                        + ticket.getParkingSpot().getSpotNumber()
        );

        System.out.println("✓ Ticket validation passed:");
        System.out.println("  - Ticket is not null");
        System.out.println("  - Vehicle matches the one that entered");
        System.out.println("  - Parking spot assigned successfully");

        // --------------------------------------------------
        // Find Vehicle
        // --------------------------------------------------

        ParkingSpot foundSpot =
                parkingManager.findVehicleSpot(car);

        assertNotNull(
                foundSpot,
                "Vehicle should be found in the parking lot"
        );

        assertEquals(
                ticket.getParkingSpot(),
                foundSpot,
                "Found spot should match the ticket's spot"
        );

        // --------------------------------------------------
        // Vehicle Leaving Parking Lot
        // --------------------------------------------------

        System.out.println("\n--- Vehicle Leaving Parking Lot ---");

        parkingLot.leaveVehicle(ticket);

        assertNotNull(
                ticket.getExitTime(),
                "Exit time should be set"
        );

        assertTrue(
                foundSpot.isAvailable(),
                "Parking spot should be available after vehicle leaves"
        );

        System.out.println(
                "✓ Vehicle exit verification passed:"
        );

        System.out.println(
                "  - Exit time recorded on ticket"
        );

        System.out.println(
                "  - Parking spot is now available for other vehicles"
        );

        System.out.println(
                "=== Parking Lot Vehicle Journey Test Completed Successfully ===\n"
        );
    }
}