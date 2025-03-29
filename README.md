# Distributed Log Aggregator

A distributed log aggregation service that collects and queries logs from different microservices.

## Features

- RESTful API for log ingestion and querying
- Efficient storage with in-memory data structures
- Thread-safe design to support concurrent operations
- Automatic log expiration after 1 hour
- Support for time-based queries

## Architecture

### Overview

This system uses Spring Boot to implement a RESTful service for log aggregation. It maintains logs in memory for fast access using thread-safe concurrent data structures.

### Key Components

1. **LogEntry**: Data model representing a log entry with service name, timestamp, and message
2. **LogService**: Core service responsible for storing and retrieving logs
3. **LogController**: REST controller that exposes the API endpoints
4. **LogAggregatorApplication**: Main application entry point

### Data Storage

Logs are stored in a two-level map structure:
- First level: Service name -> Log collection (ConcurrentHashMap)
- Second level: Timestamp -> Log entry (ConcurrentSkipListMap for automatic time ordering)

This structure enables:
- Fast access to logs by service name
- Automatic time-based ordering
- Efficient range queries
- Thread-safe operations

### Log Expiration

A scheduled task runs every 5 minutes to remove logs older than 1 hour, ensuring memory efficiency.

## API Endpoints

### POST /logs (Ingest log entry)

Accepts a JSON payload with:
- `service_name` (string): Name of the service that generated the log
- `timestamp` (string): When the log was created
- `message` (string): Log message

Example request:
```json
{
  "service_name": "auth-service",
  "timestamp": "2025-03-17T10:15:00Z",
  "message": "User login successful"
}
```

### GET /logs?service={service_name}&start={timestamp}&end={timestamp} (Query logs)

Returns all log messages for the given service within the time range [start, end]

Example request:
```
GET /logs?service=auth-service&start=2025-03-17T10:00:00Z&end=2025-03-17T10:30:00Z
```

Example response:
```json
[
  {
    "timestamp": "2025-03-17T10:05:00Z",
    "message": "User attempted login"
  },
  {
    "timestamp": "2025-03-17T10:15:00Z",
    "message": "User login successful"
  }
]
```

## How to Run

### Prerequisites
- Java 11+ installed
- Maven 3.6+ installed

### Build and Run
1. Clone this repository
2. Navigate to the project directory
3. Build the project:
   ```
   mvn clean package
   ```
4. Run the application:
   ```
   java -jar target/log-aggregator-1.0.0.jar
   ```

The service will start on port 8080 by default.

## Testing

You can test the API using curl or any HTTP client:

### Store a log
```bash
curl -X POST -H "Content-Type: application/json" -d '{"service_name":"auth-service","timestamp":"2025-03-17T10:15:00Z","message":"User login successful"}' http://localhost:8080/logs
```

### Query logs
```bash
curl "http://localhost:8080/logs?service=auth-service&start=2025-03-17T10:00:00Z&end=2025-03-17T10:30:00Z"
```

## Performance Considerations

- The system uses in-memory storage for fast access but is not suitable for long-term persistence
- For production use, consider adding a persistent storage layer
- For high-volume environments, consider implementing sharding or a more distributed approach
- Monitor memory usage, especially with high log volumes