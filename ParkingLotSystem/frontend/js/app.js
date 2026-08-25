// ============================================================
// Parking Lot Management System
// Frontend JavaScript
// Vanilla JavaScript - No Framework
// ============================================================


// ============================================================
// Configuration
// ============================================================

// Relative path - the backend now serves this frontend itself,
// so API calls stay on the same origin no matter where it's deployed.
const API_BASE_URL = "/api/parking";


// ============================================================
// DOM Elements
// ============================================================

const entryForm = document.getElementById("entryForm");

const exitForm = document.getElementById("exitForm");

const licensePlateInput =
    document.getElementById("licensePlate");

const vehicleTypeInput =
    document.getElementById("vehicleType");

const exitTicketIdInput =
    document.getElementById("exitTicketId");

const ticketSection =
    document.getElementById("ticketSection");

const fareSection =
    document.getElementById("fareSection");

const parkingGrid =
    document.getElementById("parkingGrid");

const messageElement =
    document.getElementById("message");

const totalSpotsElement =
    document.getElementById("totalSpots");

const availableSpotsElement =
    document.getElementById("availableSpots");

const occupiedSpotsElement =
    document.getElementById("occupiedSpots");


// ============================================================
// Application Initialization
// ============================================================

document.addEventListener("DOMContentLoaded", () => {

    console.log(
        "Parking Lot Management System started."
    );

    loadParkingSpots();

});


// ============================================================
// VEHICLE ENTRY
// ============================================================

if (entryForm) {

    entryForm.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();

            await enterVehicle();

        }
    );

}


// ============================================================
// Enter Vehicle
// ============================================================

async function enterVehicle() {

    const licensePlate =
        licensePlateInput.value.trim();

    const vehicleType =
        vehicleTypeInput.value;

    // ------------------------------------------
    // Validation
    // ------------------------------------------

    if (!licensePlate) {

        showMessage(
            "Please enter a license plate.",
            "error"
        );

        licensePlateInput.focus();

        return;
    }


    if (!vehicleType) {

        showMessage(
            "Please select a vehicle type.",
            "error"
        );

        vehicleTypeInput.focus();

        return;
    }


    // ------------------------------------------
    // Disable button
    // ------------------------------------------

    const submitButton =
        entryForm.querySelector("button[type='submit']");

    setButtonLoading(
        submitButton,
        true,
        "Parking..."
    );


    try {

        // --------------------------------------
        // Send request to Java backend
        // --------------------------------------

        const response = await fetch(
            `${API_BASE_URL}/enter`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    licensePlate: licensePlate,
                    vehicleType: vehicleType
                })
            }
        );


        // --------------------------------------
        // Handle HTTP errors
        // --------------------------------------

        if (!response.ok) {

            const errorMessage =
                await getErrorMessage(response);

            throw new Error(errorMessage);
        }


        // --------------------------------------
        // Read JSON response
        // --------------------------------------

        const ticket =
            await response.json();


        console.log(
            "Vehicle entry response:",
            ticket
        );


        // --------------------------------------
        // Display ticket
        // --------------------------------------

        displayTicket(ticket);


        // --------------------------------------
        // Success message
        // --------------------------------------

        showMessage(
            "Vehicle parked successfully.",
            "success"
        );


        // --------------------------------------
        // Clear form
        // --------------------------------------

        entryForm.reset();


        // --------------------------------------
        // Refresh parking spots
        // --------------------------------------

        await loadParkingSpots();

    }

    catch (error) {

        console.error(
            "Vehicle entry error:",
            error
        );

        showMessage(
            error.message ||
            "Unable to park vehicle.",
            "error"
        );

    }

    finally {

        setButtonLoading(
            submitButton,
            false,
            "Park Vehicle"
        );

    }

}


// ============================================================
// DISPLAY TICKET
// ============================================================

