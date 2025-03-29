package com.example.logaggregator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logs")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * POST endpoint to ingest a new log entry.
     * Returns detailed success or error information.
     *
     * @param logEntry The log entry to store, validated using annotations
     * @return ResponseEntity with a map containing status, message, timestamp and data/error details
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> ingestLog(@Valid @RequestBody LogEntry logEntry) {
        logService.storeLog(logEntry);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Log entry stored successfully");
        response.put("timestamp", Instant.now().toString());
        response.put("data", logEntry);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET endpoint to query logs for a specific service within a time range.
     * Returns a list of matching logs or detailed error information.
     *
     * @param service The service name to query logs for
     * @param start The start timestamp (inclusive)
     * @param end The end timestamp (inclusive)
     * @return ResponseEntity with a map containing status, service, time range, count and log data/error details
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> queryLogs(
            @RequestParam("service") String service,
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        validateTimestamps(start, end);

        List<LogEntry> logs = logService.queryLogs(service, start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("timestamp", Instant.now().toString());
        response.put("service", service);
        response.put("timeRange", Map.of("start", start, "end", end));
        response.put("count", logs.size());
        response.put("data", logs);

        return ResponseEntity.ok(response);
    }

    /**
     * Validates timestamp formats and ensures start time is not after end time.
     *
     * @param start The start timestamp to validate
     * @param end The end timestamp to validate
     * @throws IllegalArgumentException If timestamps are invalid or start is after end
     */
    private void validateTimestamps(String start, String end) {
        try {
            Instant.parse(start);
            Instant.parse(end);

            if (Instant.parse(start).isAfter(Instant.parse(end))) {
                throw new IllegalArgumentException("Start time cannot be after end time");
            }
        } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException)) {
                throw new IllegalArgumentException("Invalid timestamp format. Use ISO-8601 format (e.g., 2025-03-28T10:15:00Z)");
            }
            throw e;
        }
    }
}