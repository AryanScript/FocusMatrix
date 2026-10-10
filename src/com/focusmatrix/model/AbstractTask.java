package com.focusmatrix.model;

import java.time.LocalDateTime;

/**
 * AbstractTask (OOP Concepts: Abstract Class & Inheritance)
 * Base template for all task entities in the FocusMatrix system.
 */
public abstract class AbstractTask implements Prioritizable, Comparable<AbstractTask> {
    protected int taskId;
    protected String title;
    protected String description;
    protected LocalDateTime deadline;
    protected int importance; // 1 to 5
    protected int urgency;    // 1 to 5
    protected String quadrant; // Q1, Q2, Q3, Q4
    protected double priorityScore;
    protected String status; // PENDING, IN_PROGRESS, COMPLETED
    protected LocalDateTime createdAt;

    public AbstractTask() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public AbstractTask(int taskId, String title, String description, LocalDateTime deadline, int importance, int urgency) {
        this();
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.importance = importance;
        this.urgency = urgency;
    }

    // Abstract method to be overridden (Polymorphism)
    public abstract String getSummary();

    @Override
    public int compareTo(AbstractTask other) {
        // Natural ordering: Highest priority score first
        return Double.compare(other.priorityScore, this.priorityScore);
    }

    // Getters and Setters (Encapsulation)
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public int getImportance() { return importance; }
    public void setImportance(int importance) { this.importance = importance; }

    public int getUrgency() { return urgency; }
    public void setUrgency(int urgency) { this.urgency = urgency; }

    public String getQuadrant() { return quadrant; }
    public void setQuadrant(String quadrant) { this.quadrant = quadrant; }

    public double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(double priorityScore) { this.priorityScore = priorityScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
