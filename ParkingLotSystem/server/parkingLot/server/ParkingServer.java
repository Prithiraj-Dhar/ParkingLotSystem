package parkingLot.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import src.main.java.parkingLot.ParkingLot;
import src.main.java.parkingLot.Ticket;

import src.main.java.parkingLot.fare.BaseFareStrategy;
import src.main.java.parkingLot.fare.FareCalculator;
import src.main.java.parkingLot.fare.FareStrategy;
import src.main.java.parkingLot.fare.PeakHoursFareStrategy;

import src.main.java.parkingLot.spot.CompactSpot;
import src.main.java.parkingLot.spot.OversizedSpot;
import src.main.java.parkingLot.spot.ParkingManager;
import src.main.java.parkingLot.spot.ParkingSpot;
import src.main.java.parkingLot.spot.RegularSpot;

import src.main.java.parkingLot.vehicle.Car;
import src.main.java.parkingLot.vehicle.Motorcycle;
import src.main.java.parkingLot.vehicle.Truck;
import src.main.java.parkingLot.vehicle.Vehicle;
import src.main.java.parkingLot.vehicle.VehicleSize;

import java.io.IOException;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.net.InetSocketAddress;
import java.net.URI;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Core Java HTTP Server
 *
 * No Spring Boot
 * No Express
 * No backend framework
 *
 * Server:
 * http://localhost:8080
 *
 * API:
 *
 * POST /api/parking/enter
 * POST /api/parking/exit
 * GET  /api/parking/spots
 * GET  /api/parking/vehicles
 */
public class ParkingServer {

    // =========================================================
    // Configuration
    // =========================================================

    // Most cloud hosts (Render, Railway, Fly, Heroku) assign a port
    // at runtime via the PORT env var and route external traffic to
    // it - a hardcoded port will fail to bind there. Falls back to
    // 8080 for local development.
    private static final int PORT =
            System.getenv("PORT") != null
                    ? Integer.parseInt(System.getenv("PORT"))
                    : 8080;


    // =========================================================
    // Backend Components
    // =========================================================

    private static ParkingLot parkingLot;

    private static ParkingManager parkingManager;

    private static FareCalculator fareCalculator;


    // =========================================================
    // Parking Data
    // =========================================================

    private static final Map<VehicleSize, List<ParkingSpot>>
            availableSpots = new HashMap<>();


    /**
     * Tickets currently active in the parking lot.
     *
     * Key   = Ticket ID
     * Value = Ticket
     */
    private static final Map<String, Ticket>
            activeTickets = new HashMap<>();


