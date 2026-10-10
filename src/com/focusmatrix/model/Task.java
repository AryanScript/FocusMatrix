package com.focusmatrix.model;

import com.focusmatrix.exception.InvalidTaskException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Concrete Task Entity (OOP Concepts: Concrete Class, Polymorphism, Encapsulation)
 * Implements the proprietary FocusMatrix Dynamic Priority Scoring Formula.
 */
public class Task extends AbstractTask {

    public Task() {
        super();
    }

    public Task(int taskId, String title, String description, LocalDateTime deadline, int importance, int urgency) 
            throws InvalidTaskException {
        super(taskId, title, description, deadline, importance, urgency);
        validate();
        this.priorityScore = calculatePriorityScore();
        this.quadrant = determineQuadrant();
    }

    /**
     * Validates business invariants (Throws custom exception on error)
     */
    public void validate() throws InvalidTaskException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidTaskException("Task title cannot be blank or empty.");
        }
        if (importance < 1 || importance > 5) {
            throw new InvalidTaskException("Importance must be scaled between 1 and 5. Received: " + importance);
        }
        if (urgency < 1 || urgency > 5) {
            throw new InvalidTaskException("Urgency must be scaled between 1 and 5. Received: " + urgency);
        }
        if (deadline == null) {
            throw new InvalidTaskException("Task deadline cannot be null.");
        }
    }

    /**
     * FocusMatrix Dynamic Priority Scoring Formula:
     * Score = (Importance * 3.0) + (10.0 / (DaysRemaining + 1)) + (Urgency * 2.0)
     */
    @Override
    public double calculatePriorityScore() {
        LocalDateTime now = LocalDateTime.now();
        double daysRemaining = 0.0;

        if (deadline != null) {
            long secondsRemaining = Duration.between(now, deadline).getSeconds();
            daysRemaining = Math.max(0.0, secondsRemaining / 86400.0);
        }

        // Deadline decay factor
        double deadlineFactor = 10.0 / (daysRemaining + 1.0);

        // Core weighted algorithm
        double score = (this.importance * 3.0) + deadlineFactor + (this.urgency * 2.0);
        
        // Round to 1 decimal place
        return Math.round(score * 10.0) / 10.0;
    }

    /**
     * Categorizes into Eisenhower Matrix quadrants
     */
    @Override
    public String determineQuadrant() {
        boolean isImportant = (this.importance >= 3);
        boolean isUrgent = (this.urgency >= 3);

        if (isImportant && isUrgent) {
            return "Q1"; // Do First
        } else if (isImportant && !isUrgent) {
            return "Q2"; // Schedule
        } else if (!isImportant && isUrgent) {
            return "Q3"; // Delegate
        } else {
            return "Q4"; // Eliminate
        }
    }

    @Override
    public String getSummary() {
        return String.format("[%s] #%d: %s (Score: %.1f | %s | Status: %s)", 
                quadrant, taskId, title, priorityScore, deadline, status);
    }
}
