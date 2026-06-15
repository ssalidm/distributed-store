# Distributed Store

A Spring Boot REST API for managing customer orders.

This project is the first phase of a larger Distributed Systems learning journey where a simple monolithic service will gradually evolve into a distributed microservices platform.

---

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Docker Compose
- Maven
- Lombok

---

## Architecture

```text
Client
   |
   v
Order Service
   |
   v
PostgreSQL
```

### Layered Architecture

```text
Controller
    |
Service
    |
Repository
    |
PostgreSQL
```

---

## Domain Model

### Order

| Field        | Type |
|--------------|---------|
| id           | UUID |
| customerName | String |
| productName  | String |
| quantity     | Integer |
| status       | OrderStatus |
| createdAt    | Instant |
| updatedAt    | Instant |

### Order Status

```text
PENDING
PROCESSING
COMPLETED
CANCELLED
```

---

## Running Locally

### Clone Repository

```bash
git clone https://github.com/ssalidm/distributed-store.git
cd distributed-store
```

### Start PostgreSQL

```bash
docker compose up -d
```

### Run Application

```bash
mvn spring-boot:run
```

Application will start on:

```text
http://localhost:8080
```

---

## API Endpoints

### Create Order

POST /api/orders

Request:

```json
{
  "customerName": "David",
  "productName": "Laptop",
  "quantity": 1
}
```

Response:

```json
{
  "success": true,
  "status": 201,
  "message": "Order created",
  "result": {
    "id": "1a6fc4ae-6610-4764-ba9e-a73bebc93663",
    "customerName": "David",
    "productName": "Laptop",
    "quantity": 1,
    "status": "PENDING",
    "createdAt": "2026-06-14T12:18:19.137618Z",
    "updatedAt": "2026-06-14T20:10:31.481127Z"
  },
  "timestamp": "2026-06-14T20:10:31.506718223Z"
}
```

---

### Get All Orders

GET /api/orders

Response:

```json
{
  "success": true,
  "status": 200,
  "message": "Orders retrieved",
  "result": [
    {
      "id": "1a6fc4ae-6610-4764-ba9e-a73bebc93663",
      "customerName": "David",
      "productName": "Laptop",
      "quantity": 1,
      "status": "PENDING",
      "createdAt": "2026-06-14T12:18:19.137618Z",
      "updatedAt": "2026-06-14T12:18:19.137790Z"
    },
    {
      "id": "3ef9e152-38cc-4fad-a636-76f03ef22b5f",
      "customerName": "Mary",
      "productName": "Bluetooth Speaker",
      "quantity": 1,
      "status": "PROCESSING",
      "createdAt": "2026-06-14T19:56:55.683729Z",
      "updatedAt": "2026-06-14T19:56:55.683771Z"
    }
  ],
  "timestamp": "2026-06-14T20:09:48.337157458Z"
}
```

---

### Get Order By Id

GET /api/orders/{id}

---

### Update Order Status

PATCH /api/orders/{id}/status

Request:

```json
{
  "status": "COMPLETED"
}
```

Response:

```json
{
  "success": true,
  "status": 200,
  "message": "Order status updated",
  "result": {
    "id": "1a6fc4ae-6610-4764-ba9e-a73bebc93663",
    "customerName": "David",
    "productName": "Laptop",
    "quantity": 1,
    "status": "COMPLETED",
    "createdAt": "2026-06-14T12:18:19.137618Z",
    "updatedAt": "2026-06-14T20:10:31.481127Z"
  },
  "timestamp": "2026-06-14T20:10:31.506718223Z"
}
```

---

### Delete Order

DELETE /api/orders/{id}

Response:

```JSON
{
  "success": true,
  "status": 200,
  "message": "Order deleted",
  "timestamp": "2026-06-14T20:23:29.900433274Z"
}
```

---

## Error Response

Example:

```json
{
  "success": false,
  "status": 404,
  "message": "Order not found",
  "path": "/api/orders/a8257e92-66ed-4d76-b174-eadb79440009",
  "timestamp": "2026-06-14T20:25:03.900565215Z"
}
```

---

## Future Enhancements

This project will gradually evolve into a distributed system with:

- Product Service
- RabbitMQ
- Event-Driven Communication
- OpenTelemetry
- Distributed Tracing
- Kubernetes
- Saga Pattern
- Resilience Patterns

---

## Author

David Ssali