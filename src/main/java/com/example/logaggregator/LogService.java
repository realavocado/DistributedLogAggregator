package com.example.logaggregator;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class LogService {
    private final Map<String, ConcurrentSkipListMap<String, LogEntry>> logStore = new ConcurrentHashMap<>();

    /**
     * Store a log entry in memory
     * @param logEntry The log entry to store
     */
    public void storeLog(LogEntry logEntry) {
        // Get or create a skip list map for this service
        ConcurrentSkipListMap<String, LogEntry> serviceLogMap = logStore.computeIfAbsent(
                logEntry.getService_name(),
                k -> new ConcurrentSkipListMap<>()
        );

        // Add the log entry to the map, using timestamp as key for automatic ordering
        serviceLogMap.put(logEntry.getTimestamp(), logEntry);
    }

    /**
     * Query logs for a specific service within a time range
     * @param serviceName The service to query logs for
     * @param start Start timestamp (inclusive)
     * @param end End timestamp (inclusive)
     * @return List of matching log entries
     */
    public List<LogEntry> queryLogs(String serviceName, String start, String end) {
        // Get the log map for the specified service
        ConcurrentSkipListMap<String, LogEntry> serviceLogMap = logStore.get(serviceName);

        // If no logs for this service, return an empty list
        if (serviceLogMap == null) {
            return Collections.emptyList();
        }

        // Return logs within the range, sorted by timestamp
        return new ArrayList<>(serviceLogMap.subMap(start, true, end, true).values());
    }

    /**
     * Scheduled task to expire old logs (older than 1 hour)
     * Runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5分钟，单位为毫秒
    public void expireOldLogs() {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        String cutoffTimestamp = oneHourAgo.toString();

        // For each service's log map
        logStore.forEach((service, logMap) -> {
            // Remove all entries older than the cutoff
            logMap.headMap(cutoffTimestamp).clear();
        });
    }
}