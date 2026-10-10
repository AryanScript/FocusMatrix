package com.focusmatrix.exception;

/**
 * Custom Exception for Task validation rules.
 * Fulfills OOP Rubric: Custom Exception Handling.
 */
public class InvalidTaskException extends Exception {
    public InvalidTaskException(String message) {
        super(message);
    }

    public InvalidTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