function displayTicket(ticket) {

    if (!ticketSection) {
        return;
    }


    // ------------------------------------------
    // Show ticket section
    // ------------------------------------------

    ticketSection.hidden = false;


    // ------------------------------------------
    // Ticket ID
    // ------------------------------------------

    const ticketIdElement =
        document.getElementById("ticketId");

    if (ticketIdElement) {

        ticketIdElement.textContent =
            ticket.ticketId ??
            ticket.id ??
            "N/A";

    }


    // ------------------------------------------
    // License Plate
    // ------------------------------------------

    const ticketLicensePlateElement =
        document.getElementById(
            "ticketLicensePlate"
        );

    if (ticketLicensePlateElement) {

        ticketLicensePlateElement.textContent =
            ticket.licensePlate ??
            ticket.vehicle?.licensePlate ??
            "N/A";

    }


    // ------------------------------------------
    // Vehicle Type
    // ------------------------------------------

    const ticketVehicleTypeElement =
        document.getElementById(
            "ticketVehicleType"
        );

    if (ticketVehicleTypeElement) {

        ticketVehicleTypeElement.textContent =
            ticket.vehicleType ??
            ticket.vehicle?.type ??
            "N/A";

    }


    // ------------------------------------------
    // Parking Spot
    // ------------------------------------------

    const ticketSpotElement =
        document.getElementById(
            "ticketSpot"
        );

    if (ticketSpotElement) {

        ticketSpotElement.textContent =
            ticket.spotNumber ??
            ticket.parkingSpot?.spotNumber ??
            "N/A";

    }


    // ------------------------------------------
    // Automatically put ticket ID
    // into exit form
    // ------------------------------------------

    if (exitTicketIdInput) {

        exitTicketIdInput.value =
            ticket.ticketId ??
            ticket.id ??
            "";

    }

}


// ============================================================
// VEHICLE EXIT
// ============================================================

if (exitForm) {

    exitForm.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();

            await exitVehicle();

        }
    );

}


// ============================================================
// Exit Vehicle
// ============================================================

async function exitVehicle() {

    const ticketId =
        exitTicketIdInput.value.trim();


    // ------------------------------------------
    // Validation
    // ------------------------------------------

    if (!ticketId) {

        showMessage(
            "Please enter a ticket ID.",
            "error"
        );

        exitTicketIdInput.focus();

        return;
    }


    // ------------------------------------------
    // Disable button
    // ------------------------------------------

    const submitButton =
        exitForm.querySelector(
            "button[type='submit']"
        );

    setButtonLoading(
        submitButton,
        true,
        "Processing..."
    );


    try {

        // --------------------------------------
        // Send exit request
        // --------------------------------------

        const response = await fetch(
            `${API_BASE_URL}/exit`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    ticketId: ticketId
                })
            }
        );


        // --------------------------------------
        // Handle HTTP errors
        // --------------------------------------

        if (!response.ok) {

            const errorMessage =
                await getErrorMessage(response);

            throw new Error(errorMessage);
        }


        // --------------------------------------
        // Read response
        // --------------------------------------

        const result =
            await response.json();


        console.log(
            "Vehicle exit response:",
            result
        );


        // --------------------------------------
        // Display fare
        // --------------------------------------

        displayFare(result);


        // --------------------------------------
        // Success message
        // --------------------------------------

        showMessage(
            "Vehicle exited successfully.",
            "success"
        );


        // --------------------------------------
        // Clear exit form
        // --------------------------------------

        exitForm.reset();


        // --------------------------------------
        // Refresh parking spots
        // --------------------------------------

        await loadParkingSpots();

    }

    catch (error) {

        console.error(
            "Vehicle exit error:",
            error
        );

        showMessage(
            error.message ||
            "Unable to process vehicle exit.",
            "error"
        );

    }

    finally {

        setButtonLoading(
            submitButton,
            false,
            "Exit Vehicle"
        );

    }

}


// ============================================================
// DISPLAY FARE
// ============================================================

function displayFare(result) {

    if (!fareSection) {
        return;
    }


    // ------------------------------------------
    // Show fare section
    // ------------------------------------------

    fareSection.hidden = false;


    // ------------------------------------------
    // Duration
    // ------------------------------------------

    const durationElement =
        document.getElementById(
            "parkingDuration"
        );

    if (durationElement) {

        durationElement.textContent =
            result.duration ??
            result.parkingDuration ??
            "N/A";

    }


    // ------------------------------------------
    // Fare
    // ------------------------------------------

    const fareElement =
        document.getElementById(
            "totalFare"
        );

    if (fareElement) {

        const fare =
            result.fare ??
            result.totalFare ??
            0;

        fareElement.textContent =
            formatCurrency(fare);

    }

}


// ============================================================
// LOAD PARKING SPOTS
// ============================================================

async function loadParkingSpots() {

    try {

        showParkingLoading();


        // --------------------------------------
        // Request parking spots
        // --------------------------------------

        const response = await fetch(
            `${API_BASE_URL}/spots`
        );


        // --------------------------------------
        // Handle HTTP errors
        // --------------------------------------

        if (!response.ok) {

            const errorMessage =
                await getErrorMessage(response);

            throw new Error(errorMessage);
        }


        // --------------------------------------
        // Read JSON
        // --------------------------------------

        const spots =
            await response.json();


        console.log(
            "Parking spots:",
            spots
        );


        // --------------------------------------
        // Display spots
        // --------------------------------------

        displayParkingSpots(spots);

    }

    catch (error) {

        console.error(
            "Parking spot loading error:",
            error
        );


        showMessage(
            error.message ||
            "Unable to load parking spots.",
            "error"
        );

    }

}


