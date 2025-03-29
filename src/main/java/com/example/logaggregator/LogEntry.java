package com.example.logaggregator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {
    @NotBlank(message = "Service name is required")
    private String service_name;

    @NotNull(message = "Timestamp is required")
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
            message = "Timestamp must be in ISO-8601 format (e.g., 2025-03-28T10:15:00Z)"
    )
    private String timestamp;

    @NotBlank(message = "Message is required")
    private String message;

    public @NotBlank String getService_name() {
        return service_name;
    }

    public void setService_name(@NotBlank String service_name) {
        this.service_name = service_name;
    }

    public @NotNull String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NotNull String timestamp) {
        this.timestamp = timestamp;
    }

    public @NotBlank String getMessage() {
        return message;
    }

    public void setMessage(@NotBlank String message) {
        this.message = message;
    }
}
