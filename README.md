# Dispatch Load Balancer

A Spring Boot REST application that assigns delivery orders to a fleet of vehicles. It respects vehicle capacity, serves high-priority orders first and keeps the total travel distance low, using the Haversine formula for distances.

- **Live app:** https://dispatch-load-balancer-29e3.onrender.com
- **Swagger UI:** https://dispatch-load-balancer-29e3.onrender.com/swagger-ui/index.html
- **Sample data:** [`samples/sample-vehicles.json`](samples/sample-vehicles.json) and [`samples/sample-orders.json`](samples/sample-orders.json)

> **Note:** the app runs on Render's free tier, so the first request after a period of inactivity can take about a minute while the service wakes up. Data is stored in an in-memory H2 database, so it resets whenever the service restarts. Load the sample files again if the plan comes back empty.

## Quick start (try it in Swagger)

1. Open the Swagger UI link above.
2. `POST /api/dispatch/vehicles` with the contents of `samples/sample-vehicles.json`.
3. `POST /api/dispatch/orders` with the contents of `samples/sample-orders.json`.
4. `GET /api/dispatch/plan` to see the dispatch plan.

With the sample data (30 orders weighing 625 in total, fleet capacity 500), every HIGH and MEDIUM order is delivered, and the 7 orders that do not fit are all LOW priority. They are returned in `unassignedOrders` with a reason.

## API

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/dispatch/orders` | Submit delivery orders. An existing `orderId` is updated, not duplicated. |
| POST | `/api/dispatch/vehicles` | Submit fleet details. An existing `vehicleId` is updated, not duplicated. |
| GET | `/api/dispatch/plan` | Get the optimized dispatch plan. |

**Order:** `orderId`, `latitude`, `longitude`, `address`, `packageWeight`, `priority` (`HIGH`, `MEDIUM` or `LOW`)

**Vehicle:** `vehicleId`, `capacity`, `currentLatitude`, `currentLongitude`, `currentAddress`

**Plan response (shortened):**

```json
{
  "dispatchPlan": [
    {
      "vehicleId": "VEH001",
      "totalLoad": 100,
      "totalDistance": "14.4 km",
      "assignedOrders": [ { "orderId": "ORD026", "address": "Karol Bagh, Delhi, India", "packageWeight": 25, "priority": "HIGH" } ]
    }
  ],
  "unassignedOrders": [
    { "order": { "orderId": "ORD016", "priority": "LOW" }, "reason": "No vehicle has enough remaining capacity" }
  ]
}
```

Errors use one consistent shape: `{ "status": "error", "message": "...", "details": [ ... ] }`.

## How the dispatch algorithm works

The problem is a capacitated vehicle routing problem, which is NP-hard, so the app uses a fast greedy heuristic:

1. Sort orders by priority (HIGH, then MEDIUM, then LOW). Inside a priority, heavier orders go first because large packages are easier to place early.
2. For each order, pick the vehicle that still has enough capacity and whose current position is nearest. The vehicle's position then moves to that order.
3. If no vehicle can take the order, it is reported as unassigned, with a reason.
4. For each vehicle, visit its stops nearest-first starting from the vehicle's location, and add up the Haversine distances.

## Assumptions

- Total distance is measured from the vehicle's current location through its stops in order, with no return trip.
- Priority decides who gets vehicle capacity first. It does not force HIGH stops to be visited first inside a route.
- Haversine gives straight-line (great-circle) distance, not road distance.
- Weights and capacities use the same unit.

## Error handling

- Validation: latitude (-90 to 90), longitude (-180 to 180), positive weight and capacity, valid priority, required fields, non-empty lists. These return `400`.
- Duplicate IDs inside a single request return `400`.
- Calling `/plan` with no orders or no vehicles returns `404` with an explanatory message.
- Orders that cannot fit in any vehicle are returned in `unassignedOrders`, not as errors.
- Unknown URLs return `404`, wrong HTTP methods `405` and non-JSON bodies `415`.

## Tech stack

Java, Spring Boot, Spring Data JPA, H2 (in-memory), MapStruct, Lombok, springdoc-openapi (Swagger), JUnit 5, AssertJ, Mockito, MockMvc, Docker.

## Project structure

```
controller   REST endpoints
service      business logic (orders, vehicles, dispatch)
strategy     DispatchStrategy interface and the greedy implementation
distance     DistanceCalculator interface and the Haversine implementation
mapper       MapStruct mappers (DTO <-> entity <-> response)
repository   Spring Data JPA repositories
entity       JPA entities (Order, Vehicle)
dto          request and response objects
exception    custom exceptions and the global exception handler
```

The allocation algorithm and the distance calculation sit behind interfaces (Strategy pattern), so another algorithm or a road-distance provider can be plugged in without touching the rest of the code.

## Run locally

Requires Java 17 or newer.

```bash
./mvnw spring-boot:run
```

Then open http://localhost:8080/swagger-ui/index.html.

## Run the tests

```bash
./mvnw test
```

The suite has 24 tests: Haversine accuracy, the dispatch algorithm (priority order, capacity limits, boundary cases, nearest-first routing, unassignable orders), the REST layer and its error responses, the service-level checks and the upsert behaviour.

## Run with Docker

```bash
docker build -t dispatch .
docker run -p 8080:8080 dispatch
```