// ============================================================
// DISPLAY PARKING SPOTS
// ============================================================

function displayParkingSpots(spots) {

    if (!parkingGrid) {
        return;
    }


    // ------------------------------------------
    // Clear previous spots
    // ------------------------------------------

    parkingGrid.innerHTML = "";


    // ------------------------------------------
    // Validate response
    // ------------------------------------------

    if (!Array.isArray(spots)) {

        console.error(
            "Invalid parking spot response:",
            spots
        );

        showMessage(
            "Invalid parking spot data received.",
            "error"
        );

        return;
    }


    // ------------------------------------------
    // Counters
    // ------------------------------------------

    let availableCount = 0;

    let occupiedCount = 0;


    // ------------------------------------------
    // Create spot elements
    // ------------------------------------------

    spots.forEach((spot) => {

        const spotElement =
            document.createElement("div");


        spotElement.classList.add(
            "parking-spot"
        );


        // --------------------------------------
        // Determine availability
        // --------------------------------------

        const isAvailable =
            spot.available === true ||
            spot.isAvailable === true;


        if (isAvailable) {

            spotElement.classList.add(
                "available"
            );

            availableCount++;

        }

        else {

            spotElement.classList.add(
                "occupied"
            );

            occupiedCount++;

        }


        // --------------------------------------
        // Spot number
        // --------------------------------------

        const spotNumber =
            spot.spotNumber ??
            spot.number ??
            spot.id ??
            "N/A";


        // --------------------------------------
        // Status
        // --------------------------------------

        const status =
            isAvailable
                ? "Available"
                : "Occupied";


        spotElement.textContent =
            `Spot ${spotNumber} - ${status}`;


        // --------------------------------------
        // Add to grid
        // --------------------------------------

        parkingGrid.appendChild(
            spotElement
        );

    });


    // ------------------------------------------
    // Update dashboard
    // ------------------------------------------

    updateDashboard(
        spots.length,
        availableCount,
        occupiedCount
    );

}


// ============================================================
// UPDATE DASHBOARD
// ============================================================

function updateDashboard(
    total,
    available,
    occupied
) {

    if (totalSpotsElement) {

        totalSpotsElement.textContent =
            total;

    }


    if (availableSpotsElement) {

        availableSpotsElement.textContent =
            available;

    }


    if (occupiedSpotsElement) {

        occupiedSpotsElement.textContent =
            occupied;

    }

}


// ============================================================
// PARKING LOADING STATE
// ============================================================

function showParkingLoading() {

    if (!parkingGrid) {
        return;
    }

    parkingGrid.innerHTML =
        "<p>Loading parking spots...</p>";

}


// ============================================================
// SHOW MESSAGE
// ============================================================

function showMessage(
    message,
    type = "success"
) {

    if (!messageElement) {
        return;
    }


    messageElement.textContent =
        message;


    messageElement.className =
        `message ${type}`;


    // ------------------------------------------
    // Automatically hide after 5 seconds
    // ------------------------------------------

    setTimeout(() => {

        messageElement.className =
            "message";

        messageElement.textContent =
            "";

    }, 5000);

}


// ============================================================
// API ERROR HANDLER
// ============================================================

async function getErrorMessage(response) {

    try {

        const data =
            await response.json();


        if (data.message) {

            return data.message;

        }


        if (data.error) {

            return data.error;

        }


        return `Request failed with status ${response.status}.`;

    }

    catch {

        return `Request failed with status ${response.status}.`;

    }

}


// ============================================================
// BUTTON LOADING STATE
// ============================================================

function setButtonLoading(
    button,
    loading,
    loadingText
) {

    if (!button) {
        return;
    }


    if (loading) {

        button.disabled = true;

        button.dataset.originalText =
            button.textContent;

        button.textContent =
            loadingText;

    }

    else {

        button.disabled = false;

        button.textContent =
            button.dataset.originalText ||
            loadingText;

    }

}


// ============================================================
// CURRENCY FORMATTER
// ============================================================

function formatCurrency(value) {

    const number =
        Number(value);


    if (Number.isNaN(number)) {

        return value;

    }


    return `৳${number.toFixed(2)}`;

}


// ============================================================
// REFRESH PARKING DATA
// ============================================================

async function refreshParkingData() {

    await loadParkingSpots();

}


// ============================================================
// AUTO REFRESH
// ============================================================

// Refresh parking information every 10 seconds.

setInterval(
    refreshParkingData,
    10000
);