# FocusMatrix — Project Documentation

## 1. Introduction

FocusMatrix is a time-management and task-prioritization
system designed to help users organize their tasks, identify
priorities, and manage their working sessions effectively.

The project combines task-prioritization techniques with
a Pomodoro-style focus timer to encourage structured work
and improve productivity.

## 2. Problem Statement

Managing multiple tasks can make it difficult to decide
which activities deserve immediate attention.

Traditional task lists often display tasks without
providing sufficient guidance about their relative
importance and urgency.

FocusMatrix addresses this challenge by organizing tasks
according to their importance, urgency, and deadlines.

## 3. Project Objectives

The primary objectives of FocusMatrix are:

- Organize tasks according to importance and urgency.
- Help users identify tasks requiring immediate attention.
- Support structured working sessions through a focus timer.
- Maintain task information in a relational database.
- Provide a web interface for interacting with the system.
- Apply core Java programming and object-oriented
  design principles to a practical application.

## 4. Core Concepts

### 4.1 Task Prioritization

FocusMatrix uses the Eisenhower Matrix to classify tasks
into four categories.

| Quadrant | Category | Purpose |
|---|---|---|
| Q1 | Do First | Important and urgent tasks |
| Q2 | Schedule | Important but less urgent tasks |
| Q3 | Delegate / Batch | Urgent but less important tasks |
| Q4 | Eliminate | Tasks that are neither important nor urgent |

This classification helps users make more informed
decisions about how to allocate their time.

### 4.2 Priority Scoring

The project uses a priority-scoring formula that considers
importance, urgency, and the time remaining before a deadline.

The scoring mechanism helps rank tasks so that users
can identify which tasks should receive attention first.

### 4.3 Focus Sessions

The application includes a Pomodoro-style focus timer
to support structured work sessions.

Time tracking can help users understand how they allocate
their working time across tasks.

## 5. Technology Stack

The project uses the following technologies:

- Java: Application logic and object-oriented programming.
- MySQL 8.0: Relational data storage.
- JDBC: Communication between Java and MySQL.
- HTML: Structure of the web interface.
- CSS: Visual presentation and styling.
- REST API integration: Communication between the web
  interface and the Java application.

## 6. High-Level System Architecture

FocusMatrix can be understood through three main layers.

### Presentation Layer

The presentation layer provides the user interface
through which users interact with the application.

It includes task visualization, task management controls,
and focus-session features.

### Application Layer

The Java application layer handles the core business logic.

Its responsibilities include task management,
priority calculation, validation, and web request handling.

### Data Layer

The data layer uses MySQL to store application data.

JDBC provides the connection between the Java application
and the relational database.

### Request Flow

The general interaction flow is:

1. A user interacts with the web interface.
2. The interface communicates with the Java application.
3. The application processes the request.
4. Database operations are performed when necessary.
5. The application returns a response.
6. The interface displays the resulting information.

## 7. Role of Object-Oriented Programming

The project applies several object-oriented programming
concepts.

### Encapsulation

Encapsulation helps control access to an object's data
and maintain valid application state.

### Abstraction

Abstraction allows common behavior to be represented
through suitable interfaces and abstract classes.

### Inheritance

Inheritance supports reuse of common behavior through
relationships between classes.

### Collections

Java collections help organize and process task data.
Priority-based collections can support efficient task
ordering.

### Exception Handling

Exception handling helps the application manage
invalid input and unexpected operational errors.

## 8. Database Management

FocusMatrix uses a relational database to maintain
task-related information and focus-session records.

The database includes task information and time-log
records, with relationships that associate tracked
sessions with their corresponding tasks.

JDBC provides the database connectivity required
for application operations.

## 9. Application Workflow

A typical workflow can be described as follows:

1. The user opens the application.
2. The user creates or manages tasks.
3. Tasks are evaluated according to their priority.
4. Tasks are organized into appropriate priority categories.
5. The user selects a task to work on.
6. The focus timer supports a structured work session.
7. Task and time-tracking information can be maintained
   by the application.

## 10. Potential Future Enhancements

Possible future enhancements could include:

- Personalized productivity reports.
- Advanced task-search and filtering options.
- Additional task analytics and visualizations.
- Configurable focus-session durations.
- Improved accessibility and keyboard navigation.
- Automated reminders for approaching deadlines.

These are potential enhancements, not claims about
features currently implemented in the project.

## 11. Conclusion

FocusMatrix combines task prioritization, time management,
database integration, and Java application development
in a single productivity-oriented project.

Its use of the Eisenhower Matrix provides a structured
approach to organizing work, while the focus timer
encourages more deliberate working sessions.

The project also demonstrates how Java, object-oriented
programming, JDBC, relational databases, and web
technologies can work together to build an application.

---

Documentation contribution:
This document provides an overview of the project's
purpose, concepts, architecture, and workflow.