    /**
     * All parking spots.
     *
     * We keep this list so that /spots can return
     * both available and occupied spots.
     */
    private static final List<ParkingSpot>
            allParkingSpots = new ArrayList<>();


    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) throws Exception {

        System.out.println();
        System.out.println(
                "=============================================="
        );

        System.out.println(
                "       PARKING LOT MANAGEMENT SERVER"
        );

        System.out.println(
                "=============================================="
        );


        // -----------------------------------------------------
        // Initialize backend
        // -----------------------------------------------------

        initializeParkingSystem();


        // -----------------------------------------------------
        // Create HTTP server
        // -----------------------------------------------------

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(PORT),
                        0
                );


        // -----------------------------------------------------
        // Register API endpoints
        // -----------------------------------------------------

        server.createContext(
                "/api/parking/enter",
                ParkingServer::handleEnterVehicle
        );

        server.createContext(
                "/api/parking/exit",
                ParkingServer::handleExitVehicle
        );

        server.createContext(
                "/api/parking/spots",
                ParkingServer::handleParkingSpots
        );

        server.createContext(
                "/api/parking/vehicles",
                ParkingServer::handleVehicles
        );


        // -----------------------------------------------------
        // Root endpoint
        // -----------------------------------------------------

        server.createContext(
                "/",
                ParkingServer::handleRoot
        );


        // -----------------------------------------------------
        // Start server
        // -----------------------------------------------------

        server.start();


        System.out.println();
        System.out.println(
                "Server started successfully!"
        );

        System.out.println(
                "Server URL:"
        );

        System.out.println(
                "http://localhost:" + PORT
        );

        System.out.println();

        System.out.println(
                "Available API endpoints:"
        );

        System.out.println(
                "POST http://localhost:" +
                        PORT +
                        "/api/parking/enter"
        );

        System.out.println(
                "POST http://localhost:" +
                        PORT +
                        "/api/parking/exit"
        );

        System.out.println(
                "GET  http://localhost:" +
                        PORT +
                        "/api/parking/spots"
        );

        System.out.println(
                "GET  http://localhost:" +
                        PORT +
                        "/api/parking/vehicles"
        );

        System.out.println();

        System.out.println(
                "Keep this program running."
        );

        System.out.println(
                "=============================================="
        );
    }


    // =========================================================
    // INITIALIZE PARKING SYSTEM
    // =========================================================

    private static void initializeParkingSystem() {

        System.out.println(
                "Initializing parking system..."
        );


        // -----------------------------------------------------
        // Create parking spots
        // -----------------------------------------------------

        availableSpots.put(
                VehicleSize.SMALL,
                new ArrayList<>()
        );

        availableSpots.put(
                VehicleSize.MEDIUM,
                new ArrayList<>()
        );

        availableSpots.put(
                VehicleSize.LARGE,
                new ArrayList<>()
        );


        // Compact spots (for Motorcycles)
        ParkingSpot compact1 =
                new CompactSpot(1);

        ParkingSpot compact2 =
                new CompactSpot(2);

        ParkingSpot compact3 =
                new CompactSpot(3);

        ParkingSpot compact4 =
                new CompactSpot(4);

        ParkingSpot compact5 =
                new CompactSpot(5);

        availableSpots
                .get(VehicleSize.SMALL)
                .add(compact1);

        availableSpots
                .get(VehicleSize.SMALL)
                .add(compact2);

        availableSpots
                .get(VehicleSize.SMALL)
                .add(compact3);

        availableSpots
                .get(VehicleSize.SMALL)
                .add(compact4);

        availableSpots
                .get(VehicleSize.SMALL)
                .add(compact5);


        // Regular spots (for Cars)
        ParkingSpot spot1 =
                new RegularSpot(6);

        ParkingSpot spot2 =
                new RegularSpot(7);

        ParkingSpot spot3 =
                new RegularSpot(8);

        ParkingSpot spot4 =
                new RegularSpot(9);

        ParkingSpot spot5 =
                new RegularSpot(10);


        availableSpots
                .get(VehicleSize.MEDIUM)
                .add(spot1);

        availableSpots
                .get(VehicleSize.MEDIUM)
                .add(spot2);

        availableSpots
                .get(VehicleSize.MEDIUM)
                .add(spot3);

        availableSpots
                .get(VehicleSize.MEDIUM)
                .add(spot4);

        availableSpots
                .get(VehicleSize.MEDIUM)
                .add(spot5);


        // Oversized spots (for Trucks)
        ParkingSpot oversized1 =
                new OversizedSpot(11);

        ParkingSpot oversized2 =
                new OversizedSpot(12);

        ParkingSpot oversized3 =
                new OversizedSpot(13);

        ParkingSpot oversized4 =
                new OversizedSpot(14);

        ParkingSpot oversized5 =
                new OversizedSpot(15);

        availableSpots
                .get(VehicleSize.LARGE)
                .add(oversized1);

        availableSpots
                .get(VehicleSize.LARGE)
                .add(oversized2);

        availableSpots
                .get(VehicleSize.LARGE)
                .add(oversized3);

        availableSpots
                .get(VehicleSize.LARGE)
                .add(oversized4);

        availableSpots
                .get(VehicleSize.LARGE)
                .add(oversized5);


        allParkingSpots.add(compact1);
        allParkingSpots.add(compact2);
        allParkingSpots.add(compact3);
        allParkingSpots.add(compact4);
        allParkingSpots.add(compact5);
        allParkingSpots.add(spot1);
        allParkingSpots.add(spot2);
        allParkingSpots.add(spot3);
        allParkingSpots.add(spot4);
        allParkingSpots.add(spot5);
        allParkingSpots.add(oversized1);
        allParkingSpots.add(oversized2);
        allParkingSpots.add(oversized3);
        allParkingSpots.add(oversized4);
        allParkingSpots.add(oversized5);


        // -----------------------------------------------------
        // Parking manager
        // -----------------------------------------------------

        parkingManager =
                new ParkingManager(
                        availableSpots
                );


        // -----------------------------------------------------
        // Fare strategies
        // -----------------------------------------------------

        List<FareStrategy> strategies =
                List.of(
                        new BaseFareStrategy(),
                        new PeakHoursFareStrategy()
                );


        fareCalculator =
                new FareCalculator(
                        strategies
                );


        // -----------------------------------------------------
        // Parking lot
        // -----------------------------------------------------

        parkingLot =
                new ParkingLot(
                        parkingManager,
                        fareCalculator
                );


        System.out.println(
                "Parking system initialized."
        );

        System.out.println(
                "Parking spots: " +
                        allParkingSpots.size()
        );

    }


    // =========================================================
    // ROOT ENDPOINT
    // =========================================================

    /**
     * Directory that holds index.html, css/, js/ - the frontend
     * this same server now serves directly, so the whole app lives
     * behind one URL/one origin (no CORS, no separate frontend host
     * to deploy or point at the right backend URL).
     *
     * Resolved relative to wherever the JVM is started from. In this
     * project's layout that's the ParkingLotSystem/ folder, which
     * contains backend/, frontend/, and server/ as siblings - so
     * "frontend" resolves correctly whether run locally or in the
     * Docker image (see Dockerfile, which sets the same WORKDIR).
     * Overridable via FRONTEND_DIR for other layouts/hosts.
     */
    private static final Path FRONTEND_DIR =
            Paths.get(
                    System.getenv("FRONTEND_DIR") != null
                            ? System.getenv("FRONTEND_DIR")
                            : "frontend"
            ).toAbsolutePath().normalize();

    private static void handleRoot(
            HttpExchange exchange
    ) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            sendJson(
                    exchange,
                    405,
                    """
                    {
                        "error": "Method Not Allowed"
                    }
                    """
            );

            return;
        }

        String requestPath = exchange.getRequestURI().getPath();

        // Default to index.html for "/" and any unknown client route
        if (requestPath.equals("/") || requestPath.isBlank()) {
            requestPath = "/index.html";
        }

        // Resolve against the frontend directory, guarding against
        // "../" path traversal outside of it
        Path filePath =
                FRONTEND_DIR
                        .resolve("." + requestPath)
                        .normalize();

        if (!filePath.startsWith(FRONTEND_DIR)
                || !Files.exists(filePath)
                || Files.isDirectory(filePath)) {

            // Not a real static file - fall back to index.html so
            // client-side routes (if any are ever added) still load
            filePath = FRONTEND_DIR.resolve("index.html");

            if (!Files.exists(filePath)) {

                sendJson(
                        exchange,
                        404,
                        """
                        {
                            "error": "Not found."
                        }
                        """
                );

                return;
            }
        }

        byte[] fileBytes = Files.readAllBytes(filePath);

        exchange.getResponseHeaders().set(
                "Content-Type",
                contentTypeFor(filePath)
        );

        exchange.sendResponseHeaders(200, fileBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(fileBytes);
        }
    }

    private static String contentTypeFor(Path filePath) {

        String name = filePath.getFileName().toString();

        if (name.endsWith(".html")) return "text/html; charset=UTF-8";
        if (name.endsWith(".css")) return "text/css; charset=UTF-8";
        if (name.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (name.endsWith(".json")) return "application/json; charset=UTF-8";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".ico")) return "image/x-icon";

        return "application/octet-stream";
    }


    // =========================================================
    // ENTER VEHICLE
    // =========================================================

    private static void handleEnterVehicle(
            HttpExchange exchange
    ) throws IOException {

        // -----------------------------------------------------
        // CORS
        // -----------------------------------------------------

        addCorsHeaders(exchange);

        if (handlePreflight(exchange)) {
            return;
        }


        // -----------------------------------------------------
        // Only POST allowed
        // -----------------------------------------------------

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            sendJson(
                    exchange,
                    405,
                    """
                    {
                        "error": "Only POST method is allowed."
                    }
                    """
            );

            return;
        }


        try {

            // -------------------------------------------------
            // Read request body
            // -------------------------------------------------

            String body =
                    readRequestBody(exchange);


            System.out.println();
            System.out.println(
                    "ENTER REQUEST:"
            );

            System.out.println(body);


            // -------------------------------------------------
            // Extract JSON values
            // -------------------------------------------------

            String licensePlate =
                    getJsonValue(
                            body,
                            "licensePlate"
                    );

            String vehicleType =
                    getJsonValue(
                            body,
                            "vehicleType"
                    );


            // -------------------------------------------------
            // Validate request
            // -------------------------------------------------

            if (licensePlate == null ||
                    licensePlate.isBlank()) {

                sendJson(
                        exchange,
                        400,
                        """
                        {
                            "error": "License plate is required."
                        }
                        """
                );

                return;
            }


            if (vehicleType == null ||
                    vehicleType.isBlank()) {

                sendJson(
                        exchange,
                        400,
                        """
                        {
                            "error": "Vehicle type is required."
                        }
                        """
                );

                return;
            }


            licensePlate =
                    licensePlate.trim()
                            .toUpperCase();

            vehicleType =
                    vehicleType.trim()
                            .toUpperCase();


            // -------------------------------------------------
            // Check duplicate vehicle
            // -------------------------------------------------

            for (Ticket ticket :
                    activeTickets.values()) {

                if (ticket.getVehicle()
                        .getLicensePlate()
                        .equalsIgnoreCase(
                                licensePlate
                        )) {

                    sendJson(
                            exchange,
                            409,
                            """
                            {
                                "error": "Vehicle is already parked."
                            }
                            """
                    );

                    return;
                }
            }


            // -------------------------------------------------
            // Create Vehicle
            // -------------------------------------------------

            Vehicle vehicle;


            switch (vehicleType) {

                case "CAR":

                    vehicle =
                            new Car(
                                    licensePlate
                            );

                    break;


                case "MOTORCYCLE":

                    vehicle =
                            new Motorcycle(
                                    licensePlate
                            );

                    break;


                case "TRUCK":

                    vehicle =
                            new Truck(
                                    licensePlate
                            );

                    break;


                default:

                    sendJson(
                            exchange,
                            400,
                            """
                            {
                                "error": "Invalid vehicle type. Use CAR, MOTORCYCLE or TRUCK."
                            }
                            """
                    );

                    return;
            }


            // -------------------------------------------------
            // Enter parking lot
            // -------------------------------------------------

            Ticket ticket =
                    parkingLot.enterVehicle(
                            vehicle
                    );


            // -------------------------------------------------
            // Check ticket
            // -------------------------------------------------

            if (ticket == null) {

                sendJson(
                        exchange,
                        409,
                        """
                        {
                            "error": "No suitable parking spot available."
                        }
                        """
                );

                return;
            }


            // -------------------------------------------------
            // Use the ticket's own ID (single source of truth,
            // generated once inside ParkingLot/Ticket)
            // -------------------------------------------------

            String ticketId =
                    ticket.getTicketId();


            // -------------------------------------------------
            // Store active ticket
            // -------------------------------------------------

            activeTickets.put(
                    ticketId,
                    ticket
            );


            // -------------------------------------------------
            // Get parking spot
            // -------------------------------------------------

            ParkingSpot parkingSpot =
                    ticket.getParkingSpot();


            int spotNumber =
                    parkingSpot.getSpotNumber();


            // -------------------------------------------------
            // Response
            // -------------------------------------------------

            String response =
                    "{"
                            + "\"ticketId\":\""
                            + escapeJson(ticketId)
                            + "\","

                            + "\"licensePlate\":\""
                            + escapeJson(licensePlate)
                            + "\","

                            + "\"vehicleType\":\""
                            + escapeJson(vehicleType)
                            + "\","

                            + "\"spotNumber\":"
                            + spotNumber

                            + "}";


            System.out.println(
                    "Vehicle entered successfully."
            );

            System.out.println(
                    "Ticket ID: " + ticketId
            );

            System.out.println(
                    "Spot: " + spotNumber
            );


            sendJson(
                    exchange,
                    201,
                    response
            );

        }

        catch (Exception exception) {

            exception.printStackTrace();

            sendJson(
                    exchange,
                    500,
                    "{"
                            + "\"error\":\""
                            + escapeJson(
                            exception.getMessage() != null
                                    ? exception.getMessage()
                                    : "Internal server error."
                    )
                            + "\""
                            + "}"
            );

        }

    }


    // =========================================================
    // EXIT VEHICLE
    // =========================================================

    private static void handleExitVehicle(
            HttpExchange exchange
    ) throws IOException {

        addCorsHeaders(exchange);

        if (handlePreflight(exchange)) {
            return;
        }


        // -----------------------------------------------------
        // Only POST
        // -----------------------------------------------------

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            sendJson(
                    exchange,
                    405,
                    """
                    {
                        "error": "Only POST method is allowed."
                    }
                    """
            );

            return;
        }


        try {

            // -------------------------------------------------
            // Read request
            // -------------------------------------------------

            String body =
                    readRequestBody(exchange);


            System.out.println();
            System.out.println(
                    "EXIT REQUEST:"
            );

            System.out.println(body);


            // -------------------------------------------------
            // Ticket ID
            // -------------------------------------------------

            String ticketId =
                    getJsonValue(
                            body,
                            "ticketId"
                    );


            if (ticketId == null ||
                    ticketId.isBlank()) {

                sendJson(
                        exchange,
                        400,
                        """
                        {
                            "error": "Ticket ID is required."
                        }
                        """
                );

                return;
            }


            // -------------------------------------------------
            // Find ticket
            // -------------------------------------------------

            Ticket ticket =
                    activeTickets.get(
                            ticketId
                    );


            if (ticket == null) {

                sendJson(
                        exchange,
                        404,
                        """
                        {
                            "error": "Ticket not found."
                        }
                        """
                );

                return;
            }


            // -------------------------------------------------
            // Get spot before leaving
            // -------------------------------------------------

            ParkingSpot parkingSpot =
                    ticket.getParkingSpot();


            int spotNumber =
                    parkingSpot.getSpotNumber();


            // -------------------------------------------------
            // Leave parking lot (calculates and stores the fare
            // on the ticket, and returns it here too)
            // -------------------------------------------------

            BigDecimal fare =
                    parkingLot.leaveVehicle(
                            ticket
                    );


            // -------------------------------------------------
            // Parking duration (in minutes)
            // -------------------------------------------------

            BigDecimal parkingDurationMinutes =
                    ticket.calculateParkingDuration();


            // -------------------------------------------------
            // Remove active ticket
            // -------------------------------------------------

            activeTickets.remove(
                    ticketId
            );


            // -------------------------------------------------
            // Response
            // -----------------------------------------------------

            String response =
                    "{"
                            + "\"ticketId\":\""
                            + escapeJson(ticketId)
                            + "\","

                            + "\"licensePlate\":\""
                            + escapeJson(
                            ticket.getVehicle()
                                    .getLicensePlate()
                    )
                            + "\","

                            + "\"spotNumber\":"
                            + spotNumber
                            + ","

                            + "\"exitTime\":\""
                            + escapeJson(
                            String.valueOf(
                                    ticket.getExitTime()
                            )
                    )
                            + "\","

                            + "\"parkingDuration\":"
                            + parkingDurationMinutes
                            + ","

                            + "\"fare\":"
                            + fare

                            + "}";


            System.out.println(
                    "Vehicle exited successfully."
            );

            System.out.println(
                    "Ticket ID: " + ticketId
            );

            System.out.println(
                    "Spot: " + spotNumber
            );

            System.out.println(
                    "Fare: " + fare
            );


            sendJson(
                    exchange,
                    200,
                    response
            );

        }

        catch (Exception exception) {

            exception.printStackTrace();

            sendJson(
                    exchange,
                    500,
                    "{"
                            + "\"error\":\""
                            + escapeJson(
                            exception.getMessage() != null
                                    ? exception.getMessage()
                                    : "Internal server error."
                    )
                            + "\""
                            + "}"
            );

        }

    }


    // =========================================================
    // GET PARKING SPOTS
    // =========================================================

    private static void handleParkingSpots(
            HttpExchange exchange
    ) throws IOException {

        addCorsHeaders(exchange);

        if (handlePreflight(exchange)) {
            return;
        }


        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            sendJson(
                    exchange,
                    405,
                    """
                    {
                        "error": "Only GET method is allowed."
                    }
                    """
            );

            return;
        }


        try {

            StringBuilder json =
                    new StringBuilder();

            json.append("[");


            for (
                    int i = 0;
                    i < allParkingSpots.size();
                    i++
            ) {

                ParkingSpot spot =
                        allParkingSpots.get(i);


                if (i > 0) {
                    json.append(",");
                }


                json.append("{");

                json.append(
                        "\"spotNumber\":"
                );

                json.append(
                        spot.getSpotNumber()
                );

                json.append(",");

                json.append(
                        "\"available\":"
                );

                json.append(
                        spot.isAvailable()
                );

                json.append("}");

            }


            json.append("]");


            sendJson(
                    exchange,
                    200,
                    json.toString()
            );

        }

        catch (Exception exception) {

            exception.printStackTrace();

            sendJson(
                    exchange,
                    500,
                    """
                    {
                        "error": "Unable to retrieve parking spots."
                    }
                    """
            );

        }

    }


    // =========================================================
    // GET PARKED VEHICLES
    // =========================================================

    private static void handleVehicles(
            HttpExchange exchange
    ) throws IOException {

        addCorsHeaders(exchange);

        if (handlePreflight(exchange)) {
            return;
        }


        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {

            sendJson(
                    exchange,
                    405,
                    """
                    {
                        "error": "Only GET method is allowed."
                    }
                    """
            );

            return;
        }


        try {

            StringBuilder json =
                    new StringBuilder();

            json.append("[");


            int index = 0;


            for (
                    Map.Entry<String, Ticket> entry :
                    activeTickets.entrySet()
            ) {

                String ticketId =
                        entry.getKey();

                Ticket ticket =
                        entry.getValue();


                if (index > 0) {
                    json.append(",");
                }


                json.append("{");


                json.append(
                        "\"ticketId\":\""
                );

                json.append(
                        escapeJson(ticketId)
                );

                json.append("\",");


                json.append(
                        "\"licensePlate\":\""
                );

                json.append(
                        escapeJson(
                                ticket.getVehicle()
                                        .getLicensePlate()
                        )
                );

                json.append("\",");


                json.append(
                        "\"spotNumber\":"
                );

                json.append(
                        ticket.getParkingSpot()
                                .getSpotNumber()
                );


                json.append("}");


                index++;

            }


            json.append("]");


            sendJson(
                    exchange,
                    200,
                    json.toString()
            );

        }

        catch (Exception exception) {

            exception.printStackTrace();

            sendJson(
                    exchange,
                    500,
                    """
                    {
                        "error": "Unable to retrieve vehicles."
                    }
                    """
            );

        }

    }


    // =========================================================
    // READ REQUEST BODY
    // =========================================================

    private static String readRequestBody(
            HttpExchange exchange
    ) throws IOException {

        return new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

    }


    // =========================================================
    // SIMPLE JSON VALUE READER
    // =========================================================

    /**
     * This is intentionally a tiny JSON reader so that
     * the project does not need Gson, Jackson or another
     * external JSON library.
     *
     * Expected input:
     *
     * {
     *     "licensePlate": "ABC123",
     *     "vehicleType": "CAR"
     * }
     */
    private static String getJsonValue(
            String json,
            String key
    ) {

        if (json == null) {
            return null;
        }


        String searchKey =
                "\"" + key + "\"";


        int keyIndex =
                json.indexOf(searchKey);


        if (keyIndex == -1) {
            return null;
        }


        int colonIndex =
                json.indexOf(
                        ":",
                        keyIndex
                );


        if (colonIndex == -1) {
            return null;
        }


        int firstQuote =
                json.indexOf(
                        "\"",
                        colonIndex
                );


        if (firstQuote == -1) {
            return null;
        }


        int secondQuote =
                json.indexOf(
                        "\"",
                        firstQuote + 1
                );


        if (secondQuote == -1) {
            return null;
        }


        return json.substring(
                firstQuote + 1,
                secondQuote
        );

    }


    // =========================================================
    // SEND JSON RESPONSE
    // =========================================================

    private static void sendJson(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        addCorsHeaders(exchange);

        if (handlePreflight(exchange)) {
            return;
        }


        byte[] responseBytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        Headers headers =
                exchange.getResponseHeaders();


        headers.set(
                "Content-Type",
                "application/json; charset=UTF-8"
        );


        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );


        try (
                OutputStream output =
                        exchange.getResponseBody()
        ) {

            output.write(
                    responseBytes
            );

        }

    }


    // =========================================================
    // CORS PREFLIGHT
    // =========================================================

    /**
     * Browsers send an OPTIONS "preflight" request before any
     * cross-origin POST that carries a Content-Type: application/json
     * header. If we don't answer OPTIONS successfully, the browser
     * blocks the real request and it never reaches this server -
     * even though direct tools like curl (which skip preflight)
     * would appear to work fine.
     *
     * Call this right after addCorsHeaders(exchange) in every
     * handler. If it returns true, the OPTIONS request has already
     * been answered and the caller should return immediately.
     */
    private static boolean handlePreflight(
            HttpExchange exchange
    ) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            return true;
        }

        return false;
    }


    // =========================================================
    // CORS
    // =========================================================

    private static void addCorsHeaders(
            HttpExchange exchange
    ) {

        Headers headers =
                exchange.getResponseHeaders();


        headers.set(
                "Access-Control-Allow-Origin",
                "*"
        );


        headers.set(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );


        headers.set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );

    }


    // =========================================================
    // JSON ESCAPE
    // =========================================================

    private static String escapeJson(
            String value
    ) {

        if (value == null) {
            return "";
        }


        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                );

    }

}