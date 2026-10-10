package com.focusmatrix.model;

import java.time.LocalDateTime;

/**
 * TimeLog Entity (Maps to timelogs table)
 * Represents a completed Pomodoro Focus Sprint or Break.
 */
public class TimeLog {
    private int logId;
    private int taskId;
    private LocalDateTime startTime;
    private int durationMinutes;
    private String sessionType; // FOCUS, SHORT_BREAK, LONG_BREAK
    private LocalDateTime createdAt;

    public TimeLog() {
        this.createdAt = LocalDateTime.now();
        this.sessionType = "FOCUS";
    }

    public TimeLog(int logId, int taskId, LocalDateTime startTime, int durationMinutes, String sessionType) {
        this();
        this.logId = logId;
        this.taskId = taskId;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.sessionType = sessionType;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
