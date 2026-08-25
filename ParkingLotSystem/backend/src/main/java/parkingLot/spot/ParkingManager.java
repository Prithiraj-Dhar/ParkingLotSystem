package src.main.java.parkingLot.spot;

import src.main.java.parkingLot.vehicle.Vehicle;
import src.main.java.parkingLot.vehicle.VehicleSize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingManager {

    private final Map<VehicleSize, List<ParkingSpot>> availableSpots;
    private final Map<Vehicle, ParkingSpot> vehicleToSpotMap;

    // Create Parking Manager based on a given map of available spots
    public ParkingManager(Map<VehicleSize, List<ParkingSpot>> availableSpots) {
        this.availableSpots = availableSpots;
        this.vehicleToSpotMap = new HashMap<>();
    }

    public ParkingSpot findSpotForVehicle(Vehicle vehicle) {

        // Get the size of the actual vehicle
        VehicleSize vehicleSize = vehicle.getSize();

        // Start looking from the smallest spot
        // that can fit the vehicle
        for (VehicleSize size : VehicleSize.values()) {

            if (size.ordinal() >= vehicleSize.ordinal()) {

                List<ParkingSpot> spots = availableSpots.get(size);

                if (spots == null) {
                    continue;
                }

                for (ParkingSpot spot : spots) {

                    if (spot.isAvailable()) {
                        return spot;
                    }
                }
            }
        }

        // No suitable spot found
        return null;
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {

        ParkingSpot spot = findSpotForVehicle(vehicle);

        if (spot != null) {

            // Occupy the parking spot
            spot.occupy(vehicle);

            // Record the parking spot for the vehicle
            vehicleToSpotMap.put(vehicle, spot);

            // Remove the spot from available spots
            availableSpots.get(spot.getSize()).remove(spot);

            return spot;
        }

        // No spot available
        return null;
    }

    public void unparkVehicle(Vehicle vehicle) {

        ParkingSpot spot = vehicleToSpotMap.remove(vehicle);

        if (spot != null) {

            // Make the spot available again
            spot.vacate();

            // Add it back to available spots
            availableSpots.get(spot.getSize()).add(spot);
        }
    }

    // Used for testing
    public ParkingSpot findVehicleSpot(Vehicle vehicle) {
        return vehicleToSpotMap.get(vehicle);
    }
}