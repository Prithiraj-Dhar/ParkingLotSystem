# Build + run the Parking Lot Management System with nothing but a JDK -
# matches how this project actually builds (plain javac, no Gradle
# wrapper committed), so this Dockerfile is the source of truth for
# how to compile and run it in production.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY backend backend
COPY server server
RUN mkdir out \
    && find backend/src -name "*.java" | xargs javac -d out \
    && find server -name "*.java" | xargs javac -d out -cp "backend:."

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/out out
COPY frontend frontend

# Most hosts (Render, Railway, Fly) inject PORT at runtime;
# ParkingServer already reads it via System.getenv("PORT").
EXPOSE 8080
CMD ["java", "-cp", "out", "parkingLot.server.ParkingServer"]
