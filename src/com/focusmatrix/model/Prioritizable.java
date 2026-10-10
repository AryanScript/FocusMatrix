package com.focusmatrix.model;

/**
 * Prioritizable Interface (OOP Concepts: Interface)
 * Defines contracts for items that can be algorithmically scored and prioritized.
 */
public interface Prioritizable {
    double calculatePriorityScore();
    String determineQuadrant();
}
